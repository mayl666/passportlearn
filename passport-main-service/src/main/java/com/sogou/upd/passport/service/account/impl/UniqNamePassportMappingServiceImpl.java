package com.sogou.upd.passport.service.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.utils.DBShardRedisUtils;
import com.sogou.upd.passport.dao.account.UniqNamePassportMappingDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.service.account.UniqNamePassportMappingService;
import org.perf4j.aop.Profiled;
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
    private DBShardRedisUtils dbShardRedisUtils;
    @Autowired
    private UniqNamePassportMappingDAO uniqNamePassportMappingDAO;

    @Profiled(el = true, logger = "dbTimingLogger", tag = "service_getPassportIdByUniqName", timeThreshold = 20, normalAndSlowSuffixesEnabled = true)
    @Override
    public String checkUniqName(String uniqname) throws ServiceException {
        String passportId = null;
        try {
            String cacheKey = CACHE_PREFIX_NICKNAME_PASSPORTID + uniqname;
            passportId = dbShardRedisUtils.get(cacheKey);
            if (Strings.isNullOrEmpty(passportId)) {
                passportId = uniqNamePassportMappingDAO.getPassportIdByUniqName(uniqname);
                if (!Strings.isNullOrEmpty(passportId)) {
                    dbShardRedisUtils.setStringWithinSeconds(cacheKey, passportId, DateAndNumTimesConstant.ONE_MONTH);
                }
            }
        } catch (Exception e) {
            logger.error("checkUniqName fail", e);
            throw new ServiceException(e);
        }
        return passportId;
    }

    @Profiled(el = true, logger = "dbTimingLogger", tag = "service_insertUniqNameMapping", timeThreshold = 20, normalAndSlowSuffixesEnabled = true)
    @Override
    public boolean insertUniqName(String passportId, String uniqname) throws ServiceException {
        try {
            int row = uniqNamePassportMappingDAO.insertUniqNamePassportMapping(uniqname, passportId);
            if (row > 0) {
                String cacheKey = CACHE_PREFIX_NICKNAME_PASSPORTID + uniqname;
                dbShardRedisUtils.setStringWithinSeconds(cacheKey, passportId, DateAndNumTimesConstant.ONE_MONTH);
                return true;
            }
        } catch (Exception e) {
            logger.error("insertUniqName fail,passportId=" + passportId + ",uniqname=" + uniqname, e);
            throw new ServiceException(e);
        }
        return false;
    }

    @Profiled(el = true, logger = "dbTimingLogger", tag = "service_removeUniqNameMapping", timeThreshold = 20, normalAndSlowSuffixesEnabled = true)
    @Override
    public boolean removeUniqName(String uniqname) throws ServiceException {
        try {
            if (!Strings.isNullOrEmpty(uniqname)) {
                //更新映射
                int row = uniqNamePassportMappingDAO.deleteUniqNamePassportMapping(uniqname);
                if (row > 0) {
                    String cacheKey = CACHE_PREFIX_NICKNAME_PASSPORTID + uniqname;
                    dbShardRedisUtils.delete(cacheKey);
                    return true;
                } else if (row == 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("removeUniqName fail", e);
            return false;
        }
        return true;
    }
}
