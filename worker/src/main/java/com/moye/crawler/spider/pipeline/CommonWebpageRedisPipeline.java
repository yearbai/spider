package com.moye.crawler.spider.pipeline;

import com.google.gson.Gson;
import com.moye.crawler.config.SpiderProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import javax.annotation.Resource;

/**
 * Created by gsh199449 on 2016/10/24.
 */
@Component
public class CommonWebpageRedisPipeline implements Pipeline {


    private final Gson gson = new Gson();
    private Logger LOG = LogManager.getLogger(CommonWebpageRedisPipeline.class);

    @Autowired
    private SpiderProperties staticValue;

    @Resource(name = "redisTemplate")
    protected RedisTemplate redisTemplate;


    @Override
    public void process(ResultItems resultItems, Task task) {
        if (!staticValue.isNeedRedis()){ return;}
        redisTemplate.convertAndSend(staticValue.getWebpageRedisPublishChannelName(), gson.toJson(resultItems.getAll()));
    }
}
