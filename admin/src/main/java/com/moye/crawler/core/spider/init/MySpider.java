package com.moye.crawler.core.spider.init;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.moye.crawler.common.dao.impl.RedisHandle;
import com.moye.crawler.common.utils.SpringUtil;
import com.moye.crawler.common.utils.State;
import com.moye.crawler.config.properties.SpiderProperties;
import com.moye.crawler.core.spider.pipeline.CommonWebpagePipeline;
import com.moye.crawler.entity.spider.SpiderInfo;
import com.moye.crawler.entity.spider.SpiderTask;
import com.moye.crawler.modules.spider.service.SpiderTaskService;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * @Author: moye
 * @Description:
 * @Date Created in  2018/4/24 11:18
 * @Modified By
 */
public class MySpider extends Spider {

    private SpiderTaskService taskService = SpringUtil.getBean(SpiderTaskService.class);

    private SpiderProperties spiderProperties;

    private CommonWebpagePipeline commonWebpagePipeline;

    private final SpiderInfo spiderInfo;

    private RedisHandle redisHandle = SpringUtil.getBean(RedisHandle.class);

    private Logger LOG = LogManager.getLogger(MySpider.class);

    private long start = System.currentTimeMillis();

    private SpiderTask task;

    public MySpider(PageProcessor pageProcessor, CommonWebpagePipeline commonWebpagePipeline, SpiderInfo spiderInfo, SpiderProperties spiderProperties) {
        super(pageProcessor);
        this.spiderInfo = spiderInfo;
        this.commonWebpagePipeline = commonWebpagePipeline;
        this.spiderProperties = spiderProperties;
    }

    @Override
    protected void onSuccess(Request request) {
        super.onSuccess(request);
        task = taskService.getTaskById(this.getUUID());
        int count = redisHandle.get("count_" + task.getId()) == null ? 0 : NumberUtils.toInt(redisHandle.get("count_" + task.getId()).toString());
        task.setCount(count);
        //已抓取数量大于最大抓取页数,退出
        boolean reachMax = (spiderInfo.getMaxPageGather() > 0 &&  task.getCount() >= spiderInfo.getMaxPageGather() );
        //如果抓取页面超过最大抓取数量ratio倍的时候,仍未达到最大抓取数量,爬虫也退出
        boolean exceedRatio = (this.getPageCount() > spiderInfo.getMaxPageGather() * spiderProperties.getCommonsWebpageCrawlRatio() && spiderInfo.getMaxPageGather() > 0);
        System.out.println("task.getCount()----"+task.getCount() + "----this.getPageCount()" +  this.getPageCount());
        if (((reachMax)) && this.getStatus() == Status.Running) {
            LOG.info("爬虫ID{}已处理{}个页面,有效页面{}个,最大抓取页数{},reachMax={},exceedRatio={},退出.", this.getUUID(), this.getPageCount(),
                    task.getCount(), spiderInfo.getMaxPageGather(), reachMax, exceedRatio);
            String desc = String.format("爬虫ID%s已处理%s个页面,有效页面%s个,达到最大抓取页数%s,reachMax=%s,exceedRatio=%s,退出.", this.getUUID(), this.getPageCount(), task.getCount(), spiderInfo.getMaxPageGather(), reachMax, exceedRatio);
            task.setDesc(desc);
            this.stop();
//            return;
        }
    }


    @Override
    protected void onError(Request request) {
        super.onError(request);
        task.setDesc("处理网页" + request.getUrl() + "时发生错误" + request.getExtras());
        redisHandle.remove("count_" + task.getId());
        updateTaskStatus( State.FAIL.toString());
    }
    @Override
    public void stop(){
        updateTaskStatus(State.STOP.toString());
        super.stop();
    }
    @Override
    public void close() {
        if (task != null) {
            //清除抓取列表缓存
            commonWebpagePipeline.deleteUrls(task.getId());
            updateTaskStatus(State.STOP.toString());
        }
        super.close();
    }

    private void updateTaskStatus(String status) {
        EntityWrapper<SpiderTask> wrapper = new EntityWrapper<>();
        wrapper.eq("id", task.getId());
        task.setState(status);
        task.setUseTime(System.currentTimeMillis() - start);
        taskService.update(task, wrapper);
        redisHandle.remove("count_" + task.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()){ return false;}
        MySpider mySpider = (MySpider) o;
        return new EqualsBuilder()
                .append(this.getUUID(), mySpider.getUUID())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(this.getUUID())
                .toHashCode();
    }
}
