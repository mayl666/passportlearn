package com.sogou.upd.passport.common;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.utils.RedisUtils;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import redis.clients.jedis.JedisShardInfo;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: mayan Date: 12-11-22 Time: 下午6:26 To change this template use
 * File | Settings | File Templates.
 */
@ContextConfiguration(locations = {"classpath:spring-config-jredis-test.xml"})
public class JredisTest extends AbstractJUnit4SpringContextTests {
    private static final String TEST_KEY = "TEST_REDIS_KEY";
    private static final String TEST_SUB_KEY = "TEST_REDIS_SUB_KEY";


    @Inject
    private RedisUtils redisUtils;

    @Inject
    private JedisConnectionFactory cacheConnectionFactory;

    @Before
    public void init() {
    }

    @Test
    public void test() {
        try {
//            String key="/internal/account/authuser";
//            String appId="1100";
//            getLimitedTimes(key,appId);
            redisUtils.set("1112","11123");

            System.out.println(redisUtils.get("1112"));

        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
    public void getLimitedTimes(String key,String appId) throws Exception {
        String cacheKey="SP.CLIENTID:INTERFACE_LIMITED_"+appId;
        String cacheTimes=redisUtils.hGet(cacheKey,key);
        if(Strings.isNullOrEmpty(cacheTimes)){
            //初始化或者5分钟失效后的初始化
            initAppLimitedList(cacheKey,key,"30"); //30接口5分钟限制的次数
            cacheTimes="30";
        }
        long times=Long.parseLong(cacheTimes);
        if(times<=0){
            System.out.println("超限");
        }else {
            redisUtils.hIncrByTimes(cacheKey,key,-10);
        }
    }
    public void initAppLimitedList(String cacheKey,String key,String limiTimes) throws Exception {
        if(Strings.isNullOrEmpty(redisUtils.hGet(cacheKey,key))){
            redisUtils.hPutExpire(cacheKey, key, limiTimes, DateAndNumTimesConstant.TIME_FIVE_MINITUES);
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

    @Test
    public void testMultiGetSet() throws Exception {
        try {
            Map<String, String> map = Maps.newHashMap();
            map.put("key1", "value1");
            map.put("key2", "value2");
            redisUtils.multiSet(map);

//            Collection<String> keyCollec = map.keySet();
            List<String> keyCollec= new ArrayList<String>();
            keyCollec.add("key1");
            keyCollec.add("key2");

            List<String> valueList =redisUtils.multiGet(keyCollec);

            for(String value:valueList){
                System.out.println("value:"+value);
            }
            System.out.println("map.toString():" + map.toString());
            System.out.println("keyCollec.toString():" + keyCollec.toString());
//            System.out.println(map.get("key1"));
//            System.out.println(map.get("key2"));
//            System.out.println(map.get("key4"));
        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }
}
