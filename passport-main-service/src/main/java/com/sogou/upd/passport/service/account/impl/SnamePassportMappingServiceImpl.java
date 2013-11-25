package com.sogou.upd.passport.service.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.dao.account.SnamePassportMappingDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.service.account.SnamePassportMappingService;
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
public class SnamePassportMappingServiceImpl implements SnamePassportMappingService {

    private static final String CACHE_PREFIX_SNAME_PASSPORTID = CacheConstant.CACHE_PREFIX_SNAME_PASSPORTID;

    @Autowired
    private SnamePassportMappingDAO snamePassportMappingDAO;
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public String queryPassportIdBySname(String sname) throws ServiceException {
        String passportId;
        try {
            String cacheKey = buildSnamePassportMappingKey(sname);
            passportId = redisUtils.get(cacheKey);
            if (Strings.isNullOrEmpty(passportId)) {
                passportId = snamePassportMappingDAO.getPassportIdBySname(sname);
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
    public boolean initialSnamePassportMapping(String sname, String passportId) throws ServiceException {
        try {
            long id = snamePassportMappingDAO.insertSnamePassportMapping(sname, passportId);
            if (id != 0) {
                String cacheKey = buildSnamePassportMappingKey(sname);
                redisUtils.set(cacheKey, passportId);
                return true;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return false;
    }


    @Override
    public boolean updateSnamePassportMapping(String sname, String passportId) throws ServiceException {
        try {
            int accountRow = snamePassportMappingDAO.updateSnamePassportMapping(sname, passportId);
            if (accountRow != 0) {
                String cacheKey = buildSnamePassportMappingKey(sname);
                redisUtils.set(cacheKey, passportId);
                return true;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return false;
    }

    @Override
    public boolean deleteSnamePassportMapping(String sname) throws ServiceException {
        try {
            int row = snamePassportMappingDAO.deleteSnamePassportMapping(sname);
            if (row != 0) {
                String cacheKey = buildSnamePassportMappingKey(sname);
                redisUtils.delete(cacheKey);
                return true;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return false;
    }

    private String buildSnamePassportMappingKey(String sname) {
        return CACHE_PREFIX_SNAME_PASSPORTID + sname;
    }

}
