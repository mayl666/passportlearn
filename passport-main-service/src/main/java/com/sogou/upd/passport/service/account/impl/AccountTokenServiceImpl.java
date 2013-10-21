package com.sogou.upd.passport.service.account.impl;

import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.utils.DateUtil;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.dao.account.AccountTokenDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.account.AccountTokenService;
import com.sogou.upd.passport.service.account.dataobject.TokenCipherDO;
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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
    public AccountToken verifyRefreshToken(String refreshToken, int clientId, String instanceId) throws ServiceException {
        AccountToken accountToken;
        RefreshTokenCipherDO refreshTokenCipherDO;
        try {
            refreshTokenCipherDO = TokenDecrypt.decryptRefreshToken(refreshToken);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        String passportId = refreshTokenCipherDO.getPassportId();
        accountToken = queryAccountTokenByPassportId(passportId, clientId, instanceId);
        if (isValidRefreshToken(accountToken, refreshToken)) {
            return accountToken;
        } else {
            return null;
        }
    }

    @Override
    public AccountToken verifyAccessToken(String accessToken) throws ServiceException {
        AccountToken accountToken;
        TokenCipherDO accessTokenCipherDO;
        try {
            accessTokenCipherDO = TokenDecrypt.decryptAccessToken(accessToken);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        String passportId = accessTokenCipherDO.getPassportId();
        int tokenClientId = accessTokenCipherDO.getClientId();
        String tokenInstanceId = accessTokenCipherDO.getInstanceId();
        accountToken = queryAccountTokenByPassportId(passportId, tokenClientId, tokenInstanceId);
        if (isValidAccessToken(accountToken, accessToken)) {
            return accountToken;
        } else {
            return null;
        }
    }

    @Override
    public AccountToken queryAccountTokenByPassportId(String passportId, int clientId, String instanceId) throws ServiceException {
        AccountToken accountToken;
        try {
            String cacheKey = buildAccountTokenKey(passportId);
            String key = buildAccountTokenSubKey(clientId, instanceId);

            accountToken = redisUtils.hGetObject(cacheKey, key, AccountToken.class);
            if (accountToken == null) {
                accountToken = accountTokenDAO.getAccountTokenByPassportId(passportId, clientId, instanceId);
                if (accountToken != null) {
                    redisUtils.hPut(cacheKey, key, accountToken);
                    // redisUtils.set(cacheKey, accountToken);
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
                String cacheKey = buildAccountTokenKey(passportId);
                String key = buildAccountTokenSubKey(clientId, instanceId);
                redisUtils.hPut(cacheKey, key, accountToken);
                // redisUtils.set(cacheKey, accountToken);
                return accountToken;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return null;
    }

    @Override
    public AccountToken updateOrInsertAccountToken(String passportId, int clientId, String instanceId) throws ServiceException {
        try {
            AccountToken accountToken = newAccountToken(passportId, clientId, instanceId);
            int accountRow = accountTokenDAO.saveAccountToken(passportId, accountToken);
            if (accountRow != 0) {
                String cacheKey = buildAccountTokenKey(passportId);
                String key = buildAccountTokenSubKey(clientId, instanceId);
                redisUtils.hPut(cacheKey, key, accountToken);
                // redisUtils.set(cacheKey, accountToken);
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
            // List<AccountToken> accountTokens = accountTokenDAO.listAccountTokenByPassportId(passportId);
            int row = accountTokenDAO.deleteAccountTokenByPassportId(passportId);
            if (row != 0) {
                String cacheKey = buildAccountTokenKey(passportId);
                redisUtils.delete(cacheKey);
                /*for (AccountToken accountToken : accountTokens) {
                    String cacheKey = buildAccountTokenKey(passportId, accountToken.getClientId(), accountToken.getInstanceId());
                    redisUtils.delete(cacheKey);
                }*/
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
                // TODO:是否有线程同步问题？
                List<AccountToken> allAccountTokens;
                String cacheKey = buildAccountTokenKey(passportId);

                Map<String, String> mapResult = redisUtils.hGetAll(cacheKey);
                if (!mapResult.isEmpty()) {
                    allAccountTokens = new LinkedList();
                    for (String subKey : mapResult.keySet()) {
                        allAccountTokens.add((AccountToken) redisUtils.hGetObject(cacheKey, subKey, AccountToken.class));
                    }
                } else {
                    allAccountTokens = accountTokenDAO.listAccountTokenByPassportIdAndClientId(passportId);
                }
                buildUpdateAccountTokens(allAccountTokens, clientId);
                if (CollectionUtils.isNotEmpty(allAccountTokens)) {
                    accountTokenDAO.batchUpdateAccountToken(allAccountTokens);
                    for (AccountToken accountToken : allAccountTokens) {
                        String key = buildAccountTokenSubKey(accountToken.getClientId(), accountToken.getInstanceId());
                      try {
                        redisUtils.hPut(cacheKey, key, accountToken);
                      } catch (Exception e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                      }
                    }
                }
            }
        });
    }


    /**
     * 验证refreshToken是否在有效期内，instanceId是否正确
     */
    private boolean isValidRefreshToken(AccountToken accountToken, String refreshToken) {
        boolean valid = false;
        if (accountToken != null) {
            String actualRefreshToken = accountToken.getRefreshToken();
            long refreshTokenValidTime = accountToken.getRefreshValidTime();
            long currentTime = System.currentTimeMillis();
            if (actualRefreshToken.equals(refreshToken) && refreshTokenValidTime > currentTime) {
                valid = true;
            }
        }
        return valid;
    }

    /**
     * 验证accessToken是否在有效期内，instanceId是否正确
     */
    private boolean isValidAccessToken(AccountToken accountToken, String accessToken) {
        boolean valid = false;
        if (accountToken != null) {
            String actualAccessToken = accountToken.getAccessToken();
            long accessTokenValidTime = accountToken.getAccessValidTime();
            long currentTime = System.currentTimeMillis();
            if (actualAccessToken.equals(accessToken) && accessTokenValidTime > currentTime) {
                valid = true;
            }
        }
        return valid;
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
            accountAuth.setAccessValidTime(DateUtil.generatorVaildTime(accessTokenExpiresIn));
            accountAuth.setRefreshToken(refreshToken);
            accountAuth.setRefreshValidTime(DateUtil.generatorVaildTime(refreshTokenExpiresIn));
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

    private String buildAccountTokenKey(String passportId) {
        return CACHE_PREFIX_PASSPORT_ACCOUNTTOKEN + passportId;
    }

    private String buildAccountTokenSubKey(int clientId, String instanceId) {
        return clientId + "_" + instanceId;
    }

}
