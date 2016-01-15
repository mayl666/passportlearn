package com.sogou.upd.passport.common.utils;

import com.google.common.base.Strings;
import org.apache.commons.collections.MapUtils;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;

/**
 * Created by nahongxu on 2016/1/15.
 */
public class RiskControlRedisUtils {

    private static final Logger  logger=LoggerFactory.getLogger(RiskControlRedisUtils.class);
    private static final Logger redisMissLogger= LoggerFactory.getLogger("riskRedisMissMissLogger");
    private RedisTemplate redisTemplate;


    /**
     * 获取hash值
     */
    @Profiled(el = true, logger = "rediesTimingLogger", tag = "risk_redies_hGet", timeThreshold = 10, normalAndSlowSuffixesEnabled = true)
    public String hGet(String cacheKey, String key) {
        try {
            BoundHashOperations<String, String, String> boundHashOperations = redisTemplate.boundHashOps(cacheKey);
            String res = boundHashOperations.get(key);
            if (Strings.isNullOrEmpty(res)) {
                redisMissLogger.info("hGet cache miss, key:" + cacheKey);
            }
            return res;
        } catch (Exception e) {
            logger.error("[Cache] hGet cache fail, cacheKey:" + cacheKey + " mapKey:" + key, e);
        }
        return null;
    }

    /*
* 获取hash中所有的映射关系
*/
    @Profiled(el = true, logger = "rediesTimingLogger", tag = "risk_redies_hGetAll", timeThreshold = 10, normalAndSlowSuffixesEnabled = true)
    public Map<String, String> hGetAll(String cacheKey) {
        try {
            BoundHashOperations boundHashOperations = redisTemplate.boundHashOps(cacheKey);
            Map<String, Object> res = boundHashOperations.entries();
            if (MapUtils.isEmpty(res)) {
                redisMissLogger.info("hGetAll cache miss, key:" + cacheKey);
            }
            return res;
        } catch (Exception e) {
            logger.error("[Cache] hGet All cache fail, key:" + cacheKey, e);
        }
        return null;
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}
