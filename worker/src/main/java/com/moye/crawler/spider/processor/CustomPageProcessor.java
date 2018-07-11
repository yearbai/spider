package com.moye.crawler.spider.processor;

import com.google.common.collect.Lists;
import com.moye.crawler.spider.common.SpiderInfo;
import com.moye.crawler.spider.utils.ContentUtil;
import com.moye.crawler.spider.utils.HANLPExtractor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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

    public CustomPageProcessor(SpiderInfo info) {
        this.site = Site.me().setDomain(info.getDomain()).setTimeOut(info.getTimeout())
                .setRetryTimes(info.getRetry()).setSleepTime(info.getSleep())
                .setCharset( StringUtils.isBlank(info.getCharset()) ? null : info.getCharset())
                .setUserAgent(info.getUserAgent());
        this.info = info;
    }

    @Override
    public void process(Page page) {
        spiderInfoPageConsumer(page, info);
       // System.out.println("是否跳过" + page.getResultItems().isSkip());
        if (!page.getResultItems().isSkip()) {//网页正常时再增加数量
           // System.out.println("----成功");
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    private void spiderInfoPageConsumer(Page page, SpiderInfo info){
        long start = System.currentTimeMillis();
        //本页是否是startUrls里面的页面
        List<String> attachmentList = Lists.newLinkedList();

        if(page.getUrl().regex( info.getPageUrlReg() ).match()){
            if(info.getGatherFirstPage() == 0){//
                page.addTargetRequests(page.getHtml().links().regex(info.getPageUrlReg()).all());
                page.addTargetRequests( page.getHtml().links().regex(info.getUrlReg()).all() );
            }
        }else if(page.getUrl().regex( info.getUrlReg() ).match()){
            page.putField("url", page.getUrl().get());
            page.putField("domain", info.getDomain());
            page.putField("spiderInfoId", info.getId());
            page.putField("gatherTime", System.currentTimeMillis());
            page.putField("spiderInfo", info);
            if (info.getSaveCapture() == 1) {
                page.putField("rawHTML", page.getHtml().get());
            }
            String content;
            if (StringUtils.isNotBlank(info.getContentXpath())) {//如果有正文的XPath的话优先使用XPath
                StringBuilder buffer = new StringBuilder();
                page.getHtml().xpath(info.getContentXpath()).all().forEach(buffer::append);
                content = buffer.toString();
            } else if (StringUtils.isNotBlank(info.getContentReg())) {//没有正文XPath
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
            page.putField("content", ContentUtil.format(content));
            if (info.getNeedContent() == 1 && StringUtils.isNoneBlank(content)) {//if the content is blank ,skip it!
                page.setSkip(true);
                return;
            }
            //抽取标题
            String title = null;
            if (!org.apache.commons.lang3.StringUtils.isBlank(info.getTitleXpath())) {//提取网页标题
                title = page.getHtml().xpath(info.getTitleXpath()).get();
            } else if (!org.apache.commons.lang3.StringUtils.isBlank(info.getTitleReg())) {
                title = page.getHtml().regex(info.getTitleReg()).get();
            } else {//如果不写默认是title
                title = page.getHtml().getDocument().title();
            }
            page.putField("title", title);
            if (info.getNeedTitle() == 1 && org.apache.commons.lang3.StringUtils.isBlank(title)) {//if the title is blank ,skip it!
                page.setSkip(true);
                return;
            }
            //抽取分类
            String category = null;
            if (!org.apache.commons.lang3.StringUtils.isBlank(info.getCategoryXpath())) {//提取网页分类
                category = page.getHtml().xpath(info.getCategoryXpath()).get();
            } else if (!org.apache.commons.lang3.StringUtils.isBlank(info.getCategoryReg())) {
                category = page.getHtml().regex(info.getCategoryReg()).get();
            }
            if (org.apache.commons.lang3.StringUtils.isNotBlank(category)) {
                page.putField("category", category);
            } else {
                page.putField("category", info.getDefaultCategory());
            }

            //抽取发布时间
            String publishTime = null;
            if (org.apache.commons.lang.StringUtils.isNotBlank(info.getPublishTimeReg())) {
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
//            else if (org.apache.commons.lang3.StringUtils.isBlank(publishTime) && info.isAutoDetectPublishDate()) {
//                //如果没有使用爬虫模板抽取到文章发布时间,或者选择了自动抽时间,则进行自动发布时间探测
//                for (Pair<String, SimpleDateFormat> formatEntry : datePattern) {
//                    publishTime = page.getHtml().regex(formatEntry.getKey(), 0).get();
//                    //如果探测到了时间就退出探测
//                    if (org.apache.commons.lang3.StringUtils.isNotBlank(publishTime)) {
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
                }
            }
            //本页面处理时长
            page.putField("processTime", System.currentTimeMillis() - start);
        }
    }



}
