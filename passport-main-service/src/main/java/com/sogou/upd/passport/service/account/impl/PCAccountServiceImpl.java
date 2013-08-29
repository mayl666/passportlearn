package com.sogou.upd.passport.service.account.impl;

import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.utils.KvUtils;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.account.PCAccountTokenService;
import com.sogou.upd.passport.service.account.generator.TokenGenerator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-7-28
 * Time: 上午11:54
 * To change this template use File | Settings | File Templates.
 */
@Service
public class PCAccountServiceImpl implements PCAccountTokenService {
    private static final Logger logger = LoggerFactory.getLogger(PCAccountServiceImpl.class);

//    private static String KEY_PREFIX = CacheConstant.KV_PREFIX_PASSPORTID_TOKEN;
    private static String KEY_PREFIX = CacheConstant.KV_PREFIX_TEST; // TODO 压力测试

    @Autowired
    private KvUtils kvUtils;
    @Autowired
    private ThreadPoolTaskExecutor batchOperateExecutor;

    @Override
    public AccountToken initialAccountToken(final String passportId, final String instanceId, AppConfig appConfig) throws ServiceException {
        final int clientId = appConfig.getClientId();
        try {
            AccountToken accountToken = newAccountToken(passportId, instanceId, appConfig);
            String key = buildKeyStr(passportId, clientId, instanceId);
            kvUtils.set(key, accountToken);
            // 异步写入映射列表
            batchOperateExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    kvUtils.pushToSet(buildMappingKeyStr(passportId), buildSecondKeyStr(clientId, instanceId));
                }
            });
            return accountToken;
        } catch (Exception e) {
            logger.error("Initial AccountToken Fail, passportId:" + passportId + ", clientId:" + clientId + ", instanceId:" + instanceId, e);
            throw new ServiceException(e);
        }
    }

    @Override
    public AccountToken queryAccountToken(String passportId, int clientId, String instanceId) throws ServiceException {
        try {
            String key = buildKeyStr(passportId, clientId, instanceId);
            AccountToken accountToken = kvUtils.getObject(key, AccountToken.class);
            return accountToken;
        } catch (Exception e) {
            logger.error("Query AccountToken Fail, passportId:" + passportId + ", clientId:" + clientId + ", instanceId:" + instanceId, e);
            throw new ServiceException(e);
        }
    }

    @Override
    public AccountToken updateOrInsertAccountToken(final String passportId, final String instanceId, AppConfig appConfig) throws ServiceException {
        final int clientId = appConfig.getClientId();
        try {
            AccountToken accountToken = newAccountToken(passportId, instanceId, appConfig);
            String key = buildKeyStr(passportId, clientId, instanceId);
            kvUtils.set(key, accountToken);
            batchOperateExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    kvUtils.pushToSet(buildMappingKeyStr(passportId), buildSecondKeyStr(clientId, instanceId));
                }
            });
            return accountToken;
        } catch (Exception e) {
            logger.error("UpdateOrInsert AccountToken Fail, passportId:" + passportId + ", clientId:" + clientId + ", instanceId:" + instanceId, e);
            throw new ServiceException(e);
        }
    }


    @Override
    public boolean verifyAccessToken(String passportId, int clientId, String instanceId, String accessToken) throws ServiceException {
        AccountToken accountToken = queryAccountToken(passportId, clientId, instanceId);
        if (accountToken != null) {
            String actualAccessToken = accountToken.getAccessToken();
            long tokenValidTime = accountToken.getAccessValidTime();
            return accessToken.equals(actualAccessToken) && isValidToken(tokenValidTime);
        }
        return false;
    }

    @Override
    public boolean verifyRefreshToken(String passportId, int clientId, String instanceId, String refreshToken) throws ServiceException {
        AccountToken accountToken = queryAccountToken(passportId, clientId, instanceId);
        if (accountToken != null) {
            String actualRefreshToken = accountToken.getRefreshToken();
            long tokenValidTime = accountToken.getRefreshValidTime();
            return refreshToken.equals(actualRefreshToken) && isValidToken(tokenValidTime);
        }
        return false;
    }

    /**
     * 构造PcAccountToken的key
     * 格式为：passport_clientId_instanceId
     * passportId_clientId_instanceId：AccountToken的映射
     */
    public static String buildKeyStr(String passportId, int clientId, String instanceId) {
        if(StringUtils.isEmpty(instanceId)){
            return  KEY_PREFIX + passportId + "_" + clientId;
        }
        return KEY_PREFIX + passportId + "_" + clientId + "_" + instanceId;
    }

    /**
     * 构造passportId映射关系的key
     * passportId: clientId_instanceId的映射
     *
     * @param passportId
     * @return
     */
    private String buildMappingKeyStr(String passportId) {
        return KEY_PREFIX + passportId;
    }

    /**
     * 构造PcAccountToken的二级key
     * 格式为：clientId_instanceId
     * passportId：clientId_instanceId的映射
     */
    private String buildSecondKeyStr(int clientId, String instanceId) {
        return clientId + "_" + instanceId;
    }

    public static AccountToken newAccountToken(String passportId, String instanceId, AppConfig appConfig) {
        AccountToken accountToken = new AccountToken();
        int accessTokenExpiresIn = appConfig.getAccessTokenExpiresin();
        int refreshTokenExpiresIn = appConfig.getRefreshTokenExpiresin();
        int clientId = appConfig.getClientId();
        String clientSecret = appConfig.getClientSecret();

        String accessToken;
        String refreshToken;
        try {
            accessToken = TokenGenerator.generatorPcToken(passportId, clientId, accessTokenExpiresIn, instanceId, clientSecret);
            refreshToken = TokenGenerator.generatorPcToken(passportId, clientId, refreshTokenExpiresIn, instanceId, clientSecret);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        accountToken.setPassportId(passportId);
        accountToken.setClientId(clientId);
        accountToken.setAccessToken(accessToken);
        accountToken.setAccessValidTime(TokenGenerator.generatorVaildTime(accessTokenExpiresIn));
        accountToken.setRefreshToken(refreshToken);
        accountToken.setRefreshValidTime(TokenGenerator.generatorVaildTime(refreshTokenExpiresIn));
        accountToken.setInstanceId(instanceId);

        return accountToken;
    }

    /**
     * 验证Token是否失效
     */
    private boolean isValidToken(long tokenValidTime) {
        long currentTime = System.currentTimeMillis();
        return tokenValidTime > currentTime;
    }

}