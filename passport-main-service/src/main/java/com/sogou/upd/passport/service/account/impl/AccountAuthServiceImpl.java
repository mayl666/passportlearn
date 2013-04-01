package com.sogou.upd.passport.service.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.exception.SystemException;
import com.sogou.upd.passport.dao.account.AccountAuthMapper;
import com.sogou.upd.passport.model.account.AccountAuth;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.account.AccountAuthService;
import com.sogou.upd.passport.service.account.generator.TokenGenerator;
import com.sogou.upd.passport.service.app.AppConfigService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-3-29
 * Time: 上午1:20
 * To change this template use File | Settings | File Templates.
 */
@Service
public class AccountAuthServiceImpl implements AccountAuthService {

    @Inject
    private AppConfigService appConfigService;

    @Inject
    private AccountAuthMapper accountAuthMapper;

    @Override
    public AccountAuth verifyRefreshToken(String refreshToken, String instanceId) {
        // TODO 加缓存
        if (!Strings.isNullOrEmpty(refreshToken)) {
            AccountAuth accountAuth = accountAuthMapper.getAccountAuthByRefreshToken(refreshToken);
            if (isValid(accountAuth, instanceId)) {
                return accountAuth;
            }
        }
        return null;
    }

    @Override
    public AccountAuth initialAccountAuth(long userId, String passportId, int clientId, String instanceId) throws SystemException {
        AccountAuth accountAuth = newAccountAuth(userId, passportId, clientId, instanceId);
        long id = accountAuthMapper.insertAccountAuth(accountAuth);
        if (id != 0) {
            return accountAuth;
        }
        return null;
    }

    @Override
    public AccountAuth updateAccountAuth(long userId, String passportId, int clientId, String instanceId) throws Exception {
        AccountAuth accountAuth = newAccountAuth(userId, passportId, clientId, instanceId);
        if (accountAuth != null) {
            int accountRow = accountAuthMapper.saveAccountAuth(accountAuth);
            return accountRow == 0 ? null : accountAuth;
        }
        return null;
    }

    /**
     * 验证refresh是否在有效期内，instanceId是否正确
     * @param accountAuth
     * @param instanceId
     * @return
     */
    private boolean isValid(AccountAuth accountAuth, String instanceId) {
        if (accountAuth != null && accountAuth.getRefreshValidTime() > System.currentTimeMillis() && instanceId.equals(accountAuth.getInstanceId())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 构造一个新的AccountAuth
     *
     * @param userId
     * @param passportID
     * @param clientId
     * @return
     */
    private AccountAuth newAccountAuth(long userId, String passportID, int clientId, String instanceId) throws SystemException {

        AppConfig appConfig = appConfigService.getAppConfigByClientId(clientId);
        AccountAuth accountAuth = new AccountAuth();
        if (appConfig != null) {
            int accessTokenExpiresIn = appConfig.getAccessTokenExpiresIn();
            int refreshTokenExpiresIn = appConfig.getRefreshTokenExpiresIn();

            String accessToken;
            String refreshToken;
            try {
                accessToken = TokenGenerator.generatorAccessToken(passportID, clientId, accessTokenExpiresIn, instanceId);
                refreshToken = TokenGenerator.generatorRefreshToken(passportID, clientId, instanceId);
            } catch (Exception e) {
                throw new SystemException(e);
            }
            accountAuth.setUserId(userId);
            accountAuth.setClientId(clientId);
            accountAuth.setAccessToken(accessToken);
            accountAuth.setAccessValidTime(TokenGenerator.generatorVaildTime(accessTokenExpiresIn));
            accountAuth.setRefreshToken(refreshToken);
            accountAuth.setRefreshValidTime(TokenGenerator.generatorVaildTime(refreshTokenExpiresIn));
            accountAuth.setInstanceId(instanceId);
        }

        return accountAuth;
    }
}
