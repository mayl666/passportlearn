package com.sogou.upd.passport.service.connect.impl;

import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.utils.DBShardRedisUtils;
import com.sogou.upd.passport.dao.connect.ConnectTokenDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.connect.ConnectToken;
import com.sogou.upd.passport.service.connect.ConnectTokenService;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA. User: shipengzhi Date: 13-3-24 Time: 下午8:08 To change this template
 * use File | Settings | File Templates.
 */
@Service
public class ConnectTokenServiceImpl implements ConnectTokenService {

    private Logger logger = LoggerFactory.getLogger(ConnectTokenService.class);

    @Autowired
    private ConnectTokenDAO connectTokenDAO;
    @Autowired
    private DBShardRedisUtils dbShardRedisUtils;

    private static final String CACHE_PREFIX_PASSPORTID_CONNECTTOKEN = CacheConstant.CACHE_PREFIX_PASSPORTID_CONNECTTOKEN;

    @Profiled(el = true, logger = "dbTimingLogger", tag = "service_initialConnectToken", timeThreshold = 20, normalAndSlowSuffixesEnabled = true)
    @Override
    public boolean initialConnectToken(ConnectToken connectToken) throws ServiceException {
        int row;
        try {
            String passportId = connectToken.getPassportId();
            row = connectTokenDAO.insertAccountConnect(passportId, connectToken);
            if (row != 0) {
                String cacheKey = buildConnectTokenCacheKey(passportId, connectToken.getProvider(), connectToken.getAppKey());
                dbShardRedisUtils.setObjectWithinSeconds(cacheKey, connectToken, DateAndNumTimesConstant.ONE_MONTH);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            logger.error("[ConnectToken] service method insertAccountConnect error.{}", e);
            throw new ServiceException(e);
        }
    }

    @Profiled(el = true, logger = "dbTimingLogger", tag = "service_updateConnectToken", timeThreshold = 20, normalAndSlowSuffixesEnabled = true)
    @Override
    public boolean updateConnectToken(ConnectToken connectToken) throws ServiceException {
        int row;
        try {
            String passportId = connectToken.getPassportId();
            row = connectTokenDAO.updateConnectToken(passportId, connectToken);
            if (row != 0) {
                String cacheKey = buildConnectTokenCacheKey(passportId, connectToken.getProvider(), connectToken.getAppKey());
                dbShardRedisUtils.setObjectWithinSeconds(cacheKey, connectToken, DateAndNumTimesConstant.ONE_MONTH);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            logger.error("[ConnectToken] service method updateConnectToken error.{}", e);
            throw new ServiceException(e);
        }
    }

    @Profiled(el = true, logger = "dbTimingLogger", tag = "service_initialOrUpdateConnectTokenCache", timeThreshold = 20, normalAndSlowSuffixesEnabled = true)
    @Override
    public boolean initialOrUpdateConnectTokenCache(String passportId, ConnectToken connectToken) throws ServiceException {
        try {
            String cacheKey = buildConnectTokenCacheKey(passportId, connectToken.getProvider(), connectToken.getAppKey());
            dbShardRedisUtils.setObjectWithinSeconds(cacheKey, connectToken, DateAndNumTimesConstant.ONE_MONTH);
            return true;
        } catch (Exception e) {
            logger.error("[ConnectToken] service method initialOrUpdateConnectUserInfo error.{}", e);
            return false;
        }
    }

    @Profiled(el = true, logger = "dbTimingLogger", tag = "service_obtainCachedConnectToken", timeThreshold = 20, normalAndSlowSuffixesEnabled = true)
    @Override
    public ConnectToken obtainCachedConnectToken(String passportId, int provider, String appKey) {
        try {
            String cacheKey = buildConnectTokenCacheKey(passportId, provider, appKey);
            ConnectToken connectToken = dbShardRedisUtils.getObject(cacheKey, ConnectToken.class);
            return connectToken;
        } catch (Exception e) {
            logger.error("[ConnectToken] service method obtainCachedConnectToken error.{}", e);
            return null;
        }
    }

    @Profiled(el = true, logger = "dbTimingLogger", tag = "service_insertOrUpdateConnectToken", timeThreshold = 20, normalAndSlowSuffixesEnabled = true)
    @Override
    public boolean insertOrUpdateConnectToken(ConnectToken connectToken) throws ServiceException {
        int row;
        try {
            String passportId = connectToken.getPassportId();
            row = connectTokenDAO.insertOrUpdateAccountConnect(passportId, connectToken);
            if (row != 0) {
                String cacheKey = buildConnectTokenCacheKey(passportId, connectToken.getProvider(), connectToken.getAppKey());
                dbShardRedisUtils.setObjectWithinSeconds(cacheKey, connectToken, DateAndNumTimesConstant.ONE_MONTH);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            logger.error("[ConnectToken] service method insertAccountConnect error.{}", e);
            throw new ServiceException(e);
        }
    }

    @Profiled(el = true, logger = "dbTimingLogger", tag = "service_queryConnectToken", timeThreshold = 20, normalAndSlowSuffixesEnabled = true)
    @Override
    public ConnectToken queryConnectToken(String passportId, int provider, String appKey) throws ServiceException {
        ConnectToken connectToken;
        String cacheKey = buildConnectTokenCacheKey(passportId, provider, appKey);
        try {

            connectToken = dbShardRedisUtils.getObject(cacheKey, ConnectToken.class);
            if (connectToken == null) {
                //读取数据库
                connectToken = connectTokenDAO.getSpecifyConnectToken(passportId, provider, appKey);
                if (connectToken == null) {
                    return null;
                }
                dbShardRedisUtils.setObjectWithinSeconds(cacheKey, connectToken, DateAndNumTimesConstant.ONE_MONTH);
            }
            return connectToken;
        } catch (Exception e) {
            logger.error("[ConnectToken] service method querySpecifyOpenId error.{}", e);
            throw new ServiceException(e);
        }
    }

    private String buildConnectTokenCacheKey(String passportId, int provider, String appKey) {
        return CACHE_PREFIX_PASSPORTID_CONNECTTOKEN + passportId + "_" + provider + "_" + appKey;
    }
}
