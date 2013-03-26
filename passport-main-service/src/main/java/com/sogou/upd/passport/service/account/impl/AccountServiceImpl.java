package com.sogou.upd.passport.service.account.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.exception.SystemException;
import com.sogou.upd.passport.common.parameter.AccountStatusEnum;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.SMSUtil;
import com.sogou.upd.passport.dao.account.AccountAuthMapper;
import com.sogou.upd.passport.dao.account.AccountMapper;
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
    private static final String CACHE_PREFIX_USERID = "PASSPORT:ID_USERID_";     //passport_id与userID映射
    @Inject
    private AccountMapper accountMapper;
    @Inject
    private AccountAuthMapper accountAuthMapper;
    @Inject
    private ShardedJedisPool shardedJedisPool;
    @Inject
    private AppConfigService appConfigService;

    private ShardedJedis jedis;


    @Override
    public boolean checkIsRegisterAccount(Account account) {
        Account accountReturn = accountMapper.checkIsRegisterAccount(account);
        return accountReturn == null ? true : false;
    }

    @Override
    public Map<String, Object> handleSendSms(String account, int appkey) {
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
            String keyCache = CACHE_PREFIX_ACCOUNT_SMSCODE + account + "_" + appkey;
            Map<String, String> map = Maps.newHashMap();
            map.put("smsCode", randomCode);    //初始化验证码
            map.put("mobile", account);        //发送手机号
            map.put("sendTime", Long.toString(System.currentTimeMillis()));   //发送时间

            jedis.hmset(keyCache, map);
            jedis.expire(keyCache, SMSUtil.SMS_VALID);      //有效时长30分钟  ，1800秒

            //todo 内容从缓存中读取
            isSend = SMSUtil.sendSMS(account, "test");
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
    public boolean checkIsExistFromCache(String cacheKey) {
        boolean flag = true;
        try {
            jedis = shardedJedisPool.getResource();
            flag = jedis.exists(CACHE_PREFIX_ACCOUNT_SMSCODE + cacheKey);
        } catch (Exception e) {
            flag = false;
        } finally {
            shardedJedisPool.returnResource(jedis);
        }
        return flag;
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
                            boolean isSend = SMSUtil.sendSMS(account, "test");
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
    public Map<String, Object> handleLogin(String mobile, String passwd, String access_token, int appkey, PostUserProfile postData) {

        //判断用户是否存在
        Account userAccount = getUserAccount(mobile, passwd);
        if (userAccount == null) {
            return ErrorUtil.buildError(ErrorUtil.ERR_CODE_ACCOUNT_LOGINERROR);
        }
        //判读access_token有效性，是否在有效的范围内
        AccountAuth accountAuth = getUserAuthByAccessToken(access_token, appkey);
        if (accountAuth == null) {
            return ErrorUtil.buildError(ErrorUtil.ERR_CODE_ACCOUNT_ACCESSTOKEN_FAILED);
        }
        long curtime = System.currentTimeMillis();
        boolean valid = curtime < accountAuth.getAccessValidTime();
        if (valid) {
            return ErrorUtil.buildSuccess("登录成功", null);
        }

        return ErrorUtil.buildError(ErrorUtil.ERR_CODE_ACCOUNT_LOGINERROR);
    }

    /**
     * 根据AccessToken获取AccountAuth信息
     *
     * @param
     * @return
     */
    public AccountAuth getUserAuthByAccessToken(String access_token, int appkey) {
        Map<String, String> mapResult = Maps.newHashMap();
        mapResult.put("access_token", access_token);
        mapResult.put("appkey", Integer.toString(appkey));
        AccountAuth accountAuth = accountMapper.getUserAuthByAccessToken(mapResult);
        return accountAuth != null ? accountAuth : null;
    }

    /**
     * 根据用户名密码获取用户Account
     *
     * @param
     * @return
     */
    public Account getUserAccount(String mobile, String passwd) {
        Map<String, String> mapResult = Maps.newHashMap();
        mapResult.put("mobile", mobile);
        mapResult.put("passwd", passwd);
        Account accountResult = accountMapper.getUserAccount(mapResult);
        return accountResult != null ? accountResult : null;
    }


    @Override
    public boolean checkSmsInfoFromCache(String account, String smsCode, String appkey) {
        try {
            jedis = shardedJedisPool.getResource();
            String keyCache = CACHE_PREFIX_ACCOUNT_SMSCODE + account + "_" + appkey;
            Map<String, String> mapCacheResult = jedis.hgetAll(keyCache);
            if (MapUtils.isNotEmpty(mapCacheResult)) {
                String smsCodeResult = mapCacheResult.get("smsCode");
                if (StringUtils.isNotBlank(smsCodeResult)) {
                    if (smsCodeResult.equals(smsCode)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            logger.error("[SMS] service method checkSmsInfo error.{}", e);
        } finally {
            shardedJedisPool.returnResource(jedis);
        }
        return false;
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
    public AccountAuth initialAccountAuth(long userId, String passportId, int appKey) throws SystemException {
        AccountAuth accountAuth = newAccountAuth(userId, passportId, appKey);
        long id = accountAuthMapper.saveAccountAuth(accountAuth);
        if (id != 0) {
            accountAuth.setId(id);
            return accountAuth;
        }
        return null;
    }

    @Override
    public boolean addPassportIdMapUserId(String passportId, long userId) {
        try {
            jedis = shardedJedisPool.getResource();

        } catch (Exception e) {
            logger.error("[SMS] service method checkSmsInfo error.{}", e);
        } finally {
            shardedJedisPool.returnResource(jedis);
        }
        return false;
    }

    @Override
    public long getUserIdByPassportId(String passportId) {
        //TODO 读缓存
        return 0;  // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getPassportIdByUserId(long userId) {
        return null;  //todo
    }

    /**
     * 构造一个新的AccountAuth
     *
     * @param userid
     * @param passportID
     * @param appKey
     * @return
     */
    private AccountAuth newAccountAuth(long userid, String passportID, int appKey) throws SystemException {
        AppConfig appConfig = appConfigService.getAppConfig(appKey);
        int accessTokenExpiresin = appConfig.getAccessTokenExpiresin();
        int refreshTokenExpiresin = appConfig.getRefreshTokenExpiresin();

        String accessToken;
        String refreshToken;
        try {
            accessToken = TokenGenerator.generatorAccessToken(passportID, appKey, accessTokenExpiresin);
            refreshToken = TokenGenerator.generatorRefreshToken(passportID, appKey);
        } catch (Exception e) {
            throw new SystemException(e);
        }
        AccountAuth accountAuth = new AccountAuth();
        accountAuth.setUserId(userid);
        accountAuth.setAppkey(appKey);
        accountAuth.setAccessToken(accessToken);
        accountAuth.setAccessValidTime(TokenGenerator.generatorVaildTime(accessTokenExpiresin));
        accountAuth.setRefreshToken(refreshToken);
        accountAuth.setRefreshValidTime(TokenGenerator.generatorVaildTime(refreshTokenExpiresin));
        return accountAuth;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
