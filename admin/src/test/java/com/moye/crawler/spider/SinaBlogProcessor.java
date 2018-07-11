package com.moye.crawler.spider;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;
import java.util.stream.Collectors;

public class SinaBlogProcessor implements PageProcessor {

    public static final String URL_LIST = "http://tech.163.com/special/.*_[0-9]{0,2}/";

    public static final String URL_POST = "http://tech.163.com/[0-9]+/[0-9]+/[0-9]+/.*html";

    private Site site = Site
            .me()
            .setDomain("tech.163.com")
            .setSleepTime(3000)
            .setUserAgent(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");

    @Override
    public void process(Page page) {
        //列表页

        List<String> links = page.getHtml().links().regex(URL_LIST).all().stream()
                .map(s -> {
                   System.out.println(s);
                    int indexOfSharp = s.indexOf("#");
                    return s.substring(0, indexOfSharp == -1 ? s.length() : indexOfSharp);
                }).collect(Collectors.toList());
        page.addTargetRequests(links);


        List<String> urls = page.getHtml().links().regex(URL_POST).all();

        page.addTargetRequests(urls);
        if (page.getUrl().regex(URL_POST).match()) {
            page.putField("title", page.getHtml().xpath("//*[@id='epContentLeft']/h1/text()"));
            System.out.println(page.getHtml().xpath("//*[@id='epContentLeft']/h1/text()"));
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new SinaBlogProcessor()).addUrl("http://tech.163.com/special/tele_2016/").run();
    }
}