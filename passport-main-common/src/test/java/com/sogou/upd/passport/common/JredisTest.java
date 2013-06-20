package com.sogou.upd.passport.common;

import com.google.common.collect.Maps;

import com.sogou.upd.passport.common.utils.RedisUtils;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA. User: mayan Date: 12-11-22 Time: 下午6:26 To change this template use
 * File | Settings | File Templates.
 */
@ContextConfiguration(locations = {"classpath:spring-config-jredis.xml"})
public class JredisTest extends AbstractJUnit4SpringContextTests {
    private static final String TEST_KEY = "TEST_REDIS_KEY";
    private static final String TEST_SUB_KEY = "TEST_REDIS_SUB_KEY";


    @Inject
    private RedisUtils redisUtils;

    @Before
    public void init() {
    }

    @Test
    public void test() {
        try {
//            String ip="192.168.1.1";
//            int i=0;
//            while (i<20){
//              String uuidName= UUID.randomUUID().toString().replaceAll("-","");
//              redisUtils.lPush(ip,uuidName);
//              redisUtils.set(ip+"_"+uuidName,1);
//              i++;
//            }
//             String uuidName= UUID.randomUUID().toString().replaceAll("-","");
//             while (i<20){
//                ip="192.168.1."+i;
//                redisUtils.lPush(uuidName,ip);
//                redisUtils.set(uuidName+"_"+ip,1);
//               i++;
//             }
//          System.out.println(redisUtils.getList(ip).size());




        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

    @Test
    public void testPutAll() throws Exception {
        try {
            Map<String, String> map = Maps.newHashMap();
            map.put("key1", "value1");
            map.put("key2", "value2");
            redisUtils.hPutAll(TEST_KEY, map);

            map = Maps.newHashMap();
            map.put("key1", "value3");
            map.put("key4", "value4");
            redisUtils.hPutAll(TEST_KEY, map);

            map = redisUtils.hGetAll(TEST_KEY);

            System.out.println(map.get("key1"));
            System.out.println(map.get("key2"));
            System.out.println(map.get("key4"));
        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testIncrement() throws Exception {
        try {
            // 测试不存在某个键，能否increment
            redisUtils.delete(TEST_KEY);
            redisUtils.increment(TEST_KEY);
            String value = redisUtils.get(TEST_KEY);
            Assert.assertTrue(value != null && "1".equals(value));

            redisUtils.delete(TEST_KEY);
            redisUtils.hIncrBy(TEST_KEY, TEST_SUB_KEY);
            String sub_value = redisUtils.hGet(TEST_KEY, TEST_SUB_KEY);
            Assert.assertTrue(sub_value != null && "1".equals(value));

            redisUtils.delete(TEST_KEY);
            redisUtils.hPut(TEST_KEY, TEST_SUB_KEY + "SUFFIX", "abc");
            redisUtils.hIncrBy(TEST_KEY, TEST_SUB_KEY);
            sub_value = redisUtils.hGet(TEST_KEY, TEST_SUB_KEY);
            Assert.assertTrue(sub_value != null && "1".equals(value));

        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }
}
