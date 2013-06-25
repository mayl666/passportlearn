package com.sogou.upd.passport.common.utils;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;

/**
 * sogou kv系统 User: mayan Date: 13-6-24 Time: 下午6:34
 */
public class KvUtils {

    private static Logger logger = LoggerFactory.getLogger(KvUtils.class);
    private static String KEY_PREFIX = "20002/action_records/";

    private static RedisTemplate kvTemplate;

    public void set(String key, String value) {
        String storeKey = KEY_PREFIX + key;
        try {
            ValueOperations<String, String> valueOperations = kvTemplate.opsForValue();
            valueOperations.set(storeKey, value);
        } catch (Exception e) {
            // logger.error("[Cache] set cache fail, key:" + key + " value:" + value, e);
            System.out.println(e.getMessage());
            try {
                delete(key);
            } catch (Exception ex) {
                logger.error("[Cache] set and delete cache fail, key:" + key + " value:" + value, e);
                throw e;
            }
        }
    }

    public String get(String key) {
        String storeKey = KEY_PREFIX + key;
        try {
            ValueOperations<String, String> valueOperations = kvTemplate.opsForValue();
            return valueOperations.get(storeKey);
        } catch (Exception e) {
            logger.error("[Cache] get cache fail, key:" + key, e);
        }
        return null;
    }

    public void delete(String key) {
        String storeKey = KEY_PREFIX + key;
        kvTemplate.delete(storeKey);
    }



    public RedisTemplate getKvTemplate() {
        return kvTemplate;
    }

    public void setKvTemplate(RedisTemplate kvTemplate) {
        this.kvTemplate = kvTemplate;
    }
}
