package com.sogou.upd.passport.dao;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.utils.DateUtil;
import com.sogou.upd.passport.common.utils.JSONUtils;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.common.utils.SMSUtil;
import com.sogou.upd.passport.model.app.AppConfig;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
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
    @Inject
    private RedisTemplate redisTemplate;

    private ShardedJedis jedis;

    @Before
    public void init() {
        jedis = shardedJedisPool.getResource();
    }

    @Test
    public void test() {
       Object obj=redisTemplate.execute(new RedisCallback<Object>(){
           @Override
           public Object doInRedis(RedisConnection connection) throws DataAccessException {
               String key = "mayanTest";
               BoundHashOperations<String, String, String> boundHashOperations = redisTemplate.boundHashOps(key);
               Map<String, String> data = new HashMap<String, String>();
               data.put("name", "name");
               data.put("age", "35");
               boundHashOperations.putAll(data);
               return true;
           }
       });
        System.out.println(obj);
//        System.out.println(setAppConfigByClientId());
//        AppConfig appConfig= getAppConfigByClientId();
//        System.out.println();


    }

    @Test
    public void testSetPassportIdToMobile(){
          Object obj=redisTemplate.execute(new RedisCallback() {
              @Override
              public Object doInRedis(RedisConnection connection) throws DataAccessException {
                  String passportId="13520069535@sohu.com";
                  String userId="12" ;
                  String mobile="13520069535";

                  Map<byte[],byte[]> mapResult= Maps.newHashMap();
                  //  passportId 与 userId
                  mapResult.put(RedisUtils.stringToByteArry("userId"),RedisUtils.stringToByteArry(userId));
                  //  passportId 与 mobile
                  mapResult.put(RedisUtils.stringToByteArry("mobile"),RedisUtils.stringToByteArry(mobile));

                  connection.hMSet(RedisUtils.stringToByteArry("PASSPORT:ACCOUNT_PASSPORTID_"+passportId),mapResult);
                  return true;
              }
          }) ;

        System.out.println(obj);
    }
    @Test
    public void testgetPassportIdToMobile(){
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                String passportId="13520069535@sohu.com";
                String userId="12" ;
                String mobile="13520069535";

                String keyType="mobile" ;

                Map<byte[], byte[]> mapResult = connection.hGetAll(RedisUtils.stringToByteArry(passportId));
                if(MapUtils.isNotEmpty(mapResult)){
                    byte []value= mapResult.get(RedisUtils.stringToByteArry(keyType));
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
        }) ;
    }

    public String getPassportIdByUserId(final String userId) {
        Object obj=null;
        try {
            obj=redisTemplate.execute(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    String strValue =null;
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

        return (String)obj;  // To change body of implemented methods use File | Settings | File Templates.
    }
    public boolean setAppConfigByClientId(){

        Object obj = null;
        try {
            obj = redisTemplate.execute(new RedisCallback() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    AppConfig appConfig=new AppConfig();
                    appConfig.setAccessTokenExpiresIn(21212);
                    appConfig.setClientId(1003);
                    appConfig.setClientSecret("4343");
                    appConfig.setRefreshTokenExpiresIn(565645);

                    connection.set(RedisUtils.stringToByteArry("1003"),
                            RedisUtils.stringToByteArry(JSONUtils.objectToJson(appConfig)));
                    return true;
                }
            });
        } catch (Exception e) {
            logger.error("[SMS] service method addClientIdMapAppConfig error.{}", e);
        }
        return obj != null ? (Boolean) obj : false;
    }
    public AppConfig getAppConfigByClientId(){
        Object obj = null;
        try {
            obj = redisTemplate.execute(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    AppConfig appConfigResult=null;
                    byte[] value=connection.get(RedisUtils.stringToByteArry("1003"));
                    if(value!=null && value.length>0){
                        appConfigResult= JSONUtils.jsonToObject(RedisUtils.byteArryToString(value), AppConfig.class);
                    }
                    return appConfigResult;
                }
            });
        } catch (Exception e) {
            logger.error("[SMS] service method addClientIdMapAppConfig error.{}", e);
        }
        return obj != null ? (AppConfig) obj : null;
    }

    public void initRedis(String mobile, String randomCode) {


        Map<String, String> map = new HashMap<String, String>();
        map.put("smsCode", randomCode);    //验证码
        map.put("sendNum", "1");
        map.put("sendTime", Long.toString(System.currentTimeMillis()));   //发送时间

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
//            initRedis(mobile,randomCode);


            Map<String, String> mapResult = jedis.hgetAll(mobile);
//            Iterator it = mapResult.entrySet().iterator();
//            while (it.hasNext()) {
//                Map.Entry m = (Map.Entry) it.next();
//                System.out.println("passport-" + m.getKey() + ":" + m.getValue());
//            }

            if (MapUtils.isNotEmpty(mapResult)) {
                long sendTime = Long.parseLong(mapResult.get("sendTime"));
                int sendNum = Integer.parseInt(mapResult.get("sendNum"));
                String smsCode = mapResult.get("smsCode");

                long curtime = System.currentTimeMillis();
                //
                boolean valid = curtime >= (sendTime + SMSUtil.SEND_SMS_INTERVAL); // 1分钟只能发1条短信
                if (valid) {
                    if (sendNum < SMSUtil.MAX_SMS_COUNT_ONEDAY) {
                        System.out.println("在30分钟内返回之前的smsCode:" + smsCode);
                        jedis.hincrBy(mobile, "sendNum", 1);
                        jedis.hset(mobile,"sendTime",Long.toString(System.currentTimeMillis()));
//                        return smsCode;
                    } else {
                        System.out.println("一天最多可发送5条短信");
                    }

                } else {
                    System.out.println("1分钟只能发送一条短信");
                }
            }
        } finally {
            shardedJedisPool.returnResource(jedis);
        }
    }

}
