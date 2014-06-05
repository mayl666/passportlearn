package com.sogou.upd.passport.service.account.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CacheConstant;
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
import com.sogou.upd.passport.dao.account.UniqNamePassportMappingDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.AccountHelper;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.generator.PassportIDGenerator;
import com.sogou.upd.passport.service.account.generator.PwdGenerator;
import org.apache.commons.lang.StringUtils;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * User: mayan Date: 13-3-22 Time: 下午3:38 To change this template use File | Settings | File Templates.
 */
@Service
public class AccountServiceImpl implements AccountService {

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
            account = redisUtils.getObject(cacheKey, Account.class);
            if (account != null) {
                account.setFlag(AccountStatusEnum.REGULAR.getValue());
                long id = accountDAO.insertAccount(username, account);
                if (id != 0) {
                    //删除临时账户缓存，成为正式账户
                    redisUtils.set(cacheKey, account);
                    //更新黑名单缓存
                    cacheKey = CACHE_PREFIX_PASSPORTID_IPBLACKLIST + ip;
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
            cacheKey = CACHE_PREFIX_PASSPORTID_ACTIVEMAILTOKEN + username;
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
            if (AccountTypeEnum.isConnect(provider)) {
                account.setPasswordtype(PasswordTypeEnum.NOPASSWORD.getValue());
            } else {
                account.setPasswordtype(PasswordTypeEnum.CRYPT.getValue());
            }
            String mobile = null;
            if (AccountTypeEnum.isPhone(username, provider)) {
                mobile = username;
            }
            account.setMobile(mobile);
            long id = accountDAO.insertOrUpdateAccount(passportId, account);
            if (id != 0) {
                String cacheKey = buildAccountKey(passportId);
                dbShardRedisUtils.setWithinSeconds(cacheKey, account, DateAndNumTimesConstant.THREE_MONTH);
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
                    dbShardRedisUtils.setWithinSeconds(cacheKey, account, DateAndNumTimesConstant.THREE_MONTH);
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
            if (userAccount == null || !AccountHelper.isNormalAccount(userAccount)) {
                result.setCode(ErrorUtil.INVALID_ACCOUNT);
                return result;
            }
            if (PwdGenerator.verify(password, needMD5, userAccount.getPassword())) {
                result.setSuccess(true);
                result.setDefaultModel(userAccount);
                return result;
            } else {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_PWD_ERROR);
                return result;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Account queryNormalAccount(String passportId) throws ServiceException {
        Account account = queryAccountByPassportId(passportId);
        if (account != null && AccountHelper.isNormalAccount(account)) {
            return account;
        }
        return null;
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
//        return false;
    }

    public void setLimitResetPwd(String passportId) throws ServiceException {
        // 设置密码修改次数限制
        String resetCacheKey = CACHE_PREFIX_PASSPORTID_RESETPWDNUM + passportId + "_" +
                DateUtil.format(new Date(), DateUtil.DATE_FMT_0);
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
            String cacheKey = CACHE_PREFIX_PASSPORTID_RESETPWDNUM + passportId + "_" +
                    DateUtil.format(new Date(), DateUtil.DATE_FMT_0);
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
                redisUtils.set(cacheKey, account);

                return true;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return false;
    }

    @Override
    public boolean isInAccountBlackListByIp(String passportId, String ip) throws ServiceException {
        boolean flag = true;
        long ipCount = 0;
        try {
            String cacheKey = CACHE_PREFIX_PASSPORTID_IPBLACKLIST + ip;
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
            String activeUrl =
                    PASSPORT_ACTIVE_EMAIL_URL + "passport_id=" + username +
                            "&client_id=" + clientId +
                            "&token=" + token;
            if (!Strings.isNullOrEmpty(ru)) {
                activeUrl = activeUrl + "&ru=" + ru;
            }

            //发送邮件
            ActiveEmail activeEmail = new ActiveEmail();
            activeEmail.setActiveUrl(activeUrl);

            //模版中参数替换
            Map<String, Object> map = Maps.newHashMap();
            map.put("activeUrl", activeUrl);
            activeEmail.setMap(map);

            activeEmail.setTemplateFile("activemail.vm");
            activeEmail.setSubject("激活您的搜狗通行证帐户");
            activeEmail.setCategory("register");
            activeEmail.setToEmail(username);

            mailUtils.sendEmail(activeEmail);
            //连接失效时间
            String cacheKey = CACHE_PREFIX_PASSPORTID_ACTIVEMAILTOKEN + username;
            redisUtils.setWithinSeconds(cacheKey, token, DateAndNumTimesConstant.TIME_TWODAY);
            /*redisUtils.set(cacheKey, token);
            redisUtils.expire(cacheKey, DateAndNumTimesConstant.TIME_TWODAY);*/
            //临时注册到缓存
            initialAccountToCache(username, passpord, ip);
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    @Override
    public boolean activeEmail(String username, String token, int clientId) throws ServiceException {
        try {
            String cacheKey = CACHE_PREFIX_PASSPORTID_ACTIVEMAILTOKEN + username;
            if (redisUtils.checkKeyIsExist(cacheKey)) {
                String tokenCache = redisUtils.get(cacheKey);
                if (tokenCache.equals(token)) {
                    return true;
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
        Map<String, Object> map = null;
        try {
            if (Strings.isNullOrEmpty(token)) {
                token = UUID.randomUUID().toString().replaceAll("-", "");
            }
            String cacheKey = CACHE_PREFIX_UUID_CAPTCHA + token;
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

    @Override
    public boolean checkCaptchaCodeIsVaild(String token, String captchaCode) throws ServiceException {
        try {
            String cacheKey = CACHE_PREFIX_UUID_CAPTCHA + token;
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
                redisUtils.set(cacheKey, account);
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
                redisUtils.set(cacheKey, account);
                return true;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return false;
    }

    /*
     * 外域邮箱注册
     */
    public void initialAccountToCache(String username, String password, String ip) throws ServiceException {
        int provider = AccountTypeEnum.EMAIL.getValue();
        Account account = new Account();
        String passportId = PassportIDGenerator.generator(username, provider);
        account.setPassportId(passportId);
        String passwordSign = null;
        try {
            if (!Strings.isNullOrEmpty(password)) {
                passwordSign = PwdGenerator.generatorStoredPwd(password, false);
            }
            account.setPassword(passwordSign);
            account.setRegTime(new Date());
            account.setAccountType(provider);
            account.setFlag(AccountStatusEnum.DISABLED.getValue());
            account.setPasswordtype(PasswordTypeEnum.CRYPT.getValue());
            account.setRegIp(ip);

            String cacheKey = buildAccountKey(username);
            redisUtils.setWithinSeconds(cacheKey, account, DateAndNumTimesConstant.TIME_TWODAY);
            /*redisUtils.set(cacheKey, account);
            redisUtils.expire(cacheKey, DateAndNumTimesConstant.TIME_TWODAY);*/

        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    private String buildAccountKey(String passportId) {
        return CACHE_PREFIX_PASSPORT_ACCOUNT + passportId;
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
            String cacheKey = CACHE_PREFIX_NICKNAME_PASSPORTID + uniqname;
            passportId = redisUtils.get(cacheKey);
            if (Strings.isNullOrEmpty(passportId)) {
                passportId = uniqNamePassportMappingDAO.getPassportIdByUniqName(uniqname);
                if (!Strings.isNullOrEmpty(passportId)) {
                    redisUtils.set(cacheKey, passportId);
                }
            }
        } catch (Exception e) {
            logger.error("checkUniqName fail", e);
        }
        return passportId;
    }

    @Override
    public boolean updateUniqName(Account account, String uniqname) throws ServiceException {
        try {
            String oldUniqName = account.getUniqname();
            String passportId = account.getPassportId();

            if (!Strings.isNullOrEmpty(uniqname) && !uniqname.equals(oldUniqName)) {
                //更新数据库
                int row = accountDAO.updateUniqName(uniqname, passportId);
                if (row > 0) {
                    String cacheKey = buildAccountKey(passportId);
                    account.setUniqname(uniqname);
                    dbShardRedisUtils.set(cacheKey, account);

                    //第一次直接插入
                    if (Strings.isNullOrEmpty(oldUniqName)) {
                        //更新新的映射表
                        row = uniqNamePassportMappingDAO.insertUniqNamePassportMapping(uniqname, passportId);
                        if (row > 0) {
                            cacheKey = CACHE_PREFIX_NICKNAME_PASSPORTID + uniqname;
                            redisUtils.set(cacheKey, passportId);
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
                                redisUtils.set(cacheKey, passportId);
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
            String oldUniqName = account.getUniqname();
            String passportId = account.getPassportId();
            //更新数据库
            int row = accountDAO.updateAvatar(avatar, passportId);
            if (row > 0) {
                String cacheKey = buildAccountKey(passportId);
                account.setAvatar(avatar);
                dbShardRedisUtils.set(cacheKey, account);
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
                    redisUtils.delete(cacheKey);
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
