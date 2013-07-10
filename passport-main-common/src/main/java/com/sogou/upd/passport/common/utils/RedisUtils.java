package com.sogou.upd.passport.common.utils;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 * User: mayan
 * Date: 13-3-27
 * Time: 上午11:19
 * To change this template use File | Settings | File Templates.
 */
public class RedisUtils {

    private static Logger logger = LoggerFactory.getLogger(RedisUtils.class);

    private static RedisTemplate redisTemplate;

    private static final String ALL_REQUEST_TIMER="REDIES_ALL_REQUEST";

    /*
    * 设置缓存内容
    */
    @Profiled(el = true,logger = "rediesTimingLogger",tag = "redies_set")
    public void set(String key, String value) throws Exception {
        try {
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            valueOperations.set(key, value);
        } catch (Exception e) {
            logger.error("[Cache] set cache fail, key:" + key + " value:" + value, e);
            try {
                delete(key);
            } catch (Exception ex) {
                logger.error("[Cache] set and delete cache fail, key:" + key + " value:" + value, e);
                throw e;
            }
        }
    }

    /*
    * 设置缓存内容
    */
    @Profiled(el = true,logger = "rediesTimingLogger",tag = "redies_set")
    public void set(String key, Object obj) throws Exception {
        try {
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            valueOperations.set(key, new ObjectMapper().writeValueAsString(obj));
        } catch (Exception e) {
            logger.error("[Cache] set cache fail, key:" + key + " value:" + obj, e);
            try {
                delete(key);
            } catch (Exception ex) {
                logger.error("[Cache] set and delete cache fail, key:" + key + " value:" + obj, e);
                throw e;
            }
        }
    }
    /*
    * 设置缓存内容
    */
    @Profiled(el = true,logger = "rediesTimingLogger",tag = "redies_set")
    public void set(String key, String value,long timeout,TimeUnit timeUnit) throws Exception {
        try {
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            valueOperations.set(key, value,timeout,timeUnit);
        } catch (Exception e) {
            logger.error("[Cache] set cache fail, key:" + key + " value:" + value, e);

        }
    }

    /*
    * 设置缓存内容
    */
    @Profiled(el = true,logger = "rediesTimingLogger",tag = "redies_set")
    public void set(String key, Object obj,long timeout,TimeUnit timeUnit) throws Exception {
        try {
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            valueOperations.set(key, new ObjectMapper().writeValueAsString(obj),timeout,timeUnit);
        } catch (Exception e) {
            logger.error("[Cache] set cache fail, key:" + key + " value:" + obj, e);

        }
    }

    /*
     * 设置缓存内容及有效期，单位为秒
     * TODO:是否抛出异常及如何处理
     */
    @Profiled(el = true,logger = "rediesTimingLogger",tag = "redies_set")
    public void setWithinSeconds(String key, String value, long timeout) throws Exception {
        set(key, value, timeout, TimeUnit.SECONDS);

    }

    @Profiled(el = true,logger = "rediesTimingLogger",tag = "redies_set")
    public void setWithinSeconds(String key, Object obj, long timeout) throws Exception {
        set(key, obj, timeout, TimeUnit.SECONDS);
    }

    /*
     * 设置缓存内容
    */
    @Profiled
    public <T> void multiSet(Map<String, T> mapData) throws Exception {
        try {
            Map<String, String> objectMap = Maps.newHashMap();
            Set<String> keySet = mapData.keySet();
            for (String key : keySet) {
                T obj = mapData.get(key);
                if (obj != null) {
                    objectMap.put(key, new ObjectMapper().writeValueAsString(obj));
                }
            }
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            valueOperations.multiSet(objectMap);
        } catch (Exception e) {
            logger.error("[Cache] set cache fail, key:" + mapData.toString(), e);
            try {
                multiDelete(mapData.keySet());
            } catch (Exception ex) {
                logger.error("[Cache] set and delete cache fail, key:" + mapData.toString(), e);
                throw e;
            }
        }
    }

    /*
      * 设置缓存内容
      */
    @Profiled(el = true,logger = "rediesTimingLogger",tag = "redies_increment")
    public long increment(String key) throws Exception {
        long countNum=0;
        try {
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            countNum=valueOperations.increment(key,1);
        } catch (Exception e) {
            logger.error("[Cache] increment fail, key:" + key, e);
            throw e;
        }
        return countNum;
    }

    /*
    * 设置缓存内容
    * 冲突不覆盖
    */
    @Profiled(el = true,logger = "rediesTimingLogger",tag = "redies_setNx")
    public boolean setNx(String cacheKey, Object obj) {
        try {
            BoundValueOperations boundValueOperation = redisTemplate.boundValueOps(cacheKey);
            return boundValueOperation.setIfAbsent(obj);
        } catch (Exception e) {
            logger.error("[Cache] set if absent cache fail, key:" + cacheKey + " value:" + obj, e);
            return false;
        }
    }

    /*
   * 根据key取缓存内容
   */
    @Profiled(el = true,logger = "rediesTimingLogger",tag = "redies_get")
    public String get(String key) {
        try {
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            return valueOperations.get(key);
        } catch (Exception e) {
            logger.error("[Cache] get cache fail, key:" + key, e);
        }
        return null;
    }

    /*
     * 根据key取缓存内容
    */
    @Profiled
    public List<String> multiGet(List<String> keyList) {
        try {
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            return  valueOperations.multiGet(keyList);
        } catch (Exception e) {
            logger.error("[Cache] get cache fail, keyCollec:" + keyList.toString(), e);
        }
        return null;
    }

    /**
     * 根据key取对象
     *
     * @param cacheKey
     * @param returnClass
     * @return
     */
    @Profiled(el = true,logger = "rediesTimingLogger",tag = "redies_getObject")
    public <T> T getObject(String cacheKey, Class returnClass) {
        try {
            String cacheStr = get(cacheKey);
            if (!Strings.isNullOrEmpty(cacheStr)) {
              T object = (T) new ObjectMapper().readValue(cacheStr, returnClass);
                return object;
            }
        } catch (Exception e) {
            logger.error("[Cache] get object cache fail, key:" + cacheKey, e);
        }
        return null;
    }


    /*
   * 判断key是否存在
   */
    @Profiled(el = true,logger = "rediesTimingLogger",tag = "redies_checkKeyIsExist")
    public boolean checkKeyIsExist(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            logger.error("[Cache] check key is exist in cache fail, key:" + key, e);
            return false;
        }
    }

    /*
   * 获取hash中所有的映射关系
   */
    @Profiled(el = true,logger = "rediesTimingLogger",tag = "redies_hGetAll")
    public Map<String, String> hGetAll(String cacheKey) {
        try {
            BoundHashOperations boundHashOperations = redisTemplate.boundHashOps(cacheKey);
            return boundHashOperations.entries();
        } catch (Exception e) {
            logger.error("[Cache] hGet All cache fail, key:" + cacheKey, e);
        }
        return null;
    }

    /*
    * 设置hash映射关系
    */
    @Profiled(el = true,logger = "rediesTimingLogger",tag = "redies_hPutAll")
    public void hPutAll(String cacheKey, Map<String, String> mapData) throws Exception {
        try {

            BoundHashOperations<String, String, String> boundHashOperations = redisTemplate.boundHashOps(cacheKey);
            boundHashOperations.putAll(mapData);
        } catch (Exception e) {
            logger.error("[Cache] hPutAll cache fail, cacheKey:" + cacheKey, e);
            try {
                delete(cacheKey);
            } catch (Exception ex) {
                logger.error("[Cache] hPutAll and delete cache fail, cacheKey:" + cacheKey, e);
                throw e;
            }
        }
    }

    /*
    * 设置hash映射关系
    */
    @Profiled(el = true,logger = "rediesTimingLogger",tag = "redies_cacheKey")
    public <T> void hPutAllObject(String cacheKey, Map<String, T> mapData) throws Exception {
        try {
            Map<String, String> objectMap = Maps.newHashMap();
            Set<String> keySet = mapData.keySet();
            for (String key : keySet) {
                T obj = mapData.get(key);
                if (obj != null) {
                    objectMap.put(key, new ObjectMapper().writeValueAsString(obj));
                }
            }
            BoundHashOperations<String, String, Object> boundHashOperations = redisTemplate.boundHashOps(cacheKey);
            boundHashOperations.putAll(objectMap);
        } catch (Exception e) {
            logger.error("[Cache] hPutAllObject cache fail, cacheKey:" + cacheKey, e);
            try {
                delete(cacheKey);
            } catch (Exception ex) {
                logger.error("[Cache] hPutAllObject and delete cache fail, cacheKey:" + cacheKey, e);
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
    @Profiled(el = true,logger = "rediesTimingLogger",tag = "redies_hPut")
    public void hPut(String cacheKey, String key, String value) throws Exception {
        try {
            BoundHashOperations<String, String, String> boundHashOperations = redisTemplate.boundHashOps(cacheKey);
            boundHashOperations.put(key, value);
        } catch (Exception e) {
            logger.error("[Cache] hPut cache fail, cacheKey:" + cacheKey + " mapKey:" + key
                      + " mapValue:" + value, e);
            try {
                delete(cacheKey);
            } catch (Exception ex) {
                logger.error("[Cache] hPut and delete cache fail, cacheKey:" + cacheKey + " mapKey:"
                          + key + " mapValue:" + value, e);
                throw e;
            }
        }
    }

    @Profiled(el = true,logger = "rediesTimingLogger",tag = "redies_hPut")
    public void hPut(String cacheKey, String key, Object obj) throws Exception {
        BoundHashOperations<String, String, String> boundHashOperations = redisTemplate.boundHashOps(cacheKey);
        boundHashOperations.put(key, new ObjectMapper().writeValueAsString(obj));
    }

    /**
     * 获取hash值
     *
     * @param cacheKey
     * @param key
     * @return
     */
    @Profiled(el = true,logger = "rediesTimingLogger",tag = "redies_hGet")
    public String hGet(String cacheKey, String key)  {
        try {
            BoundHashOperations<String, String, String> boundHashOperations = redisTemplate.boundHashOps(
                cacheKey);
            return boundHashOperations.get(key);
        } catch (Exception e) {
            logger.error("[Cache] hGet cache fail, cacheKey:" + cacheKey + " mapKey:" + key, e);
        }
        return null;
    }

    @Profiled(el = true,logger = "rediesTimingLogger",tag = "redies_hGetObject")
    public  <T> T hGetObject(String cacheKey, String key, Class returnClass) {
        try {
            BoundHashOperations<String, String, String> boundHashOperations = redisTemplate.boundHashOps(cacheKey);
            String cacheStr = boundHashOperations.get(key);
            if (!Strings.isNullOrEmpty(cacheStr)) {
              T object = (T) new ObjectMapper().readValue(cacheStr, returnClass);
                return object;
            }
        } catch (Exception e) {
            logger.error("[Cache] hGet object cache fail, cacheKey:" + cacheKey + " mapKey:" + key, e);
        }
        return null;
    }

    /**
     * 删除Hash键值
     *
     * @param cacheKey
     * @param key
     */
    @Profiled(el = true,logger = "rediesTimingLogger",tag = "redies_hDelete")
    public void hDelete(String cacheKey, String key) {
        try {
            BoundHashOperations<String, String, String> boundHashOperations = redisTemplate.boundHashOps(cacheKey);
            boundHashOperations.delete(key);
        } catch (Exception e) {
            logger.error("[Cache] hDelete cache fail, cacheKey:" + cacheKey + " mapKey:" + key, e);
        }
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
            logger.error("[Cache] hPut if absent cache fail, key:" + cacheKey + "value:" + value, e);
            return false;
        }
    }

    @Profiled(el = true,logger = "rediesTimingLogger",tag = "redies_hIncrBy")
    public void hIncrBy(String cacheKey, String key) {
        try {
            BoundHashOperations<String, String, String> boundHashOperations = redisTemplate.boundHashOps(cacheKey);
            boundHashOperations.increment(key, 1);
        } catch (Exception e) {
            logger.error("[Cache] hIncr num cache fail, key:" + cacheKey + "value:" + key, e);
        }
    }

    @Profiled(el = true,logger = "rediesTimingLogger",tag = "redies_expire")
    public void expire(String cacheKey, long timeout) {
        try {
            redisTemplate.expire(cacheKey, timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error("[Cache] set cache expire fail, key:" + cacheKey + "timeout:" + timeout, e);
        }
    }

    @Profiled(el = true,logger = "rediesTimingLogger",tag = "redies_delete")
    public void delete(String cacheKey) {
        redisTemplate.delete(cacheKey);
    }

    @Profiled(el = true,logger = "rediesTimingLogger",tag = "redies_multiDelete")
    public void multiDelete(Collection cacheKeyList) {
        redisTemplate.delete(cacheKeyList);
    }
    /**
     * Map<String,String>转换成Map<String,Object>
     *
     * @param mapData
     * @param returnClass
     * @return
     */
    public static <T> Map<String, T> strMapToObjectMap(Map<String, String> mapData, Class returnClass)
        throws Exception {
        Map<String, T> results = Maps.newHashMap();
        Set<String> keySet = mapData.keySet();
        for (String key : keySet) {
            String value = mapData.get(key);
            if (!Strings.isNullOrEmpty(value)) {
              T object = (T) new ObjectMapper().readValue(value,returnClass);
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

    // 将value添加到键key的列表尾部
    public void rPush(String key, String value) {
        ListOperations<String, String> valueList = redisTemplate.opsForList();
        valueList.rightPush(key, value);
    }

    // 将value添加到键key的列表头部
    public void lPush(String key, String value) {
        ListOperations<String, String> valueList = redisTemplate.opsForList();
        valueList.leftPush(key, value);
    }

  // set add
  public void sadd(String key, String value) {
    SetOperations operations=redisTemplate.opsForSet();
    operations.add(key,value);
  }

  public Set<String> smember(String key) {
    SetOperations operations=redisTemplate.opsForSet();
    return operations.members(key);
  }

    // 将value添加到键key的列表尾部，超过maxLen则删除头部元素
    public void rPushWithMaxLen(String key, String value, int maxLen) {
        ListOperations<String, String> valueList = redisTemplate.opsForList();
        valueList.rightPush(key, value);
        if (maxLen <= 0) {
            return ;
        }
        while (valueList.size(key) > maxLen) {
            valueList.leftPop(key);
        }
    }

    // 将value添加到键key的列表头部，超过maxLen则删除尾部元素
    public void lPushWithMaxLen(String key, String value, int maxLen) {
        ListOperations<String, String> valueList = redisTemplate.opsForList();
        valueList.leftPush(key, value);
        if (maxLen <= 0) {
            return ;
        }
        while (valueList.size(key) > maxLen) {
            valueList.rightPop(key);
        }
    }

    public void lPushObjectWithMaxLen(String key, Object obj, int maxLen) {
        try {
            lPushWithMaxLen(key, new ObjectMapper().writeValueAsString(obj), maxLen);
        } catch (Exception e) {
            logger.error("[Cache] lpush object key: " + key, e);
        }
    }

    public <T> T lTop(String key, Class<T> returnClass) {
        try {
            ListOperations<String, String> valueList = redisTemplate.opsForList();
            long len = valueList.size(key);
            if (len <= 0) {
                return null;
            }

            List<String> storeList = valueList.range(key, 0, 0);
            if (CollectionUtils.isNotEmpty(storeList)) {
                String value = storeList.get(0);
                T object = new ObjectMapper().readValue(value, returnClass);
                return object;
            }
            return null;
        } catch (Exception e) {
            logger.error("[Cache] get top value for object key: " + key, e);
            return null;
        }
    }

    // 查询键key的列表
    @Profiled(el = true,logger = "rediesTimingLogger",tag = "getList")
    public List<String> getList(String key) {
        ListOperations<String, String> valueList = redisTemplate.opsForList();
        long len = valueList.size(key);
        if (len <= 0) {
            return null;
        }

        List<String> storeList = valueList.range(key, 0, len - 1);
        return storeList;
    }

    @Profiled(el = true,logger = "rediesTimingLogger",tag = "getList")
    public <T> List<T> getList(String key, Class returnClass) {
        try {
            List<String> storeList = getList(key);
            if (storeList == null) {
                // 不需要检查大小是否为0
                return null;
            }

            List<T> resultList = Lists.newLinkedList();
            Iterator it = storeList.iterator();
            while (it.hasNext()) {
                String value = (String) it.next();
                T object = (T) new ObjectMapper().readValue(value, returnClass);
                resultList.add(object);
            }
            return resultList;
        } catch (Exception e) {
            logger.error("[Cache] get list for object key: " + key, e);
            return null;
        }
    }
}
