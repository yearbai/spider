package com.moye.crawler;

import com.moye.crawler.config.SpiderProperties;
import com.moye.crawler.spider.MySpider;
import com.moye.crawler.spider.common.SpiderInfo;
import com.moye.crawler.spider.downloader.CasperjsDownloader;
import com.moye.crawler.spider.downloader.ContentLengthLimitHttpClientDownloader;
import com.moye.crawler.spider.processor.CustomPageProcessor;
import com.moye.crawler.spider.scheduler.MyRedisScheduler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;

import java.util.ArrayList;
import java.util.Arrays;

@RestController
@SpringBootApplication
public class WorkerApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkerApplication.class, args);
	}

	@Autowired
	private SpiderProperties spiderProperties;
	@Autowired
	private CasperjsDownloader casperjsDownloader;
	@Autowired
	private ContentLengthLimitHttpClientDownloader contentLengthLimitHttpClientDownloader;

	@Autowired
	private MyRedisScheduler redisScheduler;

	@RequestMapping(value = "/worker/test")
	public String testSpider(){
		SpiderInfo info = makeSpiderInfo();
		MySpider spider =  (MySpider) makeSpider(info).setScheduler(redisScheduler);
		//添加其他的数据管道
		spider.addPipeline(new JsonFilePipeline("D:\\webmagic\\"));
		spider.startUrls( Arrays.asList(info.getStartUrl().split(",")));
//		spider.setScheduler(redisScheduler);
		ArrayList<SpiderListener> listeners = new ArrayList<>();
		listeners.add(new SpiderListener() {
			@Override
			public void onSuccess(Request request) {
				System.out.println("爬去成功");
			}
			@Override
			public void onError(Request request) {
				Integer cycleTriedTimes =  (Integer)request.getExtra(Request.CYCLE_TRIED_TIMES);
				request.putExtra(Request.CYCLE_TRIED_TIMES, cycleTriedTimes == null ? 1 : cycleTriedTimes + 1);
				System.out.println("失败重试 " + cycleTriedTimes);
				spider.addRequest(request);
			}
		});
		spider.setSpiderListeners(listeners);
		//慎用爬虫监控,可能导致内存泄露
//        spiderMonitor.register(spider);
		spider.run();
		return "success";
	}

	private MySpider makeSpider(SpiderInfo info) {
		MySpider spider = ((MySpider) new MySpider(new CustomPageProcessor(info), spiderProperties)
				.thread(info.getThread())
//                .setUUID( UUID.randomUUID().toString().replaceAll( "-","" ))
		);
		if (info.getAjaxSite() == 1 && StringUtils.isNotBlank(spiderProperties.getAjaxDownloader())) {
			spider.setDownloader(casperjsDownloader);
		} else {
			spider.setDownloader(contentLengthLimitHttpClientDownloader);
		}
		return spider;
	}

	private SpiderInfo makeSpiderInfo(){
		SpiderInfo info = new SpiderInfo();
		info.setSiteName("网易科技");
		info.setDomain( "tech.163.com" );
		info.setStartUrl( "http://tech.163.com/special/it_2016_02/" );
		info.setPageUrlReg( "http://tech.163.com/special/.*_[0-9]{0,2}/" );
		info.setUrlReg( "http://tech.163.com/[0-9]+/[0-9]+/[0-9]+/.*html" );
		info.setTitleXpath( "//*[@id='epContentLeft']/h1/text()" );
		info.setPublishTimeReg( "[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}" );
		info.setPublishTimeFormat( "yyyy-MM-dd HH:mm:ss" );
		info.setPublishTimeXpath( "//div[@class='post_time_source']/text()" );
		return info;
	}
}
