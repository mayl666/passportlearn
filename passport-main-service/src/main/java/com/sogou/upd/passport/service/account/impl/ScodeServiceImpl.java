package com.sogou.upd.passport.service.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.service.account.ScodeService;
import com.sogou.upd.passport.service.account.generator.SecureCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xieyilun on 2016/1/26.
 */
@Service
public class ScodeServiceImpl implements ScodeService {
    @Autowired
    private RedisUtils redisUtils;

    public String generate(final String passportId, final int appid) {
        String scode = null;
        try {
            scode = SecureCodeGenerator.generatorSecureCode(passportId, appid);
            String cacheKey = buildCacheKeyForScode(passportId, appid);
            redisUtils.setWithinSeconds(cacheKey, scode, DateAndNumTimesConstant.TIME_FIVEMINUTES);
        } catch (Exception e) {
            return null;
        }
        return scode;
    }

    public boolean verify(final String passportId, final int appid, final String scode) {
        if (Strings.isNullOrEmpty(scode)) {
            return false;
        }
        String cacheKey = buildCacheKeyForScode(passportId, appid);
        String cacheScode = redisUtils.get(cacheKey);
        if (cacheScode.equals(scode)) {
            redisUtils.delete(cacheKey);
            return true;
        }
        return false;
    }

    private String buildCacheKeyForScode(String passportId, int appid) {
        return CacheConstant.CACHE_PREFIX_PASSPORTID_PASSPORTID_SECURECODE + appid + "_" + passportId;
    }
}
