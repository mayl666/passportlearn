package com.sogou.upd.passport.service.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.LoginConstant;
import com.sogou.upd.passport.common.utils.DateUtil;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.service.account.OperateTimesService;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * User: chenjiameng Date: 13-6-8 Time: 下午3:38 To change this template use File | Settings | File Templates.
 */
@Service
public class OperateTimesServiceImpl implements OperateTimesService {

    private static final Logger logger = LoggerFactory.getLogger(OperateTimesServiceImpl.class);
    @Autowired
    private RedisUtils redisUtils;

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
    public long incLoginSuccessTimes(String username, String ip) throws ServiceException {
        try {
            String userNameCacheKey = CacheConstant.CACHE_PREFIX_USERNAME_LOGINSUCCESSNUM + username;
            recordTimes(userNameCacheKey, DateAndNumTimesConstant.TIME_ONEHOUR);

            if (!Strings.isNullOrEmpty(ip)) {
                String ipCacheKey = CacheConstant.CACHE_PREFIX_IP_LOGINSUCCESSNUM + ip;
                recordTimes(ipCacheKey, DateAndNumTimesConstant.TIME_ONEHOUR);
            }

        } catch (Exception e) {
            logger.error("incLoginSuccessTimes:username" + username + ",ip:" + ip, e);
            throw new ServiceException(e);
        }
        return 1;
    }


    @Override
    public long incLoginFailedTimes(String username, String ip) throws ServiceException {
        try {
            String userNameCacheKey = CacheConstant.CACHE_PREFIX_USERNAME_LOGINFAILEDNUM + username;
            recordTimes(userNameCacheKey, DateAndNumTimesConstant.TIME_ONEHOUR);

            if (!Strings.isNullOrEmpty(ip)) {
                String ipCacheKey = CacheConstant.CACHE_PREFIX_IP_LOGINFAILEDNUM + ip;
                recordTimes(ipCacheKey, DateAndNumTimesConstant.TIME_ONEHOUR);
            }

        } catch (Exception e) {
            logger.error("incLoginFailedTimes:username" + username + ",ip:" + ip, e);
            throw new ServiceException(e);
        }
        return 1;
    }

    @Override
    public boolean checkLoginUserInBlackList(String username, String ip) throws ServiceException {
        boolean result = false;
        try {
            //username
            String loginFailedUserNameKey = CacheConstant.CACHE_PREFIX_USERNAME_LOGINFAILEDNUM + username;
            result = checkTimesByKey(loginFailedUserNameKey, LoginConstant.LOGIN_FAILED_EXCEED_MAX_LIMIT_COUNT);
            if (result) {
                return true;
            }

            String loginSuccessUserNameKey = CacheConstant.CACHE_PREFIX_USERNAME_LOGINSUCCESSNUM + username;
            result = checkTimesByKey(loginSuccessUserNameKey, LoginConstant.LOGIN_SUCCESS_EXCEED_MAX_LIMIT_COUNT);
            if (result) {
                return true;
            }

            //IP

            if(!Strings.isNullOrEmpty(ip)) {
                String loginFailedIPKey = CacheConstant.CACHE_PREFIX_IP_LOGINFAILEDNUM + ip;
                result = checkTimesByKey(loginFailedIPKey, LoginConstant.LOGIN_IP_FAILED_EXCEED_MAX_LIMIT_COUNT);
                if (result) {
                    return true;
                }

                String loginSuccessIPKey = CacheConstant.CACHE_PREFIX_IP_LOGINSUCCESSNUM + ip;
                result = checkTimesByKey(loginSuccessIPKey, LoginConstant.LOGIN_IP_SUCCESS_EXCEED_MAX_LIMIT_COUNT);
                if (result) {
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("userInBlackList:username" + username + ",ip:" + ip, e);
            throw new ServiceException(e);
        }
        return false;
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
    public long incRegTimes(String ip,String cookieStr) throws ServiceException {
        try {
            String ipCacheKey =  CacheConstant.CACHE_PREFIX_REGISTER_IPBLACKLIST + ip;
            recordTimes(ipCacheKey, DateAndNumTimesConstant.TIME_ONEDAY);

//            String cookieCacheKey =  CacheConstant.CACHE_PREFIX_REGISTER_COOKIEBLACKLIST + cookieStr;
//            recordTimes(cookieCacheKey, DateAndNumTimesConstant.TIME_ONEDAY);

        } catch (Exception e) {
            logger.error("incRegIPTimes:ip" + ip, e);
            throw new ServiceException(e);
        }
        return 1;

    }

    @Override
    public boolean checkRegInBlackList(String ip,String cookieStr) throws ServiceException {
        //修改为list模式添加cookie处理 by mayan
        try {
            //ip与cookie映射
            String ipCacheKey =  CacheConstant.CACHE_PREFIX_REGISTER_IPBLACKLIST + ip;
            List<String> listCookieVal= redisUtils.getList(ipCacheKey);
            if (CollectionUtils.isNotEmpty(listCookieVal)) {
              int sz = listCookieVal.size();
              if (sz >= LoginConstant.REGISTER_IP_LIMITED) {
                 return true;
              }
            }
            //cookie与ip映射
            String cookieCacheKey =  CacheConstant.CACHE_PREFIX_REGISTER_COOKIEBLACKLIST + cookieStr;
            List<String> listIpVal= redisUtils.getList(cookieCacheKey);
            if (CollectionUtils.isNotEmpty(listIpVal)) {
              int sz = listIpVal.size();
              if (sz >= LoginConstant.REGISTER_COOKIE_LIMITED) {
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
        boolean result = false;
        try {
            // 根据username判断是否需要弹出验证码
            String userNameCacheKey = CacheConstant.CACHE_PREFIX_USERNAME_LOGINFAILEDNUM + username;
            result = checkTimesByKey(userNameCacheKey, LoginConstant.LOGIN_FAILED_NEED_CAPTCHA_LIMIT_COUNT);
            if(result){
                return true;
            }
            //  根据ip判断是否需要弹出验证码
            if(!Strings.isNullOrEmpty(ip)){
                String ipCacheKey = CacheConstant.CACHE_PREFIX_IP_LOGINFAILEDNUM + ip;
                result = checkTimesByKey(ipCacheKey, LoginConstant.LOGIN_FAILED_NEED_CAPTCHA_IP_LIMIT_COUNT);
                if(result){
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("getAccountLoginFailedCount:username" + username+",ip:"+ip, e);
            throw new ServiceException(e);
        }
        return false;
    }

    @Override
    public long incAddProblemTimes(String passportId,String ip) throws ServiceException {
        try {
            String passportIdCacheKey =  CacheConstant.CACHE_PREFIX_PROBLEM_PASSPORTIDINBLACKLIST + passportId;
            recordTimes(passportIdCacheKey, DateAndNumTimesConstant.TIME_ONEDAY);

            if(!Strings.isNullOrEmpty(ip)){
                String ipCacheKey =  CacheConstant.CACHE_PREFIX_PROBLEM_IPINBLACKLIST + ip;
                recordTimes(ipCacheKey, DateAndNumTimesConstant.TIME_ONEDAY);
            }

        } catch (Exception e) {
            logger.error("incAddProblemTimes:passportId"+passportId+",ip" + ip, e);
            throw new ServiceException(e);
        }
        return 1;
    }
    @Override
    public boolean checkAddProblemInBlackList(String passportId,String ip) throws ServiceException {
        boolean result = false;
        try {
            String passportIdCacheKey =  CacheConstant.CACHE_PREFIX_PROBLEM_PASSPORTIDINBLACKLIST + passportId;
            result = checkTimesByKey(passportIdCacheKey, LoginConstant.ADDPROBLEM_PASSPORTID_LIMITED);
            if(result){
                return true;
            }
            if(!Strings.isNullOrEmpty(ip)){
                String ipCacheKey =  CacheConstant.CACHE_PREFIX_PROBLEM_IPINBLACKLIST + ip;
                result = checkTimesByKey(ipCacheKey, LoginConstant.ADDPROBLEM_IP_LIMITED);
                if(result){
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("checkRegIPInBlackList:ip" + ip, e);
            throw new ServiceException(e);
        }
        return result;
    }

    @Override
    public boolean incLimitBindEmail(String userId, int clientId) throws ServiceException {
        try {
            String cacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_BINDEMAILNUM + userId +
                    "_" + DateUtil.format(new Date(), DateUtil.DATE_FMT_0);
            recordTimes(cacheKey, DateAndNumTimesConstant.TIME_ONEDAY);
            return true;
        } catch (Exception e) {
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
            return false;
        }
    }

    public boolean checkLimitBindEmail(String userId, int clientId) throws ServiceException {
        try {
            String cacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_BINDEMAILNUM + userId +
                              "_" + DateUtil.format(new Date(), DateUtil.DATE_FMT_0);
            return !checkTimesByKey(cacheKey, DateAndNumTimesConstant.BIND_LIMIT);
        } catch (Exception e) {
            return true;
        }
    }

    public boolean checkLimitBindMobile(String userId, int clientId) throws ServiceException {
        try {
            String cacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_BINDMOBILENUM + userId +
                              "_" + DateUtil.format(new Date(), DateUtil.DATE_FMT_0);
            return !checkTimesByKey(cacheKey, DateAndNumTimesConstant.BIND_LIMIT);
        } catch (Exception e) {
            return true;
        }
    }

    public boolean checkLimitBindQues(String userId, int clientId) throws ServiceException {
        try {
            String cacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_BINDQUESNUM + userId +
                              "_" + DateUtil.format(new Date(), DateUtil.DATE_FMT_0);
            return !checkTimesByKey(cacheKey, DateAndNumTimesConstant.BIND_LIMIT);
        } catch (Exception e) {
            return true;
        }
    }
}
