package com.sogou.upd.passport.manager;

import com.sogou.upd.passport.common.utils.RedisUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import redis.clients.jedis.ShardedJedisPool;


/**
 * Created with IntelliJ IDEA. User: mayan Date: 12-11-22 Time: 下午6:26 To change this template use
 * File | Settings | File Templates.
 */
@ContextConfiguration(locations = {"classpath:spring-config-manager-jredis.xml"})
public class JredisT extends AbstractJUnit4SpringContextTests {

    @Autowired
    private ShardedJedisPool shardedJedisPool;

    @Autowired
    private RedisUtil redisUtils;

    @Test
    public void test()  {

        redisUtils.set("nnnnnn", "mmmmmm");
        System.out.println();


    }


}
