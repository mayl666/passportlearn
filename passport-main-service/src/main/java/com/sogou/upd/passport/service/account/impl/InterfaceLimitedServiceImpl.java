package com.sogou.upd.passport.service.account.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.service.account.InterfaceLimitedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

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

    private static final float INTERFACE_PERCENT = (float) 0.03;

    @Override
    public void initAppLimitedList(String cacheKey, String key, String limiTimes) throws ServiceException {
        try {
            if (Strings.isNullOrEmpty(redisUtils.hGet(cacheKey, key))) {
                redisUtils.hPutExpire(cacheKey, key, limiTimes, DateAndNumTimesConstant.TIME_FIVE_MINITUES);
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Map<Object, Object> isObtainLimitedTimesSuccess(int clientId, String url) {

        Map map = Maps.newHashMap();

        //获取接口初始化限制次数
        String cacheKey = CacheConstant.CACHE_PREFIX_CLIENTID_INTERFACE_LIMITED_INIT + clientId;
        String interfaceTimes = redisUtils.hGet(cacheKey, url);

        //在受限制的接口列表内 ，每台机器从缓存中获取3/100的限制数
        int getTime = (int) Math.floor(Float.parseFloat(interfaceTimes) * INTERFACE_PERCENT);
        int getTimes = getTime == 0 ? 1 : getTime;
        map.put("getTimes", getTimes);
        map.put("interfaceTimes", interfaceTimes);

        if (getTimes!=0) {
            cacheKey = CacheConstant.CACHE_PREFIX_CLIENTID_INTERFACE_LIMITED + clientId;
            String cacheTimes = redisUtils.hGet(cacheKey, url);
            if (Strings.isNullOrEmpty(cacheTimes)) {
                //初始化或者5分钟失效后的初始化
                initAppLimitedList(cacheKey, url, interfaceTimes); //5分钟限制的次数
                cacheTimes = interfaceTimes;
            }
            long times = Long.parseLong(cacheTimes);
            if (times <= 0) {
                map.put("flag", false);
                return map;
            } else {
                redisUtils.hIncrByTimes(cacheKey, url, -getTimes);
            }
            map.put("flag", true);
            return map;
        }
        map.put("flag", false);
        return map;
    }
}
