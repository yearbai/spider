package com.moye.crawler.core.spider.pipeline;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.hash.Hashing;
import com.moye.crawler.common.constant.CodeConstant;
import com.moye.crawler.common.utils.es.ElasticsearchUtils;
import com.moye.crawler.core.spider.common.Webpage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;

/**
 * CommonWebpagePipeline
 *
 * @author Gao Shen
 * @version 16/4/12
 */
@Component
public class CommonWebpagePipeline implements DuplicateRemover, Pipeline {

    private static final String DYNAMIC_FIELD = "dynamic_fields";
    private final static String COMMON_INDEX_CONFIG = "commonIndex.json";


    private static int COUNT = 0;
    private Logger LOG = LogManager.getLogger(CommonWebpagePipeline.class);
    private Map<String, Set<String>> urls = Maps.newConcurrentMap();


    public CommonWebpagePipeline(){
        LOG.debug("检查ES index,type 是否存在");
        ElasticsearchUtils.checkIndex( CodeConstant.INDEX_NAME, COMMON_INDEX_CONFIG);
        ElasticsearchUtils.checkType(CodeConstant.INDEX_NAME, CodeConstant.TYPE_NAME, "webpage.json");
    }

    /**
     * 将webmagic的resultItems转换成webpage对象
     *
     * @param resultItems
     * @return
     */
    public static Webpage convertResultItems2Webpage(ResultItems resultItems) {
        Webpage webpage = new Webpage();
        webpage.setContent(resultItems.get("content"));
        webpage.setTitle(resultItems.get("title"));
        webpage.setUrl(resultItems.get("url"));
        webpage.setId(Hashing.md5().hashString(webpage.getUrl(), Charset.forName("utf-8")).toString());
        webpage.setDomain(resultItems.get("domain"));
        webpage.setSpiderInfoId(resultItems.get("spiderInfoId").toString());
        webpage.setGathertime(resultItems.get("gatherTime"));
        webpage.setSpiderUUID(resultItems.get("spiderUUID"));
        webpage.setKeywords(resultItems.get("keywords"));
        webpage.setSummary(resultItems.get("summary"));
        webpage.setNamedEntity(resultItems.get("namedEntity"));
        webpage.setPublishTime(resultItems.get("publishTime") == null ? null : resultItems.get("publishTime"));
        webpage.setCategory(resultItems.get("category"));
        webpage.setRawHTML(resultItems.get("rawHTML"));
        webpage.setDynamicFields(resultItems.get(DYNAMIC_FIELD));
        webpage.setStaticFields(resultItems.get("staticField"));
        webpage.setAttachmentList(resultItems.get("attachmentList"));
        webpage.setImageList(resultItems.get("imageList"));
        webpage.setProcessTime(resultItems.get("processTime"));
        return webpage;
    }


    /**
     * 判处是否重复
     * @param request
     * @param task
     * @return
     */
    @Override
    public boolean isDuplicate(Request request, Task task) {
        Set<String> tempLists = urls.computeIfAbsent(task.getUUID(), k -> Sets.newConcurrentHashSet());
        //初始化已采集网站列表缓存
        if (tempLists.add(request.getUrl())) {//先检查当前生命周期是否抓取过,如果当前生命周期未抓取,则进一步检查ES
            boolean flag = ElasticsearchUtils.isExists(CodeConstant.INDEX_NAME, CodeConstant.TYPE_NAME, Hashing.md5().hashString(request.getUrl(), Charset.forName("utf-8")).toString());
            System.out.println("是否重复 ----- " + flag);
            return flag;
        } else {//如果当前生命周期已抓取,直接置为重复
            return true;
        }
    }

    @Override
    public void resetDuplicateCheck(Task task) {

    }

    @Override
    public int getTotalRequestsCount(Task task) {
        return COUNT++;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        Webpage webpage = convertResultItems2Webpage(resultItems);
        try {
            ElasticsearchUtils.prepareIndex(CodeConstant.INDEX_NAME, CodeConstant.TYPE_NAME, webpage);
        } catch (Exception e) {
            LOG.error("索引 Webpage 出错," + e.getLocalizedMessage());
        }
    }

    /**
     * 清除已停止任务的抓取url列表
     *
     * @param taskId 任务id
     */
    public void deleteUrls(String taskId) {
        urls.remove(taskId);
        LOG.info("任务{}已结束,抓取列表缓存已清除", taskId);
    }
}
