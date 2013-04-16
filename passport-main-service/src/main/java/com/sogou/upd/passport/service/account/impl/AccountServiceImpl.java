package com.sogou.upd.passport.service.account.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.exception.SystemException;
import com.sogou.upd.passport.common.parameter.AccountStatusEnum;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.common.utils.SMSUtil;
import com.sogou.upd.passport.dao.account.AccountAuthMapper;
import com.sogou.upd.passport.dao.account.AccountMapper;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.generator.PassportIDGenerator;
import com.sogou.upd.passport.service.account.generator.PwdGenerator;
import com.sogou.upd.passport.service.app.AppConfigService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

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
    private static final String CACHE_PREFIX_ACCOUNT_SMSCODE = CacheConstant.CACHE_PREFIX_MOBILE_SMSCODE;   //account与smscode映射
    private static final String CACHE_PREFIX_ACCOUNT_SENDNUM = CacheConstant.CACHE_PREFIX_MOBILE_SENDNUM;
    private static final String CACHE_PREFIX_PASSPORTID = CacheConstant.CACHE_PREFIX_PASSPORTID_USERID;     //passport_id与userID映射
    @Inject
    private AccountMapper accountMapper;
    @Inject
    private AccountAuthMapper accountAuthMapper;
    @Inject
    private AppConfigService appConfigService;
    @Inject
    private TaskExecutor taskExecutor;
    @Inject
    private StringRedisTemplate redisTemplate;
    @Inject
    private RedisUtils redisUtils;

    @Override
    public Result handleSendSms(final String mobile, final int clientId) {
        Result result = null;
        try {
            String cacheKey = CACHE_PREFIX_ACCOUNT_SENDNUM + mobile;

            if (!RedisUtils.checkKeyIsExist(cacheKey)) {
                boolean flag = redisUtils.hPutIfAbsent(cacheKey, "sendNum", "1");
                if (flag) {
                    redisUtils.expire(cacheKey, SMSUtil.SMS_ONEDAY);
                }
            } else {
                //如果存在，判断是否已经超出日发送最高限额   (比如30分钟后失效了，再次获取验证码 需要和此用户当天发送的总的条数对比)
                Map<String, String> mapCacheSendNumResult = redisUtils.hGetAll(cacheKey);
                if (MapUtils.isNotEmpty(mapCacheSendNumResult)) {
                    int sendNum = Integer.parseInt(mapCacheSendNumResult.get("sendNum"));
                    if (sendNum < SMSUtil.MAX_SMS_COUNT_ONEDAY) {     //每日最多发送短信验证码条数
                        redisUtils.hIncrBy(cacheKey, "sendNum");
                    } else {
                        result = Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_CANTSENTSMS);
                        return result;
                    }
                }
            }
            //生成随机数
            String randomCode = RandomStringUtils.randomNumeric(5);
            //写入缓存
            cacheKey = CACHE_PREFIX_ACCOUNT_SMSCODE + mobile + "_" + clientId;
            //初始化缓存映射
            Map<String, String> mapData = Maps.newHashMap();
            mapData.put("smsCode", randomCode);    //初始化验证码
            mapData.put("mobile", mobile);        //发送手机号
            mapData.put("sendTime", Long.toString(System.currentTimeMillis()));   //发送时间

            redisUtils.hPutAll(cacheKey, mapData);

            //设置失效时间 30分钟  ，1800秒
            redisUtils.expire(cacheKey, SMSUtil.SMS_VALID);

            //读取短信内容
            String smsText = getSmsText(clientId, randomCode);
            boolean isSend = false;
            if (!Strings.isNullOrEmpty(smsText)) {
                isSend = SMSUtil.sendSMS(mobile, smsText);
                if (isSend) {
                    result = Result.buildSuccess("获取验证码成功", "smscode", randomCode);
                    return result;
                }
            } else {
                result = Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE_SEND);
                return result;
            }
        } catch (Exception e) {
            result=Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE_SEND) ;
            logger.error("[SMS] service method handleSendSms error.{}", e);
        }
        return result;
    }

    /*
     * 获取sms信息
     */
    public String getSmsText(final int clientId, String smsCode) {
        //缓存中根据clientId获取AppConfig
        AppConfig appConfig = appConfigService.getAppConfigByClientId(clientId);
        if (appConfig != null) {
            return String.format(appConfig.getSmsText(), smsCode);
        }
        return null;
    }

    @Override
    public boolean checkCacheKeyIsExist(String cacheKey) {

        cacheKey = CACHE_PREFIX_ACCOUNT_SMSCODE + cacheKey;
        boolean flag = false;
        try {
            flag = RedisUtils.checkKeyIsExist(cacheKey);
        } catch (Exception e) {
            logger.error("[SMS] service method checkCacheKeyIsExist error.{}", e);
        }
        return flag;
    }

    @Override
    public Result updateSmsCacheInfoByKeyAndClientId(String cacheKey, final int clientId) {
        Result result = null;
        try {
            cacheKey = CACHE_PREFIX_ACCOUNT_SMSCODE + cacheKey;
            BoundHashOperations hashOperations = redisTemplate.boundHashOps(cacheKey);

            Map<String, String> mapCacheResult = hashOperations.entries();

            if (MapUtils.isNotEmpty(mapCacheResult)) {
                //获取缓存数据
                long sendTime = Long.parseLong(mapCacheResult.get("sendTime"));
                String smsCode = mapCacheResult.get("smsCode");
                String mobile = mapCacheResult.get("mobile");
                //获取当天发送次数
                String cacheKeySendNum = CACHE_PREFIX_ACCOUNT_SENDNUM + mobile;
                BoundHashOperations keySendNumCacheOperations = redisTemplate.boundHashOps(cacheKeySendNum);

                Map<String, String> mapCacheSendNumResult = keySendNumCacheOperations.entries();
                if (MapUtils.isNotEmpty(mapCacheSendNumResult)) {
                    int sendNum = Integer.parseInt(mapCacheSendNumResult.get("sendNum"));
                    long curtime = System.currentTimeMillis();
                    boolean valid = curtime >= (sendTime + SMSUtil.SEND_SMS_INTERVAL); // 1分钟只能发1条短信
                    if (valid) {
                        if (sendNum < SMSUtil.MAX_SMS_COUNT_ONEDAY) {     //每日最多发送短信验证码条数
                            keySendNumCacheOperations.increment("sendNum", 1);
                            hashOperations.put("sendTime", String.valueOf(System.currentTimeMillis()));
                            //读取短信内容
                            String smsText = getSmsText(clientId, smsCode);
                            if (!Strings.isNullOrEmpty(smsText)) {
                                boolean isSend = SMSUtil.sendSMS(mobile, smsText);
                                if (isSend) {
                                    //30分钟之内返回原先验证码
                                    result = Result.buildSuccess("获取验证码成功", "smscode", smsCode);
                                    return result;
                                }
                            } else {
                                result = Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE_SEND);
                                return result;
                            }
                        } else {
                            result = Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_CANTSENTSMS);
                            return result;
                        }
                    } else {
                        result = Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_MINUTELIMIT);
                        return result;
                    }
                }
            } else {
                result = Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE_SEND);
                return result;
            }

        } catch (Exception e) {
            result = Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE_SEND);
            logger.error("[SMS] service method updateSmsCacheInfoByKeyAndClientId error.{}", e);
        }

        return result;
    }

    @Override
    public boolean checkSmsInfoFromCache(final String account, final String smsCode, final String clientId) {
        Object obj = null;
        try {
            obj = redisTemplate.execute(new RedisCallback() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    String keyCache = CACHE_PREFIX_ACCOUNT_SMSCODE + account + "_" + clientId;
                    String strValue;
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
    public Account initialAccount(String username, String password, String ip, int provider) throws SystemException {
        Account account = new Account();
        account.setPassportId(PassportIDGenerator.generator(username, provider));
        String passwordSign = null;
        if (!Strings.isNullOrEmpty(password)) {
            passwordSign = PwdGenerator.generatorPwdSign(password);
        }
        account.setPasswd(passwordSign);
        account.setRegTime(new Date());
        account.setRegIp(ip);
        account.setAccountType(provider);
        account.setStatus(AccountStatusEnum.REGULAR.getValue());
        account.setVersion(Account.NEW_ACCOUNT_VERSION);
        String mobile = null;
        if (AccountTypeEnum.isPhone(username, provider)) {
            mobile = username;
        }
        account.setMobile(mobile);
        long id = accountMapper.saveAccount(account);
        if (id != 0) {
            return account;
        }
        return null;
    }

    @Override
    public Account initialConnectAccount(String connectUid, String ip, int provider) throws SystemException {
        return initialAccount(connectUid, null, ip, provider);
    }

    @Override
    public Account verifyUserPwdVaild(String username, String password) {
        if (!Strings.isNullOrEmpty(username) && !Strings.isNullOrEmpty(password)) {
            String pwdSign;
            try {
                pwdSign = PwdGenerator.generatorPwdSign(password);
            } catch (SystemException e) {
                logger.error("username:{} passport:{} sign fail", username, password);
                return null;
            }
            Account userAccount = getAccountByUserName(username);
            if (userAccount != null && pwdSign.equals(userAccount.getPasswd())) {
                return userAccount;
            }
        }
        return null;
    }

    @Override
    public Account getAccountByUserName(String username) {
        // TODO 加缓存,两个方法可以合并，采用动态查询sql,但合并的话缓存写起来不太方便
        Account account;
        if (PhoneUtil.verifyPhoneNumberFormat(username)) {
            account = accountMapper.getAccountByMobile(username);
        } else {
            account = accountMapper.getAccountByPassportId(username);
        }
        return account;
    }

    @Override
    public Account verifyAccountVaild(long userId) {
        Account account = accountMapper.getAccountByUserId(userId);
        if (account.isNormalAccount()) {
            return account;
        }
        return null;
    }
    /**
     * 根据主键ID获取passportId
     *
     * @param passportId
     * @return
     */
    @Override
    public long getUserIdByPassportId(String passportId) {

        long userIdResult = 0;
        try {
            String cacheKey = CACHE_PREFIX_PASSPORTID + passportId;
            String userId = getFromCache(cacheKey);
            if (Strings.isNullOrEmpty(userId)) {
                //读取数据库
                userIdResult = getUserIdByPassportId(passportId);
                if (userIdResult != 0) {
                    addPassportIdMapUserIdToCache(passportId, userId);
                }
            } else {
                userIdResult = Long.parseLong(userId);
            }
        } catch (Exception e) {
            logger.error("[SMS] service method getUserIdByPassportId error.{}", e);
        }
        return userIdResult;
    }

    @Override
    public boolean addPassportIdMapUserIdToCache(final String passportId, final String userId) {
        boolean flag = true;
        try {
            String cacheKey = CACHE_PREFIX_PASSPORTID + passportId;
            redisUtils.setNx(cacheKey,userId);
        } catch (Exception e) {
            flag = false;
            logger.error("[SMS] service method addPassportIdMapUserIdToCache error.{}", e);
        }
        return flag;
    }

    @Override
    public boolean deleteSmsCache(final String mobile, final String clientId) {
        boolean flag=true;
        try {
            redisUtils.delete(CACHE_PREFIX_ACCOUNT_SMSCODE + mobile + "_" + clientId);
            redisUtils.delete(CACHE_PREFIX_ACCOUNT_SENDNUM + mobile);
        } catch (Exception e) {
            flag=false;
            logger.error("[SMS] service method deleteSmsCache error.{}", e);
        }
        return flag;
    }

    @Override
    public Account resetPassword(String mobile, String password) throws SystemException {
        Account account = accountMapper.getAccountByMobile(mobile);
        int row = 0;
        if (account != null) {
            Account accountResult = new Account();
            accountResult.setMobile(mobile);
            accountResult.setPasswd(PwdGenerator.generatorPwdSign(password));
            accountResult.setId(account.getId());
            row = accountMapper.updateAccount(accountResult);
        }
        return row == 0 ? null : account;
    }




    /*
     * 根据key从缓存中获取value
     */
    public String getFromCache(final String key) throws Exception {
        String valResult = null;
        try {
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            valResult = valueOperations.get(key);
        } catch (Exception e) {
            logger.error("[SMS] service method getFromCache error.{}", e);
        }
        return valResult;
    }

}
