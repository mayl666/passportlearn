package com.sogou.upd.passport.dao;

import com.sogou.upd.passport.common.utils.DateUtil;
import com.sogou.upd.passport.common.utils.SMSUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: mayan
 * Date: 12-11-22
 * Time: 下午6:26
 * To change this template use File | Settings | File Templates.
 */
@ContextConfiguration(locations = {"classpath:spring-config-jredis.xml"})
public class TestJredis extends AbstractJUnit4SpringContextTests {

    @Inject
    private ShardedJedisPool shardedJedisPool;

    private ShardedJedis jedis;

    @Before
    public void init() {
        jedis = shardedJedisPool.getResource();
    }

    @Test
    public void test() {
        generalCodeValidTime();
    }

    public long generalCodeValidTime() {
        Date ct = new Date();
        String dayOfYear = DateUtil.formatCompactDate(ct);
        Calendar c = Calendar.getInstance();
        c.setTime(ct);
        c.add(Calendar.MINUTE, SMSUtil.SMS_VALID);
        return c.getTimeInMillis();
    }

    public void initRedis(String mobile, String randomCode) {


        Map<String, String> map = new HashMap<String, String>();
        map.put("smsCode", randomCode);    //验证码
        map.put("sendNum", "1");
        map.put("sendTime", Long.toString(System.currentTimeMillis()));   //发送时间
        map.put("codeValidTime", Long.toString(generalCodeValidTime()));  //失效时间

        jedis.hmset(mobile, map);
        jedis.expire(mobile, 6 * 60);      //有效时长

    }

    @Test
    public void testJredisConnection() {
        try {
            //生成5位随机数
            String randomCode = RandomStringUtils.randomNumeric(5);
            //手机号
            String mobile = "13520066363";
            //初始化redis
            initRedis(mobile,randomCode);


//            Map<String, String> mapResult = jedis.hgetAll(mobile);
////            Iterator it = mapResult.entrySet().iterator();
////            while (it.hasNext()) {
////                Map.Entry m = (Map.Entry) it.next();
////                System.out.println("passport-" + m.getKey() + ":" + m.getValue());
////            }
//
//            if (MapUtils.isNotEmpty(mapResult)) {
//                long sendTime = Long.parseLong(mapResult.get("sendTime"));
//                int sendNum = Integer.parseInt(mapResult.get("sendNum"));
//                long codeValidTime = Long.parseLong(mapResult.get("codeValidTime"));
//                String smsCode = mapResult.get("smsCode");
//
//                long curtime = System.currentTimeMillis();
//                //
//                boolean valid = curtime >= (sendTime + SMSUtil.SEND_SMS_INTERVAL); // 1分钟只能发1条短信
//                if (valid) {
//                    if (sendNum < SMSUtil.MAX_SMS_COUNT_ONEDAY) {
//                        if ((curtime - codeValidTime) > 0) {
//                            //失效重新生成一个smsCode,更新缓存
//                            initRedis(mobile, randomCode);
//                            System.out.println("失效重新生成一个smsCode:" + randomCode);
////                            return smsCode;
//                        }
//                        System.out.println("在30分钟内返回之前的smsCode:" + smsCode);
//                        jedis.hincrBy(mobile, "sendNum", 1);
//                        jedis.hset(mobile,"sendTime",Long.toString(System.currentTimeMillis()));
////                        return smsCode;
//                    } else {
//                        System.out.println("一天最多可发送5条短信");
//                    }
//
//                } else {
//                    System.out.println("1分钟只能发送一条短信");
//                }
//            }
        } finally {
            shardedJedisPool.returnResource(jedis);
        }
    }

}
