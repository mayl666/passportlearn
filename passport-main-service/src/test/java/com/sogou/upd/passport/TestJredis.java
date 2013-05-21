package com.sogou.upd.passport;

import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.model.app.AppConfig;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import javax.inject.Inject;

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
    private RedisUtils redisUtils;

    @Before
    public void init() {
    }

    @Test
    public void test() throws Exception {

        AppConfig appConfig=new AppConfig();
        appConfig.setClientId(1001);
        appConfig.setClientSecret("测试");

      redisUtils.set("mayan",new ObjectMapper().writeValueAsString(appConfig));

      AppConfig config=new ObjectMapper().readValue(redisUtils.get("mayan"),AppConfig.class) ;
      System.out.println(config.getClientId());
      System.out.println(config.getClientSecret());
    }

//    @Test
//    public void testSetPassportIdToMobile() {
//        Object obj = redisTemplate.execute(new RedisCallback() {
//            @Override
//            public Object doInRedis(RedisConnection connection) throws DataAccessException {
//                String passportId = "13520069535@sohu.com";
//                String userId = "12";
//                String mobile = "13520069535";
//
//                Map<byte[], byte[]> mapResult = Maps.newHashMap();
//                //  passportId 与 userId
//                mapResult.put(RedisUtils.stringToByteArry("userId"), RedisUtils.stringToByteArry(userId));
//                //  passportId 与 mobile
//                mapResult.put(RedisUtils.stringToByteArry("mobile"), RedisUtils.stringToByteArry(mobile));
//
//                connection.hMSet(RedisUtils.stringToByteArry("PASSPORT:ACCOUNT_PASSPORTID_" + passportId), mapResult);
//                return true;
//            }
//        });
//
//        System.out.println(obj);
//    }
//
//    @Test
//    public void testgetPassportIdToMobile() {
//        redisTemplate.execute(new RedisCallback() {
//            @Override
//            public Object doInRedis(RedisConnection connection) throws DataAccessException {
//                String passportId = "13520069535@sohu.com";
//                String userId = "12";
//                String mobile = "13520069535";
//
//                String keyType = "mobile";
//
//                Map<byte[], byte[]> mapResult = connection.hGetAll(RedisUtils.stringToByteArry(passportId));
//                if (MapUtils.isNotEmpty(mapResult)) {
//                    byte[] value = mapResult.get(RedisUtils.stringToByteArry(keyType));
//                    String strValue = RedisUtils.byteArryToString(value);
//                    System.out.println(strValue);
//                }
////                Iterator it = mapCacheResult.entrySet().iterator();
////                while (it.hasNext()) {
////                    Map.Entry m = (Map.Entry) it.next();
////                    System.out.println(RedisUtils.byteArryToString((byte[])m.getKey()) + ":" + RedisUtils.byteArryToString((byte[]) m.getValue()));
////                }
//                return null;
//            }
//        });
//    }
//
//    public String getPassportIdByUserId(final String userId) {
//        Object obj = null;
//        try {
//            obj = redisTemplate.execute(new RedisCallback<Object>() {
//                @Override
//                public Object doInRedis(RedisConnection connection) throws DataAccessException {
//                    String strValue = null;
//                    byte[] key = RedisUtils.stringToByteArry(userId);
//                    if (connection.exists(key)) {
//                        byte[] value = connection.get(key);
//                        strValue = RedisUtils.byteArryToString(value);
//                    }
//                    return Strings.isNullOrEmpty(strValue) ? null : strValue;
//                }
//            });
//        } catch (Exception e) {
//            logger.error("[SMS] service method getUserIdByPassportId error.{}", e);
//        }
//
//        return (String) obj;  // To change body of implemented methods use File | Settings | File Templates.
//    }
//
//    @Test
//    public void testDelete() {
////        String cacheKey = CacheConstant.CACHE_PREFIX_CLIENTID_APPCONFIG + 1001;
//        String cacheKey = "PASSPORT:OPENID_CONNECTRELATION_1987360834_4";
//        try {
//            redisTemplate.delete(cacheKey);
//            Assert.assertTrue(true);
//        } catch (Exception e) {
//            Assert.assertTrue(false);
//        }
//    }
//
//    @Test
//    public void testGetAppConfig() {
//        String key = "PASSPORT:CLIENTID_APPCONFIG_" + 1001;
//        Type type = new TypeToken<AppConfig>() {
//        }.getType();
//        AppConfig appConfig = redisUtils.getObject(key, type);
//        Assert.assertTrue(appConfig != null);
//    }
//
//    @Test
//    public void deleteCacheByKey() {
//        String cacheKey = "PASSPORT:CLIENTID_APPCONFIG_" + 1001;
//        redisTemplate.delete(cacheKey);
//    }
//
//    @Test
//    public void testSetAppConfigByClientId() {
//        boolean flag = true;
//        try {
//            String cacheKey = "PASSPORT:CLIENTID_APPCONFIG_" + 1001;
//            AppConfig appConfig = buildAppConfig();
//            redisUtils.set(cacheKey, appConfig);
//        } catch (Exception e) {
//            flag = false;
//            logger.error("[App] service method addClientIdMapAppConfig error.{}", e);
//        }
//        Assert.assertTrue(flag);
//    }
//
//    public void initRedis(String mobile, String randomCode) {
//
//
//        Map<String, String> map = new HashMap<String, String>();
//        map.put("smsCode", randomCode);    //验证码
//        map.put("sendNum", "1");
//        map.put("sendTime", Long.toString(System.currentTimeMillis()));   //发送时间
//
////        jedis.hmset(mobile, map);
////        jedis.expire(mobile, 6 * 60);      //有效时长
//
//    }
//
//    @Test
//    public void testJredisConnection() {
////        try {
////            //生成5位随机数
////            String randomCode = RandomStringUtils.randomNumeric(5);
////            //手机号
////            String mobile = "13520066363";
////            //初始化redis
//////            initRedis(mobile,randomCode);
////
////
////            Map<String, String> mapResult = jedis.hgetAll(mobile);
//////            Iterator it = mapResult.entrySet().iterator();
//////            while (it.hasNext()) {
//////                Map.Entry m = (Map.Entry) it.next();
//////                System.out.println("passport-" + m.getKey() + ":" + m.getValue());
//////            }
////
////            if (MapUtils.isNotEmpty(mapResult)) {
////                long sendTime = Long.parseLong(mapResult.get("sendTime"));
////                int sendNum = Integer.parseInt(mapResult.get("sendNum"));
////                String smsCode = mapResult.get("smsCode");
////
////                long curtime = System.currentTimeMillis();
////                //
////                boolean valid = curtime >= (sendTime + SMSUtil.SEND_SMS_INTERVAL); // 1分钟只能发1条短信
////                if (valid) {
////                    if (sendNum < SMSUtil.MAX_SMS_COUNT_ONEDAY) {
////                        System.out.println("在30分钟内返回之前的smsCode:" + smsCode);
////                        jedis.hincrBy(mobile, "sendNum", 1);
////                        jedis.hset(mobile,"sendTime",Long.toString(System.currentTimeMillis()));
//////                        return smsCode;
////                    } else {
////                        System.out.println("一天最多可发送5条短信");
////                    }
////
////                } else {
////                    System.out.println("1分钟只能发送一条短信");
////                }
////            }
////        } finally {
////            shardedJedisPool.returnResource(jedis);
////        }
//    }
//
//    private AppConfig buildAppConfig() {
//        AppConfig appConfig = new AppConfig();
//        appConfig.setClientId(1001);
//        appConfig.setSmsText("您的bobo验证码为：%s，三十分钟内有效哦");
//        appConfig.setAccessTokenExpiresin(604800);
//        appConfig.setRefreshTokenExpiresin(15552000);
//        appConfig.setClientSecret("40db9c5a312a145e8ee8181f4de8957334c5800a");
//        appConfig.setServerSecret("c3425ddc98da66f51628ee6a59eb08cb784d610c");
//        appConfig.setCreateTime(new Date());
//        return appConfig;
//    }

}
