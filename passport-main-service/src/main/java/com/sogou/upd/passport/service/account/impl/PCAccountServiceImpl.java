package com.sogou.upd.passport.service.account.impl;

import com.google.common.collect.Lists;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.KvUtils;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.account.PcAccountTokenService;
import com.sogou.upd.passport.service.account.generator.TokenGenerator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-7-28
 * Time: 上午11:54
 * To change this template use File | Settings | File Templates.
 */
@Service
public class PcAccountServiceImpl implements PcAccountTokenService {
    private static final Logger logger = LoggerFactory.getLogger(PcAccountServiceImpl.class);

    @Autowired
    private KvUtils kvUtils;
    @Autowired
    private TaskExecutor batchOperateExecutor;

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
                    kvUtils.pushStringToList(passportId, buildSecondKeyStr(clientId, instanceId));
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
            return accountToken;  //To change body of implemented methods use File | Settings | File Templates.
        } catch (Exception e) {
            logger.error("Query AccountToken Fail, passportId:" + passportId + ", clientId:" + clientId + ", instanceId:" + instanceId, e);
            throw new ServiceException(e);
        }
    }

    @Override
    public AccountToken updateOrInsertAccountToken(String passportId, String instanceId, AppConfig appConfig) throws ServiceException {
        int clientId = appConfig.getClientId();
        try {
            AccountToken accountToken = newAccountToken(passportId, instanceId, appConfig);
            String key = buildKeyStr(passportId, clientId, instanceId);
            kvUtils.set(key, accountToken);
            return accountToken;
        } catch (Exception e) {
            logger.error("UpdateOrInsert AccountToken Fail, passportId:" + passportId + ", clientId:" + clientId + ", instanceId:" + instanceId, e);
            throw new ServiceException(e);
        }
    }


    @Override
    public boolean verifyAccessToken(String passportId, int clientId, String instanceId, String token) throws ServiceException {
        String key = buildKeyStr(passportId, clientId, instanceId);
        // TODO token和key的校验应该在controller做过了
        // TODO 检查是否超过有效期
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(token)) {
            return false;
        }
        boolean result = false;
        try {
            String storeToken = kvUtils.get(key);
            if (token.equals(storeToken)) {
                result = true;
            }
        } catch (Exception e) {
            logger.error("recordNum:cacheKey" + key, e);
            throw new ServiceException(e);
        }
        return result;
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
    private String buildKeyStr(String passportId, int clientId, String instanceId) {
        return passportId + "_" + clientId + "_" + instanceId;
    }

    /**
     * 构造PcAccountToken的二级key
     * 格式为：clientId_instanceId
     * passportId：clientId_instanceId的映射
     */
    private String buildSecondKeyStr(int clientId, String instanceId) {
        return clientId + "_" + instanceId;
    }

    private AccountToken newAccountToken(String passportId, String instanceId, AppConfig appConfig) {
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
