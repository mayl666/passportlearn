package com.sogou.upd.passport.service.account.impl;

import com.google.gson.reflect.TypeToken;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.dao.account.AccountTokenDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.account.AccountTokenService;
import com.sogou.upd.passport.service.account.dataobject.AccessTokenCipherDO;
import com.sogou.upd.passport.service.account.dataobject.RefreshTokenCipherDO;
import com.sogou.upd.passport.service.account.generator.TokenDecrypt;
import com.sogou.upd.passport.service.account.generator.TokenGenerator;
import com.sogou.upd.passport.service.app.AppConfigService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: shipengzhi Date: 13-3-29 Time: 上午1:20 To change this template use File | Settings |
 * File Templates.
 */
@Service
public class AccountTokenServiceImpl implements AccountTokenService {

    private static Logger logger = LoggerFactory.getLogger(AccountTokenService.class);
    private static final String CACHE_PREFIX_PASSPORT_ACCOUNTTOKEN = CacheConstant.CACHE_PREFIX_PASSPORT_ACCOUNTTOKEN;

    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private AccountTokenDAO accountTokenDAO;
    @Autowired
    private TaskExecutor batchOperateExecutor;
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public AccountToken verifyRefreshToken(String refreshToken, String instanceId) throws ServiceException {
        AccountToken accountToken;
        RefreshTokenCipherDO refreshTokenCipherDO;
        try {
            refreshTokenCipherDO = TokenDecrypt.decryptRefreshToken(refreshToken);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        String passportId = refreshTokenCipherDO.getPassportId();
        int clientId = refreshTokenCipherDO.getClientId();
        String tokenInstanceId = refreshTokenCipherDO.getInstanceId();
        try {
            accountToken = queryAccountTokenByPassportId(passportId, clientId, tokenInstanceId);
            if (isValidRefreshToken(accountToken, instanceId)) {
                return accountToken;
            } else {
                return null;
            }
        } catch (ServiceException e) {
            throw e;
        }
    }

    @Override
    public AccountToken verifyAccessToken(String accessToken) throws ServiceException {
        AccountToken accountToken;
        AccessTokenCipherDO accessTokenCipherDO;
        try {
            accessTokenCipherDO = TokenDecrypt.decryptAccessToken(accessToken);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        try {
            String passportId = accessTokenCipherDO.getPassportId();
            int clientId = accessTokenCipherDO.getClientId();
            String tokenInstanceId = accessTokenCipherDO.getInstanceId();
            accountToken = queryAccountTokenByPassportId(passportId, clientId, tokenInstanceId);
            if (isValidAccessToken(accountToken)) {
                return accountToken;
            } else {
                return null;
            }
        } catch (ServiceException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public AccountToken queryAccountTokenByPassportId(String passportId, int clientId, String instanceId) throws ServiceException {
        AccountToken accountToken;
        try {
            String cacheKey = buildAccountTokenKey(passportId, clientId, instanceId);
            Type type = new TypeToken<AccountToken>() {
            }.getType();
            accountToken = redisUtils.getObject(cacheKey, type);
            if (accountToken == null) {
                accountToken = accountTokenDAO.getAccountTokenByPassportId(passportId, clientId, instanceId);
                if (accountToken != null) {
                    redisUtils.set(cacheKey, accountToken);
                }
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return accountToken;
    }

    @Override
    public AccountToken initialAccountToken(String passportId, int clientId,
                                            String instanceId) throws ServiceException {
        try {
            AccountToken accountToken = newAccountToken(passportId, clientId, instanceId);
            long id = accountTokenDAO.insertAccountToken(passportId, accountToken);
            if (id != 0) {
                String cacheKey = buildAccountTokenKey(passportId, clientId, instanceId);
                redisUtils.set(cacheKey, accountToken);
                return accountToken;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return null;
    }

    @Override
    public AccountToken updateAccountToken(String passportId, int clientId, String instanceId) throws ServiceException {
        try {
            AccountToken accountToken = newAccountToken(passportId, clientId, instanceId);
            int accountRow = accountTokenDAO.saveAccountToken(passportId, accountToken);
            if (accountRow != 0) {
                String cacheKey = buildAccountTokenKey(passportId, clientId, instanceId);
                redisUtils.set(cacheKey, accountToken);
                return accountToken;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return null;
    }

    @Override
    public boolean deleteAccountTokenByPassportId(String passportId) throws ServiceException {
        try {
            List<AccountToken> accountTokens = accountTokenDAO.listAccountTokenByPassportId(passportId);
            int row = accountTokenDAO.deleteAccountTokenByPassportId(passportId);
            if (row != 0) {
                for (AccountToken accountToken : accountTokens) {
                    String cacheKey = buildAccountTokenKey(passportId, accountToken.getClientId(), accountToken.getInstanceId());
                    redisUtils.delete(cacheKey);
                }
                return true;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return false;
    }

    /**
     * 异步生成某用户的除当前客户端外的其它客户端的用户状态信息
     */
    @Override
    public void asynbatchUpdateAccountToken(final String passportId, final int clientId) throws ServiceException {
        batchOperateExecutor.execute(new Runnable() {
            @Override
            public void run() {
                //根据该用户的id去auth表里查询用户状态记录，返回list
                List<AccountToken> allAccountTokens = accountTokenDAO.listAccountTokenByPassportIdAndClientId(passportId);
                buildUpdateAccountTokens(allAccountTokens, clientId);
                if (CollectionUtils.isNotEmpty(allAccountTokens)) {
                    accountTokenDAO.batchUpdateAccountToken(allAccountTokens);
                }
            }
        });
    }


    /**
     * 验证refreshToken是否在有效期内，instanceId是否正确
     */
    private boolean isValidRefreshToken(AccountToken accountToken, String instanceId) {
        return accountToken != null && accountToken.getRefreshValidTime() > System.currentTimeMillis()
                && instanceId.equals(accountToken.getInstanceId());
    }

    /**
     * 验证accessToken是否在有效期内，instanceId是否正确
     */
    private boolean isValidAccessToken(AccountToken accountToken) {
        return accountToken != null && accountToken.getAccessValidTime() > System.currentTimeMillis();
    }

    /**
     * 构造一个新的AccountAuth
     */
    private AccountToken newAccountToken(String passportId, int clientId,
                                         String instanceId) throws ServiceException {

        AccountToken accountAuth = new AccountToken();
        AppConfig appConfig;
        try {
            appConfig = appConfigService.queryAppConfigByClientId(clientId);
        } catch (ServiceException e) {
            throw e;
        }
        if (appConfig != null) {
            int accessTokenExpiresIn = appConfig.getAccessTokenExpiresin();
            int refreshTokenExpiresIn = appConfig.getRefreshTokenExpiresin();

            String accessToken;
            String refreshToken;
            try {
                accessToken = TokenGenerator
                        .generatorAccessToken(passportId, clientId, accessTokenExpiresIn, instanceId);
                refreshToken = TokenGenerator.generatorRefreshToken(passportId, clientId, instanceId);
            } catch (Exception e) {
                throw new ServiceException(e);
            }
            accountAuth.setPassportId(passportId);
            accountAuth.setClientId(clientId);
            accountAuth.setAccessToken(accessToken);
            accountAuth.setAccessValidTime(TokenGenerator.generatorVaildTime(accessTokenExpiresIn));
            accountAuth.setRefreshToken(refreshToken);
            accountAuth.setRefreshValidTime(TokenGenerator.generatorVaildTime(refreshTokenExpiresIn));
            accountAuth.setInstanceId(instanceId);
        }

        return accountAuth;
    }

    private void buildUpdateAccountTokens(List<AccountToken> accountTokens, int clientId) throws ServiceException {
        AppConfig appConfig = appConfigService.queryAppConfigByClientId(clientId);
        int accessExpiresIn = appConfig.getAccessTokenExpiresin();

        for (AccountToken accountToken : accountTokens) {
            String passportId = accountToken.getPassportId();
            String instanceId = accountToken.getInstanceId();
            try {
                String newAccessToken = TokenGenerator.generatorAccessToken(passportId, clientId, accessExpiresIn, instanceId);
                String newRefreshToken = TokenGenerator.generatorRefreshToken(passportId, clientId, instanceId);
                accountToken.setAccessToken(newAccessToken);
                accountToken.setRefreshToken(newRefreshToken);
            } catch (Exception e) {
                logger.error("New AccessToken or RefreshToken Generator fail, NO update Account Token, passportId:" +
                        passportId + " clientId:" + clientId + " instanceId:" + instanceId, e);
            }

        }
    }

    private String buildAccountTokenKey(String passportId, int clientId, String instanceId) {
        return CACHE_PREFIX_PASSPORT_ACCOUNTTOKEN + passportId + clientId + instanceId;
    }

}
