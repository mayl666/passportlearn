package com.sogou.upd.passport.service.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.dao.account.MobilePassportMappingDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.service.account.MobilePassportMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-22
 * Time: 下午9:51
 * To change this template use File | Settings | File Templates.
 */
@Service
public class MobilePassportMappingServiceImpl implements MobilePassportMappingService {

    private static final String CACHE_PREFIX_MOBILE_PASSPORT = CacheConstant.CACHE_PREFIX_MOBILE_PASSPORTID;

    @Autowired
    private MobilePassportMappingDAO mobilePassportMappingDAO;
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public String queryPassportIdByMobile(String mobile) throws ServiceException {
        String passportId;
        try {
            String cacheKey = buildMobilePassportMappingKey(mobile);
            passportId = redisUtils.get(cacheKey);
            if (Strings.isNullOrEmpty(passportId)) {
                passportId = mobilePassportMappingDAO.getPassportIdByMobile(mobile);
                if (!Strings.isNullOrEmpty(passportId)) {
                    redisUtils.set(cacheKey, passportId);
                }
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return passportId;
    }

    @Override
    public String queryPassportIdByUsername(String username) throws ServiceException {
        String passportId;
        if (PhoneUtil.verifyPhoneNumberFormat(username)) {
            passportId = queryPassportIdByMobile(username);
        } else {
            passportId = username;
        }
        return passportId;
    }

    @Override
    public boolean initialMobilePassportMapping(String mobile, String passportId) throws ServiceException {
        try {
            long id = mobilePassportMappingDAO.insertMobilePassportMapping(mobile, passportId);
            if (id != 0) {
                String cacheKey = buildMobilePassportMappingKey(mobile);
                redisUtils.set(cacheKey, passportId);
                return true;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return false;
    }


    @Override
    public boolean updateMobilePassportMapping(String mobile, String passportId) throws ServiceException {
        try {
            int accountRow = mobilePassportMappingDAO.updateMobilePassportMapping(mobile, passportId);
            if (accountRow != 0) {
                String cacheKey = buildMobilePassportMappingKey(mobile);
                redisUtils.set(cacheKey, passportId);
                return true;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return false;
    }

    @Override
    public boolean deleteMobilePassportMapping(String mobile) throws ServiceException {
        try {
            int row = mobilePassportMappingDAO.deleteMobilePassportMapping(mobile);
            if (row != 0) {
                String cacheKey = buildMobilePassportMappingKey(mobile);
                redisUtils.delete(cacheKey);
                return true;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return false;
    }

    @Override
    public boolean deleteMobilePassportMappingCache(String mobile) throws ServiceException {
        try {
            String cacheKey = buildMobilePassportMappingKey(mobile);
            redisUtils.delete(cacheKey);
            return true;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    private String buildMobilePassportMappingKey(String mobile) {
        return CACHE_PREFIX_MOBILE_PASSPORT + mobile;
    }

}
