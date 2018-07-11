package com.moye.crawler.spider.scheduler;

import com.alibaba.fastjson.JSON;
import com.moye.crawler.spider.downloader.ContentLengthLimitHttpClientDownloader;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;
import us.codecraft.webmagic.scheduler.RedisPriorityScheduler;
import us.codecraft.webmagic.scheduler.RedisScheduler;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

/**
 * @author: moye
 * @description:思路是采用set来存储已经抓取过的url， list来存储待抓url队列，
 * hash来存储序列化数据(哈希中的键为url的SHA值，值为Request的json序列化字符串)。
 * 所有数据类型的键都是基于Spider的UUID来生成的，也就是说每个Spider实例所拥有的都是不同的
 * @date Created in  2018/5/18 17:30
 * @modified By2
 */
@Component
public class MyRedisScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler, DuplicateRemover {
    private final static Logger LOG = LogManager.getLogger(ContentLengthLimitHttpClientDownloader.class);

    @Autowired
    private JedisPool pool;

    private static final String QUEUE_PREFIX = "queue_";
    private static final String SET_PREFIX = "set_";
    private static final String ITEM_PREFIX = "item_";

    public MyRedisScheduler() {
        this.setDuplicateRemover( this );
    }

    public MyRedisScheduler(JedisPool pool) {
        this.pool = pool;
        this.setDuplicateRemover( this );
    }

    @Override
    public void resetDuplicateCheck(Task task) {
        Jedis jedis = this.pool.getResource();
        try {
            jedis.del( this.getSetKey( task ) );
        } finally {
            this.pool.returnResource( jedis );
        }
    }


    /**
     * 判断是否重复
     *
     * @param request
     * @param task
     * @return
     */
    @Override
    public boolean isDuplicate(Request request, Task task) {
        Jedis jedis = this.pool.getResource();
        boolean isDuplicate = true;
        try {
            isDuplicate = jedis.sismember( this.getSetKey( task ), request.getUrl() ).booleanValue();
            if (!isDuplicate) {
                jedis.sadd(this.getSetKey( task ), new String[]{request.getUrl()} );
            }
        } finally {
            this.pool.returnResource( jedis );
        }

        return isDuplicate;
    }

    /**
     * 如果不重复，则加入待采集队列
     * @param request
     * @param task
     */
    @Override
    protected void pushWhenNoDuplicate(Request request, Task task) {
        LOG.trace( "get a candidate url {}", request.getUrl() );
        System.out.println("pushWhenNoDuplicate --- " + request.getUrl());
        if (!this.isDuplicate( request, task ) || this.shouldReserved( request )) {
            LOG.debug( "push to queue {}", request.getUrl() );
            Jedis jedis = pool.getResource();
            try {
                jedis.rpush( getQueueKey( task ), request.getUrl() );
                System.out.println("rpush=="+request.getUrl() );
                if (request.getExtras() != null) {
                    String field = DigestUtils.shaHex( request.getUrl() );
                    String value = JSON.toJSONString( request );
                    jedis.hset( (ITEM_PREFIX + task.getUUID()), field, value );
                }
            } finally {
                pool.returnResource( jedis );
            }
        }
    }

    @Override
    public synchronized Request poll(Task task) {
        Jedis jedis = pool.getResource();
        try {
            String url = jedis.lpop( getQueueKey( task ) );
            System.out.println("lpop=="+url);
            if (url == null) {
                return null;
            }
            String key = ITEM_PREFIX + task.getUUID();
            String field = DigestUtils.shaHex( url );
            byte[] bytes = jedis.hget( key.getBytes(), field.getBytes() );
            if (bytes != null) {
                Request request = JSON.parseObject( new String( bytes ), Request.class );
                return request;
            }

            return new Request( url );
        } finally {
            pool.returnResource( jedis );
        }
    }

    protected String getSetKey(Task task) {
//        System.out.println( task.getUUID() );
        return SET_PREFIX + task.getUUID();
    }

    protected String getQueueKey(Task task) {
//        System.out.println( task.getUUID() );
        return QUEUE_PREFIX + task.getUUID();
    }

    protected String getItemKey(Task task) {
        return ITEM_PREFIX + task.getUUID();
    }

    @Override
    public int getLeftRequestsCount(Task task) {
        Jedis jedis = pool.getResource();
        try {
            Long size = jedis.llen( getQueueKey( task ) );
            return size.intValue();
        } finally {
            pool.returnResource( jedis );
        }
    }

    @Override
    public int getTotalRequestsCount(Task task) {
        Jedis jedis = pool.getResource();
        try {
            Long size = jedis.scard( getSetKey( task ) );
            return size.intValue();
        } finally {
            pool.returnResource( jedis );
        }
    }
}
