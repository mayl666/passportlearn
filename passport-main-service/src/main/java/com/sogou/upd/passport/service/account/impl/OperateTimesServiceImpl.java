package com.sogou.upd.passport.service.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.LoginConstant;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.utils.DateUtil;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.service.account.OperateTimesService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: chenjiameng Date: 13-6-8 Time: 下午3:38 To change this template use File | Settings | File Templates.
 */
@Service
public class OperateTimesServiceImpl implements OperateTimesService {
//    public static Set<String> ipListSet = new HashSet<String>();
//
//    static {
//        ipListSet.add("1.194");
//        ipListSet.add("123.101");
//        ipListSet.add("223.241");
////        ipListSet.add("180.109");
//        ipListSet.add("123.53");
//        ipListSet.add("114.99");
//    }

    private static final Logger logger = LoggerFactory.getLogger(OperateTimesServiceImpl.class);
    private static final Logger regBlackListLogger = LoggerFactory.getLogger("com.sogou.upd.passport.blackListFileAppender");
    private static final Logger loginBlackListLogger = LoggerFactory.getLogger("com.sogou.upd.passport.loginBlackListFileAppender");
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private ThreadPoolTaskExecutor discardTaskExecutor;

    @Override
    public long recordTimes(String cacheKey, long timeout) throws ServiceException {
        try {
            if (redisUtils.checkKeyIsExist(cacheKey)) {
                return redisUtils.increment(cacheKey);
            } else {
                redisUtils.setWithinSeconds(cacheKey, 1, timeout);
            }
        } catch (Exception e) {
            logger.error("recordNum:cacheKey" + cacheKey, e);
            throw new ServiceException(e);
        }
        return 1;
    }

    @Override
    public void hRecordTimes(String hKey, String key, long timeout) throws ServiceException {
        try {
            if (redisUtils.checkKeyIsExist(hKey)) {
                redisUtils.hIncrBy(hKey, key);
            } else {
                redisUtils.hPut(hKey, key, 1);
                redisUtils.expire(hKey, timeout);
            }
        } catch (Exception e) {
            logger.error("recordNum:cacheKey" + hKey, e);
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean checkTimesByKey(String cacheKey, final int max) throws ServiceException {
        int num = 0;
        try {
            String value = redisUtils.get(cacheKey);
            if (!Strings.isNullOrEmpty(value)) {
                num = Integer.valueOf(value);
            }
            if (num >= max) {
                return true;
            }
        } catch (Exception e) {
            logger.error("checkNumByKey:" + cacheKey + ",max:" + max, e);
            throw new ServiceException(e);
        }
        return false;
    }

    @Override
    public boolean hCheckTimesByKey(String hKey, String key, final int max) throws ServiceException {
        try {
            int num = 0;
            String value = redisUtils.hGet(hKey, key);
            if (!Strings.isNullOrEmpty(value)) {
                num = Integer.valueOf(value);
            }
            if (num >= max) {
                return true;
            }
        } catch (Exception e) {
            logger.error("checkNumByKey," + hKey + "," + max, e);
            throw new ServiceException(e);
        }
        return false;
    }

    @Override
    public boolean checkTimesByKeyList(List<String> keyList, List<Integer> maxList) throws ServiceException {
        if (CollectionUtils.isEmpty(keyList) || CollectionUtils.isEmpty(maxList)) {
            return false;
        }
        try {
            List<String> valueList = redisUtils.multiGet(keyList);
            if (!CollectionUtils.isEmpty(keyList)) {
                int num = 0, valueSize = valueList.size(), maxSize = maxList.size();
                for (int i = 0; i < valueSize && i < maxSize; i++) {
                    String value = valueList.get(i);
                    if (!Strings.isNullOrEmpty(value)) {
                        num = Integer.valueOf(value);
                        if (num >= maxList.get(i)) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("checkNumByKey:" + keyList.toString() + ",maxList:" + maxList.toString(), e);
            throw new ServiceException(e);
        }
        return false;
    }

    @Override
    public boolean isHalfTimes(String cacheKey, final int max) throws ServiceException {
        int num = 0;
        try {
            String value = redisUtils.get(cacheKey);
            if (!Strings.isNullOrEmpty(value)) {
                num = Integer.valueOf(value);
            }
            if (num == (max / 2)) {
                return true;
            }
        } catch (Exception e) {
            logger.error("checkNumByKey:" + cacheKey + ",max:" + max, e);
            throw new ServiceException(e);
        }
        return false;
    }

    private void incLoginSuccessTimes(final String username, final String ip) throws ServiceException {
        try {
            discardTaskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    String username_hKey = CacheConstant.CACHE_PREFIX_USERNAME_LOGINNUM + username;
                    hRecordTimes(username_hKey, CacheConstant.CACHE_SUCCESS_KEY, DateAndNumTimesConstant.TIME_ONEHOUR);
                    if (!Strings.isNullOrEmpty(ip)) {
                        String ip_hKey = CacheConstant.CACHE_PREFIX_IP_LOGINNUM + ip;
                        hRecordTimes(ip_hKey, CacheConstant.CACHE_SUCCESS_KEY, DateAndNumTimesConstant.TIME_ONEDAY);
                    }
                }
            });

        } catch (Exception e) {
            logger.error("incLoginSuccessTimes:username" + username + ",ip:" + ip, e);
            throw new ServiceException(e);
        }
    }

    private void incLoginFailedTimes(final String username, final String ip) throws ServiceException {
        try {

            String username_hKey = CacheConstant.CACHE_PREFIX_USERNAME_LOGINNUM + username;
            hRecordTimes(username_hKey, CacheConstant.CACHE_FAILED_KEY, DateAndNumTimesConstant.TIME_ONEHOUR);
            if (!Strings.isNullOrEmpty(ip)) {
                String ip_hKey = CacheConstant.CACHE_PREFIX_IP_LOGINNUM + ip;
                hRecordTimes(ip_hKey, CacheConstant.CACHE_FAILED_KEY, DateAndNumTimesConstant.TIME_ONEDAY);
            }
        } catch (Exception e) {
            logger.error("incLoginSuccessTimes:username" + username + ",ip:" + ip, e);
            throw new ServiceException(e);
        }
    }

    @Override
    public void incLoginTimes(final String username, final String ip, final boolean isSuccess) throws ServiceException {
        try {
            if (isSuccess) {
                incLoginSuccessTimes(username, ip);
            } else {
                incLoginFailedTimes(username, ip);
            }
        } catch (Exception e) {
            logger.error("incLoginTimes," + username + "," + ip + "," + isSuccess, e);
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean checkLoginUserInBlackList(String username, String ip) throws ServiceException {
        try {
            //username
            int num = 0;
            String userName_hKey = CacheConstant.CACHE_PREFIX_USERNAME_LOGINNUM + username;
            Map<String, String> username_hmap = redisUtils.hGetAll(userName_hKey);
            if (!MapUtils.isEmpty(username_hmap)) {
                String username_failedNum = username_hmap.get(CacheConstant.CACHE_FAILED_KEY);
                if (!StringUtils.isEmpty(username_failedNum)) {
                    num = Integer.parseInt(username_failedNum);
                    if (num >= LoginConstant.LOGIN_FAILED_EXCEED_MAX_LIMIT_COUNT) {
                        logLoginBlackList(username, ip, userName_hKey + "_" + CacheConstant.CACHE_FAILED_KEY, num);
                        return true;
                    }
                }
                String username_successNum = username_hmap.get(CacheConstant.CACHE_SUCCESS_KEY);
                if (!StringUtils.isEmpty(username_successNum)) {
                    num = Integer.parseInt(username_successNum);
                    if (num >= LoginConstant.LOGIN_SUCCESS_EXCEED_MAX_LIMIT_COUNT) {
                        logLoginBlackList(username, ip, userName_hKey + "_" + CacheConstant.CACHE_SUCCESS_KEY, num);
                        return true;
                    }
                }

            }

            if (!Strings.isNullOrEmpty(ip)) {      //  根据ip判断是否需要弹出验证码
                String ip_hKey = CacheConstant.CACHE_PREFIX_IP_LOGINNUM + ip;
                Map<String, String> ip_hmap = redisUtils.hGetAll(ip_hKey);
                if (!MapUtils.isEmpty(ip_hmap)) {
                    String ip_failedNum = ip_hmap.get(CacheConstant.CACHE_FAILED_KEY);
                    if (!StringUtils.isEmpty(ip_failedNum)) {
                        num = Integer.parseInt(ip_failedNum);
                        if (checkInSubIpList(ip)) {
                            if (num >= LoginConstant.LOGIN_FAILED_SUB_IP_LIMIT_COUNT) {
                                logLoginBlackList(username, ip, userName_hKey + "_" + CacheConstant.CACHE_FAILED_KEY, num);
                                return true;
                            }
                        } else {
                            if (num >= LoginConstant.LOGIN_FAILED_NEED_CAPTCHA_IP_LIMIT_COUNT) {
                                logLoginBlackList(username, ip, userName_hKey + "_" + CacheConstant.CACHE_FAILED_KEY, num);
                                return true;
                            }
                        }
                    }
                    String ip_successNum = ip_hmap.get(CacheConstant.CACHE_SUCCESS_KEY);
                    if (!StringUtils.isEmpty(ip_successNum)) {
                        num = Integer.parseInt(ip_successNum);
                        if (num >= LoginConstant.LOGIN_IP_SUCCESS_EXCEED_MAX_LIMIT_COUNT) {
                            logLoginBlackList(username, ip, userName_hKey + "_" + CacheConstant.CACHE_SUCCESS_KEY, num);
                            return true;
                        }
                    }
                }
            }
            return false;
        } catch (Exception e) {
            logger.error("userInBlackList," + username + "," + ip, e);
            throw new ServiceException(e);
        }
    }

    private void logLoginBlackList(String username, String ip, String blackKey, int blackNum) {
        StringBuilder log = new StringBuilder();
        Date date = new Date();
        log.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date)).append(" ").append(username)
                .append(" ").append(ip).append(" ").append(blackKey).append(" ").append(blackNum);
        loginBlackListLogger.info(log.toString());
    }

    @Override
    public long incResetPasswordTimes(String passportId) throws ServiceException {
        try {
            String resetCacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_RESETPWDNUM + passportId + "_" +
                    DateUtil.format(new Date(), DateUtil.DATE_FMT_0);
            return recordTimes(resetCacheKey, DateAndNumTimesConstant.TIME_ONEDAY);
        } catch (Exception e) {
            logger.error("incResetPasswordTimes:passportId" + passportId, e);
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean checkLimitResetPwd(String passportId) throws ServiceException {
        try {
            String cacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_RESETPWDNUM + passportId + "_" +
                    DateUtil.format(new Date(), DateUtil.DATE_FMT_0);
            return checkTimesByKey(cacheKey, LoginConstant.RESETNUM_LIMITED);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public long incResetPwdIPTimes(String ip) throws ServiceException {
        try {
            String resetCacheKey = CacheConstant.CACHE_PREFIX_IP_UPDATEPWDNUM + ip;
            return recordTimes(resetCacheKey, DateAndNumTimesConstant.TIME_ONEDAY);
        } catch (Exception e) {
            logger.error("incResetPasswordTimes:ip" + ip, e);
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean checkIPLimitResetPwd(String ip) throws ServiceException {
        try {
            String cacheKey = CacheConstant.CACHE_PREFIX_IP_UPDATEPWDNUM + ip;
            return checkTimesByKey(cacheKey, LoginConstant.UPDATENUM_IP_LIMITED);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    //@Override
    public long incIPBindTimes(String ip) throws ServiceException {
        try {
            String resetCacheKey = CacheConstant.CACHE_PREFIX_IP_BINDNUM + ip;
            return recordTimes(resetCacheKey, DateAndNumTimesConstant.TIME_ONEDAY);
        } catch (Exception e) {
            logger.error("incBindTimes:ip" + ip, e);
            throw new ServiceException(e);
        }
    }

    //@Override
    public boolean checkIPBindLimit(String ip) throws ServiceException {
        try {
            String cacheKey = CacheConstant.CACHE_PREFIX_IP_BINDNUM + ip;
            return checkTimesByKey(cacheKey, LoginConstant.BINDNUM_IP_LIMITED);
        } catch (Exception e) {
            logger.error("checkBindTimes:ip" + ip, e);
            throw new ServiceException(e);
        }
    }

    @Override
    public void incRegTimes(final String ip, final String cookieStr) throws ServiceException {
        discardTaskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                //修改为list模式添加cookie处理 by mayan
                try {
                    if (!Strings.isNullOrEmpty(cookieStr)) {
                        //ip与cookie列表映射
                        String ipCacheKey = CacheConstant.CACHE_PREFIX_REGISTER_IPBLACKLIST + ip;
                        if (redisUtils.checkKeyIsExist(ipCacheKey)) {
                            redisUtils.sadd(ipCacheKey, cookieStr);
                        } else {
                            redisUtils.sadd(ipCacheKey, cookieStr);
                            redisUtils.expire(ipCacheKey, DateAndNumTimesConstant.TIME_ONEDAY);
                        }

                        //cookie与ip列表映射
                        String cookieCacheKey = CacheConstant.CACHE_PREFIX_REGISTER_COOKIEBLACKLIST + cookieStr;
                        if (redisUtils.checkKeyIsExist(cookieCacheKey)) {
                            redisUtils.sadd(cookieCacheKey, ip);
                        } else {
                            redisUtils.sadd(cookieCacheKey, ip);
                            redisUtils.expire(cookieCacheKey, DateAndNumTimesConstant.TIME_ONEDAY);
                        }
                    }
                    //ip与cookie映射
                    String ipCookieKey = CacheConstant.CACHE_PREFIX_REGISTER_IPBLACKLIST + ip + "_" + cookieStr;
                    recordTimes(ipCookieKey, DateAndNumTimesConstant.TIME_ONEDAY);
                } catch (Exception e) {
                    logger.error("incRegIPTimes:ip" + ip, e);
                    throw new ServiceException(e);
                }
            }
        });


    }

    @Override
    public boolean checkRegInWhiteList(String ip) throws ServiceException {
        try {
            String whiteListKey = CacheConstant.CACHE_PREFIX_REGISTER_WHITELIST;
            Set<String> whiteList = redisUtils.smember(whiteListKey);
            if (CollectionUtils.isNotEmpty(whiteList)) {
                if (whiteList.contains(ip)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            logger.error("checkRegInWhiteList:ip=" + ip, e);
            return false;
        }
    }

    @Override
    public boolean checkRegInBlackList(String ip, String cookieStr) throws ServiceException {
        //修改为list模式添加cookie处理 by mayan
        try {
            //cookie与ip映射
            String cookieCacheKey = CacheConstant.CACHE_PREFIX_REGISTER_COOKIEBLACKLIST + cookieStr;
            Set<String> setIpVal = redisUtils.smember(cookieCacheKey);
            if (CollectionUtils.isNotEmpty(setIpVal)) {
                int sz = setIpVal.size();
                if (sz == (LoginConstant.REGISTER_COOKIE_LIMITED / 2)) {
                    regBlackListLogger.info(new Date() + ",checkRegInBlackList,cookieCacheKey=" + cookieCacheKey
                            + ",ipSize=" + sz + ",ipSet=" + setIpVal.toArray().toString());
                }
                if (sz >= LoginConstant.REGISTER_COOKIE_LIMITED) {
                    regBlackListLogger.info(new Date() + "checkRegInBlackList,cookieCacheKey=" + cookieCacheKey
                            + ",ipSize=" + sz + ",ipSet=" + setIpVal.toArray().toString());
                    return true;
                }
            }

            //通过ip+cookie限制注册次数
            String ipCookieKey = CacheConstant.CACHE_PREFIX_REGISTER_IPBLACKLIST + ip + "_" + cookieStr;
            String value = redisUtils.get(ipCookieKey);
            if (!Strings.isNullOrEmpty(value)) {
                int num = Integer.valueOf(value);
                if (num == (LoginConstant.REGISTER_IP_COOKIE_LIMITED / 2)) {
                    regBlackListLogger.info(new Date() + ",checkRegInBlackList,ipCookieKey=" + ipCookieKey
                            + ",num=" + num);
                }
                if (num >= LoginConstant.REGISTER_IP_COOKIE_LIMITED) {
                    regBlackListLogger.info(new Date() + ",checkRegInBlackList,ipCookieKey=" + ipCookieKey
                            + ",num=" + num);
                    return true;
                }
            }

            //ip与cookie映射
            String ipCacheKey = CacheConstant.CACHE_PREFIX_REGISTER_IPBLACKLIST + ip;
            Set<String> setCookieVal = redisUtils.smember(ipCacheKey);
            if (CollectionUtils.isNotEmpty(setCookieVal)) {
                int sz = setCookieVal.size();
                if (sz == (LoginConstant.REGISTER_IP_LIMITED / 2)) {
                    regBlackListLogger.info(new Date() + ",checkRegInBlackList,ipCacheKey=" + ipCacheKey
                            + ",setCookieVal=" + sz + ",setCookieVal=" + setCookieVal.toArray().toString());
                }
                if (sz >= LoginConstant.REGISTER_IP_LIMITED) {
                    regBlackListLogger.info(new Date() + ",checkRegInBlackList,ipCacheKey=" + ipCacheKey
                            + ",setCookieVal=" + sz + ",setCookieVal=" + setCookieVal.toArray().toString());
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("checkRegIPInBlackList:ip" + ip, e);
            throw new ServiceException(e);
        }
        return false;
    }

    /**
     * 根据登陆错误次数，判断是否需要在登陆时输入验证码
     *
     * @param username
     * @param ip
     * @return
     */
    @Override
    public boolean loginFailedTimesNeedCaptcha(String username, String ip) throws ServiceException {
        try {
            // 根据username判断是否需要弹出验证码
            String userName_hKey = CacheConstant.CACHE_PREFIX_USERNAME_LOGINNUM + username;
            return hCheckTimesByKey(userName_hKey, CacheConstant.CACHE_FAILED_KEY, LoginConstant.LOGIN_FAILED_NEED_CAPTCHA_LIMIT_COUNT);
        } catch (Exception e) {
            logger.error("loginFailedTimesNeedCaptcha," + username + "," + ip, e);
            throw new ServiceException(e);
        }
    }

    @Override
    public long incAddProblemTimes(String ip) throws ServiceException {
        try {
            if (Strings.isNullOrEmpty(ip)) {
                return 0;
            }
            String ipCacheKey = CacheConstant.CACHE_PREFIX_PROBLEM_IPINBLACKLIST + ip;
            return recordTimes(ipCacheKey, DateAndNumTimesConstant.TIME_ONEDAY);
        } catch (Exception e) {
            logger.error("incAddProblemTimes:ip" + ip, e);
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean checkAddProblemInBlackList(String ip) throws ServiceException {
        try {
            if (Strings.isNullOrEmpty(ip)) {
                return false;
            }
            String ipCacheKey = CacheConstant.CACHE_PREFIX_PROBLEM_IPINBLACKLIST + ip;
            return checkTimesByKey(ipCacheKey, LoginConstant.ADDPROBLEM_IP_LIMITED);
        } catch (Exception e) {
            logger.error("checkRegIPInBlackList:ip" + ip, e);
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean incLimitBind(String userId, int clientId) throws ServiceException {
        try {
            String cacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_BINDNUM + userId +
                              "_" + DateUtil.format(new Date(), DateUtil.DATE_FMT_0);
            recordTimes(cacheKey, DateAndNumTimesConstant.TIME_ONEDAY);
            return true;
        } catch (Exception e) {
            logger.error("incLimitBind:passportId" + userId, e);
            return false;
        }
    }

    @Override
    public boolean checkLimitBind(String userId, int clientId) throws ServiceException {
        try {
            String cacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_BINDNUM + userId +
                              "_" + DateUtil.format(new Date(), DateUtil.DATE_FMT_0);
            return !checkTimesByKey(cacheKey, DateAndNumTimesConstant.BIND_LIMIT);
        } catch (Exception e) {
            logger.error("checkLimitBind:passportId" + userId, e);
            return true;
        }
    }

/*    @Override
    public boolean incLimitBindEmail(String userId, int clientId) throws ServiceException {
        try {
            String cacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_BINDEMAILNUM + userId +
                    "_" + DateUtil.format(new Date(), DateUtil.DATE_FMT_0);
            recordTimes(cacheKey, DateAndNumTimesConstant.TIME_ONEDAY);
            return true;
        } catch (Exception e) {
            logger.error("incLimitBindEmail:passportId" + userId, e);
            return false;
        }
    }

    @Override
    public boolean incLimitBindMobile(String userId, int clientId) throws ServiceException {
        try {
            String cacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_BINDMOBILENUM + userId +
                    "_" + DateUtil.format(new Date(), DateUtil.DATE_FMT_0);
            recordTimes(cacheKey, DateAndNumTimesConstant.TIME_ONEDAY);
            return true;
        } catch (Exception e) {
            logger.error("incLimitBindMobile:passportId" + userId, e);
            return false;
        }
    }

    @Override
    public boolean incLimitBindQues(String userId, int clientId) throws ServiceException {
        try {
            String cacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_BINDQUESNUM + userId +
                    "_" + DateUtil.format(new Date(), DateUtil.DATE_FMT_0);
            recordTimes(cacheKey, DateAndNumTimesConstant.TIME_ONEDAY);
            return true;
        } catch (Exception e) {
            logger.error("incLimitBindQues:passportId" + userId, e);
            return false;
        }
    }


    @Override
    public boolean checkLimitBindEmail(String userId, int clientId) throws ServiceException {
        try {
            String cacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_BINDEMAILNUM + userId +
                    "_" + DateUtil.format(new Date(), DateUtil.DATE_FMT_0);
            return !checkTimesByKey(cacheKey, DateAndNumTimesConstant.BIND_LIMIT);
        } catch (Exception e) {
            logger.error("checkLimitBindEmail:passportId" + userId, e);
            return true;
        }
    }

    @Override
    public boolean checkLimitBindMobile(String userId, int clientId) throws ServiceException {
        try {
            String cacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_BINDMOBILENUM + userId +
                    "_" + DateUtil.format(new Date(), DateUtil.DATE_FMT_0);
            return !checkTimesByKey(cacheKey, DateAndNumTimesConstant.BIND_LIMIT);
        } catch (Exception e) {
            logger.error("checkLimitBindMobile:passportId" + userId, e);
            return true;
        }
    }

    @Override
    public boolean checkLimitBindQues(String userId, int clientId) throws ServiceException {
        try {
            String cacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_BINDQUESNUM + userId +
                    "_" + DateUtil.format(new Date(), DateUtil.DATE_FMT_0);
            return !checkTimesByKey(cacheKey, DateAndNumTimesConstant.BIND_LIMIT);
        } catch (Exception e) {
            logger.error("checkLimitBindQues:passportId" + userId, e);
            return true;
        }
    }*/

    @Override
    public boolean incLimitResetPwd(String userId, int clientId) throws ServiceException {
        try {
            String cacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_RESETPWDNUM + userId +
                    "_" + clientId + "_" + DateUtil.format(new Date(), DateUtil.DATE_FMT_0);
            recordTimes(cacheKey, DateAndNumTimesConstant.TIME_ONEDAY);
            return true;
        } catch (Exception e) {
            logger.error("incLimitResetPwd:passportId" + userId, e);
            return false;
        }
    }

    @Override
    public boolean checkLimitResetPwd(String userId, int clientId) throws ServiceException {
        try {
            String cacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_RESETPWDNUM + userId +
                    "_" + clientId + "_" + DateUtil.format(new Date(), DateUtil.DATE_FMT_0);
            return checkTimesByKey(cacheKey, DateAndNumTimesConstant.RESETPWD_NUM);
        } catch (Exception e) {
            logger.error("checkLimitResetPwd:passportId" + userId, e);
            return true;
        }
    }


    @Override
    public boolean incLimitCheckPwdFail(String userId, int clientId, AccountModuleEnum module) throws ServiceException {
        try {
            String cacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_CHECKPWDFAIL + module + "_" + userId +
                    "_" + clientId + "_" + DateUtil.format(new Date(), DateUtil.DATE_FMT_0);
            recordTimes(cacheKey, DateAndNumTimesConstant.TIME_ONEDAY);
            return true;
        } catch (Exception e) {
            logger.error("incLimitCheckPwdFail:passportId" + userId, e);
            return false;
        }
    }

    @Override
    public boolean checkLimitCheckPwdFail(String userId, int clientId, AccountModuleEnum module) throws ServiceException {
        try {
            String cacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_CHECKPWDFAIL + module + "_" + userId +
                    "_" + clientId + "_" + DateUtil.format(new Date(), DateUtil.DATE_FMT_0);
            return !checkTimesByKey(cacheKey, DateAndNumTimesConstant.CHECKPWD_NUM);
        } catch (Exception e) {
            logger.error("checkLimitCheckPwdFail:passportId" + userId, e);
            return true;
        }
    }

    @Override
    public boolean checkLoginUserInWhiteList(String username, String ip) throws ServiceException {
        try {
            String whiteListKey = CacheConstant.CACHE_PREFIX_LOGIN_WHITELIST;
            Set<String> whiteList = redisUtils.smember(whiteListKey);
            if (CollectionUtils.isNotEmpty(whiteList)) {
                if (whiteList.contains(username)) {
                    return true;
                }
                if (whiteList.contains(ip)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            logger.error("checkLoginUserWhiteList:username=" + username + ",ip=" + ip, e);
            return false;
        }
    }

    private boolean checkInSubIpList(String ip) throws ServiceException {
        try {
            if (!Strings.isNullOrEmpty(ip)) {
                String[] subIpArr = ip.split("\\.");

                StringBuilder sb = new StringBuilder();
                sb.append(subIpArr[0]);
                sb.append(".");
                sb.append(subIpArr[1]);

                String subIpListKey = CacheConstant.CACHE_PREFIX_IP_SUBIPBLACKLIST;
                Set<String> subIpList = redisUtils.smember(subIpListKey);
                if (CollectionUtils.isNotEmpty(subIpList) && subIpList.contains(sb.toString())) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            logger.error("checkLoginUserWhiteList," + ip, e);
            return false;
        }
    }
}
