package com.sogou.upd.passport.service.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.CommonHelper;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.utils.CoreKvUtils;
import com.sogou.upd.passport.common.utils.DateUtil;
import com.sogou.upd.passport.common.utils.TokenRedisUtils;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.account.PCAccountTokenService;
import com.sogou.upd.passport.service.account.generator.TokenDecrypt;
import com.sogou.upd.passport.service.account.generator.TokenGenerator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

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

    private static String CORE_KV_PREFIX_PASSPROTID_TOKEN = CacheConstant.CORE_KV_PREFIX_PASSPROTID_TOKEN;

    //kv key 分隔符
    private static final String KEY_KV_SPLIT = "_";
    //instanceid为空的默认赋值
    private static final String EMPTY_INSTANCEID_SIGN = "NULL";
    //允许pc token失效的最大数目
    private static final int REMOVE_PCTOKEN_MAX_NUM = 50;
    //kv token key 生成规则: KEY_PREFIX + passportId + "_" + clientId + "_" + instanceId
    private static final String KEY_CORE_KV_FORMAT = "%s" + KEY_KV_SPLIT + "%s" + KEY_KV_SPLIT + "%s";
    private static final String KEY_DEFAULT_CORE_KV_FORMAT = "%s" + KEY_KV_SPLIT + "%s";

    @Autowired
    private TokenRedisUtils tokenRedisUtils;
    @Autowired
    private CoreKvUtils coreKvUtils;
    @Autowired
    private ThreadPoolTaskExecutor batchOperateExecutor; //批量操作，并不可丢弃任务线程池

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

    /**
     * kv 迁移，同步向核心kv集群写数据
     *
     * @param passportId
     * @param instanceId
     * @param appConfig
     * @param accountToken
     * @throws ServiceException
     */
    @Override
    public void saveAccountToken(final String passportId, final String instanceId, AppConfig appConfig, AccountToken accountToken) throws ServiceException {
        final int clientId = appConfig.getClientId();
        try {
            //kv 写操作同步至核心kv集群  2014-03-13 add by chengang
            String coreKvKey = buildCoreKvKey(CORE_KV_PREFIX_PASSPROTID_TOKEN, passportId, clientId, instanceId);
            coreKvUtils.set(coreKvKey, accountToken);

            //重新设置缓存
            String redisKey = buildTokenRedisKeyStr(passportId, clientId, instanceId);
            tokenRedisUtils.setWithinSeconds(redisKey, accountToken, DateAndNumTimesConstant.ONE_MONTH);
            //保存映射关系
            coreKvUtils.pushStringToLinkedHashSet(buildMappingCoreKvKey(passportId), buildMappingCoreKvValue(clientId, instanceId));
        } catch (Exception e) {
            logger.error("setAccountToken Fail, passportId:" + passportId + ", clientId:" + clientId + ", instanceId:" + instanceId, e);
            throw new ServiceException(e);
        }
    }

    @Override
    public AccountToken queryAccountToken(String passportId, int clientId, String instanceId) throws ServiceException {
        try {
            String tokenRedisKey = buildTokenRedisKeyStr(passportId, clientId, instanceId);
            AccountToken accountToken = tokenRedisUtils.getObject(tokenRedisKey, AccountToken.class);
            if (accountToken == null) {
                accountToken = coreKvUtils.getObject(buildCoreKvKey(CORE_KV_PREFIX_PASSPROTID_TOKEN, passportId, clientId, instanceId), AccountToken.class);
                if (accountToken != null) {
                    tokenRedisUtils.setWithinSeconds(tokenRedisKey, accountToken, DateAndNumTimesConstant.ONE_MONTH);
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
    public String getPassportIdByToken(String token, String clientSecret) throws ServiceException {
        String passportId;
        try {
            passportId = TokenDecrypt.decryptPcToken(token, clientSecret);
            return passportId;
        } catch (Exception e) {
            logger.error("getPassportIdByToken:" + token, e);
            return null;
        }
    }

    @Override
    public String getPassportIdByOldToken(String token, String clientSecret) throws ServiceException {
        String passportId;
        try {
            passportId = TokenDecrypt.decryptOldPcToken(token, clientSecret);
            return passportId;
        } catch (Exception e) {
            logger.error("getPassportIdByToken:" + token, e);
            return null;
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

    @Override
    public void batchRemoveAccountToken(final String passportId, boolean isAsyn) {
        String tokenMappingKey = buildMappingCoreKvKey(passportId);
        final Set<String> tokenMappingSet = coreKvUtils.pullStringFromLinkedHashSet(tokenMappingKey);
        if (isAsyn) {
            batchOperateExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    batchRemoveAccountToken(tokenMappingSet, passportId);
                }
            });
        } else {
            batchRemoveAccountToken(tokenMappingSet, passportId);
        }
    }

    private void batchRemoveAccountToken(Set<String> tokenMappingSet, String passportId) {
        Stack<String> tokenStack = new Stack();
        for (String s : tokenMappingSet) {
            tokenStack.push(s);
        }
        int removeMaxNum = 0;
        for (Iterator<String> iter = tokenStack.iterator(); iter.hasNext(); ) {
            String secondTokenKey = tokenStack.pop();
            String[] secondTokenKeyArray = secondTokenKey.split(KEY_KV_SPLIT);
            if (removeMaxNum < REMOVE_PCTOKEN_MAX_NUM && secondTokenKeyArray.length >= 2) {
                String clientIdStr = secondTokenKeyArray[0];
                String instanceId = EMPTY_INSTANCEID_SIGN.equals(secondTokenKeyArray[1]) ? "" : secondTokenKeyArray[1];
                try {
                    int clientId = Integer.parseInt(clientIdStr);
                    removeAccountToken(passportId, clientId, instanceId);
                    removeMaxNum++;
                } catch (Exception e) {
                    logger.error("client not interge, passportId:" + passportId + ", clientId:" + clientIdStr);
                    return;
                }
            } else {
                logger.error("Second Token Key less two, passportId:" + passportId + ", secondTokenKey:" + secondTokenKey);
                return;
            }
        }
    }

    @Override
    public void removeAccountToken(String passportId, int clientId, String instanceId) {
        //重新缓存里的pc token记录
        String redisKey = buildTokenRedisKeyStr(passportId, clientId, instanceId);
        tokenRedisUtils.delete(redisKey);

        //清除kv里的pc token记录
        String coreKvKey = buildCoreKvKey(CORE_KV_PREFIX_PASSPROTID_TOKEN, passportId, clientId, instanceId);
        coreKvUtils.delete(coreKvKey);
    }

    /**
     * 构建存储在kv accountToken key
     *
     * @param kvPrefix   key前缀
     * @param passportId
     * @param clientId
     * @param instanceId
     * @return
     */
    private static String buildCoreKvKey(String kvPrefix, String passportId, int clientId, String instanceId) {
        if (Strings.isNullOrEmpty(instanceId)) {
            return String.format(KEY_DEFAULT_CORE_KV_FORMAT, kvPrefix + passportId, clientId);
        }
        return String.format(KEY_CORE_KV_FORMAT, kvPrefix + passportId, clientId, instanceId);
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
    private String buildMappingCoreKvKey(String passportId) {
        return CORE_KV_PREFIX_PASSPROTID_TOKEN + passportId;
    }

    /**
     * 构造PcAccountToken的二级key
     * 格式为：clientId_instanceId
     * passportId：clientId_instanceId的映射
     */
    private String buildMappingCoreKvValue(int clientId, String instanceId) {
        String value;
        if (Strings.isNullOrEmpty(instanceId)) {
            //instanceId赋值为NULL便于以后扩展value字段
            value = clientId + KEY_KV_SPLIT + EMPTY_INSTANCEID_SIGN;
        } else {
            value = clientId + KEY_KV_SPLIT + instanceId;
        }
        return value;
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
        long expiresIn_long = (long) (expiresIn);
        long halfExpireTime = expiresIn_long * 500;
        return leftTime < halfExpireTime;
    }
}