package com.moye.crawler.core.spider.listener;

import com.moye.crawler.core.spider.init.MySpider;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.SpiderListener;

/**
 * @author: moye
 * @description:
 * @date Created in  2018/5/21 15:14
 * @modified By
 */
public class MySpiderListener implements SpiderListener {

    private MySpider spider;



    public MySpiderListener(MySpider spider){
        this.spider = spider;
    }

    @Override
    public void onSuccess(Request request) {
        System.out.println("listener success ---" + request.getUrl());
//        redisHandle.addSet(CodeConstant.FINISH_LIST, new String[]{request.getUrl()} );
    }

    @Override
    public void onError(Request request) {
        Integer cycleTriedTimes =  (Integer)request.getExtra(Request.CYCLE_TRIED_TIMES);
        request.putExtra(Request.CYCLE_TRIED_TIMES, cycleTriedTimes == null ? 1 : cycleTriedTimes + 1);
        System.out.println("失败重试 " + cycleTriedTimes);
        spider.addRequest(request);
    }
}
