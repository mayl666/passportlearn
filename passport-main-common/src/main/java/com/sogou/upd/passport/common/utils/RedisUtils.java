package com.sogou.upd.passport.common.utils;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 * User: mayan
 * Date: 13-3-27
 * Time: 上午11:19
 * To change this template use File | Settings | File Templates.
 */
public class RedisUtils {

    private static Logger log = LoggerFactory.getLogger(RedisUtils.class);

    private static RedisTemplate redisTemplate;

    /*
    * 设置缓存内容
    */
    public void set(String key, String value) throws Exception {
        try {
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            valueOperations.set(key, value);
        } catch (Exception e) {
            log.error("[Cache] set cache fail, key:" + key + " value:" + value, e);
            try {
                delete(key);
            } catch (Exception ex) {
                log.error("[Cache] set and delete cache fail, key:" + key + " value:" + value, e);
                throw e;
            }
        }
    }

    /*
    * 设置缓存内容
    */
    public void set(String key, Object obj) throws Exception {
        try {
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            valueOperations.set(key, new Gson().toJson(obj));
        } catch (Exception e) {
            log.error("[Cache] set cache fail, key:" + key + " value:" + obj, e);
            try {
                delete(key);
            } catch (Exception ex) {
                log.error("[Cache] set and delete cache fail, key:" + key + " value:" + obj, e);
                throw e;
            }
        }
    }
  /*
    * 设置缓存内容
    */
  public void set(String key, String value,long timeout,TimeUnit timeUnit) throws Exception {
    try {
      ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
      valueOperations.set(key, value,timeout,timeUnit);
    } catch (Exception e) {
      log.error("[Cache] set cache fail, key:" + key + " value:" + value, e);

    }
  }

  /*
    * 设置缓存内容
    */
  public long increment(String key) throws Exception {
    long countNum=0;
    try {
      ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
      countNum=valueOperations.increment(key,1);
    } catch (Exception e) {
      log.error("[Cache] increment fail, key:" + key, e);
      throw e;
    }
    return countNum;
  }

    /*
    * 设置缓存内容
    * 冲突不覆盖
    */
    public boolean setNx(String cacheKey, Object obj) {
        try {
            BoundValueOperations boundValueOperation = redisTemplate.boundValueOps(cacheKey);
            return boundValueOperation.setIfAbsent(obj);
        } catch (Exception e) {
            log.error("[Cache] set if absent cache fail, key:" + cacheKey + " value:" + obj, e);
            return false;
        }
    }

    /*
   * 根据key取缓存内容
   */
    public String get(String key) {
        try {
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            return valueOperations.get(key);
        } catch (Exception e) {
            log.error("[Cache] get cache fail, key:" + key, e);
        }
        return null;
    }

    /**
     * 根据key取对象
     *
     * @param cacheKey
     * @param returnType
     * @return
     */
    public <T> T getObject(String cacheKey, Type returnType) {
        try {
            String cacheStr = get(cacheKey);
            if (!Strings.isNullOrEmpty(cacheStr)) {
                T object = new Gson().fromJson(cacheStr, returnType);
                return object;
            }
        } catch (Exception e) {
            log.error("[Cache] get object cache fail, key:" + cacheKey, e);
        }
        return null;
    }


    /*
   * 判断key是否存在
   */
    public boolean checkKeyIsExist(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("[Cache] check key is exist in cache fail, key:" + key, e);
            return false;
        }
    }

    /*
   * 获取hash中所有的映射关系
   */
    public Map<String, String> hGetAll(String cacheKey) {
        try {
            BoundHashOperations boundHashOperations = redisTemplate.boundHashOps(cacheKey);
            return boundHashOperations.entries();
        } catch (Exception e) {
            log.error("[Cache] hGet All cache fail, key:" + cacheKey, e);
        }
        return null;
    }

    /*
    * 设置hash映射关系
    */
    public void hPutAll(String cacheKey, Map<String, String> mapData) throws Exception {
        try {
            BoundHashOperations<String, String, String> boundHashOperations = redisTemplate.boundHashOps(cacheKey);
            boundHashOperations.putAll(mapData);
        } catch (Exception e) {
            log.error("[Cache] hPutAll cache fail, cacheKey:" + cacheKey, e);
            try {
                delete(cacheKey);
            } catch (Exception ex) {
                log.error("[Cache] hPutAll and delete cache fail, cacheKey:" + cacheKey, e);
                throw e;
            }
        }
    }

    /*
    * 设置hash映射关系
    */
    public <T> void hPutAllObject(String cacheKey, Map<String, T> mapData) throws Exception {
        try {
            Map<String, String> objectMap = Maps.newHashMap();
            Set<String> keySet = mapData.keySet();
            for (String key : keySet) {
                T obj = mapData.get(key);
                if (obj != null) {
                    objectMap.put(key, new Gson().toJson(obj));
                }
            }
            BoundHashOperations<String, String, Object> boundHashOperations = redisTemplate.boundHashOps(cacheKey);
            boundHashOperations.putAll(objectMap);
        } catch (Exception e) {
            log.error("[Cache] hPutAllObject cache fail, cacheKey:" + cacheKey, e);
            try {
                delete(cacheKey);
            } catch (Exception ex) {
                log.error("[Cache] hPutAllObject and delete cache fail, cacheKey:" + cacheKey, e);
                throw e;
            }
        }
    }

    /**
     * 记录存在则覆盖，不存在则插入
     *
     * @param cacheKey
     * @param key
     * @param value
     */
    public void hPut(String cacheKey, String key, String value) throws Exception {
        try {
            BoundHashOperations<String, String, String> boundHashOperations = redisTemplate.boundHashOps(cacheKey);
            boundHashOperations.put(key, value);
        } catch (Exception e) {
            log.error("[Cache] hPut cache fail, cacheKey:" + cacheKey + " mapKey:" + key + " mapValue:" + value, e);
            try {
                delete(cacheKey);
            } catch (Exception ex) {
                log.error("[Cache] hPut and delete cache fail, cacheKey:" + cacheKey + " mapKey:" + key + " mapValue:" + value, e);
                throw e;
            }
        }
    }

    public void hPut(String cacheKey, String key, Object obj) {
        BoundHashOperations<String, String, String> boundHashOperations = redisTemplate.boundHashOps(cacheKey);
        boundHashOperations.put(key, new Gson().toJson(obj));
    }

    /**
     * 记录存在则不覆盖返回false，不存在则插入返回true
     *
     * @param cacheKey
     * @param key
     * @param value
     * @return
     */
    public boolean hPutIfAbsent(String cacheKey, String key, String value) {
        try {
            BoundHashOperations<String, String, String> boundHashOperations = redisTemplate.boundHashOps(cacheKey);
            return boundHashOperations.putIfAbsent(key, value);
        } catch (Exception e) {
            log.error("[Cache] hPut if absent cache fail, key:" + cacheKey + "value:" + value, e);
            return false;
        }
    }

    public void hIncrBy(String cacheKey, String key) {
        try {
            BoundHashOperations<String, String, String> boundHashOperations = redisTemplate.boundHashOps(cacheKey);
            boundHashOperations.increment(key, 1);
        } catch (Exception e) {
            log.error("[Cache] hIncr num cache fail, key:" + cacheKey + "value:" + key, e);
        }
    }

    public void expire(String cacheKey, long timeout) {
        try {
            redisTemplate.expire(cacheKey, timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("[Cache] set cache expire fail, key:" + cacheKey + "timeout:" + timeout, e);
        }
    }

    public void delete(String cacheKey) {
        redisTemplate.delete(cacheKey);
    }

    /**
     * Map<String,String>转换成Map<String,Object>
     *
     * @param mapData
     * @param returnType
     * @param <T>
     * @return
     */
    public static <T> Map<String, T> strMapToObjectMap(Map<String, String> mapData, Type returnType) {
        Map<String, T> results = Maps.newHashMap();
        Set<String> keySet = mapData.keySet();
        for (String key : keySet) {
            String value = mapData.get(key);
            if (!Strings.isNullOrEmpty(value)) {
                T object = new Gson().fromJson(value, returnType);
                results.put(key, object);
            }
        }
        return results;
    }

    /*
     * 字符串转换byte数组
     */
    public static byte[] stringToByteArry(String str) {
        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        return stringSerializer.serialize(str);
    }

    /*
   * byte数组转换字符串
   */
    public static String byteArryToString(byte[] bytes) {
        String parseResult = null;
        if (bytes != null && bytes.length > 0) {
            RedisSerializer<String> stringSerializer = new StringRedisSerializer();
            parseResult = stringSerializer.deserialize(bytes);
        }
        return parseResult;
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}
