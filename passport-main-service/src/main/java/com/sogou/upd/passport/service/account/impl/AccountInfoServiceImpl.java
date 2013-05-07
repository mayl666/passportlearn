package com.sogou.upd.passport.service.account.impl;

import com.google.gson.reflect.TypeToken;

import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.dao.account.AccountInfoDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.account.AccountInfo;
import com.sogou.upd.passport.service.account.AccountInfoService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-4-26 Time: 下午2:38 To change this template use
 * File | Settings | File Templates.
 */
@Service
public class AccountInfoServiceImpl implements AccountInfoService {

    private static final String CACHE_PREFIX_PASSPORT_ACCOUNT_INFO =
            CacheConstant.CACHE_PREFIX_PASSPORT_ACCOUNTINFO;
    private static final Logger logger = LoggerFactory.getLogger(AccountInfoServiceImpl.class);

    @Autowired
    private AccountInfoDAO accountInfoDAO;
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public AccountInfo queryAccountInfoByPassportId(String passportId) throws ServiceException {
        AccountInfo accountInfo;
        try {
            String cacheKey = buildAccountInfoKey(passportId);
            Type type = new TypeToken<AccountInfo>() {
            }.getType();
            accountInfo = redisUtils.getObject(cacheKey, type);
            if (accountInfo == null) {
                accountInfo = accountInfoDAO.getAccountInfoByPassportId(passportId);
                if (accountInfo != null) {
                    redisUtils.set(cacheKey, accountInfo);
                }
            }
        } catch (Exception e) {
            throw new ServiceException();
        }
        return accountInfo;
    }

    @Override
    public AccountInfo modifyEmailByPassportId(String passportId, String email)
            throws ServiceException {
        AccountInfo accountInfo;
        try {
            accountInfo = new AccountInfo(passportId);
            accountInfo.setEmail(email);
            int row = accountInfoDAO.saveEmailOrInsert(passportId, accountInfo);
            if (row != 0) {
                // 检查缓存中是否存在：存在则取缓存修改再更新缓存，不存在则查询数据库再设置缓存 ---hjf 2013.5.3
                String cacheKey = buildAccountInfoKey(passportId);
                Type type = new TypeToken<AccountInfo>() {
                }.getType();
                if ((accountInfo = (AccountInfo) redisUtils.getObject(cacheKey, type)) != null) {
                    accountInfo.setEmail(email);
                } else {
                    accountInfo = accountInfoDAO.getAccountInfoByPassportId(passportId);
                }
                redisUtils.set(cacheKey, accountInfo);
            }
        } catch (Exception e) {
            throw new ServiceException();
        }
        return accountInfo;
    }

    @Override
    public AccountInfo modifyQuesByPassportId(String passportId, String question, String answer)
            throws ServiceException {
        AccountInfo accountInfo;
        try {
            accountInfo = new AccountInfo(passportId);
            accountInfo.setQuestion(question);
            accountInfo.setAnswer(answer);
            int row = accountInfoDAO.saveQuesOrInsert(passportId, accountInfo);
            if (row != 0) {
                // 检查缓存中是否存在：存在则取缓存修改再更新缓存，不存在则查询数据库再设置缓存 ---hjf 2013.5.3
                String cacheKey = buildAccountInfoKey(passportId);
                Type type = new TypeToken<AccountInfo>() {
                }.getType();
                if ((accountInfo = (AccountInfo) redisUtils.getObject(cacheKey, type)) != null) {
                    accountInfo.setQuestion(question);
                    accountInfo.setAnswer(answer);
                } else {
                    accountInfo = accountInfoDAO.getAccountInfoByPassportId(passportId);
                }
                redisUtils.set(cacheKey, accountInfo);
            }
        } catch (Exception e) {
            throw new ServiceException();
        }
        return accountInfo;
    }

    private String buildAccountInfoKey(String passportId) {
        return CACHE_PREFIX_PASSPORT_ACCOUNT_INFO + passportId;
    }
}
