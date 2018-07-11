package com.moye.crawler.core.spider.scheduler;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.hash.Hashing;
import com.moye.crawler.common.constant.CodeConstant;
import com.moye.crawler.common.utils.es.ElasticsearchUtils;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;

/**
 * @author: moye
 * @description:
 * @date Created in  2018/5/21 14:51
 * @modified By
 */
public class ElasticScheduler  implements DuplicateRemover {
    private static int COUNT = 0;
    private Map<String, Set<String>> urls = Maps.newConcurrentMap();
    @Override
    public boolean isDuplicate(Request request, Task task) {
        Set<String> tempLists = urls.computeIfAbsent(task.getUUID(), k -> Sets.newConcurrentHashSet());
        //初始化已采集网站列表缓存
        if (tempLists.add(request.getUrl())) {//先检查当前生命周期是否抓取过,如果当前生命周期未抓取,则进一步检查ES
            boolean flag = ElasticsearchUtils.isExists( CodeConstant.INDEX_NAME, CodeConstant.TYPE_NAME, Hashing.md5().hashString(request.getUrl(), Charset.forName("utf-8")).toString());
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
}
