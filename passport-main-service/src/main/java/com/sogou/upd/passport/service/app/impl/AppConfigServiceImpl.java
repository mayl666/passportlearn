package com.sogou.upd.passport.service.app.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.dao.app.AppConfigDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.service.app.AppConfigService;
import com.sogou.upd.passport.service.app.ConnectConfigService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
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
 * Date: 13-3-26
 * Time: 下午8:22
 * To change this template use File | Settings | File Templates.
 */
@Service
public class AppConfigServiceImpl implements AppConfigService {

    private Logger logger = LoggerFactory.getLogger(AppConfigService.class);
    private static final String CACHE_PREFIX_CLIENTID = CacheConstant.CACHE_PREFIX_CLIENTID_APPCONFIG; //clientId与appConfig映射
    private static LoadingCache<String, AppConfig> appLocalCache = null;

    @Autowired
    private AppConfigDAO appConfigDAO;
    @Autowired
    private ConnectConfigService connectConfigService;
    @Inject
    private RedisUtils redisUtils;

    public AppConfigServiceImpl() {
        appLocalCache = CacheBuilder.newBuilder()
                .expireAfterWrite(CacheConstant.CACHE_REFRESH_INTERVAL, TimeUnit.MINUTES)
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
            logger.error("[App] service method loadAppconfig error,cacheKey:" + cacheKey);
            throw new ServiceException(e);
        }
        return appConfig;
    }

    public List<AppConfig> listAllAppConfig() throws ServiceException {
        return appConfigDAO.listAllAppConfig();
    }

    @Override
    public AppConfig queryAppConfigByClientId(int clientId) throws ServiceException {
        AppConfig appConfig = null;
        String cacheKey = buildAppConfigCacheKey(clientId);

        if (appLocalCache != null) {
            try {
                appConfig = appLocalCache.get(cacheKey);
            } catch (Exception e) {
                logger.warn("[App] queryAppConfigByClientId fail,clientId:" + clientId);
                return null;
            }
        } else {
            logger.error("appLocalCache initial,failed");
            appConfig = loadAppconfig(cacheKey);
        }
        return appConfig;
    }

    @Override
    public boolean insertAppConfig(String sms_text, int access_token_expiresin,
                                   int refresh_token_expiresin, String client_name) throws ServiceException {
        try {
            int client_id = appConfigDAO.getMaxClientId() + 1;
            String server_secret = RandomStringUtils.randomAlphanumeric(30);

            String randomClient = RandomStringUtils.randomAlphanumeric(10);
            long timestamp = System.currentTimeMillis();
            String baseStrClient = client_id + "|" + timestamp + "|" + randomClient;
            String client_secret = new String(Coder.encryptMD5(baseStrClient));

            int row = appConfigDAO.insertAppConfig(client_id, sms_text, access_token_expiresin,
                    refresh_token_expiresin, server_secret, client_secret, client_name);
            if (row > 0) {
                return true;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return false;
    }

    private final String SMS_TEXT = "您的“搜狗通行证”验证码为：%s，30分钟内有效。若非本人操作请忽略";

    private final int ACCESS_TOKEN_EXPIRESIN = 604800;
    private final int REFRESH_TOKEN_EXPIRESIN = 15552000;

    @Override
    public boolean insertAppConfig(int clientId, String clientName, String serverSecret,
                                   String clientSecret) throws ServiceException {
        try {
            int row = appConfigDAO.insertAppConfig(clientId, SMS_TEXT, ACCESS_TOKEN_EXPIRESIN,
                                                   REFRESH_TOKEN_EXPIRESIN, serverSecret, clientSecret, clientName);
            if (row > 0) {
                return true;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return false;
    }

    @Override
    public boolean updateAppConfig(int client_id, String sms_text, int access_token_expiresin,
                               int refresh_token_expiresin, String client_name) throws ServiceException {
        try {
            int row = appConfigDAO.updateAppConfig(client_id, sms_text, access_token_expiresin,
                    refresh_token_expiresin, client_name);
            if (row > 0) {
                String cacheKey = buildAppConfigCacheKey(client_id);
                redisUtils.delete(cacheKey);
                return true;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return false;
    }

    @Override
    public boolean updateAppConfigName(int client_id, String client_name) throws ServiceException {
        try {
            int row = appConfigDAO.updateAppConfigName(client_id, client_name);
            if (row > 0) {
                String cacheKey = buildAppConfigCacheKey(client_id);
                redisUtils.delete(cacheKey);
                return true;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return false;
    }

    @Override
    public boolean deleteAppConfig(int client_id) throws ServiceException {
        try {
            // 先删除第三方配置
            List<ConnectConfig> connectConfigList = connectConfigService.ListConnectConfigByClientId(client_id);
            if(CollectionUtils.isNotEmpty(connectConfigList)) {
                for (ConnectConfig connectConfig : connectConfigList) {
                    connectConfigService.deleteConnectConfig(connectConfig.getClientId(), connectConfig.getProvider(), connectConfig.getAppKey());
                }
            }

            int row = appConfigDAO.deleteAppConfig(client_id);
            if (row > 0) {
                String cacheKey = buildAppConfigCacheKey(client_id);
                redisUtils.delete(cacheKey);
                return true;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return false;
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
        String clientName = null;
        AppConfig appConfig = queryAppConfigByClientId(clientId);
        if (appConfig != null) {
            clientName = appConfig.getClientName();
        }
        return clientName;
    }

    private boolean addClientIdMapAppConfigToCache(int clientId, AppConfig appConfig) {
        boolean flag = true;
        try {
            String cacheKey = buildAppConfigCacheKey(clientId);
            redisUtils.setWithinSeconds(cacheKey, appConfig, DateAndNumTimesConstant.ONE_MONTH);
        } catch (Exception e) {
            flag = false;
            logger.error("[App] service method addClientIdMapAppConfig fail,clientId:" + clientId);
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
            logger.error("[app] Verify ClientVaild Fail,clientId:" + clientId);
            return null;
        }
    }

    private String buildAppConfigCacheKey(int client_id) {
        return CACHE_PREFIX_CLIENTID + client_id;
    }
}
