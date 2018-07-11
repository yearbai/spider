package com.moye.crawler.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**Pipeline
 * @Author: moye
 * @Description:
 * @Date Created in  2018/4/23 10:18
 * @Modified By
 */
@Configuration
@ConfigurationProperties(prefix ="spider")
public class SpiderProperties {

    private Integer maxHttpDownloadLength;// 1048576
    private boolean commonsSpiderDebug;// false
    private Integer taskDeleteDelay;// 1
    private Integer taskDeletePeriod;// 2
    private Integer limitOfCommonWebpageDownloadQueue;// 100000 最大网页下载队列长度
    private boolean needRedis;// true
    private boolean needEs;// true
    private String webpageRedisPublishChannelName;// webpage
    private Integer commonsWebpageCrawlRatio;// 2
    private String ajaxDownloader;// http://localhost:7788/

    public Integer getMaxHttpDownloadLength() {
        return maxHttpDownloadLength;
    }

    public void setMaxHttpDownloadLength(Integer maxHttpDownloadLength) {
        this.maxHttpDownloadLength = maxHttpDownloadLength;
    }

    public boolean isCommonsSpiderDebug() {
        return commonsSpiderDebug;
    }

    public void setCommonsSpiderDebug(boolean commonsSpiderDebug) {
        this.commonsSpiderDebug = commonsSpiderDebug;
    }

    public Integer getTaskDeleteDelay() {
        return taskDeleteDelay;
    }

    public void setTaskDeleteDelay(Integer taskDeleteDelay) {
        this.taskDeleteDelay = taskDeleteDelay;
    }

    public Integer getTaskDeletePeriod() {
        return taskDeletePeriod;
    }

    public void setTaskDeletePeriod(Integer taskDeletePeriod) {
        this.taskDeletePeriod = taskDeletePeriod;
    }

    public Integer getLimitOfCommonWebpageDownloadQueue() {
        return limitOfCommonWebpageDownloadQueue;
    }

    public void setLimitOfCommonWebpageDownloadQueue(Integer limitOfCommonWebpageDownloadQueue) {
        this.limitOfCommonWebpageDownloadQueue = limitOfCommonWebpageDownloadQueue;
    }

    public boolean isNeedRedis() {
        return needRedis;
    }

    public void setNeedRedis(boolean needRedis) {
        this.needRedis = needRedis;
    }

    public boolean isNeedEs() {
        return needEs;
    }

    public void setNeedEs(boolean needEs) {
        this.needEs = needEs;
    }

    public String getWebpageRedisPublishChannelName() {
        return webpageRedisPublishChannelName;
    }

    public void setWebpageRedisPublishChannelName(String webpageRedisPublishChannelName) {
        this.webpageRedisPublishChannelName = webpageRedisPublishChannelName;
    }

    public Integer getCommonsWebpageCrawlRatio() {
        return commonsWebpageCrawlRatio;
    }

    public void setCommonsWebpageCrawlRatio(Integer commonsWebpageCrawlRatio) {
        this.commonsWebpageCrawlRatio = commonsWebpageCrawlRatio;
    }

    public String getAjaxDownloader() {
        return ajaxDownloader;
    }

    public void setAjaxDownloader(String ajaxDownloader) {
        this.ajaxDownloader = ajaxDownloader;
    }
}
