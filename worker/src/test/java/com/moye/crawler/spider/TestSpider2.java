package com.moye.crawler.spider;

import com.moye.crawler.spider.scheduler.MyRedisScheduler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Spider;

/**
 * @author: moye
 * @description:
 * @date Created in  2018/5/21 11:46
 * @modified By
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestSpider2 {

    @Autowired
    private MyRedisScheduler redisScheduler;
    @Test
    public void test(){
        Spider.create(new MeishiPageProcessor())
                .setScheduler( redisScheduler )
                //从"https://github.com/code4craft"开始抓
                .addUrl("http://tech.163.com/special/it_2016_02/")
                //开启5个线程抓取
                .thread(5)
                //启动爬虫
                .run();
    }
}
