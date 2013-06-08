package com.sogou.upd.passport.service.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.LoginConstant;
import com.sogou.upd.passport.common.utils.DateUtil;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.service.account.OperateTimesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

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
            recordTimes(userNameCacheKey,DateAndNumTimesConstant.TIME_ONEHOUR);

            String ipCacheKey = CacheConstant.CACHE_PREFIX_IP_LOGINSUCCESSNUM + ip;
            recordTimes(ipCacheKey,DateAndNumTimesConstant.TIME_ONEHOUR);
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
            recordTimes(userNameCacheKey,DateAndNumTimesConstant.TIME_ONEHOUR);

            String ipCacheKey = CacheConstant.CACHE_PREFIX_IP_LOGINFAILEDNUM + ip;
            recordTimes(ipCacheKey,DateAndNumTimesConstant.TIME_ONEHOUR);
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
            //失败次数
            String loginFailedUserNameKey = CacheConstant.CACHE_PREFIX_USERNAME_LOGINFAILEDNUM + username;
            result = checkTimesByKey(loginFailedUserNameKey, LoginConstant.LOGIN_FAILED_EXCEED_MAX_LIMIT_COUNT);
            if (result) {
                return true;
            }

            String loginFailedIPKey = CacheConstant.CACHE_PREFIX_IP_LOGINFAILEDNUM + ip;
            result = checkTimesByKey(loginFailedIPKey, LoginConstant.LOGIN_IP_FAILED_EXCEED_MAX_LIMIT_COUNT);
            if (result) {
                return true;
            }

            //成功次数
            String loginSuccessUserNameKey = CacheConstant.CACHE_PREFIX_USERNAME_LOGINSUCCESSNUM + username;
            result = checkTimesByKey(loginSuccessUserNameKey, LoginConstant.LOGIN_SUCCESS_EXCEED_MAX_LIMIT_COUNT);
            if (result) {
                return true;
            }

            String loginSuccessIPKey = CacheConstant.CACHE_PREFIX_IP_LOGINSUCCESSNUM + ip;
            result = checkTimesByKey(loginSuccessIPKey, LoginConstant.LOGIN_IP_SUCCESS_EXCEED_MAX_LIMIT_COUNT);
            if (result) {
                return true;
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
    public long incRegIPTimes(String ip) throws ServiceException {
        try {
            String cacheKey =  CacheConstant.CACHE_PREFIX_REGISTER_IPBLACKLIST + ip;
            return recordTimes(cacheKey, DateAndNumTimesConstant.TIME_ONEDAY);
        } catch (Exception e) {
            logger.error("incRegIPTimes:ip" + ip, e);
            throw new ServiceException(e);
        }

    }

    @Override
    public boolean checkRegIPInBlackList(String ip) throws ServiceException {
        try {
            String cacheKey =  CacheConstant.CACHE_PREFIX_REGISTER_IPBLACKLIST + ip;
            return checkTimesByKey(cacheKey, LoginConstant.IP_LIMITED);
        } catch (Exception e) {
            logger.error("checkRegIPInBlackList:ip" + ip, e);
            throw new ServiceException(e);
        }
    }

}
