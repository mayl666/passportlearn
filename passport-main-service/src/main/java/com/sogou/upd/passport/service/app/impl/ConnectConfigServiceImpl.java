package com.sogou.upd.passport.service.app.impl;

import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.dao.app.ConnectConfigDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.service.app.ConnectConfigService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-22
 * Time: 上午1:16
 * To change this template use File | Settings | File Templates.
 */
@Service
public class ConnectConfigServiceImpl implements ConnectConfigService {

    private Logger logger = LoggerFactory.getLogger(ConnectConfigService.class);
    private static final String CACHE_PREFIX_CLIENTID = CacheConstant.CACHE_PREFIX_CLIENTID_CONNECTCONFIG; //clientid与connectConfig映射
    private static LoadingCache<String, ConnectConfig> connectCacheByAppId = null;
    private static LoadingCache<String, ConnectConfig> connectCacheByClientId = null;

    @Autowired
    private ConnectConfigDAO connectConfigDAO;
    @Inject
    private RedisUtils redisUtils;

    public ConnectConfigServiceImpl() {
        connectCacheByAppId = CacheBuilder.newBuilder()
                .refreshAfterWrite(CacheConstant.CACHE_REFRESH_INTERVAL, TimeUnit.MINUTES)
                .build(new CacheLoader<String, ConnectConfig>() {
                    @Override
                    public ConnectConfig load(String key) throws Exception {
                        return loadConnectByAppId(key);
                    }
                });

        connectCacheByClientId = CacheBuilder.newBuilder()
                .refreshAfterWrite(CacheConstant.CACHE_REFRESH_INTERVAL, TimeUnit.MINUTES)
                .build(new CacheLoader<String, ConnectConfig>() {
                    @Override
                    public ConnectConfig load(String key) throws Exception {
                        return loadConnectByClientId(key);
                    }
                });
    }

    @Override
    public ConnectConfig queryDefaultConnectConfig(int provider) throws ServiceException {
        return queryConnectConfigByClientId(CommonConstant.SGPP_DEFAULT_CLIENTID, provider);
    }

    @Override
    public ConnectConfig queryConnectConfigByAppId(String appId, int provider) throws ServiceException {
        ConnectConfig connectConfig = null;
        String cacheKey = buildConnectConfigCacheKeyByAppId(appId, provider);
        if (connectCacheByAppId != null) {

            if (Strings.isNullOrEmpty(appId)) {
                return queryConnectConfigByClientId(CommonConstant.SGPP_DEFAULT_CLIENTID, provider);
            }

            try {
                connectConfig = connectCacheByAppId.get(cacheKey);
            } catch (Exception e) {
                logger.warn("[App] queryConnectConfigByAppId.{}", e);
                return null;
            }

        } else {
            logger.error("connectCacheByClientId initial,failed");
            connectConfig = loadConnectByAppId(cacheKey);
        }

        return connectConfig;
    }

    @Override
    public ConnectConfig queryConnectConfigByClientId(int clientId, int provider) throws ServiceException {
        ConnectConfig connectConfig = null;
        String cacheKey = buildConnectConfigCacheKey(clientId, provider);
        if (connectCacheByClientId != null) {
            try {
                connectConfig = connectCacheByClientId.get(cacheKey);
            } catch (Exception e) {
                logger.warn("[App] queryConnectConfigByClientId.{}", e);
                return null;
            }
        } else {
            logger.error("connectCacheByClientId initial,failed");
            connectConfig = loadConnectByClientId(cacheKey);
        }
        return connectConfig;
    }

    public ConnectConfig loadConnectByAppId(String cacheKey) throws ServiceException {
        ConnectConfig connectConfig = null;
        String[] keyArrays = StringUtils.split(cacheKey, "_");
        String appId = keyArrays[1];
        int provider = Integer.parseInt(keyArrays[2]);
        try {
            if (Strings.isNullOrEmpty(appId)) {
                return queryConnectConfigByClientId(CommonConstant.SGPP_DEFAULT_CLIENTID, provider);
            }
            //缓存根据clientId读取ConnectConfig
            connectConfig = redisUtils.getObject(cacheKey, ConnectConfig.class);
            if (connectConfig == null) {
                //读取数据库
                connectConfig = connectConfigDAO.getConnectConfigByAppIdAndProvider(appId, provider);
                if (connectConfig != null) {
                    addClientIdMapConnectConfigToCache(cacheKey, connectConfig);
                }
            }
        } catch (Exception e) {
            logger.error("[App] service method loadConnectByAppId error.{}", e);
            throw new ServiceException(e);
        }
        return connectConfig;
    }

    public ConnectConfig loadConnectByClientId(String cacheKey) throws ServiceException {
        ConnectConfig connectConfig = null;
        String[] keyArray = StringUtils.split(cacheKey, "_");
        int clientId = Integer.parseInt(keyArray[1]);
        int provider = Integer.parseInt(keyArray[2]);
        try {
            //缓存根据clientId读取ConnectConfig
            connectConfig = redisUtils.getObject(cacheKey, ConnectConfig.class);
            if (connectConfig == null) {
                //读取数据库
                connectConfig = connectConfigDAO.getConnectConfigByClientIdAndProvider(clientId, provider);
                if (connectConfig != null) {
                    addClientIdMapConnectConfigToCache(cacheKey, connectConfig);
                }
            }
        } catch (Exception e) {
            logger.error("[App] service method loadConnectByClientId error.{}", e);
            throw new ServiceException(e);
        }
        return connectConfig;
    }

    @Override
    public List<ConnectConfig> ListConnectConfigByClientId(int clientId) throws ServiceException {
        try {
            return connectConfigDAO.listConnectConfigByClientId(clientId);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean modifyConnectConfig(ConnectConfig connectConfig) throws ServiceException {
        try {
            int row = connectConfigDAO.updateConnectConfig(connectConfig);
            if (row > 0) {
                String cacheKey = buildConnectConfigCacheKey(connectConfig.getClientId(), connectConfig.getProvider());
                redisUtils.setWithinSeconds(cacheKey, connectConfig, DateAndNumTimesConstant.ONE_MONTH);
                return true;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return false;
    }

    private boolean addClientIdMapConnectConfigToCache(String cacheKey, ConnectConfig connectConfig) {
        boolean flag = true;
        try {
            redisUtils.setWithinSeconds(cacheKey, connectConfig, DateAndNumTimesConstant.ONE_MONTH);
        } catch (Exception e) {
            flag = false;
            logger.error("[App] service method addClientIdMapAppConfig error.{}", e);
        }
        return flag;
    }

    private String buildConnectConfigCacheKey(int clientId, int provider) {
        return CACHE_PREFIX_CLIENTID + clientId + "_" + provider;
    }

    private String buildConnectConfigCacheKeyByAppId(String appId, int provider) {
        return CACHE_PREFIX_CLIENTID + appId + "_" + provider;
    }

}
