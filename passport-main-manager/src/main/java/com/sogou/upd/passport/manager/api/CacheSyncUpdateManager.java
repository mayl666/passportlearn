package com.sogou.upd.passport.manager.api;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.parameter.CacheOperEnum;
import com.sogou.upd.passport.common.parameter.CacheTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.DBShardRedisUtils;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.common.utils.TokenRedisUtils;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.service.account.AccountInfoService;
import com.sogou.upd.passport.service.account.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 数据同步中缓存更新方法
 * User: shipengzhi
 * Date: 14-6-3
 * Time: 下午8:58
 * To change this template use File | Settings | File Templates.
 */
@Component
public class CacheSyncUpdateManager {

    private static Logger log = LoggerFactory.getLogger(CacheSyncUpdateManager.class);

    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountInfoService accountInfoService;
    @Autowired
    private DBShardRedisUtils dbShardRedisUtils;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private TokenRedisUtils tokenRedisUtils;

    public Result deleteTableCache(String passportId) {
        Result result = new APIResultSupport(false);
        try {
            boolean isDelAccount = accountService.deleteAccountCacheByPassportId(passportId);
            boolean isDelAccountInfo = accountInfoService.deleteAccountInfoCacheByPassportId(passportId);
            if (isDelAccount && isDelAccountInfo) {
//                    mobilePassportMappingService.deleteMobilePassportMappingCache(key);
                result.setSuccess(true);
                result.setMessage("passportId：" + passportId + " delete cache success!");
            } else {
                result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            }
        } catch (ServiceException e) {
            log.error("passportId：" + passportId + " delete cache fail!", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }

    public Result readOperCache(String cacheKey, String cacheType, String cacheOper) {
        Result result = new APIResultSupport(false);
        String cacheValueStr = "";
        Map cacheValueMap = Maps.newHashMap();
        try {
            if (CacheTypeEnum.db.toString().equals(cacheType)) {
                if (CacheOperEnum.get.toString().equals(cacheOper)) {
                    cacheValueStr = dbShardRedisUtils.get(cacheKey);
                } else if (CacheOperEnum.hget.toString().equals(cacheOper)) {
                    if (cacheKey.contains(",")) {
                        String[] keyArray = cacheKey.split(",");
                        cacheValueStr = dbShardRedisUtils.hGet(keyArray[0], keyArray[1]);
                    } else {
                        result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                        result.setMessage("oper=hget，key not contains “，”");
                        return result;
                    }
                } else if (CacheOperEnum.hgetall.toString().equals(cacheOper)) {
                    cacheValueMap = dbShardRedisUtils.hGetAll(cacheKey);
                }
            } else if (CacheTypeEnum.cache.toString().equals(cacheType)) {
                if (CacheOperEnum.get.toString().equals(cacheOper)) {
                    cacheValueStr = redisUtils.get(cacheKey);
                } else if (CacheOperEnum.hget.toString().equals(cacheOper)) {
                    if (cacheKey.contains(",")) {
                        String[] keyArray = cacheKey.split(",");
                        cacheValueStr = redisUtils.hGet(keyArray[0], keyArray[1]);
                    } else {
                        result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                        result.setMessage("oper=hget，key not contains “，”");
                        return result;
                    }
                } else if (CacheOperEnum.hgetall.toString().equals(cacheOper)) {
                    cacheValueMap = redisUtils.hGetAll(cacheKey);
                }
            } else if (CacheTypeEnum.token.toString().equals(cacheType)) {
                if (CacheOperEnum.get.toString().equals(cacheOper)) {
                    cacheValueStr = tokenRedisUtils.get(cacheKey);
                } else if (CacheOperEnum.hget.toString().equals(cacheOper)) {
                    if (cacheKey.contains(",")) {
                        String[] keyArray = cacheKey.split(",");
                        cacheValueStr = tokenRedisUtils.hGet(keyArray[0], keyArray[1]);
                    } else {
                        result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                        result.setMessage("oper=hget，key not contains “，”");
                        return result;
                    }
                } else if (CacheOperEnum.hgetall.toString().equals(cacheOper)) {
                    cacheValueMap = tokenRedisUtils.hGetAll(cacheKey);
                }
            }
            result.setSuccess(true);
            if(CacheOperEnum.hgetall.toString().equals(cacheOper)) {
               result.setDefaultModel("value", cacheValueMap);
            } else {
                result.setDefaultModel("value", cacheValueStr);
            }
        } catch (ServiceException e) {
            log.error("cacheKey：" + cacheKey + ",cacheType：" + cacheType + ",cacheOper：" + cacheOper + ",fail!", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }

}

