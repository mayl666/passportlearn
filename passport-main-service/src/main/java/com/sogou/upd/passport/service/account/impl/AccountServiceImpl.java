package com.sogou.upd.passport.service.account.impl;

import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.parameter.AccountStatusEnum;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.SMSUtil;
import com.sogou.upd.passport.dao.account.AccountMapper;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountAuth;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.generator.PassportIDGenerator;
import com.sogou.upd.passport.service.account.generator.TokenGenerator;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
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
    private static final String CACHE_PREFIX_ACCOUNT_SMSCODE = "PASSPORT:ACCOUNT_SMSCODE_";
    private static final String CACHE_PREFIX_ACCOUNT_SENDNUM = "PASSPORT:ACCOUNT_SENDNUM_";
    @Inject
    private AccountMapper accountMapper;
    @Inject
    private ShardedJedisPool shardedJedisPool;

    private ShardedJedis jedis;


    @Override
    public boolean checkIsRegisterAccount(Account account) {
        Account a = accountMapper.checkIsRegisterAccount(account);
        return a == null ? true : false;
    }

    @Override
    public Map<String, Object> handleSendSms(String account, int appkey) {
        Map<String, Object> mapResult = Maps.newHashMap();
        boolean isSend = true;
        try {
            //生成随机数
            String randomCode = RandomStringUtils.randomNumeric(5);
            //写入缓存
            String keyCache = CACHE_PREFIX_ACCOUNT_SMSCODE + account + "_" + appkey;
            Map<String, String> map = Maps.newHashMap();
            map.put("smsCode", randomCode);    //初始化验证码
            map.put("mobile", account);        //发送手机号
            map.put("sendTime", Long.toString(System.currentTimeMillis()));   //发送时间

            jedis = shardedJedisPool.getResource();
            jedis.hmset(keyCache, map);
            jedis.expire(keyCache, SMSUtil.SMS_VALID);      //有效时长30分钟  ，1800秒

            //设置每日最多发送短信验证码条数
            String keySendNumCache = CACHE_PREFIX_ACCOUNT_SENDNUM + account;
            jedis.hset(keySendNumCache, "sendNum", "1");
            jedis.expire(keySendNumCache, SMSUtil.SMS_ONEDAY);
            //todo 内容从缓存中读取
            isSend = SMSUtil.sendSMS(account, "test");
            if (isSend) {
                mapResult.put("smscode", randomCode);
                return mapResult;
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
                                return mapResult;
                            } else {
                                return ErrorUtil.buildError(ErrorUtil.ERR_CODE_ACCOUNT_CANTSENTSMS, "短信发送已达今天的最高上限" + SMSUtil.MAX_SMS_COUNT_ONEDAY + "条");
                            }
                        } else {
                            return ErrorUtil.buildError(ErrorUtil.ERR_CODE_ACCOUNT_MINUTELIMIT, "1分钟只能发送一条短信");
                        }
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
    public boolean checkSmsInfo(String account, String smsCode, String appkey) {
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
    public Account initialAccount(String account, String pwd, String ip, int provider) {
        Account a = new Account();
        a.setPassportId(PassportIDGenerator.generator(account, provider));
        a.setPasswd(pwd);
        a.setRegTime(new Date());
        a.setRegIp(ip);
        a.setAccountType(provider);
        a.setStatus(AccountStatusEnum.REGULAR.getValue());
        a.setVersion(Account.NEW_ACCOUNT_VERSION);
        a.setMobile(account);
        long id = accountMapper.userRegister(a);
        if(id != 0){
            a.setId(id);
            return a;
        }
        return null;
    }

    @Override
    public Account initialConnectAccount(String account, String ip, int provider) {
        return initialAccount(account, null, ip, provider);
    }

    @Override
    public AccountAuth initialAccountAuth(Account account, int appkey) {
        long userid = account.getId();
        String passportID = account.getPassportId();
        TokenGenerator generator = new TokenGenerator();
        long vaildTime = generator.generatorVaildTime(appkey);
        String accessToken;
        String refreshToken;
        try {
            accessToken = generator.generatorAccessToken(passportID, appkey);
            refreshToken = generator.generatorRefreshToken(passportID, appkey);
        } catch (Exception e) {
            // TODO record error log
            return null;
        }

        AccountAuth accountAuth = new AccountAuth();
        accountAuth.setUserId(userid);
        accountAuth.setAppkey(appkey);
        accountAuth.setAccessToken(accessToken);
        accountAuth.setValidTime(vaildTime);
        accountAuth.setRefreshToken(refreshToken);

        // TODO DAO insert AccountAuth table
        return accountAuth;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
