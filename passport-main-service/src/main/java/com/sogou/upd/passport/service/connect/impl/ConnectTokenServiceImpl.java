package com.sogou.upd.passport.service.connect.impl;

import com.google.gson.reflect.TypeToken;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.dao.connect.ConnectTokenDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.connect.ConnectToken;
import com.sogou.upd.passport.service.connect.ConnectTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;

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
    private RedisUtils redisUtils;

    private static final String CACHE_PREFIX_PASSPORTID_CONNECTTOKEN = CacheConstant.CACHE_PREFIX_PASSPORTID_CONNECTTOKEN;

    @Override
    public boolean initialConnectToken(ConnectToken connectToken) throws ServiceException {
        int row = 0;
        try {
            String passportId = connectToken.getPassportId();
            row = connectTokenDAO.insertAccountConnect(passportId, connectToken);
            if (row != 0) {
                String cacheKey = buildConnectTokenCacheKey(passportId, connectToken.getProvider(), connectToken.getAppKey());
                redisUtils.set(cacheKey, connectToken);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            logger.error("[ConnectToken] service method insertAccountConnect error.{}", e);
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean updateConnectToken(ConnectToken connectToken) throws ServiceException {
        int row = 0;
        try {
            String passportId = connectToken.getPassportId();
            row = connectTokenDAO.updateConnectToken(passportId, connectToken);
            if (row != 0) {
                String cacheKey = buildConnectTokenCacheKey(passportId, connectToken.getProvider(), connectToken.getAppKey());
                redisUtils.set(cacheKey, connectToken);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            logger.error("[ConnectToken] service method updateConnectToken error.{}", e);
            throw new ServiceException(e);
        }
    }

    @Override
    public String querySpecifyOpenId(String passportId, int provider, String appKey) throws ServiceException {
        ConnectToken connectToken = queryConnectToken(passportId, provider, appKey);
        String openId = null;
        if (connectToken != null) {
            openId = connectToken.getOpenid();
        }
        return openId;
    }

    @Override
    public ConnectToken queryConnectToken(String passportId, int provider, String appKey) throws ServiceException {
        ConnectToken connectToken;
        String cacheKey = buildConnectTokenCacheKey(passportId, provider, appKey);
        try {
            Type type = new TypeToken<ConnectToken>() {
            }.getType();
            connectToken = redisUtils.getObject(cacheKey, type);
            if (connectToken == null) {
                //读取数据库
                connectToken = connectTokenDAO.getSpecifyConnectToken(passportId, provider, appKey);
                if (connectToken == null) {
                    return null;
                }
                redisUtils.set(cacheKey, connectToken);
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
