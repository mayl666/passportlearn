package com.sogou.upd.passport.service.account.impl;

import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.DBShardRedisUtils;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.dao.account.AccountInfoDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.account.AccountInfo;
import com.sogou.upd.passport.service.account.AccountInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-4-26 Time: 下午2:38 To change this template use
 * File | Settings | File Templates.
 */
@Service
public class AccountInfoServiceImpl implements AccountInfoService {

    private static final String CACHE_PREFIX_PASSPORTID_ACCOUNT_INFO =
            CacheConstant.CACHE_PREFIX_PASSPORTID_ACCOUNTINFO;
    private static final Logger logger = LoggerFactory.getLogger(AccountInfoServiceImpl.class);

    @Autowired
    private AccountInfoDAO accountInfoDAO;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private DBShardRedisUtils dbShardRedisUtils;

    @Override
    public AccountInfo queryAccountInfoByPassportId(String passportId) throws ServiceException {
        AccountInfo accountInfo;
        try {
            String cacheKey = buildAccountInfoKey(passportId);
            accountInfo = dbShardRedisUtils.getObject(cacheKey, AccountInfo.class);
            if (accountInfo == null) {
                accountInfo = accountInfoDAO.getAccountInfoByPassportId(passportId);
                if (accountInfo != null) {
                    dbShardRedisUtils.set(cacheKey, accountInfo);
                }
            }
        } catch (Exception e) {
            throw new ServiceException(e);
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
                // 检查缓存中是否存在：存在则取缓存修改再更新缓存，不存在则查询数据库再设置缓存
                String cacheKey = buildAccountInfoKey(passportId);

                if ((accountInfo = dbShardRedisUtils.getObject(cacheKey, AccountInfo.class)) != null) {
                    accountInfo.setEmail(email);
                } else {
                    accountInfo = accountInfoDAO.getAccountInfoByPassportId(passportId);
                }
                dbShardRedisUtils.set(cacheKey, accountInfo);
                return accountInfo;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return null;
    }

    @Override
    public AccountInfo modifyQuesByPassportId(String passportId, String question, String answer)
            throws ServiceException {
        Result result = new APIResultSupport(false);
        AccountInfo accountInfo;
        try {
            accountInfo = new AccountInfo(passportId);
            accountInfo.setQuestion(question);
            accountInfo.setAnswer(answer);
            int row = accountInfoDAO.saveQuesOrInsert(passportId, accountInfo);
            if (row != 0) {
                // 检查缓存中是否存在：存在则取缓存修改再更新缓存，不存在则查询数据库再设置缓存
                String cacheKey = buildAccountInfoKey(passportId);

                if ((accountInfo = dbShardRedisUtils.getObject(cacheKey, AccountInfo.class)) != null) {
                    accountInfo.setQuestion(question);
                    accountInfo.setAnswer(answer);
                } else {
                    accountInfo = accountInfoDAO.getAccountInfoByPassportId(passportId);
                }
                dbShardRedisUtils.set(cacheKey, accountInfo);
                return accountInfo;
            }
            return null;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    private String buildAccountInfoKey(String passportId) {
        return CACHE_PREFIX_PASSPORTID_ACCOUNT_INFO + passportId;
    }

    @Override
    public boolean updateAccountInfo(AccountInfo accountInfo) throws ServiceException {
        try {
            String passportId = accountInfo.getPassportId();
            int row = accountInfoDAO.saveInfoOrInsert(passportId, accountInfo);
            if (row != 0) {
                // 检查缓存中是否存在：存在则取缓存修改再更新缓存，不存在则查询数据库再设置缓存
                String cacheKey = buildAccountInfoKey(passportId);
                AccountInfo accountInfoTmp = null;
                if ((accountInfoTmp = (AccountInfo) dbShardRedisUtils.getObject(cacheKey, AccountInfo.class)) != null) {
                    accountInfoTmp.setBirthday(accountInfo.getBirthday());
                    accountInfoTmp.setCity(accountInfo.getCity());
                    accountInfoTmp.setGender(accountInfo.getGender());
                    accountInfoTmp.setProvince(accountInfo.getProvince());
                    accountInfoTmp.setFullname(accountInfo.getFullname());
                    accountInfoTmp.setPersonalid(accountInfo.getPersonalid());
                    accountInfoTmp.setModifyip(accountInfo.getModifyip());
                    accountInfoTmp.setUpdateTime(new Date());
                } else {
                    accountInfoTmp = accountInfoDAO.getAccountInfoByPassportId(passportId);
                }
                dbShardRedisUtils.set(cacheKey, accountInfoTmp);
                return true;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return false;
    }
}
