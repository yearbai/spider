package com.moye.crawler.spider;

import com.moye.crawler.spider.meishi.PageParse;
import org.assertj.core.util.Lists;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;

public class MeishiPageProcessor implements PageProcessor {
    private Site site = Site.me().setRetryTimes(3).setUserAgent("Mozilla/5.0 (Windows NT 5.2) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.122 Safari/534.30")
//            .addHeader(  )

            .setSleepTime(1000).setTimeOut(1000);

    public MeishiPageProcessor() {
    }
    @Override
    public void process(Page page) {
//        System.out.println(page.getHtml());
        page.putField("name", page.getHtml().xpath("//div[@class='name']/text()").get());
        page.putField("address", page.getHtml().xpath( "//div[@class='address']/p[1]/text()" ).get());
        page.putField("phone", page.getHtml().xpath( "//div[@class='address']/p[2]/text()" ).get());
        page.putField("business_hour", page.getHtml().xpath( "//div[@class='address']/p[3]/text()" ).get());

        System.out.println(page.getHtml().xpath("//div[@class='name']/text()").toString());
        System.out.println(page.getHtml().xpath( "//div[@class='address']/p[1]/text()" ).get());
        System.out.println(page.getHtml().xpath( "//div[@class='address']/p[2]/text()" ).get());
        System.out.println(page.getHtml().xpath( "//div[@class='address']/p[3]/text()" ).get());
    }

    @Override
    public Site getSite() {
        return this.site;
    }

    public static void main(String[] args) {
//        List<String> idList = PageParse.getPageList();
//        List<String> ids = Lists.newArrayList();
//        String url = "http://www.meituan.com/meishi/%s/";
//        idList.forEach( id ->{
//            ids.add( String.format( url,id ) );
//            System.out.println(String.format( url,id ));
//        } );
//        String[] urls = new String[ids.size()];
//        ids.toArray(urls);
        Spider.create(new MeishiPageProcessor()).addUrl("http://www.meituan.com/meishi/160230629/").addPipeline( new JsonFilePipeline("d://meishi") ).thread(5).run();
    }
}