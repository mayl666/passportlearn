package com.sogou.upd.passport.service.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.CommonHelper;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.utils.DateUtil;
import com.sogou.upd.passport.common.utils.KvUtils;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.common.utils.TokenRedisUtils;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.account.PCAccountTokenService;
import com.sogou.upd.passport.service.account.SHTokenService;
import com.sogou.upd.passport.service.account.generator.TokenDecrypt;
import com.sogou.upd.passport.service.account.generator.TokenGenerator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static String KEY_PREFIX = CacheConstant.KV_PREFIX_PASSPORTID_TOKEN;

    @Autowired
    private KvUtils kvUtils;
    @Autowired
    private TokenRedisUtils tokenRedisUtils;

    @Override
    public AccountToken initialAccountToken(final String passportId, final String instanceId, AppConfig appConfig) throws ServiceException {
        final int clientId = appConfig.getClientId();
        try {
            AccountToken accountToken = newAccountToken(passportId, instanceId, appConfig);
            saveAccountToken(passportId, instanceId, appConfig, accountToken);

            return accountToken;
        } catch (Exception e) {
            logger.error("initialAccountToken Fail, passportId:" + passportId + ", clientId:" + clientId + ", instanceId:" + instanceId, e);
            throw new ServiceException(e);
        }
    }

    @Override
    public AccountToken updateAccountToken(final String passportId, final String instanceId, AppConfig appConfig) throws ServiceException {
        //todo 暂时每次都返回新的token
//        return initialAccountToken(passportId,instanceId,appConfig);
        final int clientId = appConfig.getClientId();
        try {
            AccountToken accountToken = queryAccountToken(passportId, clientId, instanceId);
            if (accountToken == null || !isValidToken(accountToken.getAccessValidTime()) || !isValidToken(accountToken.getRefreshValidTime())) {
                return initialAccountToken(passportId, instanceId, appConfig);
            }
            if (isNeedUpdate(accountToken, appConfig)) {
                if (isNeedExtendTime(accountToken.getAccessValidTime(), appConfig.getAccessTokenExpiresin())) {
                    long newAccessValidTime = DateUtil.generatorVaildTime(appConfig.getAccessTokenExpiresin());
                    accountToken.setAccessValidTime(newAccessValidTime);
                }
                if (isNeedExtendTime(accountToken.getRefreshValidTime(), appConfig.getRefreshTokenExpiresin())) {
                    long newRefreshValidTime = DateUtil.generatorVaildTime(appConfig.getRefreshTokenExpiresin());
                    accountToken.setRefreshValidTime(newRefreshValidTime);
                }
                saveAccountToken(passportId, instanceId, appConfig, accountToken);
            }
            return accountToken;
        } catch (Exception e) {
            logger.error("updateAccountToken Fail, passportId:" + passportId + ", clientId:" + clientId + ", instanceId:" + instanceId, e);
            throw new ServiceException(e);
        }
    }

    @Override
    public void saveAccountToken(final String passportId, final String instanceId, AppConfig appConfig, AccountToken accountToken) throws ServiceException {
        final int clientId = appConfig.getClientId();
        try {
            String kvKey = buildKeyStr(passportId, clientId, instanceId);
            kvUtils.set(kvKey, accountToken);
            //重新设置缓存
            String redisKey = buildTokenRedisKeyStr(passportId, clientId, instanceId);
            tokenRedisUtils.set(redisKey, accountToken);
            //保存映射关系
//            kvUtils.pushToSet(buildMappingKeyStr(passportId), buildSecondKeyStr(clientId, instanceId));
        } catch (Exception e) {
            logger.error("setAccountToken Fail, passportId:" + passportId + ", clientId:" + clientId + ", instanceId:" + instanceId, e);
            throw new ServiceException(e);
        }
    }

    @Override
    public AccountToken queryAccountToken(String passportId, int clientId, String instanceId) throws ServiceException {
        try {
            String tokenRedisKey = buildTokenRedisKeyStr(passportId, clientId, instanceId);
//            long start = System.currentTimeMillis();

            AccountToken accountToken = tokenRedisUtils.getObject(tokenRedisKey, AccountToken.class);
//            CommonHelper.recordTimestamp(start, "queryAccountToken-tokenRedies");

            if (accountToken == null) {
                accountToken = kvUtils.getObject(buildKeyStr(passportId, clientId, instanceId), AccountToken.class);
                if (accountToken != null) {
                    tokenRedisUtils.set(tokenRedisKey, accountToken);
                }
            }
            return accountToken;
        } catch (Exception e) {
            logger.error("Query AccountToken Fail, passportId:" + passportId + ", clientId:" + clientId + ", instanceId:" + instanceId, e);
            throw new ServiceException(e);
        }
    }

    @Override
    public String queryOldPCToken(String passportId, int clientId, String instanceId) throws ServiceException {
        String oldRTokenKey = buildOldRTokenKeyStr(passportId, clientId, instanceId);
        String oldRToken = tokenRedisUtils.get(oldRTokenKey);
        return oldRToken;
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
        boolean res = false;
        AccountToken accountToken = queryAccountToken(passportId, clientId, instanceId);
        if (accountToken != null) {
            String actualRefreshToken = accountToken.getRefreshToken();
            long tokenValidTime = accountToken.getRefreshValidTime();
            res = refreshToken.equals(actualRefreshToken) && isValidToken(tokenValidTime);
        }
        return res;
    }

    @Override
    public String getPassportIdByToken(String token,String clientSecret) throws ServiceException{
        String passportId = null;
        try {
            passportId = TokenDecrypt.decryptPcToken(token, clientSecret);
            return  passportId;
        } catch (Exception e) {
            logger.error("getPassportIdByToken:" + token, e);
            return null;
        }
    }


    @Override
    public boolean verifyNoStoreToken(String token,String clientSecret) throws ServiceException {
        try {
            String passportId = TokenDecrypt.decryptPcToken(token, clientSecret);
            if(!Strings.isNullOrEmpty(passportId)){
                return true;
            }
            return false;
        }catch (Exception e){
            logger.error("verifyNoStoreRefreshToken:" + token, e);
            return false;
        }
    }


    @Override
    public boolean verifyPCOldRefreshToken(String passportId, int clientId, String instanceId, String refreshToken) throws ServiceException {
        if (CommonHelper.isExplorerToken(clientId)) {
            String oldRToken = queryOldPCToken(passportId, clientId, instanceId);
            return refreshToken.equals(oldRToken);
        }
        return false;
    }

    @Override
    public void saveOldRefreshToken(final String passportId, final String instanceId, AppConfig appConfig, String refreshToken) throws ServiceException {
        final int clientId = appConfig.getClientId();
        try {
            //保存老的token，与sohu保持一致，有效期为1天
            String oldRTokenKey = buildOldRTokenKeyStr(passportId, clientId, instanceId);
            tokenRedisUtils.setWithinSeconds(oldRTokenKey, refreshToken, DateAndNumTimesConstant.TIME_ONEDAY);
        } catch (Exception e) {
            logger.error("setAccountToken Fail, passportId:" + passportId + ", clientId:" + clientId + ", instanceId:" + instanceId, e);
            throw new ServiceException(e);
        }
    }

    /**
     * 构造PcAccountToken的key
     * 格式为：passport_clientId_instanceId
     * passportId_clientId_instanceId：AccountToken的映射
     */
    public static String buildKeyStr(String passportId, int clientId, String instanceId) {
        if (StringUtils.isEmpty(instanceId)) {
            return KEY_PREFIX + passportId + "_" + clientId;
        }
        return KEY_PREFIX + passportId + "_" + clientId + "_" + instanceId;
    }

    /**
     * 构造PcAccountToken在redis中的key
     * 格式为：passport_clientId_instanceId
     */
    public static String buildTokenRedisKeyStr(String passportId, int clientId, String instanceId) {
        if (StringUtils.isEmpty(instanceId)) {
            return passportId + "_" + clientId;
        }
        return passportId + "_" + clientId + "_" + instanceId;
    }

    private String buildOldRTokenKeyStr(String passportId, int clientId, String instanceId) {
        String key;
        if (Strings.isNullOrEmpty(instanceId)) {
            key = "old_" + passportId + "_" + clientId;
        } else {
            key = "old_" + passportId + "_" + clientId + "_" + instanceId;
        }
        return key;
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
            accessToken = TokenGenerator.generatorPcToken(passportId, accessTokenExpiresIn, clientSecret);
            refreshToken = TokenGenerator.generatorPcToken(passportId, refreshTokenExpiresIn, clientSecret);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        accountToken.setPassportId(passportId);
        accountToken.setClientId(clientId);
        accountToken.setAccessToken(accessToken);
        accountToken.setAccessValidTime(DateUtil.generatorVaildTime(accessTokenExpiresIn));
        accountToken.setRefreshToken(refreshToken);
        accountToken.setRefreshValidTime(DateUtil.generatorVaildTime(refreshTokenExpiresIn));
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

    /**
     * 验证Token是否失效
     */
    private boolean isNeedUpdate(AccountToken accountToken, AppConfig appConfig) {
        if (accountToken == null)
            return true;
        return (isNeedExtendTime(accountToken.getAccessValidTime(), appConfig.getAccessTokenExpiresin())
                || isNeedExtendTime(accountToken.getRefreshValidTime(), appConfig.getRefreshTokenExpiresin()));
    }

    /**
     * 验证Token是否失效
     */
    private boolean isNeedExtendTime(long tokenValidTime, int expiresIn) {
        long currentTime = System.currentTimeMillis();
        long leftTime = tokenValidTime - currentTime;
        long halfExpireTime = (long) (expiresIn / 2);
        return leftTime < halfExpireTime;
    }
}