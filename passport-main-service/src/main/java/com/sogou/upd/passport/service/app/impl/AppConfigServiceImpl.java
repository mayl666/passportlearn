package com.sogou.upd.passport.service.app.impl;

import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.dao.app.AppConfigDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.app.AppConfigService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-3-26
 * Time: 下午8:22
 * To change this template use File | Settings | File Templates.
 */
@Service
public class AppConfigServiceImpl implements AppConfigService {

    private Logger logger = LoggerFactory.getLogger(AppConfigService.class);
    private static final String CACHE_PREFIX_CLIENTID = CacheConstant.CACHE_PREFIX_CLIENTID_APPCONFIG; //clientId与appConfig映射
    private static ConcurrentMap<Integer, String> CLIENTNAMES_MAP = Maps.newConcurrentMap();
    private static LoadingCache<String, AppConfig> appLocalCache = null;

    @Autowired
    private AppConfigDAO appConfigDAO;
    @Inject
    private RedisUtils redisUtils;

    public AppConfigServiceImpl() {
        appLocalCache = CacheBuilder.newBuilder()
                .refreshAfterWrite(CacheConstant.CACHE_REFRESH_INTERVAL, TimeUnit.MINUTES)
                .build(new CacheLoader<String, AppConfig>() {
                    @Override
                    public AppConfig load(String key) throws Exception {
                        return loadAppconfig(key);
                    }
                });

    }

    //根据cacheKey先去redis中查appConfig，如果没有就去db中查
    public AppConfig loadAppconfig(String cacheKey) throws ServiceException {
        AppConfig appConfig;
        try {
            int clientId = Integer.parseInt(StringUtils.substringAfter(cacheKey, CACHE_PREFIX_CLIENTID));
            //缓存根据clientId读取AppConfig
            appConfig = redisUtils.getObject(cacheKey, AppConfig.class);
            if (appConfig == null) {
                //读取数据库
                appConfig = appConfigDAO.getAppConfigByClientId(clientId);
                if (appConfig != null) {
                    addClientIdMapAppConfigToCache(clientId, appConfig);
                }
            }
        } catch (Exception e) {
            logger.error("[App] service method loadAppconfig error.{}");
            throw new ServiceException(e);
        }
        return appConfig;
    }

    @Override
    public AppConfig queryAppConfigByClientId(int clientId) throws ServiceException {
        AppConfig appConfig = null;
        String cacheKey = CACHE_PREFIX_CLIENTID + clientId;

        if (appLocalCache != null) {
            try {
                appConfig = appLocalCache.get(cacheKey);
            } catch (Exception e) {
                logger.warn("[App] queryAppConfigByClientId.{}");
                return null;
            }
        } else {
            logger.error("appLocalCache initial,failed");
            appConfig = loadAppconfig(cacheKey);
        }
        return appConfig;
    }

    @Override
    public String querySmsText(int clientId, String smsCode) throws ServiceException {
        //缓存中根据clientId获取AppConfig
        AppConfig appConfig = queryAppConfigByClientId(clientId);
        if (appConfig != null) {
            return String.format(appConfig.getSmsText(), smsCode);
        }
        return null;
    }

    @Override
    public String queryClientName(int clientId) throws ServiceException {
        String clientName = CLIENTNAMES_MAP.get(clientId);
        if (Strings.isNullOrEmpty(clientName)) {
            clientName = queryNameToMap(clientId);
        }
        return clientName;
    }

    private String queryNameToMap(int clientId) {
        String clientName = null;
        AppConfig appConfig = queryAppConfigByClientId(clientId);
        if (appConfig != null) {
            clientName = appConfig.getClientName();
            if (!Strings.isNullOrEmpty(clientName)) {
                CLIENTNAMES_MAP.putIfAbsent(clientId, clientName);
            }
        }
        return clientName;
    }

    private boolean addClientIdMapAppConfigToCache(int clientId, AppConfig appConfig) {
        boolean flag = true;
        try {
            String cacheKey = CACHE_PREFIX_CLIENTID + clientId;
            redisUtils.setWithinSeconds(cacheKey, appConfig, DateAndNumTimesConstant.ONE_MONTH);
        } catch (Exception e) {
            flag = false;
            logger.error("[App] service method addClientIdMapAppConfig error.{}");
        }
        return flag;
    }

    @Override
    public AppConfig verifyClientVaild(int clientId, String clientSecret) {
        try {
            AppConfig appConfig = queryAppConfigByClientId(clientId);
            if (appConfig == null) {
                return null;
            } else if (!clientSecret.equals(appConfig.getClientSecret())) {
                return null;
            }
            return appConfig;
        } catch (ServiceException e) {
            logger.error("[app] Verify ClientVaild Fail:");
            return null;
        }
    }
}