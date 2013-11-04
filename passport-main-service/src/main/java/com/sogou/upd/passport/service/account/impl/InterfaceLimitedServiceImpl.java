package com.sogou.upd.passport.service.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.service.account.InterfaceLimitedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User: mayan
 * Date: 13-10-31
 * Time: 下午5:01
 * To change this template use File | Settings | File Templates.
 */
@Service
public class InterfaceLimitedServiceImpl implements InterfaceLimitedService {
    private static final Logger logger = LoggerFactory.getLogger(InterfaceLimitedServiceImpl.class);

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public void initAppLimitedList(String cacheKey, String key, String limiTimes) throws ServiceException{
        try {
            if(Strings.isNullOrEmpty(redisUtils.hGet(cacheKey, key))){
                redisUtils.hPutExpire(cacheKey, key, limiTimes, DateAndNumTimesConstant.TIME_FIVE_MINITUES);
            }
        }catch (Exception e){
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean isObtainLimitedTimesSuccess(String key, int appId,String getTimes,String interfaceTimes) {
        String cacheKey= CacheConstant.CACHE_PREFIX_CLIENTID_INTERFACE_LIMITED + appId;
        String cacheTimes=redisUtils.hGet(cacheKey,key);
        if(Strings.isNullOrEmpty(cacheTimes)){
            //初始化或者5分钟失效后的初始化
            initAppLimitedList(cacheKey,key,interfaceTimes); //5分钟限制的次数
            cacheTimes=interfaceTimes;
        }
        long times=Long.parseLong(cacheTimes);
        if(times <= 0){
            return false;
        }else {
            redisUtils.hIncrByTimes(cacheKey,key,-Integer.parseInt(getTimes));
        }
        return true;
    }
}
