package com.sogou.upd.passport.service.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.dao.account.UniqNamePassportMappingDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.service.account.UniqNamePassportMappingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-11-28
 * Time: 上午2:15
 * To change this template use File | Settings | File Templates.
 */
@Service
public class UniqNamePassportMappingServiceImpl implements UniqNamePassportMappingService {

    private static final String CACHE_PREFIX_NICKNAME_PASSPORTID = CacheConstant.CACHE_PREFIX_NICKNAME_PASSPORTID;

    private static final Logger logger = LoggerFactory.getLogger(UniqNamePassportMappingService.class);

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private UniqNamePassportMappingDAO uniqNamePassportMappingDAO;

    @Override
    public String checkUniqName(String nickname) throws ServiceException {
        String passportId = null;
        try {
            String cacheKey = CACHE_PREFIX_NICKNAME_PASSPORTID + nickname;
            passportId = redisUtils.get(cacheKey);
            if (Strings.isNullOrEmpty(passportId)) {
                passportId = uniqNamePassportMappingDAO.getPassportIdByUniqName(nickname);
                if (!Strings.isNullOrEmpty(passportId)) {
                    redisUtils.set(cacheKey, passportId);
                }
            }
        } catch (Exception e) {
            logger.error("checkUniqName fail", e);
            throw new ServiceException(e);
        }
        return passportId;
    }

    @Override
    public boolean insertUniqName(String passportId, String uniqName) throws ServiceException {
        try {
            int row = uniqNamePassportMappingDAO.insertUniqNamePassportMapping(uniqName, passportId);
            if (row > 0) {
                String cacheKey = CACHE_PREFIX_NICKNAME_PASSPORTID + uniqName;
                redisUtils.set(cacheKey, passportId);
                return true;
            }
        } catch (Exception e) {
            logger.error("insertUniqName fail", e);
            throw new ServiceException(e);
        }
        return false;
    }
}
