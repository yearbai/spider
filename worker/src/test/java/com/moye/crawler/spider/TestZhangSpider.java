package com.moye.crawler.spider;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.moye.crawler.config.SpiderProperties;
import com.moye.crawler.spider.common.SpiderInfo;
import com.moye.crawler.spider.downloader.CasperjsDownloader;
import com.moye.crawler.spider.downloader.ContentLengthLimitHttpClientDownloader;
import com.moye.crawler.spider.meishi.PageParse;
import com.moye.crawler.spider.pipeline.FilePipeline;
import com.moye.crawler.spider.processor.CustomPageProcessor;
import com.moye.crawler.spider.utils.HttpClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author: moye
 * @description:
 * @date Created in  2018/5/23 9:24
 * @modified By
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestZhangSpider {


    @Autowired
    private CasperjsDownloader casperjsDownloader;

    @Autowired
    private HttpClientUtil httpClientUtil;


    public void spider(String[] urls) {

//        String url = "http://www.meituan.com/meishi/%s/";
        for(String url : urls){
        Spider.create( new MeishiPageProcessor() ).addUrl( url )
                .thread( 3 )
                .setDownloader( casperjsDownloader )
                .addPipeline( new FilePipeline( "d://meishi" ) ).run();

        }

    }

    @Test
    public void getPageSpider() {
        int offset = 0;
        int pagesize = 32;
        int limit = 1;
        String url = "";
        String response = "";
        try {
            for (int page = 10; page < 23; page++) {
                offset = (page - 1) * pagesize;
                limit = pagesize;
                url = "http://apimobile.meituan.com/group/v4/poi/pcsearch/253?uuid=3c52519a6abb4359be63.1527037848.1.0.0" +
                        "&userid=-1&limit=" + limit + "&offset=" + offset + "&cateId=-1&q=%E7%83%A7%E7%83%A4";
                response = httpClientUtil.get( url );
                List<String> urlList = parseId( response );
                String[] urls = new String[urlList.size()];
                urlList.toArray( urls );
                spider( urls );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private List<String> parseId(String jsonStr) {
        String url = "http://www.meituan.com/meishi/%s/";
        List<String> idList = Lists.newArrayList();
        JSONObject jsonObject = JSONObject.parseObject( jsonStr );
        JSONObject data = jsonObject.getJSONObject( "data" );
        JSONArray array = data.getJSONArray( "searchResult" );
        array.forEach( obj -> {
            JSONObject meishi = (JSONObject) obj;
            idList.add( String.format( url, meishi.get( "id" ).toString() ) );
        } );
        return idList;
    }


}
