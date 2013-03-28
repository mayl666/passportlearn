package com.sogou.upd.passport.service.account.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.primitives.Bytes;
import com.mchange.lang.ByteUtils;
import com.sogou.upd.passport.common.exception.SystemException;
import com.sogou.upd.passport.common.parameter.AccountStatusEnum;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.JSONUtils;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.common.utils.SMSUtil;
import com.sogou.upd.passport.dao.account.AccountAuthMapper;
import com.sogou.upd.passport.dao.account.AccountMapper;
import com.sogou.upd.passport.dao.app.AppConfigMapper;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountAuth;
import com.sogou.upd.passport.model.account.PostUserProfile;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.generator.PassportIDGenerator;
import com.sogou.upd.passport.service.account.generator.PwdGenerator;
import com.sogou.upd.passport.service.account.generator.TokenGenerator;
import com.sogou.upd.passport.service.app.AppConfigService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import javax.inject.Inject;
import java.util.Date;
import java.util.Map;

/**
 * User: mayan
 * Date: 13-3-22
 * Time: 下午3:38
 * To change this template use File | Settings | File Templates.
 */
@Service
public class AccountServiceImpl implements AccountService {
    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);
    private static final String CACHE_PREFIX_ACCOUNT_SMSCODE = "PASSPORT:ACCOUNT_SMSCODE_";   //account与smscode映射
    private static final String CACHE_PREFIX_ACCOUNT_SENDNUM = "PASSPORT:ACCOUNT_SENDNUM_";
    private static final String CACHE_PREFIX_PASSPORT = "PASSPORT:ACCOUNT_PASSPORTID_";     //passport_id与userID映射
    private static final String CACHE_PREFIX_USERID = "PASSPORT:ACCOUNT_USERID_";     //passport_id与userID映射
    @Inject
    private AccountMapper accountMapper;
    @Inject
    private AccountAuthMapper accountAuthMapper;
    @Inject
    private ShardedJedisPool shardedJedisPool;
    @Inject
    private AppConfigMapper appConfigMapper;

    private ShardedJedis jedis;
    @Inject
    private TaskExecutor taskExecutor;

    @Inject
    private RedisTemplate redisTemplate;


    @Override
    public boolean checkIsRegisterAccount(Account account) {
        Account accountReturn = accountMapper.checkIsRegisterAccount(account);
        return accountReturn == null ? true : false;
    }

    @Override
    public Map<String, Object> handleSendSms(String account, int client_id) {
        Map<String, Object> mapResult = Maps.newHashMap();
        boolean isSend = true;
        try {
            //设置每日最多发送短信验证码条数
            String keySendNumCache = CACHE_PREFIX_ACCOUNT_SENDNUM + account;
            jedis = shardedJedisPool.getResource();
            if (!jedis.exists(keySendNumCache)) {
                jedis.hset(keySendNumCache, "sendNum", "1");
                jedis.expire(keySendNumCache, SMSUtil.SMS_ONEDAY);
            } else {
                //如果存在，判断是否已经超出日发送最高限额   (比如30分钟后失效了，再次获取验证码 需要和此用户当天发送的总的条数对比)
                Map<String, String> mapCacheSendNumResult = jedis.hgetAll(CACHE_PREFIX_ACCOUNT_SENDNUM + account);
                if (MapUtils.isNotEmpty(mapCacheSendNumResult)) {
                    int sendNum = Integer.parseInt(mapCacheSendNumResult.get("sendNum"));
                    if (sendNum < SMSUtil.MAX_SMS_COUNT_ONEDAY) {     //每日最多发送短信验证码条数
                        jedis.hincrBy(CACHE_PREFIX_ACCOUNT_SENDNUM + account, "sendNum", 1);
                    } else {
                        return ErrorUtil.buildError(ErrorUtil.ERR_CODE_ACCOUNT_CANTSENTSMS, "短信发送已达今天的最高上限" + SMSUtil.MAX_SMS_COUNT_ONEDAY + "条");
                    }
                }
            }
            //生成随机数
            String randomCode = RandomStringUtils.randomNumeric(5);
            //写入缓存
            String keyCache = CACHE_PREFIX_ACCOUNT_SMSCODE + account + "_" + client_id;
            Map<String, String> map = Maps.newHashMap();
            map.put("smsCode", randomCode);    //初始化验证码
            map.put("mobile", account);        //发送手机号
            map.put("sendTime", Long.toString(System.currentTimeMillis()));   //发送时间

            jedis.hmset(keyCache, map);
            jedis.expire(keyCache, SMSUtil.SMS_VALID);      //有效时长30分钟  ，1800秒

            //todo 内容从缓存中读取
            isSend = SMSUtil.sendSMS(account, randomCode);
            if (isSend) {
                mapResult.put("smscode", randomCode);
                return ErrorUtil.buildSuccess("获取注册验证码成功", mapResult);
            }
        } catch (Exception e) {
            logger.error("[SMS] service method handleSendSms error.{}", e);
        } finally {
            shardedJedisPool.returnResource(jedis);
        }
        return null;
    }

    @Override
    public boolean checkIsExistFromCache(final String cacheKey) {
        Object obj = null;
        try {
            obj = redisTemplate.execute(new RedisCallback() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    boolean flag = connection.exists(RedisUtils.stringToByteArry(CACHE_PREFIX_ACCOUNT_SMSCODE + cacheKey));
                    return flag;
                }
            });
        } catch (Exception e) {
            logger.error("[SMS] service method checkIsExistFromCache error.{}", e);
        }
        return obj != null ? (Boolean) obj : false;
    }

    @Override
    public Map<String, Object> updateCacheStatusByAccount(String cacheKey) {
        cacheKey = CACHE_PREFIX_ACCOUNT_SMSCODE + cacheKey;
        Map<String, Object> mapResult = Maps.newHashMap();
        try {
            jedis = shardedJedisPool.getResource();

            Map<String, String> mapCacheResult = jedis.hgetAll(cacheKey);
            if (MapUtils.isNotEmpty(mapCacheResult)) {
                //获取缓存数据
                long sendTime = Long.parseLong(mapCacheResult.get("sendTime"));
                String smsCode = mapCacheResult.get("smsCode");
                String account = mapCacheResult.get("mobile");
                Map<String, String> mapCacheSendNumResult = jedis.hgetAll(CACHE_PREFIX_ACCOUNT_SENDNUM + account);
                if (MapUtils.isNotEmpty(mapCacheSendNumResult)) {
                    int sendNum = Integer.parseInt(mapCacheSendNumResult.get("sendNum"));
                    long curtime = System.currentTimeMillis();
                    boolean valid = curtime >= (sendTime + SMSUtil.SEND_SMS_INTERVAL); // 1分钟只能发1条短信
                    if (valid) {
                        if (sendNum < SMSUtil.MAX_SMS_COUNT_ONEDAY) {     //每日最多发送短信验证码条数
                            jedis.hincrBy(CACHE_PREFIX_ACCOUNT_SENDNUM + account, "sendNum", 1);
                            jedis.hset(cacheKey, "sendTime", Long.toString(System.currentTimeMillis()));
                            //todo 内容从缓存中读取
                            boolean isSend = SMSUtil.sendSMS(account, smsCode);
                            if (isSend) {
                                //30分钟之内返回原先验证码
                                mapResult.put("smscode", smsCode);
                                return ErrorUtil.buildSuccess("获取注册验证码成功", mapResult);
                            }
                        } else {
                            return ErrorUtil.buildError(ErrorUtil.ERR_CODE_ACCOUNT_CANTSENTSMS, "短信发送已达今天的最高上限" + SMSUtil.MAX_SMS_COUNT_ONEDAY + "条");
                        }
                    } else {
                        return ErrorUtil.buildError(ErrorUtil.ERR_CODE_ACCOUNT_MINUTELIMIT, "1分钟只能发送一条短信");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("[SMS] service method updateCacheStatusByAccount error.{}", e);
        } finally {
            shardedJedisPool.returnResource(jedis);
        }

        return null;
    }

    @Override
    public long userRegister(Account account) {
        return accountMapper.userRegister(account);
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map<String, Object> handleLogin(String mobile, String passwd, int client_id, PostUserProfile postData) throws SystemException {
        Account userAccount = null;
        //判断用户是否存在
        try {
            userAccount = getUserAccount(mobile, passwd);
        } catch (Exception e) {
            return ErrorUtil.buildError(ErrorUtil.ERR_CODE_ACCOUNT_LOGINERROR);
        }
        if (userAccount == null) {
            return ErrorUtil.buildError(ErrorUtil.ERR_CODE_ACCOUNT_LOGINERROR);
        }
        //判读access_token有效性，是否在有效的范围内
        AccountAuth accountAuth = accountAuthMapper.getUserAuthByUserId(userAccount.getId());

        long curtime = System.currentTimeMillis();
        boolean valid = curtime < accountAuth.getAccessValidTime();

//        AccountAuth accountAuth=newAccountAuth(userAccount.getId(),userAccount.getPassportId(),appkey);
//        int updateNum=accountAuthMapper.updateAccountAuth(accountAuth);
//
//        if (accountAuth == null) {
//            return ErrorUtil.buildError(ErrorUtil.ERR_CODE_ACCOUNT_ACCESSTOKEN_FAILED);
//        }

        if (valid) {
            return ErrorUtil.buildSuccess("登录成功", null);
        }

        return ErrorUtil.buildError(ErrorUtil.ERR_CODE_ACCOUNT_LOGINERROR);
    }

    /**
     * 根据用户名密码获取用户Account
     *
     * @param
     * @return
     */
    public Account getUserAccount(String mobile, String passwd) throws SystemException {
        Map<String, String> mapResult = Maps.newHashMap();
        mapResult.put("mobile", mobile);
        mapResult.put("passwd", Strings.isNullOrEmpty(passwd) ? "" : PwdGenerator.generatorPwdSign(passwd));
        Account accountResult = accountMapper.getUserAccount(mapResult);
        return accountResult != null ? accountResult : null;
    }


    @Override
    public boolean checkSmsInfoFromCache(final String account, final String smsCode, final String client_id) {
        Object obj = null;
        try {
            obj = redisTemplate.execute(new RedisCallback() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    String keyCache = CACHE_PREFIX_ACCOUNT_SMSCODE + account + "_" + client_id;
                    String strValue = null;
                    Map<byte[], byte[]> mapResult = connection.hGetAll(RedisUtils.stringToByteArry(keyCache));
                    if (MapUtils.isNotEmpty(mapResult)) {
                        byte[] value = mapResult.get(RedisUtils.stringToByteArry("smsCode"));
                        strValue = RedisUtils.byteArryToString(value);
                        if (StringUtils.isNotBlank(strValue)) {
                            if (strValue.equals(smsCode)) {
                                return true;
                            } else {
                                return false;
                            }
                        }
                    } else {
                        return false;
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            logger.error("[SMS] service method checkSmsInfoFromCache error.{}", e);
        }
        return obj != null ? (Boolean) obj : false;
    }

    @Override
    public Account initialAccount(String account, String pwd, String ip, int provider) throws SystemException {
        Account accountReturn = new Account();
        accountReturn.setPassportId(PassportIDGenerator.generator(account, provider));
        String passwdSign = null;
        if (!Strings.isNullOrEmpty(pwd)) {
            passwdSign = PwdGenerator.generatorPwdSign(pwd);
        }
        accountReturn.setPasswd(passwdSign);
        accountReturn.setRegTime(new Date());
        accountReturn.setRegIp(ip);
        accountReturn.setAccountType(provider);
        accountReturn.setStatus(AccountStatusEnum.REGULAR.getValue());
        accountReturn.setVersion(Account.NEW_ACCOUNT_VERSION);
        accountReturn.setMobile(account);
        long id = accountMapper.userRegister(accountReturn);
        if (id != 0) {
            accountReturn.setId(id);
            return accountReturn;
        }
        return null;
    }

    @Override
    public Account initialConnectAccount(String account, String ip, int provider) throws SystemException {
        return initialAccount(account, null, ip, provider);
    }

    @Override
    public AccountAuth initialAccountAuth(long userId, String passportId, int clientId) throws SystemException {
        AccountAuth accountAuth = newAccountAuth(userId, passportId, clientId);
        long id = accountAuthMapper.saveAccountAuth(accountAuth);
        if (id != 0) {
            accountAuth.setId(id);
            return accountAuth;
        }
        return null;
    }

    @Override
    public AccountAuth updateAccountAuth(long userId, String passportId, int clientId) throws Exception {
        AccountAuth accountAuth = newAccountAuth(userId, passportId, clientId);
        if (accountAuth != null) {
            int accountRow = accountAuthMapper.updateAccountAuth(accountAuth);
            return accountRow == 0 ? null : accountAuth;
        }
        return null;
    }

    @Override
    public boolean addPassportIdMapUserId(final String passportId, final String userId, final String mobile) {
        Object obj = null;
        try {
            obj = redisTemplate.execute(new RedisCallback() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    String cacheKey = CACHE_PREFIX_PASSPORT + passportId;
                    //判断缓存是否存在
                    if (!connection.exists(RedisUtils.stringToByteArry(cacheKey))) {
                        Map<byte[], byte[]> mapResult = Maps.newHashMap();
                        //  passportId 与 userId映射
                        mapResult.put(RedisUtils.stringToByteArry("userId"), RedisUtils.stringToByteArry(userId));
                        //  passportId 与 mobile映射
                        mapResult.put(RedisUtils.stringToByteArry("mobile"), RedisUtils.stringToByteArry(mobile));
                        connection.hMSet(RedisUtils.stringToByteArry(cacheKey), mapResult);
                    }
                    return true;
                }
            });
        } catch (Exception e) {
            logger.error("[SMS] service method addPassportIdMapUserId error.{}", e);
        }
        return obj != null ? (Boolean) obj : false;
    }

    @Override
    public boolean addUserIdMapPassportId(final String userId, final String passportId) {
        Object obj = null;
        try {
            obj = redisTemplate.execute(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    String cacheKey = CACHE_PREFIX_USERID + userId;
                    if (!connection.exists(RedisUtils.stringToByteArry(cacheKey))) {
                        connection.set(RedisUtils.stringToByteArry(cacheKey),
                                RedisUtils.stringToByteArry(passportId));
                    }
                    return true;
                }
            });
        } catch (Exception e) {
            logger.error("[SMS] service method addUserIdMapPassportId error.{}", e);
        }
        return obj != null ? (Boolean) obj : false;
    }

    @Override
    public boolean addClientIdMapAppConfig(final String clientId, final AppConfig appConfig) {
        Object obj = null;
        try {
            obj = redisTemplate.execute(new RedisCallback() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {

                    connection.set(RedisUtils.stringToByteArry(clientId),
                            RedisUtils.stringToByteArry(JSONUtils.objectToJson(appConfig)));
                    return true;
                }
            });
        } catch (Exception e) {
            logger.error("[SMS] service method addClientIdMapAppConfig error.{}", e);
        }
        return obj != null ? (Boolean) obj : false;
    }

    @Override
    public String getUserIdOrMobileByPassportId(final String passportId, final String keyType) {
        Object obj = null;
        try {
            obj = redisTemplate.execute(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    String strValue = null;
                    Map<byte[], byte[]> mapResult = connection.hGetAll(RedisUtils.stringToByteArry(CACHE_PREFIX_PASSPORT + passportId));
                    if (MapUtils.isNotEmpty(mapResult)) {
                        byte[] value = mapResult.get(RedisUtils.stringToByteArry(keyType));
                        strValue = RedisUtils.byteArryToString(value);
                    }
                    return Strings.isNullOrEmpty(strValue) ? null : strValue;
                }
            });
        } catch (Exception e) {
            logger.error("[SMS] service method getUserIdByPassportId error.{}", e);
        }
        return obj != null ? (String) obj : null;
    }

    @Override
    public String getPassportIdByUserId(final long userId) {
        Object obj = null;
        try {
            obj = redisTemplate.execute(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    String strValue = null;
                    byte[] key = RedisUtils.stringToByteArry(Long.toString(userId));
                    if (connection.exists(key)) {
                        byte[] value = connection.get(key);
                        strValue = RedisUtils.byteArryToString(value);
                    }
                    return Strings.isNullOrEmpty(strValue) ? null : strValue;
                }
            });
        } catch (Exception e) {
            logger.error("[SMS] service method getPassportIdByUserId error.{}", e);
        }
        return obj != null ? (String) obj : null;
    }

    @Override
    public AppConfig getAppConfigByClientId(final int clientId) {
        Object obj = null;
        try {
            obj = redisTemplate.execute(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    AppConfig appConfigResult=null;
                    byte[] value=connection.get(RedisUtils.stringToByteArry(Integer.toString(clientId)));
                    if(value!=null && value.length>0){
                        appConfigResult=JSONUtils.jsonToObject(RedisUtils.byteArryToString(value),AppConfig.class);
                    }
                    return appConfigResult;
                }
            });
        } catch (Exception e) {
            logger.error("[SMS] service method addClientIdMapAppConfig error.{}", e);
        }
        return obj != null ? (AppConfig) obj : null;
    }

    /**
     * 修改用户状态表
     *
     * @param accountAuth
     * @return
     */
    @Override
    public int updateAccountAuth(AccountAuth accountAuth) {
        if (accountAuth != null) {
            int accountRow = accountAuthMapper.updateAccountAuth(accountAuth);
            return accountRow == 0 ? 0 : accountRow;
        }
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * 根据passportId获取手机号码
     *
     * @param passportId
     * @return
     */
    @Override
    public String getMobileByPassportId(String passportId) {
        Account account = null;
        if (!Strings.isNullOrEmpty(passportId)) {
            account = accountMapper.getAccountByPassportId(passportId);
            if (account != null) {
                final Account finalAccount = account;
                taskExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        //写缓存
                        addPassportIdMapUserId(finalAccount.getPassportId(), Long.toString(finalAccount.getId()), finalAccount.getMobile());
                    }
                });
            }
        }
        return account != null ? account.getMobile() : null;
    }

    /**
     * 构造一个新的AccountAuth
     *
     * @param userId
     * @param passportID
     * @param clientId
     * @return
     */
    private AccountAuth newAccountAuth(long userId, String passportID, int clientId) throws SystemException {
        //TODO 读缓存
        AppConfig appConfig = appConfigMapper.getAppConfigByClientId(clientId);
        AccountAuth accountAuth = new AccountAuth();
        if (appConfig != null) {
            int accessTokenExpiresIn = appConfig.getAccessTokenExpiresIn();
            int refreshTokenExpiresIn = appConfig.getRefreshTokenExpiresIn();

            String accessToken;
            String refreshToken;
            try {
                accessToken = TokenGenerator.generatorAccessToken(passportID, clientId, accessTokenExpiresIn);
                refreshToken = TokenGenerator.generatorRefreshToken(passportID, clientId);
            } catch (Exception e) {
                throw new SystemException(e);
            }
            accountAuth.setUserId(userId);
            accountAuth.setClientId(clientId);
            accountAuth.setAccessToken(accessToken);
            accountAuth.setAccessValidTime(TokenGenerator.generatorVaildTime(accessTokenExpiresIn));
            accountAuth.setRefreshToken(refreshToken);
            accountAuth.setRefreshValidTime(TokenGenerator.generatorVaildTime(refreshTokenExpiresIn));
        }

        return accountAuth;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
