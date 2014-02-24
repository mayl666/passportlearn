package com.sogou.upd.passport.service.connect.impl;

import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.service.connect.AccessTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA. User: shipengzhi Date: 13-3-24 Time: 下午8:08 To change this template
 * use File | Settings | File Templates.
 */
@Service
public class AccessTokenServiceImpl implements AccessTokenService {

    private Logger logger = LoggerFactory.getLogger(AccessTokenService.class);
    @Autowired
    private RedisUtils redisUtils;

    private static final String CACHE_PREFIX_PASSPORTID_ACCESSTOKEN = CacheConstant.CACHE_PREFIX_PASSPORTID_ACCESSTOKEN;

    @Override
    public String getAccessToken(String userid) throws ServiceException {
        try {
            String cacheKey = buildConnectTokenCacheKey(userid);
            String value = redisUtils.get(cacheKey);
            return value;
        } catch (Exception e) {
            logger.error("[ConnectToken] service method insertAccountConnect error.{}", e);
            throw new ServiceException(e);
        }
    }

    private String buildConnectTokenCacheKey(String passportId) {
        return CACHE_PREFIX_PASSPORTID_ACCESSTOKEN + passportId;
    }
}
