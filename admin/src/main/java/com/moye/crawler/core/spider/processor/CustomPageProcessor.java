package com.moye.crawler.core.spider.processor;

import com.google.common.collect.Lists;
import com.moye.crawler.common.constant.CodeConstant;
import com.moye.crawler.common.dao.impl.RedisHandle;
import com.moye.crawler.common.utils.SpringUtil;
import com.moye.crawler.core.spider.utils.ContentUtil;
import com.moye.crawler.core.spider.utils.HANLPExtractor;
import com.moye.crawler.entity.spider.SpiderInfo;
import com.moye.crawler.entity.spider.SpiderTask;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.utils.UrlUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: moye
 * @Description:
 * @Date Created in  2018/4/24 11:27
 * @Modified By
 */

public class CustomPageProcessor implements PageProcessor {
    private static final Logger LOG = LogManager.getLogger(CustomPageProcessor.class);

    private Site site;
    private SpiderInfo info;
    private SpiderTask task;

    private RedisHandle redisHandle = SpringUtil.getBean(RedisHandle.class);
    private JedisPool jedisPool = SpringUtil.getBean(JedisPool.class);

    public CustomPageProcessor(SpiderInfo info, SpiderTask task) {
        this.site = Site.me().setDomain(info.getDomain()).setTimeOut(info.getTimeout())
                .setRetryTimes(info.getRetry()).setSleepTime(info.getSleep())
                .setCharset(StringUtils.isBlank(info.getCharset()) ? null : info.getCharset())
                .setUserAgent(info.getUserAgent());
        //设置抓取代理IP与接口
        if (StringUtils.isNotBlank(info.getProxyHost()) && info.getProxyPort() > 0) {
            this.site.setHttpProxy(new HttpHost(info.getProxyHost(), info.getProxyPort()));
            //设置代理的认证
            if (StringUtils.isNotBlank(info.getProxyUsername()) && StringUtils.isNotBlank(info.getProxyPassword())) {
                this.site.setUsernamePasswordCredentials(new UsernamePasswordCredentials(info.getProxyUsername(), info.getProxyPassword()));
            }
        }
        this.info = info;
        this.task = task;
    }

    @Override
    public void process(Page page) {
        spiderInfoPageConsumer.accept(page, info, task);
        System.out.println("是否跳过" + page.getResultItems().isSkip());
        if (!page.getResultItems().isSkip()) {//网页正常时再增加数量
            task.increaseCount();
            redisHandle.set("count_"+task.getId(), task.getCount());
            Jedis jedis = this.jedisPool.getResource();
            try{
                jedis.sadd( CodeConstant.FINISH_LIST, new String[]{page.getUrl().get()} );
            }finally {
                this.jedisPool.returnResource( jedis );
            }

        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    @SuppressWarnings("unchecked")
    private final PageConsumer spiderInfoPageConsumer = (page, info, task) -> {
        System.out.println("11111111111");
        try {
            long start = System.currentTimeMillis();

            //本页是否是startUrls里面的页面
            final boolean startPage = info.getStartUrl().contains(page.getUrl().get());
            List<String> attachmentList = Lists.newLinkedList();
            //判断本网站是否只抽取入口页,和当前页面是不是入口页
            if (info.getGatherFirstPage() == 0 ) {
                List<String> links = null;
                if (StringUtils.isNotBlank(info.getUrlReg())) {//url正则式不为空
                    //判断有没有设置分页
                    if(StringUtils.isNotBlank(info.getPageUrlReg()) && page.getUrl().regex( info.getPageUrlReg() ).match()){
                      page.addTargetRequests(page.getHtml().links().regex(info.getPageUrlReg()).all());
                    }
                    links = page.getHtml().links().regex(info.getUrlReg()).all();
                } else {//url正则式为空则抽取本域名下的所有连接,并使用黑名单对链接进行过滤
                    links = page.getHtml().links()
                            .regex("https?://" + info.getDomain().replace(".", "\\.") + "/.*")
                            .all().stream().map(s -> {
                                int indexOfSharp = s.indexOf("#");
                                return s.substring(0, indexOfSharp == -1 ? s.length() : indexOfSharp);
                            })
//                            .filter(s -> {
//                                for (String ignoredPostfix : ignoredUrls) {
//                                    if (s.toLowerCase().endsWith(ignoredPostfix)) {
//                                        return false;
//                                    }
//                                }
//                                return true;
//                            })
                            .collect(Collectors.toList());
                }
                //如果页面包含iframe则也进行抽取
                for (Element iframe : page.getHtml().getDocument().getElementsByTag("iframe")) {
                    final String src = iframe.attr("src");
                    //iframe抽取规则遵循设定的url正则
                    if (StringUtils.isNotBlank(info.getUrlReg()) && src.matches(info.getUrlReg())) {
                        links.add(src);
                    }
                    //如无url正则,则遵循同源策略
                    else if (StringUtils.isBlank(info.getUrlReg()) && UrlUtils.getDomain(src).equals(info.getDomain())) {
                        links.add(src);
                    }
                }
                if (links != null && links.size() > 0) {
                    page.addTargetRequests(links);
                }
            }
            //去掉startUrl页面
            if (startPage) {
                page.setSkip(true);
            }
            page.putField("url", page.getUrl().get());
            page.putField("domain", info.getDomain());
            page.putField("spiderInfoId", info.getId());
            page.putField("gatherTime", System.currentTimeMillis());
            page.putField("spiderInfo", info);
            page.putField("spiderUUID", task.getId());
            if (info.getSaveCapture() == 1) {
                page.putField("rawHTML", page.getHtml().get());
            }
            //转换静态字段
//            if (info.getStaticFields() != null && info.getStaticFields().size() > 0) {
//                Map<String, String> staticFieldList = Maps.newHashMap();
//                for (SpiderInfo.StaticField staticField : info.getStaticFields()) {
//                    staticFieldList.put(staticField.getName(), staticField.getValue());
//                }
//                page.putField("staticField", staticFieldList);
//            }
            ///////////////////////////////////////////////////////
            String content;
            if (!StringUtils.isBlank(info.getContentXpath())) {//如果有正文的XPath的话优先使用XPath
                StringBuilder buffer = new StringBuilder();
                page.getHtml().xpath(info.getContentXpath()).all().forEach(buffer::append);
                content = buffer.toString();
            } else if (!StringUtils.isBlank(info.getContentReg())) {//没有正文XPath
                StringBuilder buffer = new StringBuilder();
                page.getHtml().regex(info.getContentReg()).all().forEach(buffer::append);
                content = buffer.toString();
            } else {//如果没有正文的相关规则则使用智能提取
                Document clone = page.getHtml().getDocument().clone();
                clone.getElementsByTag("p").append("***");
                clone.getElementsByTag("br").append("***");
                clone.getElementsByTag("script").remove();
                //移除不可见元素
                clone.getElementsByAttributeValueContaining("style", "display:none").remove();
                content = new Html(clone).smartContent().get();
            }
            content = ContentUtil.format(content);
            page.putField("content", content);
            if (info.getNeedContent() == 1 && StringUtils.isBlank(content)) {//if the content is blank ,skip it!
                page.setSkip(true);
                return;
            }
            //抽取标题
            String title = null;
            if (!StringUtils.isBlank(info.getTitleXpath())) {//提取网页标题
                title = page.getHtml().xpath(info.getTitleXpath()).get();
            } else if (!StringUtils.isBlank(info.getTitleReg())) {
                title = page.getHtml().regex(info.getTitleReg()).get();
            } else {//如果不写默认是title
                title = page.getHtml().getDocument().title();
            }
            page.putField("title", title);
            if (info.getNeedTitle() == 1 && StringUtils.isBlank(title)) {//if the title is blank ,skip it!
                page.setSkip(true);
                return;
            }

            //抽取分类
            String category = null;
            if (!StringUtils.isBlank(info.getCategoryXpath())) {//提取网页分类
                category = page.getHtml().xpath(info.getCategoryXpath()).get();
            } else if (!StringUtils.isBlank(info.getCategoryReg())) {
                category = page.getHtml().regex(info.getCategoryReg()).get();
            }
            if (StringUtils.isNotBlank(category)) {
                page.putField("category", category);
            } else {
                page.putField("category", info.getDefaultCategory());
            }

            //抽取发布时间
            String publishTime = null;
            if (StringUtils.isNotBlank(info.getPublishTimeReg())) {
//                System.out.println(page.getHtml());
                System.out.println(page.getHtml().xpath(info.getPublishTimeXpath()).get());
                publishTime = page.getHtml().xpath(info.getPublishTimeXpath()).regex(info.getPublishTimeReg()).get();
                System.out.println(publishTime);
            }
            Date publishDate = null;
            SimpleDateFormat simpleDateFormat = null;
            //获取SimpleDateFormat时间匹配模板,首先检测爬虫模板指定的,如果为空则自动探测
            if (StringUtils.isNotBlank(info.getPublishTimeFormat())) {
                //使用爬虫模板指定的时间匹配模板
                if (StringUtils.isNotBlank(info.getLang())) {
                    simpleDateFormat = new SimpleDateFormat(info.getPublishTimeFormat(), new Locale(info.getLang(), info.getCountry()));
                } else {
                    simpleDateFormat = new SimpleDateFormat(info.getPublishTimeFormat());
                }
            }
//            else if (StringUtils.isBlank(publishTime) && info.isAutoDetectPublishDate()) {
//                //如果没有使用爬虫模板抽取到文章发布时间,或者选择了自动抽时间,则进行自动发布时间探测
//                for (Pair<String, SimpleDateFormat> formatEntry : datePattern) {
//                    publishTime = page.getHtml().regex(formatEntry.getKey(), 0).get();
//                    //如果探测到了时间就退出探测
//                    if (StringUtils.isNotBlank(publishTime)) {
//                        simpleDateFormat = formatEntry.getValue();
//                        break;
//                    }
//                }
//            }
            //解析发布时间成date类型
            if (simpleDateFormat != null && StringUtils.isNotBlank(publishTime)) {
                try {
                    publishDate = simpleDateFormat.parse(publishTime);
                    //如果时间没有包含年份,则默认使用当前年
                    if (!simpleDateFormat.toPattern().contains("yyyy")) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(publishDate);
                        calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
                        publishDate = calendar.getTime();
                    }
                    page.putField("publishTime", publishDate.getTime());
                } catch (ParseException e) {
                    LOG.debug("解析文章发布时间出错,source:" + publishTime + ",format:" + simpleDateFormat.toPattern());
                    String desc = String.format("解析文章发布时间出错,url:%s source:%s ,format:%s", page.getUrl().toString(), publishTime, simpleDateFormat.toPattern());
                    task.setDesc(desc);
                    if (info.getNeedPublishTime() == 1) {//if the publishTime is blank ,skip it!
                        page.setSkip(true);
                        return;
                    }
                }
            } else if (info.getNeedPublishTime() == 1) {//if the publishTime is blank ,skip it!
                page.setSkip(true);
                return;
            }
            ///////////////////////////////////////////////////////
            if (info.getDoNLP() == 1) {//判断本网站是否需要进行自然语言处理
                //进行nlp处理之前先去除标签
                String contentWithoutHtml = content.replaceAll("<br/>", "");
                try {
                    //抽取关键词,10个词
                    page.putField("keywords", HANLPExtractor.extractKeywords(contentWithoutHtml));
                    //抽取摘要,5句话
                    page.putField("summary", HANLPExtractor.extractSummary(contentWithoutHtml));
                    //抽取命名实体
                    page.putField("namedEntity", HANLPExtractor.extractNamedEntity(contentWithoutHtml));
                } catch (Exception e) {
                    e.printStackTrace();
                    LOG.error("对网页进行NLP处理失败,{}", e.getLocalizedMessage());
                    task.setDesc("对网页进行NLP处理失败, " + e.getLocalizedMessage());
                }
            }
            //本页面处理时长
            page.putField("processTime", System.currentTimeMillis() - start);
        } catch (Exception e) {
            task.setDesc("处理网页出错，" + e.toString());
        }
    };



}
