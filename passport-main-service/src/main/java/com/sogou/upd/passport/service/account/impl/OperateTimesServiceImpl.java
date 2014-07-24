package com.sogou.upd.passport.service.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.LoginConstant;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.utils.DateUtil;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.black.BlackItem;
import com.sogou.upd.passport.service.account.OperateTimesService;
import com.sogou.upd.passport.service.black.BlackItemService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * User: chenjiameng Date: 13-6-8 Time: 下午3:38 To change this template use File | Settings | File Templates.
 */
@Service
public class OperateTimesServiceImpl implements OperateTimesService {

    private static final Logger logger = LoggerFactory.getLogger(OperateTimesServiceImpl.class);
    private static final Logger regBlackListLogger = LoggerFactory.getLogger("com.sogou.upd.passport.regBlackListFileAppender");
    private static final Logger loginBlackListLogger = LoggerFactory.getLogger("com.sogou.upd.passport.loginBlackListFileAppender");
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private ThreadPoolTaskExecutor discardTaskExecutor;
    @Autowired
    private BlackItemService blackItemService;

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


    private void incLoginSuccessTimes(final String username, final String ip) throws ServiceException {
        try {
            discardTaskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    String username_hKey = CacheConstant.CACHE_PREFIX_USERNAME_LOGINNUM + username;
                    hRecordTimes(username_hKey, CacheConstant.CACHE_SUCCESS_KEY, DateAndNumTimesConstant.TIME_ONEHOUR);
                    if (!Strings.isNullOrEmpty(ip)) {
                        String ip_hKey = CacheConstant.CACHE_PREFIX_IP_LOGINNUM + ip;
                        hRecordTimes(ip_hKey, CacheConstant.CACHE_SUCCESS_KEY, DateAndNumTimesConstant.TIME_ONEHOUR);
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
                hRecordTimes(ip_hKey, CacheConstant.CACHE_FAILED_KEY, DateAndNumTimesConstant.TIME_ONEHOUR);
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
    public boolean isLoginTimesForBlackList(String username, String ip) throws ServiceException {
        try {
            //username
            int num = 0;
            String userName_hKey = buildUserNameLoginTimesKeyStr(username);
            Map<String, String> username_hmap = redisUtils.hGetAll(userName_hKey);
            if (!MapUtils.isEmpty(username_hmap)) {
                String username_failedNum = username_hmap.get(CacheConstant.CACHE_FAILED_KEY);
                if (!Strings.isNullOrEmpty(username_failedNum)) {
                    num = Integer.parseInt(username_failedNum);
                    if (num >= LoginConstant.LOGIN_FAILED_EXCEED_MAX_LIMIT_COUNT) {
                        blackItemService.addIPOrUsernameToLoginBlackList(username, BlackItem.FAILED_LIMIT, false);
                        redisUtils.delete(userName_hKey);
                        return true;
                    }
                }
                String username_successNum = username_hmap.get(CacheConstant.CACHE_SUCCESS_KEY);
                if (!Strings.isNullOrEmpty(username_successNum)) {
                    num = Integer.parseInt(username_successNum);
                    if (num >= LoginConstant.LOGIN_SUCCESS_EXCEED_MAX_LIMIT_COUNT) {
                        blackItemService.addIPOrUsernameToLoginBlackList(username, BlackItem.SUCCESS_LIMIT, false);
                        redisUtils.delete(userName_hKey);
                        return true;

                    }
                }

            }

            if (!Strings.isNullOrEmpty(ip)) {      //  根据ip判断是否进入黑名单
                String ip_hKey = buildIPLoginTimesKeyStr(ip);
                Map<String, String> ip_hmap = redisUtils.hGetAll(ip_hKey);
                if (!MapUtils.isEmpty(ip_hmap)) {
                    String ip_failedNum = ip_hmap.get(CacheConstant.CACHE_FAILED_KEY);
                    if (!Strings.isNullOrEmpty(ip_failedNum)) {
                        num = Integer.parseInt(ip_failedNum);
                        if (checkInSubIpList(ip)) {
                            if (num >= LoginConstant.LOGIN_FAILED_SUB_IP_LIMIT_COUNT) {
                                blackItemService.addIPOrUsernameToLoginBlackList(ip, BlackItem.FAILED_LIMIT, true);
                                redisUtils.delete(ip_hKey);
                                return true;

                            }
                        } else {
                            if (num >= LoginConstant.LOGIN_FAILED_NEED_CAPTCHA_IP_LIMIT_COUNT) {
                                blackItemService.addIPOrUsernameToLoginBlackList(ip, BlackItem.FAILED_LIMIT, true);
                                redisUtils.delete(ip_hKey);
                                return true;

                            }
                        }
                    }
                    String ip_successNum = ip_hmap.get(CacheConstant.CACHE_SUCCESS_KEY);
                    if (!Strings.isNullOrEmpty(ip_successNum)) {
                        num = Integer.parseInt(ip_successNum);
                        if (num >= LoginConstant.LOGIN_IP_SUCCESS_EXCEED_MAX_LIMIT_COUNT) {
                            blackItemService.addIPOrUsernameToLoginBlackList(ip, BlackItem.SUCCESS_LIMIT, true);
                            redisUtils.delete(ip_hKey);
                            return true;

                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("userInBlackList," + username + "," + ip, e);
            throw new ServiceException(e);
        }
        return false;
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
    public void incRegTimesForInternal(final String ip, int clientId) throws ServiceException {
        if (clientId == 1115) {
            String clientIdKey = CacheConstant.CACHE_PREFIX_REGISTER_CLIENTIDBLACKLIST + clientId;
            recordTimes(clientIdKey, DateAndNumTimesConstant.TIME_ONEHOUR);
        }
        //ip与cookie映射
        String ipCookieKey = CacheConstant.CACHE_PREFIX_REGISTER_IPBLACKLIST + ip + "_null";
        recordTimes(ipCookieKey, DateAndNumTimesConstant.TIME_ONEDAY);
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
    public boolean checkRegInBlackListForInternal(String ip, int clientId) throws ServiceException {
        if (clientId == 1115) {
            String clientIdKey = CacheConstant.CACHE_PREFIX_REGISTER_CLIENTIDBLACKLIST + clientId;
            String clientIdValue = redisUtils.get(clientIdKey);
            if (!Strings.isNullOrEmpty(clientIdValue)) {
                int num = Integer.valueOf(clientIdValue);
                if (num >= LoginConstant.REGISTER_IP_COOKIE_LIMITED_FOR_INTERNAL) {
                    return true;
                }
            }
        }
        //通过ip+cookie限制注册次数
        String ipCookieKey = CacheConstant.CACHE_PREFIX_REGISTER_IPBLACKLIST + ip + "_null";
        String value = redisUtils.get(ipCookieKey);
        if (!Strings.isNullOrEmpty(value)) {
            int num = Integer.valueOf(value);
            if (num >= LoginConstant.REGISTER_IP_COOKIE_LIMITED_FOR_INTERNAL) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean checkUserInBlackListForInternal(String ip, String username) {
        try {
            String username_black_key = CacheConstant.CACHE_PREFIX_EXIST_INTERNAL_USERNAME_BLACK + username;
            String value = redisUtils.get(username_black_key);
            if (CommonConstant.LOGIN_IN_BLACKLIST.equals(value)) {
                return true;
            }

            String username_hKey = CacheConstant.CACHE_PREFIX_CHECK_USER_INTERNAL_USERNAME_NUM + username;
            boolean checkTimes = checkTimesByKey(username_hKey, LoginConstant.CHECK_USER_EXIST_INTERNAL_USER_LIMIT);
            if (checkTimes) {
                redisUtils.setWithinSeconds(username_black_key, CommonConstant.LOGIN_IN_BLACKLIST, DateAndNumTimesConstant.ONE_HOUR_INSECONDS);
                return true;
            }

            if (!StringUtils.isBlank(ip)) {
                String ip_black_key = CacheConstant.CACHE_PREFIX_EXIST_INTERNAL_IP_BLACK + ip;
                value = redisUtils.get(ip_black_key);
                if (CommonConstant.LOGIN_IN_BLACKLIST.equals(value)) {
                    return true;
                }

                String ip_hKey = CacheConstant.CACHE_PREFIX_CHECK_USER_INTERNAL_IP_NUM + ip;
                boolean checkIpTimes = checkTimesByKey(ip_hKey, LoginConstant.CHECK_USER_EXIST_INTERNAL_IP_LIMIT);
                if (checkIpTimes) {
                    redisUtils.setWithinSeconds(ip_black_key, CommonConstant.LOGIN_IN_BLACKLIST, DateAndNumTimesConstant.ONE_HOUR_INSECONDS);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            logger.error(String.format("checkUserInBlackListForInternal error. username:{} ,ip:{}", username, ip), e);
            return false;
        }
    }

    @Override
    public boolean checkRegInWhiteList(String ip) throws ServiceException {
        try {
            String whiteListKey = CacheConstant.CACHE_PREFIX_LOGIN_WHITELIST;
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

    private void logRegisterBlackList(String ip, String cacheKey, int cacheNum, String ipValues) {
        StringBuilder log = new StringBuilder();
        Date date = new Date();
        log.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date))
                .append(" ").append(ip).append(" ").append(cacheKey).append(" ").append(cacheNum).append(" ").append(ipValues);
        regBlackListLogger.info(log.toString());
    }

    @Override
    public boolean checkRegInBlackList(String ip, String cookieStr) throws ServiceException {
        //修改为list模式添加cookie处理 by mayan
        try {
            if (!Strings.isNullOrEmpty(cookieStr)) {
                //cookie与ip映射
                String cookieCacheKey = CacheConstant.CACHE_PREFIX_REGISTER_COOKIEBLACKLIST + cookieStr;
                Set<String> setIpVal = redisUtils.smember(cookieCacheKey);
                if (CollectionUtils.isNotEmpty(setIpVal)) {
                    int sz = setIpVal.size();
                    if (sz >= LoginConstant.REGISTER_COOKIE_LIMITED) {
                        return true;
                    }
                }
                //ip与cookie映射
                String ipCacheKey = CacheConstant.CACHE_PREFIX_REGISTER_IPBLACKLIST + ip;
                Set<String> setCookieVal = redisUtils.smember(ipCacheKey);
                if (CollectionUtils.isNotEmpty(setCookieVal)) {
                    int sz = setCookieVal.size();
                    if (sz == (LoginConstant.REGISTER_IP_LIMITED / 2)) {
                        logRegisterBlackList(ip, ipCacheKey, sz, setCookieVal.toArray().toString());
                    }
                    if (sz >= LoginConstant.REGISTER_IP_LIMITED) {
                        logRegisterBlackList(ip, ipCacheKey, sz, setCookieVal.toArray().toString());
                        return true;
                    }
                }
            }
            //通过ip+cookie限制注册次数
            String ipCookieKey = CacheConstant.CACHE_PREFIX_REGISTER_IPBLACKLIST + ip + "_" + cookieStr;
            String value = redisUtils.get(ipCookieKey);
            if (!Strings.isNullOrEmpty(value)) {
                int num = Integer.valueOf(value);
                if (num >= LoginConstant.REGISTER_IP_COOKIE_LIMITED) {
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
    public boolean checkBindLimit(String passportId, int clientId) throws ServiceException {
        try {
            String cacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_BINDNUM + passportId +
                    "_" + DateUtil.format(new Date(), DateUtil.DATE_FMT_0);
            return !checkTimesByKey(cacheKey, DateAndNumTimesConstant.BIND_LIMIT);
        } catch (Exception e) {
            logger.error("checkBindLimit:passportId" + passportId, e);
            return true;
        }
    }

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
    public boolean isUserInBlackList(String username, String ip) throws ServiceException {
        try {
            String username_black_key = buildLoginUserNameBlackKeyStr(username);
            String value = redisUtils.get(username_black_key);
            if (CommonConstant.LOGIN_IN_BLACKLIST.equals(value)) {
                return true;
            }
            if (!StringUtils.isBlank(ip)) {
                String ip_black_key = buildLoginIPBlackKeyStr(ip);
                value = redisUtils.get(ip_black_key);
                if (CommonConstant.LOGIN_IN_BLACKLIST.equals(value)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            logger.error("checkLoginUserWhiteList:username=" + username + ",ip=" + ip, e);
            return false;
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

    @Override
    public boolean isMobileSendSMSInBlackList(String ip) throws ServiceException {
        try {
            String ipCacheKey = CacheConstant.CACHE_PREFIX_MOBILE_SMSCODE_IPBLACKLIST + ip;
            String value = redisUtils.get(ipCacheKey);
            if (!Strings.isNullOrEmpty(value)) {
                int num = Integer.valueOf(value);
                if (num >= LoginConstant.MOBILE_SEND_SMSCODE_LIMITED) {
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("isMobileSendSMSInBlackList:ip " + ip, e);
            throw new ServiceException(e);
        }
        return false;
    }


    @Override
    public void incSendTimesForMobile(final String ip) throws ServiceException {
        //ip与发短信验证码次数映射
        String ipCacheKey = CacheConstant.CACHE_PREFIX_MOBILE_SMSCODE_IPBLACKLIST + ip;
        recordTimes(ipCacheKey, DateAndNumTimesConstant.TIME_ONEDAY);
    }

    @Override
    public boolean incFindPwdTimes(String passportId) throws ServiceException {
        try {
            String cacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_FINDPWDTIMES + passportId;
            recordTimes(cacheKey, DateAndNumTimesConstant.TIME_ONEDAY);
            return true;
        } catch (Exception e) {
            logger.error("incFindPwdTimes:passportId" + passportId, e);
            return false;
        }
    }

    @Override
    public boolean checkFindPwdTimes(String passportId) throws ServiceException {
        try {
            String cacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_FINDPWDTIMES + passportId;
            return checkTimesByKey(cacheKey, DateAndNumTimesConstant.FINDPWD_LIMIT);
        } catch (Exception e) {
            logger.error("checkFindPwdTimes:passportId" + passportId, e);
            return false;
        }
    }

    @Override
    public boolean incLimitFindPwdResetPwd(String userId, int clientId, String ip) throws ServiceException {
        try {
            String userIdCacheKey = buildFindPwdResetPwdKey(userId, clientId);
            recordTimes(userIdCacheKey, DateAndNumTimesConstant.TIME_ONEDAY);

            String ipCacheKey = buildFindPwdResetPwdKey(ip, clientId);
            recordTimes(ipCacheKey, DateAndNumTimesConstant.TIME_ONEDAY);
            return true;
        } catch (Exception e) {
            logger.error("incLimitResetPwd: passportId" + userId, e);
            return false;
        }
    }

    @Override
    public boolean isOverLimitFindPwdResetPwd(String userId, int clientId, String ip) throws ServiceException {
        try {
            String userIdCacheKey = buildFindPwdResetPwdKey(userId, clientId);
            if (checkTimesByKey(userIdCacheKey, DateAndNumTimesConstant.RESETPWD_NUM)) {
                return true;
            }
            String ipCacheKey = buildFindPwdResetPwdKey(ip, clientId);
            if (checkTimesByKey(ipCacheKey, DateAndNumTimesConstant.IP_RESETPWD_NUM)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("checkLimitResetPwd:passportId" + userId, e);
            return false;
        }
    }

    private String buildFindPwdResetPwdKey(String userIdOrIp, int clientId) {
        String cacheKey = null;
        if (clientId == 0) {
            cacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_RESETPWDNUM + userIdOrIp +
                    "_" + DateUtil.format(new Date(), DateUtil.DATE_FMT_0);
        } else {
            cacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_RESETPWDNUM + userIdOrIp +
                    "_" + clientId + "_" + DateUtil.format(new Date(), DateUtil.DATE_FMT_0);
        }
        return cacheKey;
    }

    public static boolean isIPAdress(String str) {
        Pattern pattern = Pattern.compile("^((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]|[*])\\.){3}(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]|[*])$");
        return pattern.matcher(str).matches();
    }

    private boolean checkInSubIpList(String ip) throws ServiceException {
        try {
            if (!Strings.isNullOrEmpty(ip) && isIPAdress(ip)) {
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

    @Override
    public boolean isUserInExistBlackList(String username, String ip) throws ServiceException {
        try {
            String username_black_key = buildExistUserBlackKeyStr(username);
            String ip_black_key = buildExistIPBlackKeyStr(ip);

            String value = redisUtils.get(username_black_key);
            if (CommonConstant.LOGIN_IN_BLACKLIST.equals(value)) {
                return true;
            }
            if (!StringUtils.isBlank(ip)) {
                value = redisUtils.get(ip_black_key);
                if (CommonConstant.LOGIN_IN_BLACKLIST.equals(value)) {
                    return true;
                }
            }

            //
            String username_hKey = CacheConstant.CACHE_PREFIX_USERNAME_EXISTNUM + username;
            boolean checkTimes = checkTimesByKey(username_hKey, LoginConstant.EXIST_USERNUM_EXCEED_MAX_LIMIT_COUNT);
            if (checkTimes) {
                redisUtils.setWithinSeconds(username_black_key, CommonConstant.LOGIN_IN_BLACKLIST, DateAndNumTimesConstant.ONE_HOUR_INSECONDS);
                return true;
            }
            if (!Strings.isNullOrEmpty(ip)) {
                String ip_hKey = CacheConstant.CACHE_PREFIX_IP_EXISTNUM + ip;
                boolean checkIpTimes = checkTimesByKey(ip_hKey, LoginConstant.EXIST_IPNUM_EXCEED_MAX_LIMIT_COUNT);
                if (checkIpTimes) {
                    redisUtils.setWithinSeconds(ip_black_key, CommonConstant.LOGIN_IN_BLACKLIST, DateAndNumTimesConstant.ONE_HOUR_INSECONDS);
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            logger.error("checkLoginUserWhiteList:username=" + username + ",ip=" + ip, e);
            return false;
        }
    }

    @Override
    public void incExistTimes(final String username, final String ip) throws ServiceException {
        try {
            String username_hKey = CacheConstant.CACHE_PREFIX_USERNAME_EXISTNUM + username;
            recordTimes(username_hKey, DateAndNumTimesConstant.TIME_ONEHOUR);
            if (!Strings.isNullOrEmpty(ip)) {
                String ip_hKey = CacheConstant.CACHE_PREFIX_IP_EXISTNUM + ip;
                recordTimes(ip_hKey, DateAndNumTimesConstant.TIME_ONEHOUR);
            }

        } catch (Exception e) {
            logger.error("incLoginSuccessTimes:username" + username + ",ip:" + ip, e);
            throw new ServiceException(e);
        }
    }

    @Override
    public void incInterCheckUserTimes(String username, String ip) throws ServiceException {
        try {
            if (!Strings.isNullOrEmpty(username)) {
                recordTimes(CacheConstant.CACHE_PREFIX_CHECK_USER_INTERNAL_USERNAME_NUM + username, DateAndNumTimesConstant.TIME_ONEHOUR);
            }
            if (!Strings.isNullOrEmpty(ip)) {
                recordTimes(CacheConstant.CACHE_PREFIX_CHECK_USER_INTERNAL_IP_NUM + ip, DateAndNumTimesConstant.TIME_ONEHOUR);
            }
        } catch (Exception e) {
            logger.error(String.format("incInterCheckUserTimes error. username:{},ip:{}", username, ip), e);
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean isUserInGetPairtokenBlackList(String username, String ip) throws ServiceException {
        try {
            String username_black_key = buildGetPairtokenBlackKeyStr(username);
            String ip_black_key = buildGetPairtokenIPBlackKeyStr(ip);
            String value = redisUtils.get(username_black_key);
            if (CommonConstant.LOGIN_IN_BLACKLIST.equals(value)) {
                return true;
            }
            if (!StringUtils.isBlank(ip)) {
                value = redisUtils.get(ip_black_key);
                if (CommonConstant.LOGIN_IN_BLACKLIST.equals(value)) {
                    return true;
                }
            }

            //检查次数是否在黑名单当中
            String username_hKey = CacheConstant.CACHE_PREFIX_USERNAME_GETPAIRTOKENNUM + username;
            boolean checkTimes = checkTimesByKey(username_hKey, LoginConstant.GETPAIRTOKEN_USERNAME_EXCEED_MAX_LIMIT_COUNT);
            if (checkTimes) {
                redisUtils.setWithinSeconds(username_black_key, CommonConstant.LOGIN_IN_BLACKLIST, DateAndNumTimesConstant.ONE_HOUR_INSECONDS);
                return true;
            }
            if (!Strings.isNullOrEmpty(ip)) {
                String ip_hKey = CacheConstant.CACHE_PREFIX_IP_GETPAIRTOKENNUM + ip;
                boolean checkIpTimes = checkTimesByKey(ip_hKey, LoginConstant.GETPAIRTOKEN_IP_EXCEED_MAX_LIMIT_COUNT);
                if (checkIpTimes) {
                    redisUtils.setWithinSeconds(ip_black_key, CommonConstant.LOGIN_IN_BLACKLIST, DateAndNumTimesConstant.ONE_HOUR_INSECONDS);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            logger.error("checkLoginUserWhiteList:username=" + username + ",ip=" + ip, e);
            return false;
        }
    }

    @Override
    public void incGetPairTokenTimes(final String username, final String ip) throws ServiceException {
        try {
            String username_hKey = CacheConstant.CACHE_PREFIX_USERNAME_GETPAIRTOKENNUM + username;
            recordTimes(username_hKey, DateAndNumTimesConstant.TIME_ONEHOUR);
            if (!Strings.isNullOrEmpty(ip)) {
                String ip_hKey = CacheConstant.CACHE_PREFIX_IP_GETPAIRTOKENNUM + ip;
                recordTimes(ip_hKey, DateAndNumTimesConstant.TIME_ONEHOUR);
            }

        } catch (Exception e) {
            logger.error("incLoginSuccessTimes:username" + username + ",ip:" + ip, e);
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean checkNickNameExistInBlackList(final String ip, final String cookie) {
        try {
            if (!Strings.isNullOrEmpty(cookie)) {
                String check_nickname_cookie_key = CacheConstant.CACHE_PREFIX_CHECK_NICKNAME_COOKIE_BLACK + cookie;
                String check_nickname_cookie_black_key = CacheConstant.CACHE_PREFIX_CHECK_NICKNAME_EXIST_COOKIE_NUM + cookie;
                String checkNickNameByCookieTimes = redisUtils.get(check_nickname_cookie_black_key);
                if (!Strings.isNullOrEmpty(checkNickNameByCookieTimes) && CommonConstant.SIGN_IN_BLACKLIST.equals(checkNickNameByCookieTimes)) {
                    return true;
                }
                boolean checkCookieTimes = checkTimesByKey(check_nickname_cookie_key, LoginConstant.CHECK_NICKNAME_EXIT_COOKIE_LIMIT_COUNT);
                if (checkCookieTimes) {
                    redisUtils.setWithinSeconds(check_nickname_cookie_black_key, CommonConstant.SIGN_IN_BLACKLIST, DateAndNumTimesConstant.ONE_HOUR_INSECONDS);
                    return true;
                }
            }

            if (!Strings.isNullOrEmpty(ip)) {
                String check_nickname_exist_key = CacheConstant.CACHE_PREFIX_CHECK_NICKNAME_EXIST_IP_NUM + ip;
                String check_nickname_exist_black_key = CacheConstant.CACHE_PREFIX_EXIST_NICKNAME_IP_BLACK + ip;
                String checkNickNameByIPTimesValue = redisUtils.get(check_nickname_exist_black_key);
                if (!Strings.isNullOrEmpty(checkNickNameByIPTimesValue) && CommonConstant.SIGN_IN_BLACKLIST.equals(checkNickNameByIPTimesValue)) {
                    return true;
                }
                boolean checkIpTimes = checkTimesByKey(check_nickname_exist_key, LoginConstant.CHECK_NICKNAME_EXIST_MAX_LIMIT_COUNT);
                if (checkIpTimes) {
                    redisUtils.setWithinSeconds(check_nickname_exist_black_key, CommonConstant.SIGN_IN_BLACKLIST, DateAndNumTimesConstant.ONE_HOUR_INSECONDS);
                    return true;
                }
            } else {
                logger.warn("checkNickNameExistInBlackList IP is NULL.");
            }
        } catch (Exception e) {
            logger.error("checkNickNameExistInBlackList error. IP:" + ip, e);
            throw new ServiceException(e);
        }
        return false;
    }

    @Override
    public void incCheckNickNameExistTimes(final String ip, final String cookie) {
        try {
            if (!Strings.isNullOrEmpty(cookie)) {
                recordTimes(CacheConstant.CACHE_PREFIX_CHECK_NICKNAME_COOKIE_BLACK + cookie, DateAndNumTimesConstant.TIME_ONEHOUR);
            } else {
                logger.warn("incCheckNickNameExistTimes cookie is NULL");
            }

            if (!Strings.isNullOrEmpty(ip)) {
                recordTimes(CacheConstant.CACHE_PREFIX_CHECK_NICKNAME_EXIST_IP_NUM + ip, DateAndNumTimesConstant.TIME_ONEHOUR);
            } else {
                logger.warn("incCheckNickNameExistTimes IP is NULL");
            }
        } catch (Exception e) {
            logger.error("incCheckNickNameExistTimes error.IP:" + ip);
        }
    }

    private static String buildGetPairtokenBlackKeyStr(String username) {
        return CacheConstant.CACHE_PREFIX_GETPAIRTOKEN_USERNAME_BLACK_ + username;
    }


    private static String buildGetPairtokenIPBlackKeyStr(String ip) {
        return CacheConstant.CACHE_PREFIX_GETPAIRTOKEN__IP_BLACK_ + ip;
    }


    private static String buildExistUserBlackKeyStr(String username) {
        return CacheConstant.CACHE_PREFIX_EXIST_USERNAME_BLACK_ + username;
    }


    private static String buildExistIPBlackKeyStr(String ip) {
        return CacheConstant.CACHE_PREFIX_EXIST_IP_BLACK_ + ip;
    }


    private static String buildLoginUserNameBlackKeyStr(String username) {
        return CacheConstant.CACHE_PREFIX_LOGIN_USERNAME_BLACK_ + username;
    }

    private static String buildLoginIPBlackKeyStr(String ip) {
        return CacheConstant.CACHE_PREFIX_LOGIN_IP_BLACK_ + ip;
    }

    private static String buildUserNameLoginTimesKeyStr(String username) {
        return CacheConstant.CACHE_PREFIX_USERNAME_LOGINNUM + username;
    }

    private static String buildIPLoginTimesKeyStr(String ip) {
        return CacheConstant.CACHE_PREFIX_IP_LOGINNUM + ip;
    }
}