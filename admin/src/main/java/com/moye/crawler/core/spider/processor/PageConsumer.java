package com.moye.crawler.core.spider.processor;



import com.moye.crawler.core.spider.common.SpiderInfoExt;
import com.moye.crawler.entity.spider.SpiderInfo;
import com.moye.crawler.entity.spider.SpiderTask;
import us.codecraft.webmagic.Page;

/**
 * PageConsumer
 *
 * @author Gao Shen
 * @version 16/7/8
 */
@FunctionalInterface
public interface PageConsumer {
    void accept(Page page, SpiderInfo info, SpiderTask task);
}
