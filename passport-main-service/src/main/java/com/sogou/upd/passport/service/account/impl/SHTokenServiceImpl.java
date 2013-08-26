package com.sogou.upd.passport.service.account.impl;

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
    private MemcacheUtils memUtils;

    /**
     * 构造SHToken的key
     * 格式为：passport|clientId|instanceId
     * passportId_clientId_instanceId：AccountToken的映射
     */
    private String buildKeyStr(String passportId, int clientId) {
        return passportId + "|" + clientId;
    }

    private String buildTsKeyStr(String passportId, int clientId, String instanceId) {
        return passportId + "|" + clientId + "|" + instanceId;
    }

    private String buildOldKeyStr(String passportId, int clientId) {
        return "old|" + passportId + "|" + clientId;
    }

    private String buildOldTsKeyStr(String passportId, int clientId, String instanceId) {
        return "old|" + passportId + "|" + clientId + "|" + instanceId;
    }

    @Override
    public String queryRefreshToken(String passportId, int clientId, String instanceId) throws ServiceException {
        try {
            String tsKey = buildTsKeyStr(passportId, clientId, instanceId);
            Object tsValue = memUtils.get(tsKey);
            if (tsValue != null) {
                return tsValue.toString();
            }

            String key = buildKeyStr(passportId, clientId);
            Object value = memUtils.get(key);
            if (value != null) {
                return value.toString();

            }
            return null;
        } catch (Exception e) {
            logger.error("Query AccountToken Fail, passportId:" + passportId + ", clientId:" + clientId + ", instanceId:" + instanceId, e);
            throw new ServiceException(e);
        }
    }

    @Override
    public String queryOldRefreshToken(String passportId, int clientId, String instanceId) throws ServiceException {
        try {
            String tsKey = buildOldTsKeyStr(passportId, clientId, instanceId);
            Object tsValue = memUtils.get(tsKey);
            if (tsValue != null) {
                return tsValue.toString();
            }

            String key = buildOldKeyStr(passportId, clientId);
            Object value = memUtils.get(key);
            if (value != null) {
                return value.toString();

            }
            return null;
        } catch (Exception e) {
            logger.error("Query AccountToken Fail, passportId:" + passportId + ", clientId:" + clientId + ", instanceId:" + instanceId, e);
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean verifshRefreshToken(String passportId, int clientId, String instanceId, String refreshToken) throws ServiceException {
        try {
            String actualRefreshToken = queryRefreshToken(passportId, clientId, instanceId);
            if(!StringUtils.isEmpty(actualRefreshToken)) {
                return actualRefreshToken.equals(refreshToken);
            }
            return false;
        } catch (Exception e) {
            logger.error("Query AccountToken Fail, passportId:" + passportId + ", clientId:" + clientId + ", instanceId:" + instanceId, e);
            throw new ServiceException(e);
        }
    }

}
