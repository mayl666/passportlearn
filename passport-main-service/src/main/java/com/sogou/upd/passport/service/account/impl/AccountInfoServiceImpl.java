package com.sogou.upd.passport.service.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.utils.DBShardRedisUtils;
import com.sogou.upd.passport.dao.account.AccountInfoDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.account.AccountInfo;
import com.sogou.upd.passport.service.account.AccountInfoService;
import org.perf4j.aop.Profiled;
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
    private DBShardRedisUtils dbShardRedisUtils;

    @Profiled(el = true, logger = "dbTimingLogger", tag = "service_queryAccountInfo", timeThreshold = 20, normalAndSlowSuffixesEnabled = true)
    @Override
    public AccountInfo queryAccountInfoByPassportId(String passportId) throws ServiceException {
        AccountInfo accountInfo;
        try {
            String cacheKey = buildAccountInfoKey(passportId);
            accountInfo = dbShardRedisUtils.getObject(cacheKey, AccountInfo.class);
            if (accountInfo == null) {
                accountInfo = accountInfoDAO.getAccountInfoByPassportId(passportId);
                if (accountInfo != null) {
                    dbShardRedisUtils.setObjectWithinSeconds(cacheKey, accountInfo, DateAndNumTimesConstant.ONE_MONTH);
                }
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return accountInfo;
    }

    @Override
    public String queryBindEmailByPassportId(String passportId) throws ServiceException {
        AccountInfo accountInfo = queryAccountInfoByPassportId(passportId);
        String bindEmail = null;
        if (accountInfo != null) {
            bindEmail = accountInfo.getEmail();
        }
        return bindEmail;
    }

    @Profiled(el = true, logger = "dbTimingLogger", tag = "service_modifyBindEmail", timeThreshold = 20, normalAndSlowSuffixesEnabled = true)
    @Override
    public AccountInfo modifyBindEmailByPassportId(String passportId, String email)
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
                dbShardRedisUtils.setObjectWithinSeconds(cacheKey, accountInfo, DateAndNumTimesConstant.ONE_MONTH);
                return accountInfo;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return null;
    }

    @Profiled(el = true, logger = "dbTimingLogger", tag = "service_modifyQues", timeThreshold = 20, normalAndSlowSuffixesEnabled = true)
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
                // 检查缓存中是否存在：存在则取缓存修改再更新缓存，不存在则查询数据库再设置缓存
                String cacheKey = buildAccountInfoKey(passportId);

                if ((accountInfo = dbShardRedisUtils.getObject(cacheKey, AccountInfo.class)) != null) {
                    accountInfo.setQuestion(question);
                    accountInfo.setAnswer(answer);
                } else {
                    accountInfo = accountInfoDAO.getAccountInfoByPassportId(passportId);
                }
                dbShardRedisUtils.setObjectWithinSeconds(cacheKey, accountInfo, DateAndNumTimesConstant.ONE_MONTH);
                return accountInfo;
            }
            return null;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Profiled(el = true, logger = "dbTimingLogger", tag = "service_updateAccountInfo", timeThreshold = 20, normalAndSlowSuffixesEnabled = true)
    @Override
    public boolean updateAccountInfo(AccountInfo accountInfo) throws ServiceException {
        try {
            String passportId = accountInfo.getPassportId();
            accountInfo.setGender(Strings.isNullOrEmpty(accountInfo.getGender()) ? "0" : accountInfo.getGender());  //性别默认值为0
            int row = accountInfoDAO.saveInfoOrInsert(passportId, accountInfo);
            logger.info("saveInfoOrInsert passportId:" + passportId + ", row:" + row);
            if (row != 0) {
                // 检查缓存中是否存在：存在则取缓存修改再更新缓存，不存在则查询数据库再设置缓存
                String cacheKey = buildAccountInfoKey(passportId);
                AccountInfo accountInfoTmp;
                if ((accountInfoTmp = (AccountInfo) dbShardRedisUtils.getObject(cacheKey, AccountInfo.class)) != null) {
                    accountInfoTmp.setBirthday(accountInfo.getBirthday());
                    accountInfoTmp.setCity(accountInfo.getCity());
                    accountInfoTmp.setGender(Strings.isNullOrEmpty(accountInfo.getGender()) ? "0" : accountInfo.getGender());  //性别默认值为0
                    accountInfoTmp.setProvince(accountInfo.getProvince());
                    accountInfoTmp.setFullname(accountInfo.getFullname());
                    accountInfoTmp.setPersonalid(accountInfo.getPersonalid());
                    accountInfoTmp.setModifyip(accountInfo.getModifyip());
                    accountInfoTmp.setUpdateTime(new Date());
                } else {
                    accountInfoTmp = accountInfoDAO.getAccountInfoByPassportId(passportId);
                }
                dbShardRedisUtils.setObjectWithinSeconds(cacheKey, accountInfoTmp, DateAndNumTimesConstant.ONE_MONTH);
                return true;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return false;
    }

    /**
     * passport 支持后台使用,不要删除
     *
     * @param accountInfo
     * @param email
     * @return
     * @throws ServiceException
     */
    public boolean updateBindMEmail(AccountInfo accountInfo, String email) throws ServiceException {
        try {
            String passportId = accountInfo.getPassportId();
            int result = accountInfoDAO.updateBindEmail(email, passportId);
            if (result > 0) {
                String cacheKey = buildAccountInfoKey(passportId);
                accountInfo.setEmail(email);
                dbShardRedisUtils.setObjectWithinSeconds(cacheKey, accountInfo, DateAndNumTimesConstant.ONE_MONTH);
                return true;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return false;
    }

    private String buildAccountInfoKey(String passportId) {
        return CACHE_PREFIX_PASSPORTID_ACCOUNT_INFO + passportId;
    }

}
