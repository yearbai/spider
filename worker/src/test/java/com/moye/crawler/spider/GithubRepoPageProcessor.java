package com.moye.crawler.spider;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

public class GithubRepoPageProcessor implements PageProcessor {

    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    @Override
    // process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑
    public void process(Page page) {
        // 部分三：从页面发现后续的url地址来抓取
        if(page.getUrl().regex( "http://tech.163.com/special/.*_[0-9]{0,2}/" ).match()){
            page.addTargetRequests(page.getHtml().links().regex("http://tech.163.com/special/.*_[0-9]{0,2}/").all());
            page.addTargetRequests( page.getHtml().links().regex( "http://tech.163.com/[0-9]+/[0-9]+/[0-9]+/.*html" ).all() );
        }else if(page.getUrl().regex( "http://tech.163.com/[0-9]+/[0-9]+/[0-9]+/.*html" ).match()){
            page.putField("title", page.getHtml().xpath("//*[@id='epContentLeft']/h1/text()").toString());
        }

    }

    @Override
    public Site getSite() {
        return site;
    }


}