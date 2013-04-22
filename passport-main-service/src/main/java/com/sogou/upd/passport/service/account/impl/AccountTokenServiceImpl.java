package com.sogou.upd.passport.service.account.impl;

import com.google.gson.reflect.TypeToken;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.exception.ServiceException;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.dao.account.AccountTokenDAO;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.AccountTokenService;
import com.sogou.upd.passport.service.account.dataobject.AccessTokenCipherDO;
import com.sogou.upd.passport.service.account.dataobject.RefreshTokenCipherDO;
import com.sogou.upd.passport.service.account.generator.TokenDecrypt;
import com.sogou.upd.passport.service.account.generator.TokenGenerator;
import com.sogou.upd.passport.service.app.AppConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
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
    private AccountService accountService;
    @Autowired
    private MobilePassportMappingServiceImpl mobilePassportMappingService;
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public AccountToken verifyRefreshToken(String refreshToken, String instanceId) throws ServiceException {
        AccountToken accountToken;
        try {
            RefreshTokenCipherDO refreshTokenCipherDO = TokenDecrypt.decryptRefreshToken(refreshToken);
            String passportId = refreshTokenCipherDO.getPassportId();
            int clientId = refreshTokenCipherDO.getClientId();
            String tokenInstanceId = refreshTokenCipherDO.getInstanceId();
            accountToken = queryAccountTokenByPassportId(passportId, clientId, tokenInstanceId);
            if (isValidRefreshToken(accountToken, instanceId)) {
                return accountToken;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new ServiceException();
        }
    }

    @Override
    public AccountToken verifyAccessToken(String accessToken) throws ServiceException {
        AccountToken accountToken;
        try {
            AccessTokenCipherDO accessTokenCipherDO = TokenDecrypt.decryptAccessToken(accessToken);
            String passportId = accessTokenCipherDO.getPassportId();
            int clientId = accessTokenCipherDO.getClientId();
            String tokenInstanceId = accessTokenCipherDO.getInstanceId();
            accountToken = queryAccountTokenByPassportId(passportId, clientId, tokenInstanceId);
            if (isValidAccessToken(accountToken)) {
                return accountToken;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new ServiceException();
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
            throw new ServiceException();
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
    public void asynUpdateAccountAuthBySql(final String username, final int clientId,
                                           final String instanceId) throws ServiceException {
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                //根据手机号查询该用户信息
                String passportId = mobilePassportMappingService.queryPassportIdByUsername(username);
                Account account = accountService.queryAccountByPassportId(passportId);
                // todo refactoring listNew-->needUpdateAccountTokens；listResult-->allAccountTokens；原来的名字看不出来啥意思
                List<AccountToken> listNew = new ArrayList<AccountToken>();
                List<AccountToken> listResult = null;
                if (account != null) {
                    //根据该用户的id去auth表里查询用户状态记录，返回list
                    // todo refactoring 在service封装一下这个方法，先从缓存读取再从mysql读
                    listResult = accountTokenDAO.listAccountTokenByPassportIdAndClientId(passportId, clientId);
                    //过滤掉同步执行过的实例，异步更新剩余实例
                    filterCurrentInstance(listResult, instanceId);

                    if (listResult != null && listResult.size() > 0) {  // todo refactoring for循环不需要加判断了
                        for (AccountToken accountToken : listResult) {
                            //生成token及对应的auth对象，添加至listNew列表中，批量更新数据库
                            AccountToken accountAuth = null;
                            try {
                                accountAuth = newAccountToken(account.getPassportId(), accountToken.getClientId(),
                                        accountToken.getInstanceId());
                                accountAuth.setId(accountToken.getId());
                            } catch (ServiceException e) {
                                e.printStackTrace();  // todo refactoring 这行还不删了？
                            }
                            if (accountAuth != null) {
                                listNew.add(accountAuth);
                            }
                        }
                    }
                }
                if (listNew != null && listNew.size() > 0) {
                    accountTokenDAO.batchUpdateAccountToken(listNew);
                }
            }
        });
    }

    private void filterCurrentInstance(List<AccountToken> listResult, String instanceId) {
        for (AccountToken accountAuth : listResult) {
            if (instanceId.equals(accountAuth.getInstanceId())) {
                listResult.remove(accountAuth);
            }
        }
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

    private String buildAccountTokenKey(String passportId, int clientId, String instanceId) {
        return CACHE_PREFIX_PASSPORT_ACCOUNTTOKEN + passportId + clientId + instanceId;
    }

}
