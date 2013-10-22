package com.sogou.upd.passport.service.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonHelper;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.utils.MemcacheUtils;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.account.PCAccountTokenService;
import com.sogou.upd.passport.service.account.SHTokenService;
import com.sogou.upd.passport.service.account.generator.TokenGenerator;
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
            long start =System.currentTimeMillis();
            Object value = aTokenMemUtils.get(key);
            CommonHelper.recordTimestamp(start, "queryAccessToken-aTokenMemUtils get");
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
    public String queryRefreshToken(String passportId, int clientId, String instanceId) throws ServiceException {
        try {
            String key = buildTokenKeyStr(passportId, clientId, instanceId);
            long start = System.currentTimeMillis();
            Object value = rTokenMemUtils.get(key);
            CommonHelper.recordTimestamp(start, "queryRefreshToken-queryRefreshToken-rTokenMemUtils get");
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
            String key = buildOldRTokenKeyStr(passportId, clientId, instanceId);
            long start = System.currentTimeMillis();
            Object value = rTokenMemUtils.get(key);
            CommonHelper.recordTimestamp(start, "queryOldRefreshToken-rTokenMemUtils get");
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
    public boolean verifyShAccessToken(String passportId, int clientId, String instanceId, String accessToken) throws ServiceException {
        String storeAccessToken = queryAccessToken(passportId, clientId, instanceId);
        if(accessToken.equals(storeAccessToken)){
            return true;
        }
        return false;
    }

    @Override
    public boolean verifyShRToken(String passportId, int clientId, String instanceId, String refreshToken) throws ServiceException {
        try {
            String actualRefreshToken = queryRefreshToken(passportId, clientId, instanceId);
            if(refreshToken.equals(actualRefreshToken)){
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("Query AccountToken Fail, passportId:" + passportId + ", clientId:" + clientId + ", instanceId:" + instanceId, e);
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean verifyAllShRToken(String passportId, int clientId, String instanceId, String refreshToken) throws ServiceException {
        try {
            String actualRefreshToken = queryRefreshToken(passportId, clientId, instanceId);
            if(refreshToken.equals(actualRefreshToken)){
                return true;
            }

            String actualOldRefreshToken = queryOldRefreshToken(passportId, clientId, instanceId);
            if(refreshToken.equals(actualOldRefreshToken)){
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("Query AccountToken Fail, passportId:" + passportId + ", clientId:" + clientId + ", instanceId:" + instanceId, e);
            throw new ServiceException(e);
        }
    }

    @Override
    public void saveAccountToken(final String passportId, final String instanceId,AppConfig appConfig,AccountToken accountToken) throws ServiceException{
        final int clientId = appConfig.getClientId();
        try {
            String key = buildTokenKeyStr(passportId, clientId, instanceId);
            aTokenMemUtils.set(key, DateAndNumTimesConstant.ONE_HOUR_INSECONDS,accountToken.getAccessToken());
            rTokenMemUtils.set(key,DateAndNumTimesConstant.ONE_MONTH_INSECONDS,accountToken.getRefreshToken());
        } catch (Exception e) {
            logger.error("Initial Or Update AccountToken Fail, passportId:" + passportId + ", clientId:" + clientId + ", instanceId:" + instanceId, e);
            throw new ServiceException(e);
        }
    }
    @Override
    public void saveOldRefreshToken(final String passportId, final String instanceId, AppConfig appConfig, String refreshToken) throws ServiceException {
        final int clientId = appConfig.getClientId();
        try {
            //保存老的token，与sohu保持一致，有效期为1天
            String key = buildOldRTokenKeyStr(passportId, clientId, instanceId);
            rTokenMemUtils.set(key, DateAndNumTimesConstant.ONE_DAY_INSECONDS, refreshToken);
        } catch (Exception e) {
            logger.error("saveOldRefreshToken Fail, passportId:" + passportId + ", clientId:" + clientId + ", instanceId:" + instanceId, e);
            throw new ServiceException(e);
        }
    }

}
