package com.sogou.upd.passport.service.account.impl;

import com.google.common.base.Strings;

import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.service.account.AccountSecureService;
import com.sogou.upd.passport.service.account.generator.SecureCodeGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-21 Time: 上午11:52 To change this template
 * use File | Settings | File Templates.
 */
@Service
public class AccountSecureServiceImpl implements AccountSecureService {

    private static final String CACHE_PREFIX_PASSPORTID_RESETPWDSECURECODE = CacheConstant.CACHE_PREFIX_PASSPORTID_RESETPWDSECURECODE;
    private static final String CACHE_PREFIX_PASSPORTID_MODSECINFOSECURECODE = CacheConstant.CACHE_PREFIX_PASSPORTID_MODSECINFOSECURECODE;


    private static final Logger logger = LoggerFactory.getLogger(AccountSecureServiceImpl.class);

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public String getSecureCodeResetPwd(String passportId, int clientId) throws ServiceException {
        return getSecureCode(passportId, clientId, CACHE_PREFIX_PASSPORTID_RESETPWDSECURECODE);
    }

    @Override
    public boolean checkSecureCodeResetPwd(String passportId, int clientId, String secureCode)
            throws ServiceException {
        return checkSecureCode(passportId, clientId, secureCode, CACHE_PREFIX_PASSPORTID_RESETPWDSECURECODE);
    }

    @Override
    public String getSecureCodeModSecInfo(String passportId, int clientId) throws ServiceException {
        return getSecureCode(passportId, clientId, CACHE_PREFIX_PASSPORTID_MODSECINFOSECURECODE);
    }

    @Override
    public boolean checkSecureCodeModSecInfo(String passportId, int clientId, String secureCode)
            throws ServiceException {
        return checkSecureCode(passportId, clientId, secureCode, CACHE_PREFIX_PASSPORTID_MODSECINFOSECURECODE);
    }

    private String getSecureCode(String passportId, int clientId, String prefix)
            throws ServiceException {
        String cacheKey = prefix + passportId + "_" + clientId;
        try {
            String secureCode = SecureCodeGenerator.generatorSecureCode(passportId, clientId);
            redisUtils.setWithinSeconds(cacheKey, secureCode, DateAndNumTimesConstant.SECURECODE_VALID);
            return secureCode;
        } catch (Exception e) {
            redisUtils.delete(cacheKey);
            throw new ServiceException(e);
        }
    }

    private boolean checkSecureCode(String passportId, int clientId, String secureCode, String prefix)
            throws ServiceException {
        try {
            String cacheKey = prefix + passportId + "_" + clientId;
            boolean flag = false;
            String scode = redisUtils.get(cacheKey);
            if (!Strings.isNullOrEmpty(scode)) {
                if (scode.equals(secureCode)) {
                    flag = true;
                }
                redisUtils.delete(scode);
            }
            return flag;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }
}
