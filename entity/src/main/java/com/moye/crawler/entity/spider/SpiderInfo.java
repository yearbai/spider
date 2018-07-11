package com.moye.crawler.entity.spider;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author theodo
 * @since 2018-04-19
 */
@TableName("spider_info")
public class SpiderInfo extends Model<SpiderInfo> {

    private static final long serialVersionUID = 1L;
	/**
	 * 主键id
	 */
	@TableId(value="id", type= IdType.AUTO)
	private Integer id;
    /**
     * 网站名称
     */
	@TableField("site_name")
	private String siteName;
    /**
     * 域名
     */
	private String domain;
    /**
     * 使用多少爬去线程
     */
	private Integer thread;
    /**
     * 失败的网页重试次数
     */
	private Integer retry;
    /**
     * 抓取每个网页睡眠时间
     */
	private Integer sleep;
    /**
     * ttp链接超时时间
     */
	private Integer timeout;
    /**
     * 最大抓取网页数量 0代表不限制
     */
	@TableField("max_page_gather")
	private Integer maxPageGather = 10;
    /**
     * 网站权重
     */
	private Integer priority;
    /**
     * 起始链接 可配置多个以逗号分隔
     */
	@TableField("start_url")
	private String startUrl;

	/**
	 * 分页地址规则
	 */
	@TableField("page_url_reg")
	private String pageUrlReg;


    /**
     * 正文正则表达式
     */
	@TableField("content_reg")
	private String contentReg;
    /**
     * 正文xpath
     */
	@TableField("content_xpath")
	private String contentXpath;
    /**
     * 标题正则
     */
	@TableField("title_reg")
	private String titleReg;
    /**
     * 标题xpath
     */
	@TableField("title_xpath")
	private String titleXpath;
    /**
     * 分类正则
     */
	@TableField("category_reg")
	private String categoryReg;
    /**
     * 分类xpath
     */
	@TableField("category_xpath")
	private String categoryXpath;
    /**
     * 默认分类
     */
	@TableField("default_category")
	private String defaultCategory;
    /**
     * url正则
     */
	@TableField("url_reg")
	private String urlReg;
    /**
     * 编码
     */
	private String charset;
    /**
     * 发布时间xpath
     */
	@TableField("publish_time_xpath")
	private String publishTimeXpath;
    /**
     * 发布时间正则
     */
	@TableField("publish_time_reg")
	private String publishTimeReg;
    /**
     * 发布时间模板
     */
	@TableField("publish_time_format")
	private String publishTimeFormat;
    /**
     * 作者正则
     */
	@TableField("author_reg")
	private String authorReg;
    /**
     * 作者xpath
     */
	@TableField("author_xpath")
	private String authorXpath;
    /**
     * 语言 用于配置发布时间
     */
	private String lang;
    /**
     * 国家用于配置发布时间
     */
	private String country;
    /**
     * 回调url
     */
	@TableField("callback_url")
	private String callbackUrl;
    /**
     * user agent
     */
	@TableField("user_agent")
	private String userAgent = "Mozilla/5.0 (Windows NT 5.2) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.122 Safari/534.30";
    /**
     * 是否进行nlp处理 0否1是
     */
	private Integer doNLP;
    /**
     * 是否只抓取首页0否 1是
     */
	@TableField("gather_first_page")
	private Integer gatherFirstPage;
    /**
     * 网页必须有标题0非必须1必须
     */
	@TableField("need_title")
	private Integer needTitle;
    /**
     * 必须有内容0非必须
     */
	@TableField("need_content")
	private Integer needContent = 1;
    /**
     * 必须有发布时间0非必须
     */
	@TableField("need_publish_time")
	private Integer needPublishTime;
    /**
     * 是否是ajax网站0不是1是
     */
	@TableField("ajax_site")
	private Integer ajaxSite;

	/**
	 * 是否保留页面快照 默认保留1
	 */
	@TableField("save_capture")
	private Integer saveCapture;
    /**
     * 动态字段列表
     */
	@TableField("dynamic_fields")
	private String dynamicFields;
    /**
     * 静态字段列表
     */
	@TableField("static_fields")
	private String staticFields;

	@TableField("create_time")
	private Date createTime;

	@TableField("proxy_host")
	private String proxyHost;
	@TableField("proxy_port")
	private int proxyPort;
	@TableField("proxy_username")
	private String proxyUsername;
	@TableField("proxy_password")
	private String proxyPassword;



	public String getPageUrlReg() {
		return pageUrlReg;
	}

	public void setPageUrlReg(String pageUrlReg) {
		this.pageUrlReg = pageUrlReg;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public Integer getThread() {
		return thread;
	}

	public void setThread(Integer thread) {
		this.thread = thread;
	}

	public Integer getRetry() {
		return retry;
	}

	public void setRetry(Integer retry) {
		this.retry = retry;
	}

	public Integer getSleep() {
		return sleep;
	}

	public void setSleep(Integer sleep) {
		this.sleep = sleep;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public Integer getMaxPageGather() {
		return maxPageGather;
	}

	public void setMaxPageGather(Integer maxPageGather) {
		this.maxPageGather = maxPageGather;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public String getStartUrl() {
		return startUrl;
	}

	public void setStartUrl(String startUrl) {
		this.startUrl = startUrl;
	}

	public String getContentReg() {
		return contentReg;
	}

	public void setContentReg(String contentReg) {
		this.contentReg = contentReg;
	}

	public String getContentXpath() {
		return contentXpath;
	}

	public void setContentXpath(String contentXpath) {
		this.contentXpath = contentXpath;
	}

	public String getTitleReg() {
		return titleReg;
	}

	public void setTitleReg(String titleReg) {
		this.titleReg = titleReg;
	}

	public String getTitleXpath() {
		return titleXpath;
	}

	public void setTitleXpath(String titleXpath) {
		this.titleXpath = titleXpath;
	}

	public String getCategoryReg() {
		return categoryReg;
	}

	public void setCategoryReg(String categoryReg) {
		this.categoryReg = categoryReg;
	}

	public String getCategoryXpath() {
		return categoryXpath;
	}

	public void setCategoryXpath(String categoryXpath) {
		this.categoryXpath = categoryXpath;
	}

	public String getDefaultCategory() {
		return defaultCategory;
	}

	public void setDefaultCategory(String defaultCategory) {
		this.defaultCategory = defaultCategory;
	}

	public String getUrlReg() {
		return urlReg;
	}

	public void setUrlReg(String urlReg) {
		this.urlReg = urlReg;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getPublishTimeXpath() {
		return publishTimeXpath;
	}

	public void setPublishTimeXpath(String publishTimeXpath) {
		this.publishTimeXpath = publishTimeXpath;
	}

	public String getPublishTimeReg() {
		return publishTimeReg;
	}

	public void setPublishTimeReg(String publishTimeReg) {
		this.publishTimeReg = publishTimeReg;
	}

	public String getPublishTimeFormat() {
		return publishTimeFormat;
	}

	public void setPublishTimeFormat(String publishTimeFormat) {
		this.publishTimeFormat = publishTimeFormat;
	}

	public String getAuthorReg() {
		return authorReg;
	}

	public void setAuthorReg(String authorReg) {
		this.authorReg = authorReg;
	}

	public String getAuthorXpath() {
		return authorXpath;
	}

	public void setAuthorXpath(String authorXpath) {
		this.authorXpath = authorXpath;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public Integer getDoNLP() {
		return doNLP;
	}

	public void setDoNLP(Integer doNLP) {
		this.doNLP = doNLP;
	}

	public Integer getGatherFirstPage() {
		return gatherFirstPage;
	}

	public void setGatherFirstPage(Integer gatherFirstPage) {
		this.gatherFirstPage = gatherFirstPage;
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

	public Integer getAjaxSite() {
		return ajaxSite;
	}

	public void setAjaxSite(Integer ajaxSite) {
		this.ajaxSite = ajaxSite;
	}

	public String getDynamicFields() {
		return dynamicFields;
	}

	public void setDynamicFields(String dynamicFields) {
		this.dynamicFields = dynamicFields;
	}

	public String getStaticFields() {
		return staticFields;
	}

	public void setStaticFields(String staticFields) {
		this.staticFields = staticFields;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getProxyUsername() {
		return proxyUsername;
	}

	public void setProxyUsername(String proxyUsername) {
		this.proxyUsername = proxyUsername;
	}

	public String getProxyPassword() {
		return proxyPassword;
	}

	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}

	public Integer getSaveCapture() {
		return saveCapture;
	}

	public void setSaveCapture(Integer saveCapture) {
		this.saveCapture = saveCapture;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	@Override
	public String toString() {
		return "SpiderInfo{" +
			", id=" + id +
			", siteName=" + siteName +
			", domain=" + domain +
			", thread=" + thread +
			", retry=" + retry +
			", sleep=" + sleep +
			", timeout=" + timeout +
			", maxPageGether=" + maxPageGather +
			", priority=" + priority +
			", startUrl=" + startUrl +
			", contentReg=" + contentReg +
			", contentXpath=" + contentXpath +
			", titleReg=" + titleReg +
			", titleXpath=" + titleXpath +
			", categoryReg=" + categoryReg +
			", categoryXpath=" + categoryXpath +
			", defaultCategory=" + defaultCategory +
			", urlReg=" + urlReg +
			", charset=" + charset +
			", publishTimeXpath=" + publishTimeXpath +
			", publishTimeReg=" + publishTimeReg +
			", publishTimeFormat=" + publishTimeFormat +
			", authorReg=" + authorReg +
			", authorXpath=" + authorXpath +
			", lang=" + lang +
			", country=" + country +
			", callbackUrl=" + callbackUrl +
			", userAgent=" + userAgent +
			", doNLP=" + doNLP +
			", gatherFirstPage=" + gatherFirstPage +
			", needTitle=" + needTitle +
			", needContent=" + needContent +
			", needPublishTime=" + needPublishTime +
			", ajaxSite=" + ajaxSite +
			", dynamicFields=" + dynamicFields +
			", staticFields=" + staticFields +
			"}";
	}
}
