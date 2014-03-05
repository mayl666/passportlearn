package com.sogou.upd.passport.service.connect.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.utils.DBShardRedisUtils;
import com.sogou.upd.passport.dao.connect.ConnectTokenDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.model.connect.ConnectToken;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.vo.ConnectUserInfoVO;
import com.sogou.upd.passport.oauth2.openresource.vo.OAuthTokenVO;
import com.sogou.upd.passport.service.connect.ConnectAuthService;
import com.sogou.upd.passport.service.connect.ConnectTokenService;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

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
    @Autowired
    private ConnectAuthService connectAuthService;

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
                dbShardRedisUtils.setWithinSeconds(cacheKey, connectToken, DateAndNumTimesConstant.THREE_MONTH);
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
                dbShardRedisUtils.setWithinSeconds(cacheKey, connectToken, DateAndNumTimesConstant.THREE_MONTH);
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
            dbShardRedisUtils.setWithinSeconds(cacheKey, connectToken, DateAndNumTimesConstant.THREE_MONTH);
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

    /**
     * 根据refreshToken是否过期，来决定是否用refreshToken来刷新accessToken
     *
     * @param connectToken
     * @param connectConfig
     * @return
     * @throws IOException
     * @throws OAuthProblemException
     */
    @Override
    public boolean verifyAccessToken(ConnectToken connectToken, ConnectConfig connectConfig) throws IOException, OAuthProblemException {
        if (!isValidToken(connectToken.getUpdateTime(), connectToken.getExpiresIn())) {
            String refreshToken = connectToken.getRefreshToken();
            //refreshToken不为空，则刷新token
            if (!Strings.isNullOrEmpty(refreshToken)) {
                OAuthTokenVO oAuthTokenVO = connectAuthService.refreshAccessToken(refreshToken, connectConfig);
                if (oAuthTokenVO == null) {
                    return false;
                }
                //如果SG库中有token信息，但是过期了，此时使用refreshToken刷新成功了，这时要双写搜狗、搜狐数据库
                connectToken.setAccessToken(oAuthTokenVO.getAccessToken());
                connectToken.setExpiresIn(oAuthTokenVO.getExpiresIn());
                connectToken.setRefreshToken(oAuthTokenVO.getRefreshToken());
                connectToken.setUpdateTime(new Date());
                boolean isUpdateSuccess = insertOrUpdateConnectToken(connectToken);
                return isUpdateSuccess;
            } else {
                return false;
            }
        }
        return true;
    }


    /**
     * 验证Token是否失效,返回true表示有效，false表示过期
     */
    private boolean isValidToken(Date createTime, long expiresIn) {
        long currentTime = System.currentTimeMillis() / (1000);
        long tokenTime = createTime.getTime() / (1000);
        return currentTime < tokenTime + expiresIn;
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
                dbShardRedisUtils.setWithinSeconds(cacheKey, connectToken, DateAndNumTimesConstant.THREE_MONTH);
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
                dbShardRedisUtils.setWithinSeconds(cacheKey, connectToken, DateAndNumTimesConstant.THREE_MONTH);
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
