package com.sogou.upd.passport;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.model.app.AppConfig;
import junit.framework.Assert;
import org.apache.commons.collections.MapUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import javax.inject.Inject;
import java.lang.reflect.Type;
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
    private RedisTemplate redisTemplate;

    @Inject
    private RedisUtils redisUtils;
    @Before
    public void init() {
    }

    @Test
    public void test() {
//       Object obj=redisTemplate.execute(new RedisCallback<Object>(){
//           @Override
//           public Object doInRedis(RedisConnection connection) throws DataAccessException {
//               String key = "mayanTest";
//               BoundHashOperations<String, String, String> boundHashOperations = redisTemplate.boundHashOps(key);
//               Map<String, String> data = new HashMap<String, String>();
//               data.put("name", "name");
//               data.put("age", "35");
//               boundHashOperations.putAll(data);
//               return true;
//           }
//       });
//        System.out.println(obj);
//        System.out.println(setAppConfigByClientId());
//        AppConfig appConfig= getAppConfigByClientId();
//        System.out.println();

//        String tips = "您的“碰头”验证码为：%s，20分钟内有效哦";
//
//        String sms = String.format(tips, "12345");
//        System.out.println(sms);
//        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
//        System.out.println(valueOperations.setIfAbsent("12345","234456"));
        redisUtils.set("zhangsan1","lisi1");
        System.out.println(redisUtils.get("zhangsan1"));
    }

    @Test
    public void testSetPassportIdToMobile() {
        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                String passportId = "13520069535@sohu.com";
                String userId = "12";
                String mobile = "13520069535";

                Map<byte[], byte[]> mapResult = Maps.newHashMap();
                //  passportId 与 userId
                mapResult.put(RedisUtils.stringToByteArry("userId"), RedisUtils.stringToByteArry(userId));
                //  passportId 与 mobile
                mapResult.put(RedisUtils.stringToByteArry("mobile"), RedisUtils.stringToByteArry(mobile));

                connection.hMSet(RedisUtils.stringToByteArry("PASSPORT:ACCOUNT_PASSPORTID_" + passportId), mapResult);
                return true;
            }
        });

        System.out.println(obj);
    }

    @Test
    public void testgetPassportIdToMobile() {
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                String passportId = "13520069535@sohu.com";
                String userId = "12";
                String mobile = "13520069535";

                String keyType = "mobile";

                Map<byte[], byte[]> mapResult = connection.hGetAll(RedisUtils.stringToByteArry(passportId));
                if (MapUtils.isNotEmpty(mapResult)) {
                    byte[] value = mapResult.get(RedisUtils.stringToByteArry(keyType));
                    String strValue = RedisUtils.byteArryToString(value);
                    System.out.println(strValue);
                }
//                Iterator it = mapCacheResult.entrySet().iterator();
//                while (it.hasNext()) {
//                    Map.Entry m = (Map.Entry) it.next();
//                    System.out.println(RedisUtils.byteArryToString((byte[])m.getKey()) + ":" + RedisUtils.byteArryToString((byte[]) m.getValue()));
//                }
                return null;
            }
        });
    }

    public String getPassportIdByUserId(final String userId) {
        Object obj = null;
        try {
            obj = redisTemplate.execute(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    String strValue = null;
                    byte[] key = RedisUtils.stringToByteArry(userId);
                    if (connection.exists(key)) {
                        byte[] value = connection.get(key);
                        strValue = RedisUtils.byteArryToString(value);
                    }
                    return Strings.isNullOrEmpty(strValue) ? null : strValue;
                }
            });
        } catch (Exception e) {
            logger.error("[SMS] service method getUserIdByPassportId error.{}", e);
        }

        return (String) obj;  // To change body of implemented methods use File | Settings | File Templates.
    }

    @Test
    public void testDeleteAppConfig() {
        String cacheKey = "PASSPORT:ACCOUNT_CLIENTID_" + 1001;
        redisTemplate.delete(cacheKey);

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String valAppConfig = valueOperations.get(cacheKey);
        if (!Strings.isNullOrEmpty(valAppConfig)) {
            Type type = new TypeToken<AppConfig>() {
            }.getType();
            AppConfig appConfig = new Gson().fromJson(valAppConfig, type);
            Assert.assertTrue(true);
        }
        Assert.assertTrue(false);
    }

    @Test
    public void deleteCacheByKey() {
        String cacheKey = "PASSPORT:ACCOUNT_PASSPORTID_" + "13621009174@sohu.com";
        redisTemplate.delete(cacheKey);
    }

    @Test
    public void testAddAppConfigByClientId() {
        boolean flag = true;
        try {
            String cacheKey = "PASSPORT:ACCOUNT_CLIENTID_" + 1001;

            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            valueOperations.setIfAbsent(String.valueOf(cacheKey), new Gson().toJson(buildAppConfig()));
        } catch (Exception e) {
            flag = false;
            logger.error("[App] service method addClientIdMapAppConfig error.{}", e);
        }
        Assert.assertTrue(flag);
//
//        Object obj = null;
//        try {
//            obj = redisTemplate.execute(new RedisCallback() {
//                @Override
//                public Object doInRedis(RedisConnection connection) throws DataAccessException {
//                    AppConfig appConfig=new AppConfig();
//                    appConfig.setAccessTokenExpiresin(21212);
//                    appConfig.setClientId(1003);
//                    appConfig.setClientSecret("4343");
//                    appConfig.setRefreshTokenExpiresin(565645);
//
//                    connection.set(RedisUtils.stringToByteArry("1003"),
//                            RedisUtils.stringToByteArry(JSONUtils.objectToJson(appConfig)));
//                    return true;
//                }
//            });
//        } catch (Exception e) {
//            logger.error("[SMS] service method addClientIdMapAppConfig error.{}", e);
//        }
//        return obj != null ? (Boolean) obj : false;
    }

//    public AppConfig getAppConfigByClientId(){
//        Object obj = null;
//        try {
//            obj = redisTemplate.execute(new RedisCallback<Object>() {
//                @Override
//                public Object doInRedis(RedisConnection connection) throws DataAccessException {
//                    AppConfig appConfigResult=null;
//                    byte[] value=connection.get(RedisUtils.stringToByteArry("1003"));
//                    if(value!=null && value.length>0){
//                        appConfigResult= JSONUtils.jsonToObject(RedisUtils.byteArryToString(value), AppConfig.class);
//                    }
//                    return appConfigResult;
//                }
//            });
//        } catch (Exception e) {
//            logger.error("[SMS] service method addClientIdMapAppConfig error.{}", e);
//        }
//        return obj != null ? (AppConfig) obj : null;
//    }

    public void initRedis(String mobile, String randomCode) {


        Map<String, String> map = new HashMap<String, String>();
        map.put("smsCode", randomCode);    //验证码
        map.put("sendNum", "1");
        map.put("sendTime", Long.toString(System.currentTimeMillis()));   //发送时间

//        jedis.hmset(mobile, map);
//        jedis.expire(mobile, 6 * 60);      //有效时长

    }

    @Test
    public void testJredisConnection() {
//        try {
//            //生成5位随机数
//            String randomCode = RandomStringUtils.randomNumeric(5);
//            //手机号
//            String mobile = "13520066363";
//            //初始化redis
////            initRedis(mobile,randomCode);
//
//
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
//                String smsCode = mapResult.get("smsCode");
//
//                long curtime = System.currentTimeMillis();
//                //
//                boolean valid = curtime >= (sendTime + SMSUtil.SEND_SMS_INTERVAL); // 1分钟只能发1条短信
//                if (valid) {
//                    if (sendNum < SMSUtil.MAX_SMS_COUNT_ONEDAY) {
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
//        } finally {
//            shardedJedisPool.returnResource(jedis);
//        }
    }

    private AppConfig buildAppConfig() {
        AppConfig appConfig = new AppConfig();
        appConfig.setClientId(1001);
        appConfig.setSmsText("您的“T3”验证码为：%s，30分钟内有效哦");
        appConfig.setAccessTokenExpiresin(604800);
        appConfig.setRefreshTokenExpiresin(15552000);
        appConfig.setClientSecret("1001136453922995472gMLyjj7u");
        appConfig.setServerSecret("1001136453922993981IaBLDFL3");
        appConfig.setUpdateTime(new Date());
        appConfig.setCreateTime(new Date());
        return appConfig;
    }

}
