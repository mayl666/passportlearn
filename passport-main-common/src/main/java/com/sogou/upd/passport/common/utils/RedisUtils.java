package com.sogou.upd.passport.common.utils;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

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
 * Redis工具类 User: mayan Date: 13-3-27 Time: 上午11:19 To change this template use File | Settings |
 * File Templates.
 */
public class RedisUtils {

  private static RedisTemplate redisTemplate;

  /*
  * 设置缓存内容
  */
  public void set(String key, String value) {
    ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
    valueOperations.set(key, value);
  }

  /*
  * 设置缓存内容
  */
  public void set(String key, Object obj) {
    ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
    valueOperations.set(key, new Gson().toJson(obj));
  }

  /*
  * 设置缓存内容
  * 冲突不覆盖
  */
  public boolean setNx(String cacheKey, Object obj) {
    BoundValueOperations boundValueOperation = redisTemplate.boundValueOps(cacheKey);
    return boundValueOperation.setIfAbsent(obj);
  }

  /*
 * 根据key取缓存内容
 */
  public String get(String key) {
    ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
    return valueOperations.get(key);
  }

  /**
   * 根据key取对象
   */
  public <T> T getObject(String cacheKey, Type returnType) {

    String cacheStr = get(cacheKey);
    if (!Strings.isNullOrEmpty(cacheStr)) {
      T object = new Gson().fromJson(cacheStr, returnType);
      return object;
    }
    return null;
  }


  /*
 * 判断key是否存在
 */
  public static boolean checkKeyIsExist(String key) {
    if (redisTemplate.hasKey(key)) {
      return true;
    }
    return false;
  }

  /*
 * 获取hash中所有的映射关系
 */
  public Map<String, String> hGetAll(String cacheKey) {
    BoundHashOperations boundHashOperations = redisTemplate.boundHashOps(cacheKey);
    return boundHashOperations.entries();
  }

  /*
  * 设置hash映射关系
  */
  public void hPutAll(String cacheKey, Map<String, String> mapData) {
    BoundHashOperations<String, String, String>
        boundHashOperations =
        redisTemplate.boundHashOps(cacheKey);
    boundHashOperations.putAll(mapData);
  }

  /*
  * 设置hash映射关系
  */
  public <T> void hPutAllObject(String cacheKey, Map<String, T> mapData) {
    if (mapData != null && !mapData.isEmpty()) {
      Map<String, String> objectMap = Maps.newHashMap();
      Set<String> keySet = mapData.keySet();
      for (String key : keySet) {
        T obj = mapData.get(key);
        if (obj != null) {
          objectMap.put(key, new Gson().toJson(obj));
        }
      }
      BoundHashOperations<String, String, Object>
          boundHashOperations =
          redisTemplate.boundHashOps(cacheKey);
      boundHashOperations.putAll(objectMap);
    }

  }

  public void hPut(String cacheKey, String key, String value) {
    BoundHashOperations<String, String, String>
        boundHashOperations =
        redisTemplate.boundHashOps(cacheKey);
    boundHashOperations.put(key, value);
  }

  public void hPut(String cacheKey, String key, Object obj) {
    BoundHashOperations<String, String, String>
        boundHashOperations =
        redisTemplate.boundHashOps(cacheKey);
    boundHashOperations.put(key, new Gson().toJson(obj));
  }


  public boolean hPutIfAbsent(String cacheKey, String key, String value) {
    BoundHashOperations<String, String, String>
        boundHashOperations =
        redisTemplate.boundHashOps(cacheKey);
    return boundHashOperations.putIfAbsent(key, value);
  }

  public void hIncrBy(String cacheKey, String key) {
    BoundHashOperations<String, String, String>
        boundHashOperations =
        redisTemplate.boundHashOps(cacheKey);
    boundHashOperations.increment(key, 1);
  }

  public void expire(String cacheKey, long timeout) {
    redisTemplate.expire(cacheKey, timeout, TimeUnit.SECONDS);
  }

  public void delete(String cacheKey) {
    redisTemplate.delete(cacheKey);
  }

  /**
   * Map<String,String>转换成Map<String,Object>
   */
  public static <T> Map<String, T> strMapToObjectMap(Map<String, String> mapData, Type returnType) {
    Map<String, T> results = Maps.newHashMap();
    if (mapData != null && !mapData.isEmpty()) {
      Set<String> keySet = mapData.keySet();
      for (String key : keySet) {
        String value = mapData.get(key);
        if (!Strings.isNullOrEmpty(value)) {
          T object = new Gson().fromJson(value, returnType);
          results.put(key, object);
        }
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
