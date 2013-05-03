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
                    // TODO:设置缓存有效时间，或者不放缓存
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
            accountInfo = queryAccountInfoByPassportId(passportId);
            if (accountInfo == null) {
                accountInfo = new AccountInfo(passportId);
            }
            accountInfo.setEmail(email);
            accountInfoDAO.modifyEmailOrInsert(passportId, accountInfo);
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
            accountInfo = queryAccountInfoByPassportId(passportId);
            if (accountInfo == null) {
                accountInfo = new AccountInfo(passportId);
            }
            accountInfo.setQuestion(question);
            accountInfo.setAnswer(answer);
            accountInfoDAO.modifyQuesOrInsert(passportId, accountInfo);
        } catch (Exception e) {
            throw new ServiceException();
        }
        return accountInfo;
    }

    private String buildAccountInfoKey(String passportId) {
        return CACHE_PREFIX_PASSPORT_ACCOUNT_INFO + passportId;
    }
}
