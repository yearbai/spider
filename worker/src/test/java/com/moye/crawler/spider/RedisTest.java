package com.moye.crawler.spider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author: moye
 * @description:
 * @date Created in  2018/5/18 17:40
 * @modified By
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {
    @Autowired
    private JedisPool pool;


    @Test
    public void testRedis(){
        Jedis jedis = this.pool.getResource();
        jedis.set( "test","111111" );
        System.out.println(jedis.get( "test" ));
    }
}
