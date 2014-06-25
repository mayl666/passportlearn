package com.sogou.upd.passport.service.account.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.ActiveEmail;
import com.sogou.upd.passport.common.parameter.AccountStatusEnum;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.parameter.PasswordTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.*;
import com.sogou.upd.passport.dao.account.AccountDAO;
import com.sogou.upd.passport.dao.account.MobilePassportMappingDAO;
import com.sogou.upd.passport.dao.account.UniqNamePassportMappingDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.AccountHelper;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.generator.PassportIDGenerator;
import com.sogou.upd.passport.service.account.generator.PwdGenerator;
import org.apache.commons.codec.digest.DigestUtils;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * User: mayan Date: 13-3-22 Time: 下午3:38 To change this template use File | Settings | File Templates.
 */
@Service
public class AccountServiceImpl implements AccountService {

    private static final int expire = 30 * DateAndNumTimesConstant.ONE_DAY_INSECONDS;
    private static final String CACHE_PREFIX_PASSPORT_ACCOUNT = CacheConstant.CACHE_PREFIX_PASSPORT_ACCOUNT;
    private static final String CACHE_PREFIX_PASSPORTID_IPBLACKLIST = CacheConstant.CACHE_PREFIX_PASSPORTID_IPBLACKLIST;
    private static final String CACHE_PREFIX_PASSPORTID_ACTIVEMAILTOKEN = CacheConstant.CACHE_PREFIX_PASSPORTID_ACTIVEMAILTOKEN;
    private static final String CACHE_PREFIX_UUID_CAPTCHA = CacheConstant.CACHE_PREFIX_UUID_CAPTCHA;
    private static final String CACHE_PREFIX_PASSPORTID_RESETPWDNUM = CacheConstant.CACHE_PREFIX_PASSPORTID_RESETPWDNUM;
    private static final String CACHE_PREFIX_NICKNAME_PASSPORTID = CacheConstant.CACHE_PREFIX_NICKNAME_PASSPORTID;


    private static final String PASSPORT_ACTIVE_EMAIL_URL = "http://account.sogou.com/web/activemail?";

    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);
    @Autowired
    private AccountDAO accountDAO;
    @Autowired
    private MobilePassportMappingDAO mobilePassportMappingDAO;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private DBShardRedisUtils dbShardRedisUtils;
    @Autowired
    private MailUtils mailUtils;
    @Autowired
    private CaptchaUtils captchaUtils;
    @Autowired
    private UniqNamePassportMappingDAO uniqNamePassportMappingDAO;

    @Override
    public Account initialWebAccount(String username, String ip) throws ServiceException {
        Account account = null;
        String cacheKey = null;
        try {
            cacheKey = buildAccountKey(username);
            account = dbShardRedisUtils.getObject(cacheKey, Account.class);
            if (account != null) {
                account.setFlag(AccountStatusEnum.REGULAR.getValue());
                long id = accountDAO.insertAccount(username, account);
                if (id != 0) {
                    //更新缓存，成为正式账户
                    dbShardRedisUtils.setObjectWithinSeconds(cacheKey, account, DateAndNumTimesConstant.ONE_MONTH);
                    //更新黑名单缓存
                    cacheKey = buildAccountBlackCacheKey(ip);
                    redisUtils.increment(cacheKey);
                    //设置cookie
                    setCookie();
                    return account;
                }
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        } finally {
            //删除激活
            cacheKey = buildCacheKey(username);
            redisUtils.delete(cacheKey);
        }
        return null;
    }

    @Profiled(el = true, logger = "dbTimingLogger", tag = "service_initialAccount", timeThreshold = 20, normalAndSlowSuffixesEnabled = true)
    @Override
    public Account initialAccount(String username, String password, boolean needMD5, String ip, int provider) throws ServiceException {
        Account account = new Account();
        String passportId = PassportIDGenerator.generator(username, provider);
        account.setPassportId(passportId);
        String passwordSign;
        try {
            if (!Strings.isNullOrEmpty(password) && !AccountTypeEnum.isConnect(provider)) {
                passwordSign = PwdGenerator.generatorStoredPwd(password, needMD5);
                account.setPassword(passwordSign);
            }
            account.setRegTime(new Date());
            account.setRegIp(ip);
            account.setAccountType(provider);
            account.setFlag(AccountStatusEnum.REGULAR.getValue());
            if (AccountTypeEnum.isConnect(provider) || AccountTypeEnum.isSOHU(provider)) {
                //对于第三方账号和sohu域账号来讲，无密码  搜狗账号迁移完成后，需要增加一个值表示无密码
                account.setPasswordtype(PasswordTypeEnum.NOPASSWORD.getValue());
            } else {
                //其它新注册的搜狗账号密码类型都为2，即需要加盐
                account.setPasswordtype(PasswordTypeEnum.CRYPT.getValue());
            }
            String mobile = null;
            if (AccountTypeEnum.isPhone(username, provider)) {
                mobile = username;
            }
            account.setMobile(mobile);
            long id = 0;
            if (AccountTypeEnum.isConnect(provider)) { //第三方使用插入或更新，之前第三方迁移出过一次bug，修复的
                id = accountDAO.insertOrUpdateAccount(passportId, account);
            } else {
                id = accountDAO.insertAccount(passportId, account);
            }
            if (id != 0) {
                //手机注册时，写mobile与passportId映射表
                if (PhoneUtil.verifyPhoneNumberFormat(passportId.substring(0, passportId.indexOf("@")))) {
                    int row = mobilePassportMappingDAO.insertMobilePassportMapping(mobile, passportId);
                    if (row == 0) {
                        return null;
                    }
                }
                String cacheKey = buildAccountKey(passportId);
                dbShardRedisUtils.setObjectWithinSeconds(cacheKey, account, DateAndNumTimesConstant.ONE_MONTH);
                return account;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return null;
    }

    @Override
    public Account initialConnectAccount(String passportId, String ip, int provider) throws ServiceException {
        return initialAccount(passportId, null, false, ip, provider);
    }

    /**
     * 非第三方账号迁移，新写 初始化 Account 方法
     *
     * @param account
     * @return
     * @throws ServiceException
     */
    @Profiled(el = true, logger = "dbTimingLogger", tag = "service_initAccount", timeThreshold = 20, normalAndSlowSuffixesEnabled = true)
    @Override
    public boolean initAccount(Account account) throws ServiceException {
        boolean initSuccess = false;
        try {
            long id = accountDAO.insertOrUpdateAccount(account.getPassportId(), account);
            if (id != 0) {
                initSuccess = true;
                String cacheKey = buildAccountKey(account.getPassportId());
                dbShardRedisUtils.setObjectWithinSeconds(cacheKey, account, DateAndNumTimesConstant.ONE_MONTH);
                return initSuccess;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return initSuccess;
    }

    @Profiled(el = true, logger = "dbTimingLogger", tag = "service_queryAccountByPassportId", timeThreshold = 20, normalAndSlowSuffixesEnabled = true)
    @Override
    public Account queryAccountByPassportId(String passportId) throws ServiceException {
        Account account;
        try {
            String cacheKey = buildAccountKey(passportId);

            account = dbShardRedisUtils.getObject(cacheKey, Account.class);
            if (account == null) {
                account = accountDAO.getAccountByPassportId(passportId);
                if (account != null) {
                    dbShardRedisUtils.setObjectWithinSeconds(cacheKey, account, DateAndNumTimesConstant.ONE_MONTH);
                }
            }
        } catch (Exception e) {
            throw new ServiceException();
        }
        return account;
    }

    @Override
    public Result verifyUserPwdVaild(String passportId, String password, boolean needMD5) throws ServiceException {
        Result result = new APIResultSupport(false);
        Account userAccount;
        try {
            userAccount = queryAccountByPassportId(passportId);
        } catch (ServiceException e) {
            throw e;
        }
        try {
            if (userAccount == null) {
                result.setCode(ErrorUtil.INVALID_ACCOUNT);
                return result;
            }
            if (AccountHelper.isDisabledAccount(userAccount)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NO_ACTIVED_FAILED);
                return result;
            }
            if (AccountHelper.isKilledAccount(userAccount)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_KILLED);
                return result;
            }
            result = verifyUserPwdValidByPasswordType(userAccount, password, needMD5);
            return result;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Result verifyUserPwdValidByPasswordType(Account account, String password, Boolean needMD5) {
        Result result = new APIResultSupport(false);
        String passwordType = String.valueOf(account.getPasswordtype());
        String storedPwd = account.getPassword();
        boolean pwdIsTrue = false;
        try {
            switch (Integer.parseInt(passwordType)) {
                case 0:   //原密码
                    pwdIsTrue = verifyPwdWithOriginal(password, storedPwd);
                    break;
                case 1:   //MD5
                    pwdIsTrue = verifyPwdWithMD5(password, storedPwd);
                    break;
                case 2:   //Crypt(password,salt)
                    pwdIsTrue = PwdGenerator.verify(password, needMD5, storedPwd);
                    break;
            }
            if (pwdIsTrue) {
                result.setSuccess(true);
                result.setMessage("操作成功");
                result.setDefaultModel("userid", account.getPassportId());
                return result;
            } else {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_PWD_ERROR);
                return result;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    private boolean verifyPwdWithOriginal(String password, String storedPwd) {
        if (storedPwd.equals(password)) {
            return true;
        }
        return false;
    }

    private boolean verifyPwdWithMD5(String password, String storedPwd) {
        String pwdMD5 = DigestUtils.md5Hex(password.getBytes());
        if (storedPwd.equals(pwdMD5)) {
            return true;
        }
        return false;
    }

    @Override
    public Account queryNormalAccount(String passportId) throws ServiceException {
        Account account = queryAccountByPassportId(passportId);
        if (account != null && AccountHelper.isNormalAccount(account)) {
            return account;
        }
        return null;
    }

    private String buildResetPwdCacheKey(String passportId) {
        return CacheConstant.CACHE_PREFIX_PASSPORTID_RESETPWDNUM + passportId + "_" +
                DateUtil.format(new Date(), DateUtil.DATE_FMT_0);
    }

    @Override
    public boolean deleteAccountCacheByPassportId(String passportId) throws ServiceException {
        try {
//            int row = accountDAO.deleteAccountByPassportId(passportId);
//            if (row != 0) {
            String cacheKey = buildAccountKey(passportId);
            dbShardRedisUtils.delete(cacheKey);
            return true;
//            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public void setLimitResetPwd(String passportId) throws ServiceException {
        // 设置密码修改次数限制
        String resetCacheKey = buildResetPwdCacheKey(passportId);
        try {
            if (redisUtils.checkKeyIsExist(resetCacheKey)) {
                redisUtils.increment(resetCacheKey);
            } else {
                redisUtils.setWithinSeconds(resetCacheKey, "1", DateAndNumTimesConstant.TIME_ONEDAY);
            }
        } catch (Exception e) {
            redisUtils.delete(resetCacheKey);// DO NOTHING 不作任何处理？
        }
    }

    @Override
    public boolean checkLimitResetPwd(String passportId) throws ServiceException {
        try {
            String cacheKey = buildResetPwdCacheKey(passportId);
            String checkNumStr = redisUtils.get(cacheKey);
            if (!Strings.isNullOrEmpty(checkNumStr)) {
                int checkNum = Integer.parseInt(checkNumStr);
                if (checkNum > DateAndNumTimesConstant.RESETPWD_NUM) {
                    // 当日验证码输入错误次数不超过上限
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            // throw new ServiceException(e);
            return true;
        }
    }

    @Override
    public boolean resetPassword(Account account, String password, boolean needMD5) throws ServiceException {
        try {
            String passportId = account.getPassportId();
            String passwdSign = PwdGenerator.generatorStoredPwd(password, needMD5);
            int row = accountDAO.updatePassword(passwdSign, passportId);
            if (row != 0) {
                String cacheKey = buildAccountKey(passportId);
                account.setPassword(passwdSign);
                dbShardRedisUtils.setObjectWithinSeconds(cacheKey, account, DateAndNumTimesConstant.ONE_MONTH);

                return true;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return false;
    }

    private String buildAccountBlackCacheKey(String ip) {
        return CacheConstant.CACHE_PREFIX_PASSPORTID_IPBLACKLIST + ip;
    }

    @Override
    public boolean isInAccountBlackListByIp(String passportId, String ip) throws ServiceException {
        boolean flag = true;
        long ipCount = 0;
        try {
            String cacheKey = buildAccountBlackCacheKey(ip);
            String ipValue = redisUtils.get(cacheKey);
            if (Strings.isNullOrEmpty(ipValue)) {
                redisUtils.setWithinSeconds(cacheKey, "1", DateAndNumTimesConstant.TIME_ONEDAY);
            } else {
                ipCount = Long.parseLong(ipValue);
                //判断ip注册限制次数（一天20次）
                if (ipCount < DateAndNumTimesConstant.IP_LIMITED) {
                    redisUtils.increment(cacheKey);
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    @Override
    public boolean sendActiveEmail(String username, String passpord, int clientId, String ip, String ru) throws ServiceException {
        boolean flag = true;
        try {
            String code = UUID.randomUUID().toString().replaceAll("-", "");
            String token = Coder.encryptMD5(username + clientId + code);
            String params =
                    PASSPORT_ACTIVE_EMAIL_URL + "passport_id=" + username +
                            "&client_id=" + clientId +
                            "&token=" + token;
            if (!Strings.isNullOrEmpty(ru)) {
                params = params + "&ru=" + Coder.encodeUTF8(ru);
            }
            String activeUrl = CommonConstant.PASSPORT_ACTIVE_EMAIL_URL + params;

            String cacheKey = buildCacheKey(username);
            Map<String, String> mapParam = new HashMap<>();
            //设置连接失效时间
            mapParam.put("token", token);
            //设置ru
            mapParam.put("ru", ru);

            //发送邮件
            ActiveEmail activeEmail = new ActiveEmail();
            activeEmail.setActiveUrl(activeUrl);

            //模版中参数替换
            Map<String, Object> map = Maps.newHashMap();
            map.put("activeUrl", activeUrl);
            map.put("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            activeEmail.setMap(map);

            activeEmail.setTemplateFile("activemail.vm");
            activeEmail.setSubject("激活您的搜狗通行证帐户");
            activeEmail.setCategory("register");
            activeEmail.setToEmail(username);

            mailUtils.sendEmail(activeEmail);

            //如果重新发送激活邮件，password是为空的，说明不是注册，否则需要临时注册到缓存
            if (!Strings.isNullOrEmpty(passpord)) {
                initialAccountToCache(username, passpord, ip);
            }
            redisUtils.hPutAll(cacheKey, mapParam);
            redisUtils.expire(cacheKey, DateAndNumTimesConstant.TIME_TWODAY);
            //临时注册到缓存
            initialAccountToCache(username, passpord, ip);
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    private String buildCacheKey(String username) {
        return CacheConstant.CACHE_PREFIX_PASSPORTID_ACTIVEMAILTOKEN + username;
    }

    @Override
    public Map<String, String> getActiveInfo(String username) {
        String cacheKey = buildCacheKey(username);
        Map<String, String> mapParam = redisUtils.hGetAll(cacheKey);
        return mapParam;

    }

    @Override
    public boolean activeEmail(String username, String token, int clientId) throws ServiceException {
        try {
            String cacheKey = buildCacheKey(username);
            String tokenCache;
            if (redisUtils.checkKeyIsExist(cacheKey)) {
                Map<String, String> mapParam = getActiveInfo(username);
                if (!mapParam.isEmpty()) {
                    tokenCache = mapParam.get("token");
                    if (tokenCache.equals(token)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return false;
    }

    @Override
    public boolean setCookie() throws Exception {
//    ServletUtil.setCookie();
        return false;
    }

    @Override
    public Map<String, Object> getCaptchaCode(String token) throws ServiceException {
        Map<String, Object> map;
        try {
            if (Strings.isNullOrEmpty(token)) {
                token = UUID.randomUUID().toString().replaceAll("-", "");
            }
            String cacheKey = buildCaptchaCacheKey(token);
            //生成验证码
            map = captchaUtils.getRandCode();

            if (map != null && map.size() > 0) {

                String captchaCode = (String) map.get("captcha");
                map.put("token", token);

                redisUtils.setWithinSeconds(cacheKey, captchaCode, DateAndNumTimesConstant.CAPTCHA_INTERVAL);
                /*redisUtils.set(cacheKey, captchaCode);
                redisUtils.expire(cacheKey, DateAndNumTimesConstant.CAPTCHA_INTERVAL);*/
            } else {
                map = Maps.newHashMap();
            }

        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return map;
    }

    private String buildCaptchaCacheKey(String token) {
        return CacheConstant.CACHE_PREFIX_UUID_CAPTCHA + token;
    }


    @Override
    public boolean checkCaptchaCodeIsVaild(String token, String captchaCode) throws ServiceException {
        try {
            String cacheKey = buildCaptchaCacheKey(token);
            String captchaCodeCache = redisUtils.get(cacheKey);
            if (!Strings.isNullOrEmpty(captchaCodeCache) && captchaCodeCache.equalsIgnoreCase(captchaCode)) {
                redisUtils.delete(cacheKey);
                return true;
            }
            return false;
        } catch (Exception e) {
            // throw new ServiceException(e);
            return false;
        }
    }

    @Override
    public boolean modifyMobile(Account account, String newMobile) throws ServiceException {
        try {
            String passportId = account.getPassportId();
            int row = accountDAO.updateMobile(newMobile, passportId);
            if (row != 0) {
                String cacheKey = buildAccountKey(passportId);
                account.setMobile(newMobile);
                dbShardRedisUtils.setObjectWithinSeconds(cacheKey, account, DateAndNumTimesConstant.ONE_MONTH);
                return true;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return false;
    }

    @Override
    public boolean updateState(Account account, int newState) throws ServiceException {
        try {
            String passportId = account.getPassportId();
            int row = accountDAO.updateState(newState, passportId);
            if (row > 0) {
                String cacheKey = buildAccountKey(passportId);
                account.setFlag(newState);
                dbShardRedisUtils.setObjectWithinSeconds(cacheKey, account, DateAndNumTimesConstant.ONE_MONTH);
                return true;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return false;
    }

    /**
     * 调SOHU接口注册前，未激活的外域邮箱先写SG缓存，保存时间为两天，再调
     *
     * @param username
     * @param password
     * @param ip
     * @throws ServiceException
     */
//    @Override
//    public void initialMailToCache(String username, String password, String ip) throws ServiceException {
//        Account account = initialAccountToCache(username, password, ip);
//        String cacheKey = buildAccountKey(username);
//        dbShardRedisUtils.setWithinSeconds(cacheKey, account, DateAndNumTimesConstant.TIME_TWODAY);
//    }

    /*
     * 外域邮箱注册
     */
    public Account initialAccountToCache(String username, String password, String ip) throws ServiceException {
        int provider = AccountTypeEnum.EMAIL.getValue();
        Account account = new Account();
        String passportId = PassportIDGenerator.generator(username, provider);
        account.setPassportId(passportId);
        String passwordSign = null;
        try {
            if (!Strings.isNullOrEmpty(password)) {
                passwordSign = PwdGenerator.generatorStoredPwd(password, true);
            }
            account.setPassword(passwordSign);
            account.setRegTime(new Date());
            account.setAccountType(provider);
            account.setFlag(AccountStatusEnum.DISABLED.getValue());
            account.setPasswordtype(PasswordTypeEnum.CRYPT.getValue());
            account.setRegIp(ip);

            String cacheKey = buildAccountKey(username);
            dbShardRedisUtils.setObjectWithinSeconds(cacheKey, account, DateAndNumTimesConstant.TIME_TWODAY);

        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return account;
    }

    protected String buildAccountKey(String passportId) {
        return CacheConstant.CACHE_PREFIX_PASSPORT_ACCOUNT + passportId;
    }


    @Override
    public boolean checkCaptchaCode(String token, String captchaCode) throws Exception {
        try {
            //校验验证码
            if (!checkCaptchaCodeIsVaild(token, captchaCode)) {
                return false;
            }
        } catch (ServiceException e) {
            logger.error("checkCaptchaCode fail", e);
            return false;
        }
        return true;
    }


    @Override
    public String checkUniqName(String uniqname) throws ServiceException {
        String passportId = null;
        try {
            String cacheKey = buildUniqnameCacheKey(uniqname);
            passportId = dbShardRedisUtils.get(cacheKey);
            if (Strings.isNullOrEmpty(passportId)) {
                passportId = uniqNamePassportMappingDAO.getPassportIdByUniqName(uniqname);
                if (!Strings.isNullOrEmpty(passportId)) {
                    dbShardRedisUtils.setStringWithinSeconds(cacheKey, passportId, DateAndNumTimesConstant.ONE_MONTH);
                }
            }
        } catch (Exception e) {
            logger.error("checkUniqName fail", e);
        }
        return passportId;
    }

    private String buildUniqnameCacheKey(String uniqname) {
        return CacheConstant.CACHE_PREFIX_NICKNAME_PASSPORTID + uniqname;
    }

    @Override
    public boolean updateUniqName(Account account, String uniqname) throws ServiceException {
        try {
            String oldUniqName = account.getUniqname();
            String passportId = account.getPassportId();
            //如果新昵称不为空且与原昵称不重复，则更新数据库及缓存
            if (!Strings.isNullOrEmpty(uniqname) && !uniqname.equals(oldUniqName)) {
                //先检查 u_p_m 中是否存在，如果昵称存在则返回
                if (!Strings.isNullOrEmpty(checkUniqName(uniqname))) {
                    return false;
                }
                //更新数据库
                int row = accountDAO.updateUniqName(uniqname, passportId);
                if (row > 0) {
                    String cacheKey = buildAccountKey(passportId);
                    account.setUniqname(uniqname);
                    dbShardRedisUtils.setObjectWithinSeconds(cacheKey, account, DateAndNumTimesConstant.ONE_MONTH);

                    //第一次直接插入
                    if (Strings.isNullOrEmpty(oldUniqName)) {
                        //更新新的映射表
                        row = uniqNamePassportMappingDAO.insertUniqNamePassportMapping(uniqname, passportId);
                        if (row > 0) {
                            cacheKey = CACHE_PREFIX_NICKNAME_PASSPORTID + uniqname;
                            dbShardRedisUtils.setStringWithinSeconds(cacheKey, passportId, DateAndNumTimesConstant.ONE_MONTH);
                        } else {
                            return false;
                        }
                    } else {
                        //移除原来映射表
                        if (removeUniqName(oldUniqName)) {
                            //更新新的映射表
                            row = uniqNamePassportMappingDAO.insertUniqNamePassportMapping(uniqname, passportId);
                            if (row > 0) {
                                cacheKey = CACHE_PREFIX_NICKNAME_PASSPORTID + uniqname;
                                dbShardRedisUtils.setStringWithinSeconds(cacheKey, passportId, DateAndNumTimesConstant.ONE_MONTH);
                            } else {
                                return false;
                            }
                        }
                    }
                    return true;
                }
            } else {
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean updateAvatar(Account account, String avatar) {
        try {
            String passportId = account.getPassportId();
            //更新数据库
            int row = accountDAO.updateAvatar(avatar, passportId);
            if (row > 0) {
                String cacheKey = buildAccountKey(passportId);
                account.setAvatar(avatar);
                dbShardRedisUtils.setObjectWithinSeconds(cacheKey, account, DateAndNumTimesConstant.ONE_MONTH);
                return true;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return false;
    }


    //缓存中移除原来昵称
    @Override
    public boolean removeUniqName(String uniqname) throws ServiceException {
        try {
            if (!Strings.isNullOrEmpty(uniqname)) {
                //更新映射
                int row = uniqNamePassportMappingDAO.deleteUniqNamePassportMapping(uniqname);
                if (row > 0) {
                    String cacheKey = CACHE_PREFIX_NICKNAME_PASSPORTID + uniqname;
                    dbShardRedisUtils.delete(cacheKey);
                    return true;
                } else if (row == 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("removeUniqName fail", e);
            return false;
        }
        return false;
    }

}
