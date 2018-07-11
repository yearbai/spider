package com.moye.crawler.redis;

import com.alibaba.fastjson.JSONObject;
import com.moye.crawler.common.dao.impl.RedisHandle;
import com.moye.crawler.common.utils.SpringUtil;
import com.moye.crawler.entity.spider.SpiderTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: moye
 * @Description:
 * @Date Created in  2018/4/24 14:52
 * @Modified By
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {


    private RedisHandle redisHandle = (RedisHandle) SpringUtil.getBean("redisHandle");

    @Test
    public void testSet(){
        Map<String, Object> map  = new HashMap<>();
        map.put("name", "zhangsan");
        map.put("pwd", "123456");
        redisHandle.set("key", map);

        SpiderTask task = new SpiderTask();
        task.setId("2342432");
        map.put("task", JSONObject.toJSONString(task));
        redisHandle.addMap("testid",map);
    }


}

