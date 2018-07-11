package com.moye.crawler.modules.spider.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.moye.crawler.common.constant.CodeConstant;
import com.moye.crawler.common.dao.impl.RedisHandle;

import com.moye.crawler.common.utils.State;
import com.moye.crawler.common.utils.es.ElasticsearchUtils;
import com.moye.crawler.common.utils.es.EsPage;
import com.moye.crawler.config.properties.SpiderProperties;
//import com.moye.crawler.core.spider.MySpider;
import com.moye.crawler.core.spider.common.Webpage;
import com.moye.crawler.core.spider.downloader.CasperjsDownloader;
import com.moye.crawler.core.spider.downloader.ContentLengthLimitHttpClientDownloader;
import com.moye.crawler.core.spider.init.MySpider;
import com.moye.crawler.core.spider.pipeline.CommonWebpagePipeline;
import com.moye.crawler.core.spider.processor.CustomPageProcessor;
import com.moye.crawler.core.spider.scheduler.MyRedisScheduler;
import com.moye.crawler.entity.spider.SpiderInfo;
import com.moye.crawler.entity.spider.SpiderTask;
import com.moye.crawler.modules.spider.dao.SpiderTaskDao;
import com.moye.crawler.modules.spider.service.SpiderTaskService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.pipeline.ResultItemsCollectorPipeline;
import us.codecraft.webmagic.scheduler.QueueScheduler;

import javax.management.JMException;
import java.util.*;
import java.util.function.Function;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author theodo
 * @since 2018-04-19
 */
@Service
public class SpiderTaskServiceImpl extends ServiceImpl<SpiderTaskDao, SpiderTask> implements SpiderTaskService {

    private Logger LOG = LogManager.getLogger(SpiderTaskServiceImpl.class);

    @Autowired
    private SpiderTaskDao spiderTaskDao;

    @Autowired
    private RedisHandle redisHandle;

    @Autowired
    private SpiderProperties spiderProperties;

    @Autowired
    private CasperjsDownloader casperjsDownloader;

    @Autowired
    private CommonWebpagePipeline commonWebpagePipeline;

    @Autowired
    private MyRedisScheduler redisScheduler;

    private Map<String, MySpider> spiderMap = new HashMap<>();

    @Autowired
    private ContentLengthLimitHttpClientDownloader contentLengthLimitHttpClientDownloader;


    /**
     * java8 函数式方法 入参spidertask 出参boolean lamda 表达式（x）->"aaa" + x;
     *
     * @param function
     * @return
     */
    private boolean findTaskBy(Function<SpiderTask, Boolean> function) {
        return redisHandle.getMap(CodeConstant.SPIDER_TASK_MAP_KEY).entrySet().stream().filter(taskEntry -> {
            Boolean b = function.apply(JSONObject.parseObject(taskEntry.getValue().toString(), SpiderTask.class));
            return b == null ? false : b;
        }).count() > 0;
    }

    /**
     * 启动爬虫
     *
     * @param info
     * @return
     */
    @Override
    public String doStartSpider(SpiderInfo info) {
        //判断提交的爬虫模板在任务队列里面是否存在


        boolean running = findTaskBy(spiderTask -> {
            Object spiderinfoObj = spiderTask.getSpiderInfo();
            if (spiderinfoObj != null && spiderinfoObj instanceof SpiderInfo) {
                SpiderInfo spiderInfo = (SpiderInfo) spiderinfoObj;
                return StringUtils.equals(spiderTask.getState(), State.RUNNING.toString()) && spiderInfo.getId().equals(info.getId());
            }
            return false;
        });
        Preconditions.checkArgument(!running, "已经提交了这个任务,模板编号%s,请勿重复提交", info.getId());
        //创建爬虫任务
        String uuid = UUID.randomUUID().toString().replaceAll("-","");
        SpiderTask task = this.initTask(uuid, info.getDomain(), info.getCallbackUrl(), "spiderInfoId=" + info.getId() + "&spiderUUID=" + uuid, info);
        MySpider spider = (MySpider) makeSpider(info, task).setScheduler( redisScheduler );

        //添加其他的数据管道
        spider.addPipeline(commonWebpagePipeline);
        spider.startUrls( Arrays.asList(info.getStartUrl().split(",")) );
        Arrays.asList(info.getStartUrl().split(",")).forEach(s -> redisScheduler.pushWhenNoDuplicate(new Request(s), spider));
        //慎用爬虫监控,可能导致内存泄露
//        spiderMonitor.register(spider);
        spiderMap.put(uuid, spider);
        spider.start();
        task.setState(State.RUNNING.toString());
        spiderTaskDao.updateById(task);
        return uuid;
    }

    /**
     * 测试爬虫模板
     *
     * @param info
     * @return
     */
    @Override
    public List<Webpage> doTestSpiderInfo(SpiderInfo info) throws JMException {
        final ResultItemsCollectorPipeline resultItemsCollectorPipeline = new ResultItemsCollectorPipeline();
        final String uuid = UUID.randomUUID().toString().replaceAll("-","");
        SpiderTask task = this.initTask(uuid, info.getDomain(), info.getCallbackUrl(), "spiderInfoId=" + info.getId() + "&spiderUUID=" + uuid, info);
        QueueScheduler queueScheduler = new QueueScheduler();
        MySpider spider = (MySpider) makeSpider(info, task)
                .addPipeline(resultItemsCollectorPipeline)
                .setScheduler(queueScheduler);
        if (info.getAjaxSite() == 1 && StringUtils.isNotBlank(spiderProperties.getAjaxDownloader())) {
            spider.setDownloader(casperjsDownloader);
        } else {
            spider.setDownloader(contentLengthLimitHttpClientDownloader);
        }

        spider.startUrls(Arrays.asList(info.getStartUrl().split(",")));
        //慎用爬虫监控,可能导致内存泄露
//        spiderMonitor.register(spider);
        spiderMap.put(uuid, spider);
        task.setState(State.RUNNING.toString());
        spiderTaskDao.updateById(task);
        spider.run();
        List<Webpage> webpageList = Lists.newLinkedList();
        resultItemsCollectorPipeline.getCollected().forEach(resultItems -> webpageList.add(CommonWebpagePipeline.convertResultItems2Webpage(resultItems)));
        return webpageList;
    }


    /**
     * 生成爬虫
     *
     * @param info 抓取模板
     * @param task 任务实体
     * @return
     */
    private MySpider makeSpider(SpiderInfo info, SpiderTask task) {
        MySpider spider = ((MySpider) new MySpider(new CustomPageProcessor(info, task), commonWebpagePipeline, info, spiderProperties)
                .thread(info.getThread())
                .setUUID(task.getId()));
        if (info.getAjaxSite() == 1 && StringUtils.isNotBlank(spiderProperties.getAjaxDownloader())) {
            spider.setDownloader(casperjsDownloader);
        } else {
            spider.setDownloader(contentLengthLimitHttpClientDownloader);
        }
        return spider;
    }

    @Override
    public SpiderTask getTaskById(String uuid) {
        LOG.info("根据任务ID:{},获取任务实体", uuid);
        String jsonstr = redisHandle.getMapField(CodeConstant.SPIDER_TASK_MAP_KEY, uuid);
        if (StringUtils.isBlank(jsonstr)) {
            return null;
        }
        return JSONObject.parseObject(jsonstr, SpiderTask.class);
    }

    /**
     * 初始化一个任务
     *
     * @param name         任务名称
     * @param callbackURL  回调地址列表
     * @param callbackPara 回调参数
     * @return 已经初始化的任务
     */
    public SpiderTask initTask(String taskid, String name, String callbackURL, String callbackPara, SpiderInfo info) {
        SpiderTask task = new SpiderTask();
        task.setId(taskid);
        task.setName(name);
        task.setSpiderInfoId(info.getId());
        task.setCallbackUrl(callbackURL);
        task.setCreateTime(new Date());
        task.setCallbackPara(callbackPara + "&taskId=" + taskid);
        task.setDesc("任务名称:" + name + "已初始化");
        spiderTaskDao.insert(task);
        task.setSpiderInfo(info);
        redisHandle.addMap( CodeConstant.SPIDER_TASK_MAP_KEY, taskid, JSONObject.toJSONString(task));
        return task;
    }

    @Override
    public Page<SpiderTask> findSipderTaskList(Page<SpiderTask> page, Map<String, String> paramMap) {
        page.setRecords(spiderTaskDao.list(page, paramMap));
        return page;
    }

    @Override
    public Page<Map<String, Object>> findSpiderDataPage(Page<Webpage> page, Map<String, String> stringMap) {
        StringBuffer sb = new StringBuffer();
        stringMap.entrySet().forEach(entry -> {
            if(StringUtils.isNoneBlank(entry.getValue())){
                sb.append(entry.getKey() + "=" + entry.getValue()).append(",");
            }
        });
        String paramstr = StringUtils.isNoneBlank(sb.toString()) ? sb.toString().substring(0,sb.lastIndexOf(",")) : "";
        String fields = "title,domain,url,spiderInfoId,publishTime";
        EsPage esPage = ElasticsearchUtils.searchDataPage(CodeConstant.INDEX_NAME, CodeConstant.TYPE_NAME, page.getOffsetCurrent(), page.getSize(), 0, 0, fields,
                "publishTime", false, "title", paramstr);
        System.out.println(JSONObject.toJSONString(esPage));
        Page<Map<String, Object>> page1 = new Page<>();
        page1.setRecords(esPage.getRecordList());
        page1.setCurrent(esPage.getCurrentPage());
        page1.setTotal(esPage.getRecordCount());
        page1.setSize(esPage.getPageSize());
        return page1;
    }


}
