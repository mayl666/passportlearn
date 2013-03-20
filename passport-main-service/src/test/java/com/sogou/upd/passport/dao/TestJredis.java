package com.sogou.upd.passport.dao;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mayan
 * Date: 12-11-22
 * Time: 下午6:26
 * To change this template use File | Settings | File Templates.
 */
@ContextConfiguration(locations = {"classpath:spring-config-jredis.xml" })
public class TestJredis extends AbstractJUnit4SpringContextTests {

    @Inject
    private ShardedJedisPool shardedJedisPool;

    private ShardedJedis jedis;

    @Before
    public void init(){
        jedis = shardedJedisPool.getResource();
    }

    @Test
    public void testJredisConnection() {
        try {

            String tel="13520066363";
//            Map<String,String> map=new HashMap<String, String>();
//            map.put("smsCode","54321");
//            map.put("sendNum","1") ;
//            jedis.hmset("13520066363",map);
//            jedis.expire("13520066363",30);

            System.out.println(jedis.hget("13520066363","sendNum"));
            jedis.hincrBy("13520066363","sendNum",1);
            System.out.println(jedis.hget("13520066363","sendNum"));
        } finally {
            shardedJedisPool.returnResource(jedis);
        }
    }

   }
