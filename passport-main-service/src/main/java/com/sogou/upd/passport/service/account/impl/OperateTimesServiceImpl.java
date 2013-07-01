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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * User: chenjiameng Date: 13-6-8 Time: 下午3:38 To change this template use File | Settings | File Templates.
 */
@Service
public class OperateTimesServiceImpl implements OperateTimesService {

    private static final Logger logger = LoggerFactory.getLogger(OperateTimesServiceImpl.class);
    private static final org.apache.log4j.Logger regBlackListLogger = org.apache.log4j.Logger.getLogger("com.sogou.upd.passport.blackListFileAppender");
    private static final org.apache.log4j.Logger loginBlackListLogger = org.apache.log4j.Logger.getLogger("com.sogou.upd.passport.loginBlackListFileAppender");
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private ThreadPoolTaskExecutor loginAfterTaskExecutor;
    @Autowired
    private ThreadPoolTaskExecutor regAfterTaskExecutor;

    @Override
    public long recordTimes(String cacheKey,long timeout) throws ServiceException {
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
    public boolean checkTimesByKeyList(List<String> keyList, List<Integer> maxList) throws ServiceException {
        if (CollectionUtils.isEmpty(keyList) || CollectionUtils.isEmpty(maxList)) {
            return false;
        }
        try {
            List<String> valueList = redisUtils.multiGet(keyList);
            if (valueList != null) {
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
            if (num == (max/2)) {
                return true;
            }
        } catch (Exception e) {
            logger.error("checkNumByKey:" + cacheKey + ",max:" + max, e);
            throw new ServiceException(e);
        }
        return false;
    }

    @Override
    public long incLoginSuccessTimes(final String username,final String ip) throws ServiceException {
        try {
            loginAfterTaskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    String userNameCacheKey = CacheConstant.CACHE_PREFIX_USERNAME_LOGINSUCCESSNUM + username;
                    recordTimes(userNameCacheKey, DateAndNumTimesConstant.TIME_ONEHOUR);

                    if(isHalfTimes(userNameCacheKey,LoginConstant.LOGIN_SUCCESS_EXCEED_MAX_LIMIT_COUNT)){
                        loginBlackListLogger.info(new Date()+",incLoginSuccessTimes,userNameCacheKey="+userNameCacheKey
                                +",userNameLoginSuccessTimes="+LoginConstant.LOGIN_SUCCESS_EXCEED_MAX_LIMIT_COUNT/2+",ip="+ip);
                    }

                    if (!Strings.isNullOrEmpty(ip)) {
                        String ipCacheKey = CacheConstant.CACHE_PREFIX_IP_LOGINSUCCESSNUM + ip;
                        recordTimes(ipCacheKey, DateAndNumTimesConstant.TIME_ONEHOUR);
                        if(isHalfTimes(ipCacheKey,LoginConstant.LOGIN_IP_SUCCESS_EXCEED_MAX_LIMIT_COUNT)){
                            loginBlackListLogger.info(new Date()+",incLoginSuccessTimes,ipCacheKey="+ipCacheKey
                                    +",ipLoginSuccessTimes="+LoginConstant.LOGIN_IP_SUCCESS_EXCEED_MAX_LIMIT_COUNT/2+",username="+username);
                        }
                    }

                }
            });

        } catch (Exception e) {
            logger.error("incLoginSuccessTimes:username" + username + ",ip:" + ip, e);
            throw new ServiceException(e);
        }
        return 1;
    }

    @Override
    public long incLoginFailedTimes(final String username,final String ip) throws ServiceException {
        try {
            loginAfterTaskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    String userNameCacheKey = CacheConstant.CACHE_PREFIX_USERNAME_LOGINFAILEDNUM + username;
                    recordTimes(userNameCacheKey, DateAndNumTimesConstant.TIME_ONEHOUR);
                    if(isHalfTimes(userNameCacheKey,LoginConstant.LOGIN_FAILED_EXCEED_MAX_LIMIT_COUNT)){
                        loginBlackListLogger.info(new Date()+",incLoginFailedTimes,userNameCacheKey="+userNameCacheKey
                                +",userNameLoginFailedTimes="+LoginConstant.LOGIN_FAILED_EXCEED_MAX_LIMIT_COUNT/2+",ip="+ip);
                    }
                    if (!Strings.isNullOrEmpty(ip)) {
                        String ipCacheKey = CacheConstant.CACHE_PREFIX_IP_LOGINFAILEDNUM + ip;
                        recordTimes(ipCacheKey, DateAndNumTimesConstant.TIME_ONEHOUR);
                        if(isHalfTimes(ipCacheKey,LoginConstant.LOGIN_FAILED_NEED_CAPTCHA_IP_LIMIT_COUNT)){
                            loginBlackListLogger.info(new Date()+",incLoginFailedTimes,ipCacheKey="+ipCacheKey
                                    +",ipLoginFailedTimes="+LoginConstant.LOGIN_FAILED_NEED_CAPTCHA_IP_LIMIT_COUNT/2+",username="+username);
                        }
                    }
                }
            });
        } catch (Exception e) {
            logger.error("incLoginFailedTimes:username" + username + ",ip:" + ip, e);
            throw new ServiceException(e);
        }
        return 1;
    }

    @Override
    public boolean checkLoginUserInBlackList(String username) throws ServiceException {
        try {
            List<String> keyList = new ArrayList<String>();
            List<Integer> maxList = new ArrayList<Integer>();
            //username
            String loginFailedUserNameKey = CacheConstant.CACHE_PREFIX_USERNAME_LOGINFAILEDNUM + username;
            keyList.add(loginFailedUserNameKey);
            maxList.add(LoginConstant.LOGIN_FAILED_EXCEED_MAX_LIMIT_COUNT);

            String loginSuccessUserNameKey = CacheConstant.CACHE_PREFIX_USERNAME_LOGINSUCCESSNUM + username;
            keyList.add(loginSuccessUserNameKey);
            maxList.add(LoginConstant.LOGIN_SUCCESS_EXCEED_MAX_LIMIT_COUNT);
            boolean result = checkTimesByKeyList(keyList,maxList);

            if(result){
                loginBlackListLogger.info(new Date()+",checkLoginUserInBlackList,keyList="+keyList.toString()+",maxList="+maxList.toString());
            }
            return result;
        } catch (Exception e) {
            logger.error("userInBlackList:username" + username, e);
            throw new ServiceException(e);
        }
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
    public void incRegTimes(final String ip,final String cookieStr) throws ServiceException {
        regAfterTaskExecutor.execute(new Runnable() {
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
    public boolean checkRegInBlackList(String ip, String cookieStr) throws ServiceException {
        //修改为list模式添加cookie处理 by mayan
        try {
            //cookie与ip映射
            String cookieCacheKey = CacheConstant.CACHE_PREFIX_REGISTER_COOKIEBLACKLIST + cookieStr;
            Set<String> setIpVal = redisUtils.smember(cookieCacheKey);
            if (CollectionUtils.isNotEmpty(setIpVal)) {
                int sz = setIpVal.size();
                if(sz == (LoginConstant.REGISTER_COOKIE_LIMITED/2)){
                    regBlackListLogger.info(new Date()+",checkRegInBlackList,cookieCacheKey=" + cookieCacheKey
                            +",ipSize="+sz+",ipSet="+setIpVal.toArray().toString());
                }
                if (sz >= LoginConstant.REGISTER_COOKIE_LIMITED) {
                    regBlackListLogger.info(new Date()+"checkRegInBlackList,cookieCacheKey=" + cookieCacheKey
                            +",ipSize="+sz+",ipSet="+setIpVal.toArray().toString());
                    return true;
                }
            }

            //通过ip+cookie限制注册次数
            String ipCookieKey = CacheConstant.CACHE_PREFIX_REGISTER_IPBLACKLIST + ip + "_" + cookieStr;
            String value = redisUtils.get(ipCookieKey);
            if (!Strings.isNullOrEmpty(value)) {
                int num = Integer.valueOf(value);
                if (num ==  (LoginConstant.REGISTER_IP_COOKIE_LIMITED/2)){
                    regBlackListLogger.info(new Date()+",checkRegInBlackList,ipCookieKey=" + ipCookieKey
                            +",num="+num);
                }
                if (num >=  LoginConstant.REGISTER_IP_COOKIE_LIMITED) {
                    regBlackListLogger.info(new Date()+",checkRegInBlackList,ipCookieKey=" + ipCookieKey
                            +",num="+num);
                    return true;
                }
            }

            //ip与cookie映射
            String ipCacheKey = CacheConstant.CACHE_PREFIX_REGISTER_IPBLACKLIST + ip;
            Set<String> setCookieVal = redisUtils.smember(ipCacheKey);
            if (CollectionUtils.isNotEmpty(setCookieVal)) {
                int sz = setCookieVal.size();
                if(sz == (LoginConstant.REGISTER_IP_LIMITED/2)){
                    regBlackListLogger.info(new Date()+",checkRegInBlackList,ipCacheKey=" + ipCacheKey
                            +",setCookieVal="+sz+",setCookieVal="+setCookieVal.toArray().toString());
                }
                if (sz >= LoginConstant.REGISTER_IP_LIMITED) {
                    regBlackListLogger.info(new Date()+",checkRegInBlackList,ipCacheKey=" + ipCacheKey
                            +",setCookieVal="+sz+",setCookieVal="+setCookieVal.toArray().toString());
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
    public boolean loginFailedTimesNeedCaptcha(String username,String ip) throws ServiceException{
        try {
            List<String> keyList = new ArrayList<String>();
            List<Integer> maxList = new ArrayList<Integer>();
            // 根据username判断是否需要弹出验证码
            String userNameCacheKey = CacheConstant.CACHE_PREFIX_USERNAME_LOGINFAILEDNUM + username;
            keyList.add(userNameCacheKey);
            maxList.add(LoginConstant.LOGIN_FAILED_NEED_CAPTCHA_LIMIT_COUNT);
            //  根据ip判断是否需要弹出验证码
            if(!Strings.isNullOrEmpty(ip)){
                //一小时内ip登陆失败20次出验证码
                String ipFailedCacheKey = CacheConstant.CACHE_PREFIX_IP_LOGINFAILEDNUM + ip;
                keyList.add(ipFailedCacheKey);
                maxList.add(LoginConstant.LOGIN_FAILED_NEED_CAPTCHA_IP_LIMIT_COUNT);

                //一小时内ip登陆成功100次出验证码
                String ipSuccessCacheKey = CacheConstant.CACHE_PREFIX_IP_LOGINSUCCESSNUM + ip;
                keyList.add(ipSuccessCacheKey);
                maxList.add(LoginConstant.LOGIN_IP_SUCCESS_EXCEED_MAX_LIMIT_COUNT);
            }
            return checkTimesByKeyList(keyList,maxList);
        } catch (Exception e) {
            logger.error("getAccountLoginFailedCount:username" + username+",ip:"+ip, e);
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
    public boolean incLimitBindEmail(String userId, int clientId) throws ServiceException {
        try {
            String cacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_BINDEMAILNUM + userId +
                    "_" + DateUtil.format(new Date(), DateUtil.DATE_FMT_0);
            recordTimes(cacheKey, DateAndNumTimesConstant.TIME_ONEDAY);
            return true;
        } catch (Exception e) {
            logger.error("incLimitBindEmail:passportId"+userId, e);
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
            logger.error("incLimitBindMobile:passportId"+userId, e);
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
            logger.error("incLimitBindQues:passportId"+userId, e);
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
            logger.error("checkLimitBindEmail:passportId"+userId, e);
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
            logger.error("checkLimitBindMobile:passportId"+userId, e);
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
            logger.error("checkLimitBindQues:passportId"+userId, e);
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
            logger.error("incLimitResetPwd:passportId"+userId, e);
            return false;
        }
    }

    @Override
    public boolean checkLimitResetPwd(String userId, int clientId) throws ServiceException {
        try {
            String cacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_RESETPWDNUM + userId +
                              "_" + clientId + "_" + DateUtil.format(new Date(), DateUtil.DATE_FMT_0);
            return !checkTimesByKey(cacheKey, DateAndNumTimesConstant.RESETPWD_NUM);
        } catch (Exception e) {
            logger.error("checkLimitResetPwd:passportId"+userId, e);
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
            logger.error("incLimitCheckPwdFail:passportId"+userId, e);
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
            logger.error("checkLimitCheckPwdFail:passportId"+userId, e);
            return true;
        }
    }
}
