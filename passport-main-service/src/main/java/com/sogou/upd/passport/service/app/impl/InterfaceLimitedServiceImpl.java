package com.sogou.upd.passport.service.app.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.dao.app.ConfigDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.config.ClientIdLevelMapping;
import com.sogou.upd.passport.model.config.InterfaceLevelMapping;
import com.sogou.upd.passport.service.app.InterfaceLimitedService;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: mayan
 * Date: 13-10-31
 * Time: 下午5:01
 * To change this template use File | Settings | File Templates.
 */
@Service
public class InterfaceLimitedServiceImpl implements InterfaceLimitedService {
    private static final Logger logger = LoggerFactory.getLogger("interfaceLimitedLogger");

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private ConfigDAO configDAO;

    private static final float INTERFACE_PERCENT = (float) 0.01;

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

    private static final String CACHE_PREFIX_PASSPORT_INTER_AND_LEVEL = CacheConstant.CACHE_PREFIX_CLIENTID_INTERFACE_LIMITED_INIT;

    private String buildCacheKey(int clientId) {
        return CACHE_PREFIX_PASSPORT_INTER_AND_LEVEL + clientId;
    }

    private Map<String, String> getMapsFromCacheKey(int clientId) throws ServiceException {
        String cacheKey = buildCacheKey(clientId);
        Map<String, String> maps;
        List<InterfaceLevelMapping> list;
        try {
            //先读缓存
            maps = redisUtils.hGetAll(cacheKey);
            //如果没有，读数据库
            if (maps == null && maps.size() == 0) {
                //先根据应用id得到该应用对应的等级
                ClientIdLevelMapping clm = configDAO.findLevelByClientId(clientId);
                if (clm != null) {
                    String level = clm.getLevelInfo();
                    //再根据该等级读出此等级下所有接口及对应的频次限制次数
                    list = configDAO.getInterfaceListAll();
                    if (list != null && list.size() > 0) {
                        for (InterfaceLevelMapping inter : list) {
                            String key = inter.getInterfaceName();
                            String value = getValue(inter, level);
                            maps.put(key, value);
                        }
                        return maps;
                    }
                }
            }
        } catch (Exception e) {
            throw new ServiceException();
        }
        return maps;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private String getValue(InterfaceLevelMapping inter, String level) {
        String value = null;
        switch (level) {
            case "0":
                value = inter.getPrimaryLevelCount();
                break;
            case "1":
                value = inter.getMiddleLevelCount();
                break;
            case "2":
                value = inter.getHighLevelCount();
                break;
        }
        return value;
    }

    @Override
    public Map<Object, Object> initInterfaceTimes(int clientId, String url) {

        Map map = null;
        //从缓存中获取接口初始化限制次数
        Map<String, String> interfaceTimesMapping = getMapsFromCacheKey(clientId);
        if (MapUtils.isNotEmpty(interfaceTimesMapping)) {
            String interfaceTimes = interfaceTimesMapping.get(url);
            if (!Strings.isNullOrEmpty(interfaceTimes)) {
                map = new ConcurrentHashMap();
                //在受限制的接口列表内 ，每台机器从缓存中获取3/100的限制数
                int getTime = (int) Math.floor(Float.parseFloat(interfaceTimes) * INTERFACE_PERCENT);

                AtomicInteger atomicGetTimes = new AtomicInteger(getTime == 0 ? 1 : getTime);
                map.put("getTimes", atomicGetTimes);     //内存初始化次数
                map.put("interfaceTimes", interfaceTimes);     //缓存初始化总次数
                //初始化缓存
                String cacheKey = null;
                if (atomicGetTimes != null && atomicGetTimes.get() != 0) {
                    cacheKey = CacheConstant.CACHE_PREFIX_CLIENTID_INTERFACE_LIMITED + clientId;
                    String cacheTimes = redisUtils.hGet(cacheKey, url);
                    if (Strings.isNullOrEmpty(cacheTimes)) {
                        //初始化或者5分钟失效后的初始化
                        initAppLimitedList(cacheKey, url, interfaceTimes); //5分钟限制的次数
                        cacheTimes = Integer.toString(atomicGetTimes.get());
                    }
                    long times = Long.parseLong(cacheTimes);
                    if (times <= 0) {
                        logger.info("ClientId:"+clientId +",Url:"+url+",Time:"+new SimpleDateFormat("HH:mm:ss").format(new Date())+",over:"+times);
                        map.put("flag", false);
                        return map;
                    } else {
                        logger.info("ClientId:"+clientId +",Url:"+url+",Time:"+new SimpleDateFormat("HH:mm:ss").format(new Date())+",redis Surplus before:"+cacheTimes);
                        redisUtils.hIncrByTimes(cacheKey, url, -atomicGetTimes.get());
                        logger.info("ClientId:"+clientId +",Url:"+url+",Time:"+new SimpleDateFormat("HH:mm:ss").format(new Date())+",redis Surplus after:"+redisUtils.hGet(cacheKey,url));
                    }
                    map.put("flag", true);
                    return map;
                }
                map.put("flag", false);
            }
        }
        return map;
    }
}
