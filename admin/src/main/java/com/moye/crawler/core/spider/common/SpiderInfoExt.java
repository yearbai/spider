package com.moye.crawler.core.spider.common;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

/**
 * 网页抽取模板
 *
 * @author Gao Shen
 * @version 16/4/12
 */
public class SpiderInfoExt {
    /**
     * 使用多少抓取线程
     */
    private int thread = 1;
    /**
     * 失败的网页重试次数
     */
    private int retry = 2;
    /**
     * 抓取每个网页睡眠时间
     */
    private int sleep = 0;
    /**
     * 最大抓取网页数量,0代表不限制
     */
    private int maxPageGather = 10;
    /**
     * HTTP链接超时时间
     */
    private int timeout = 5000;
    /**
     * 网站权重
     */
    private int priority;
    /**
     * 是否只抓取首页
     */
    private int gatherFirstPage = 0;
    /**
     * 抓取模板id
     */
    private String id;
    /**
     * 网站名称
     */
    private String siteName;
    /**
     * 域名
     */
    private String domain;
    /**
     * 起始链接
     */
    private String startUrl;
    /**
     * 正文正则表达式
     */
    private String contentReg;
    /**
     * 正文Xpath
     */
    private String contentXpath;
    /**
     * 标题正则
     */
    private String titleReg;
    /**
     * 标题Xpath
     */
    private String titleXpath;
    /**
     * 分类信息正则
     */
    private String categoryReg;
    /**
     * 分类信息Xpath
     */
    private String categoryXpath;
    /**
     * 默认分类
     */
    private String defaultCategory;
    /**
     * url正则
     */
    private String urlReg;
    /**
     * 编码
     */
    private String charset;
    /**
     * 发布时间Xpath
     */
    private String publishTimeXpath;
    /**
     * 发布时间正则
     */
    private String publishTimeReg;
    /**
     * 发布时间模板
     */
    private String publishTimeFormat;
    /**
     * 回调url
     */
    private String callbackURL;
    /**
     * 是否进行nlp处理
     */
    private Integer doNLP = 0;
    /**
     * 网页必须有标题
     */
    private Integer needTitle = 0;
    /**
     * 网页必须有正文
     */
    private Integer needContent = 0;
    /**
     * 网页必须有发布时间
     */
    private Integer needPublishTime = 0;
    /**
     * 动态字段列表
     */
    private List<FieldConfig> dynamicFields = Lists.newLinkedList();
    /**
     * 静态字段
     */
    private List<StaticField> staticFields = Lists.newArrayList();
    /**
     * 语言,用于配置发布时间
     */
    private String lang;
    /**
     * 国家,用于配置发布时间
     */
    private String country;
    /**
     * User Agent
     */
    private String userAgent = "Mozilla/5.0 (Windows NT 5.2) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.122 Safari/534.30";
    /**
     * 是否保存网页快照,默认保存
     */
    private Integer saveCapture = 0;
    /**
     * 是否是ajax网站,如果是则使用casperjs下载器
     */
    private Integer ajaxSite = 0;
    /**
     * 自动探测发布时间
     */
    private boolean autoDetectPublishDate = false;
    private String proxyHost;
    private int proxyPort;
    private String proxyUsername;
    private String proxyPassword;

    public int getThread() {
        return thread;
    }

    public SpiderInfoExt setThread(int thread) {
        this.thread = thread;
        return this;
    }

    public int getRetry() {
        return retry;
    }

    public SpiderInfoExt setRetry(int retry) {
        this.retry = retry;
        return this;
    }

    public int getSleep() {
        return sleep;
    }

    public SpiderInfoExt setSleep(int sleep) {
        this.sleep = sleep;
        return this;
    }

    public int getMaxPageGather() {
        return maxPageGather;
    }

    public SpiderInfoExt setMaxPageGather(int maxPageGather) {
        this.maxPageGather = maxPageGather;
        return this;
    }

    public int getTimeout() {
        return timeout;
    }

    public SpiderInfoExt setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public int getPriority() {
        return priority;
    }

    public SpiderInfoExt setPriority(int priority) {
        this.priority = priority;
        return this;
    }



    public String getId() {
        return id;
    }

    public SpiderInfoExt setId(String id) {
        this.id = id;
        return this;
    }

    public String getSiteName() {
        return siteName;
    }

    public SpiderInfoExt setSiteName(String siteName) {
        this.siteName = siteName;
        return this;
    }

    public String getDomain() {
        return domain;
    }

    public SpiderInfoExt setDomain(String domain) {
        this.domain = domain;
        return this;
    }



    public String getContentReg() {
        return contentReg;
    }

    public SpiderInfoExt setContentReg(String contentReg) {
        this.contentReg = contentReg;
        return this;
    }

    public String getContentXpath() {
        return contentXpath;
    }

    public SpiderInfoExt setContentXpath(String contentXpath) {
        this.contentXpath = contentXpath;
        return this;
    }

    public String getTitleReg() {
        return titleReg;
    }

    public SpiderInfoExt setTitleReg(String titleReg) {
        this.titleReg = titleReg;
        return this;
    }

    public String getTitleXpath() {
        return titleXpath;
    }

    public SpiderInfoExt setTitleXpath(String titleXpath) {
        this.titleXpath = titleXpath;
        return this;
    }

    public String getCategoryReg() {
        return categoryReg;
    }

    public SpiderInfoExt setCategoryReg(String categoryReg) {
        this.categoryReg = categoryReg;
        return this;
    }

    public String getCategoryXpath() {
        return categoryXpath;
    }

    public SpiderInfoExt setCategoryXpath(String categoryXpath) {
        this.categoryXpath = categoryXpath;
        return this;
    }

    public String getDefaultCategory() {
        return defaultCategory;
    }

    public SpiderInfoExt setDefaultCategory(String defaultCategory) {
        this.defaultCategory = defaultCategory;
        return this;
    }

    public String getUrlReg() {
        return urlReg;
    }

    public SpiderInfoExt setUrlReg(String urlReg) {
        this.urlReg = urlReg;
        return this;
    }

    public String getCharset() {
        return charset;
    }

    public SpiderInfoExt setCharset(String charset) {
        this.charset = charset;
        return this;
    }

    public String getPublishTimeXpath() {
        return publishTimeXpath;
    }

    public SpiderInfoExt setPublishTimeXpath(String publishTimeXpath) {
        this.publishTimeXpath = publishTimeXpath;
        return this;
    }

    public String getPublishTimeReg() {
        return publishTimeReg;
    }

    public SpiderInfoExt setPublishTimeReg(String publishTimeReg) {
        this.publishTimeReg = publishTimeReg;
        return this;
    }

    public String getPublishTimeFormat() {
        return publishTimeFormat;
    }

    public SpiderInfoExt setPublishTimeFormat(String publishTimeFormat) {
        this.publishTimeFormat = publishTimeFormat;
        return this;
    }

 
 

    public String getLang() {
        return lang;
    }

    public SpiderInfoExt setLang(String lang) {
        this.lang = lang;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public SpiderInfoExt setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public SpiderInfoExt setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public List<FieldConfig> getDynamicFields() {
        return dynamicFields;
    }

    public SpiderInfoExt setDynamicFields(List<FieldConfig> dynamicFields) {
        this.dynamicFields = dynamicFields;
        return this;
    }

    public List<StaticField> getStaticFields() {
        return staticFields;
    }

    public SpiderInfoExt setStaticFields(List<StaticField> staticFields) {
        this.staticFields = staticFields;
        return this;
    }



    public boolean isAutoDetectPublishDate() {
        return autoDetectPublishDate;
    }

    public SpiderInfoExt setAutoDetectPublishDate(boolean autoDetectPublishDate) {
        this.autoDetectPublishDate = autoDetectPublishDate;
        return this;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public SpiderInfoExt setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
        return this;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public SpiderInfoExt setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
        return this;
    }

    public String getProxyUsername() {
        return proxyUsername;
    }

    public SpiderInfoExt setProxyUsername(String proxyUsername) {
        this.proxyUsername = proxyUsername;
        return this;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public SpiderInfoExt setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
        return this;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SpiderInfoExt that = (SpiderInfoExt) o;

        return new EqualsBuilder()
                .append(getThread(), that.getThread())
                .append(getRetry(), that.getRetry())
                .append(getSleep(), that.getSleep())
                .append(getMaxPageGather(), that.getMaxPageGather())
                .append(getTimeout(), that.getTimeout())
                .append(getPriority(), that.getPriority())
                .append(getGatherFirstPage(), that.getGatherFirstPage())
                .append(getDoNLP(), that.getDoNLP())
                .append(getNeedTitle(), that.getNeedTitle())
                .append(getNeedContent(), that.getNeedContent())
                .append(getNeedPublishTime(), that.getNeedPublishTime())
                .append(getSiteName(), that.getSiteName())
                .append(getDomain(), that.getDomain())
                .append(getStartUrl(), that.getStartUrl())
                .append(getContentReg(), that.getContentReg())
                .append(getContentXpath(), that.getContentXpath())
                .append(getTitleReg(), that.getTitleReg())
                .append(getTitleXpath(), that.getTitleXpath())
                .append(getCategoryReg(), that.getCategoryReg())
                .append(getCategoryXpath(), that.getCategoryXpath())
                .append(getDefaultCategory(), that.getDefaultCategory())
                .append(getUrlReg(), that.getUrlReg())
                .append(getCharset(), that.getCharset())
                .append(getPublishTimeXpath(), that.getPublishTimeXpath())
                .append(getPublishTimeReg(), that.getPublishTimeReg())
                .append(getPublishTimeFormat(), that.getPublishTimeFormat())
                .append(getLang(), that.getLang())
                .append(getCountry(), that.getCountry())
                .append(getUserAgent(), that.getUserAgent())
                .append(getDynamicFields(), that.getDynamicFields())
                .append(getStaticFields(), that.getStaticFields())
                .append(getSaveCapture(), that.getSaveCapture())
                .append(isAutoDetectPublishDate(), that.isAutoDetectPublishDate())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getThread())
                .append(getRetry())
                .append(getSleep())
                .append(getMaxPageGather())
                .append(getTimeout())
                .append(getPriority())
                .append(getSiteName())
                .append(getDomain())
                .append(getStartUrl())
                .append(getContentReg())
                .append(getContentXpath())
                .append(getTitleReg())
                .append(getTitleXpath())
                .append(getCategoryReg())
                .append(getCategoryXpath())
                .append(getDefaultCategory())
                .append(getUrlReg())
                .append(getCharset())
                .append(getPublishTimeXpath())
                .append(getPublishTimeReg())
                .append(getPublishTimeFormat())
                .append(getDoNLP())
                .append(getNeedTitle())
                .append(getNeedContent())
                .append(getNeedPublishTime())
                .append(getLang())
                .append(getCountry())
                .append(getUserAgent())
                .append(getDynamicFields())
                .append(getStaticFields())
                .append(getSaveCapture())
                .append(isAutoDetectPublishDate())
                .toHashCode();
    }

    public class StaticField {
        private String name;
        private String value;

        public StaticField(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public StaticField setName(String name) {
            this.name = name;
            return this;
        }

        public String getValue() {
            return value;
        }

        public StaticField setValue(String value) {
            this.value = value;
            return this;
        }
    }

    public class FieldConfig {
        private String regex;
        private String Xpath;
        private String name;
        private boolean need = false;

        public FieldConfig(String regex, String Xpath, String name, boolean need) {
            this.regex = regex;
            this.Xpath = Xpath;
            this.name = name;
            this.need = need;
        }

        public FieldConfig() {
        }

        public String getRegex() {
            return regex;
        }

        public FieldConfig setRegex(String regex) {
            this.regex = regex;
            return this;
        }

        public String getXpath() {
            return Xpath;
        }

        public FieldConfig setXpath(String Xpath) {
            this.Xpath = Xpath;
            return this;
        }

        public String getName() {
            return name;
        }

        public FieldConfig setName(String name) {
            this.name = name;
            return this;
        }

        public boolean isNeed() {
            return need;
        }

        public FieldConfig setNeed(boolean need) {
            this.need = need;
            return this;
        }
    }

    public int getGatherFirstPage() {
        return gatherFirstPage;
    }

    public void setGatherFirstPage(int gatherFirstPage) {
        this.gatherFirstPage = gatherFirstPage;
    }

    public String getStartUrl() {
        return startUrl;
    }

    public void setStartUrl(String startUrl) {
        this.startUrl = startUrl;
    }

    public String getCallbackURL() {
        return callbackURL;
    }

    public void setCallbackURL(String callbackURL) {
        this.callbackURL = callbackURL;
    }

    public Integer getDoNLP() {
        return doNLP;
    }

    public void setDoNLP(Integer doNLP) {
        this.doNLP = doNLP;
    }

    public Integer getNeedTitle() {
        return needTitle;
    }

    public void setNeedTitle(Integer needTitle) {
        this.needTitle = needTitle;
    }

    public Integer getNeedContent() {
        return needContent;
    }

    public void setNeedContent(Integer needContent) {
        this.needContent = needContent;
    }

    public Integer getNeedPublishTime() {
        return needPublishTime;
    }

    public void setNeedPublishTime(Integer needPublishTime) {
        this.needPublishTime = needPublishTime;
    }

    public Integer getSaveCapture() {
        return saveCapture;
    }

    public void setSaveCapture(Integer saveCapture) {
        this.saveCapture = saveCapture;
    }

    public Integer getAjaxSite() {
        return ajaxSite;
    }

    public void setAjaxSite(Integer ajaxSite) {
        this.ajaxSite = ajaxSite;
    }
}
