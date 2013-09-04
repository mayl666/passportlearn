package com.sogou.upd.passport.service.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.utils.MemcacheUtils;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.service.account.SHTokenService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-8-21
 * Time: 下午1:50
 * To change this template use File | Settings | File Templates.
 */
@Service
public class SHTokenServiceImpl implements SHTokenService {
    private static final Logger logger = LoggerFactory.getLogger(SHTokenServiceImpl.class);

    @Autowired
    private MemcacheUtils rTokenMemUtils;
    @Autowired
    private MemcacheUtils aTokenMemUtils;

    /**
     * 构造SHToken的key
     * 格式为：passport|clientId|instanceId
     * passportId_clientId_instanceId：AccountToken的映射
     */
    private String buildTokenKeyStr(String passportId, int clientId, String instanceId) {
        String key;
        if (Strings.isNullOrEmpty(instanceId)) {
            key = passportId + "|" + clientId;
        } else {
            key = passportId + "|" + clientId + "|" + instanceId;
        }
        return key;
    }

    private String buildOldRTokenKeyStr(String passportId, int clientId, String instanceId) {
        String key;
        if (Strings.isNullOrEmpty(instanceId)) {
            key = "old|" + passportId + "|" + clientId;
        } else {
            key = "old|" + passportId + "|" + clientId + "|" + instanceId;
        }
        return key;
    }

    @Override
    public String queryAccessToken(String passportId, int clientId, String instanceId) throws ServiceException {
        try {
            String key = buildTokenKeyStr(passportId, clientId, instanceId);
            Object value = aTokenMemUtils.get(key);
            if (value != null) {
                return value.toString();

            }
            logger.info("query sohu memcache accessToken is null, key=" + key);
            return null;
        } catch (Exception e) {
            logger.error("Query AccountToken Fail, passportId:" + passportId + ", clientId:" + clientId + ", instanceId:" + instanceId, e);
            throw new ServiceException(e);
        }
    }

    @Override
    public String queryRefreshToken(String passportId, int clientId, String instanceId) throws ServiceException {
        try {
            String key = buildTokenKeyStr(passportId, clientId, instanceId);
            Object tsValue = rTokenMemUtils.get(key);
            if (tsValue != null) {
                return tsValue.toString();
            }
            logger.info("query sohu memcache new refreshToken is null, key=" + key);
            return null;
        } catch (Exception e) {
            logger.error("Query AccountToken Fail, passportId:" + passportId + ", clientId:" + clientId + ", instanceId:" + instanceId, e);
            throw new ServiceException(e);
        }
    }

    @Override
    public String queryOldRefreshToken(String passportId, int clientId, String instanceId) throws ServiceException {
        try {
            String key = buildOldRTokenKeyStr(passportId, clientId, instanceId);
            Object tsValue = rTokenMemUtils.get(key);
            if (tsValue != null) {
                return tsValue.toString();
            }
            logger.info("query sohu memcache old refreshToken is null, key=" + key);
            return null;
        } catch (Exception e) {
            logger.error("Query AccountToken Fail, passportId:" + passportId + ", clientId:" + clientId + ", instanceId:" + instanceId, e);
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean verifyShAccessToken(String passportId, int clientId, String instanceId, String accessToken) throws ServiceException {
        String storeAccessToken = queryAccessToken(passportId, clientId, instanceId);
        return accessToken.equals(storeAccessToken);
    }

    @Override
    public boolean verifyShRefreshToken(String passportId, int clientId, String instanceId, String refreshToken) throws ServiceException {
        try {
            String actualRefreshToken = queryRefreshToken(passportId, clientId, instanceId);
            if (!StringUtils.isEmpty(actualRefreshToken)) {
                if (actualRefreshToken.equals(refreshToken)) {
                    return true;
                }
            }

            String actualOldRefreshToken = queryOldRefreshToken(passportId, clientId, instanceId);
            if (!StringUtils.isEmpty(actualOldRefreshToken)) {
                if (actualOldRefreshToken.equals(refreshToken)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            logger.error("Query AccountToken Fail, passportId:" + passportId + ", clientId:" + clientId + ", instanceId:" + instanceId, e);
            throw new ServiceException(e);
        }
    }

}
