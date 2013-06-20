package com.sogou.upd.passport.service.app.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.dao.app.AppConfigDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.app.AppConfigService;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import javax.inject.Inject;

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
    private static Map<Integer, String> CLIENTNAMES_MAP = Maps.newHashMap();

    @Autowired
    private AppConfigDAO appConfigDAO;
    @Inject
    private RedisUtils redisUtils;

    @Override
    public boolean verifyClientVaild(int clientId, String clientSecret) throws ServiceException {
        AppConfig appConfig = queryAppConfigByClientId(clientId);
        if (appConfig == null) {
            return false;
        } else if (!clientSecret.equals(appConfig.getClientSecret())) {
            return false;
        }
        return true;
    }

    @Override
    public AppConfig queryAppConfigByClientId(int clientId) throws ServiceException {
        AppConfig appConfig = null;
        try {
            String cacheKey = CACHE_PREFIX_CLIENTID + clientId;
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
            logger.error("[App] service method queryAppConfigByClientId error.{}", e);
            throw new ServiceException(e);
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
        if (MapUtils.isEmpty(CLIENTNAMES_MAP)) {
            CLIENTNAMES_MAP = Maps.newHashMap();
        }
        String clientName = CLIENTNAMES_MAP.get(clientId);
        if (Strings.isNullOrEmpty(clientName)) {
            return queryNameToMap(clientId);
        }
        return clientName;

    }

    private String queryNameToMap(int clientId) {
        AppConfig appConfig = queryAppConfigByClientId(clientId);
        if (appConfig != null) {
            String clientName = appConfig.getClientName();
            CLIENTNAMES_MAP.put(clientId, clientName);
            return clientName;
        }
        return null;
    }

    private boolean addClientIdMapAppConfigToCache(int clientId, AppConfig appConfig) {
        boolean flag = true;
        try {
            String cacheKey = CACHE_PREFIX_CLIENTID + clientId;
            redisUtils.set(cacheKey, appConfig);
        } catch (Exception e) {
            flag = false;
            logger.error("[App] service method addClientIdMapAppConfig error.{}", e);
        }
        return flag;
    }

}
