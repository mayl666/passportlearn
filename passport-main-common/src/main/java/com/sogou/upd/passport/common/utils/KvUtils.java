package com.sogou.upd.passport.common.utils;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/**
 * sogou kv系统 User: mayan Date: 13-6-24 Time: 下午6:34
 */
public class KvUtils {

    private static Logger logger = LoggerFactory.getLogger(KvUtils.class);

    private final static String KV_PERF4J_LOGGER = "kvTimingLogger";

    private static RedisTemplate kvTemplate;

//    @Profiled(el = true, logger = KV_PERF4J_LOGGER, tag = "kv_set")
    public void set(String key, String value) {
        String storeKey = key;
        try {
            ValueOperations<String, String> valueOperations = kvTemplate.opsForValue();
            valueOperations.set(storeKey, value);
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

    @Profiled(el = true, logger = KV_PERF4J_LOGGER, tag = "kv_setObject")
    public void set(String key, Object obj) throws IOException {
        set(key, new ObjectMapper().writeValueAsString(obj));
    }

//    @Profiled(el = true, logger = KV_PERF4J_LOGGER, tag = "kv_get")
    public String get(String key) {
        String storeKey = key;
        try {
            ValueOperations<String, String> valueOperations = kvTemplate.opsForValue();
            return valueOperations.get(storeKey);
        } catch (Exception e) {
            logger.error("[KvCache] get cache fail, key:" + key, e);
        }
        return null;
    }

    @Profiled(el = true, logger = KV_PERF4J_LOGGER, tag = "kv_getObject")
    public <T> T getObject(String key, Class<T> returnClass) {
        try {
            String strValue = get(key);
            if (!Strings.isNullOrEmpty(strValue)) {
                T object = new ObjectMapper().readValue(strValue, returnClass);
                return object;
            }
        } catch (Exception e) {
            logger.error("[KvCache] getObject fail, key:" + key, e);
        }
        return null;
    }

    @Profiled(el = true, logger = KV_PERF4J_LOGGER, tag = "kv_delete")
    public void delete(String key) {
        try {
            String storeKey = key;
            kvTemplate.delete(storeKey);
        } catch (Exception e) {

        }
    }

    /**
     * String：Set的映射中，向Set里新增一个元素
     * @param key
     * @param value
     */
    @Profiled(el = true, logger = KV_PERF4J_LOGGER, tag = "kv_pushStringToSet")
    public void pushToSet(String key, String value){
        try{
            Set set = getObject(key, Set.class);
            if(CollectionUtils.isEmpty(set)){
                set = Sets.newHashSet();
            }
            if(!set.contains(value)){
                set.add(value);
                set(key, new ObjectMapper().writeValueAsString(set));
            }

        } catch (Exception e) {
            logger.error("[KvCache] lPush String To Set, key:" + key, e);
        }
    }

    /**
     * 将value添加到值列表中
     * key：{value1、value2...}
     * @param key
     * @param value
     * @param maxLen 如果maxLen为-1，则不限制列表长度
     */
    @Profiled(el = true, logger = KV_PERF4J_LOGGER, tag = "kv_pushStringToList")
    public void pushWithMaxLen(String key, String value, int maxLen) {
        try {
            LinkedList<String> list;
            String strValue = get(key);
            if (Strings.isNullOrEmpty(strValue)) {
                list = Lists.newLinkedList();
            } else {
                list = new ObjectMapper().readValue(strValue, LinkedList.class);
                if (CollectionUtils.isEmpty(list)) {
                    list = Lists.newLinkedList();
                }
            }

            list.addFirst(value);
            if (maxLen > 0) {
                while (list.size() > maxLen) {
                    list.pollLast();
                }
            }
            set(key, new ObjectMapper().writeValueAsString(list));
        } catch (Exception e) {
            logger.error("[KvCache] lPush with maxLen fail, key:" + key, e);
        }
    }

    @Profiled(el = true, logger = KV_PERF4J_LOGGER, tag = "kv_pushObjectToList")
    public void pushObjectWithMaxLen(String key, Object obj, int maxLen) {
        try {
            pushWithMaxLen(key, new ObjectMapper().writeValueAsString(obj), maxLen);
        } catch (Exception e) {
            logger.error("[KvCache] lpush object with maxlen key: " + key, e);
        }
    }

    /*
     * 获取list中的第一个成员
     */
    @Profiled(el = true, logger = KV_PERF4J_LOGGER, tag = "kv_getFirstStringFromList")
    public <T> T top(String key, Class<T> returnClass) {
        try {
            String strValue = get(key);
            if (Strings.isNullOrEmpty(strValue)) {
                return null;
            }
            LinkedList<String> list = new ObjectMapper().readValue(strValue, LinkedList.class);
            if (CollectionUtils.isEmpty(list)) {
                return null;
            } else {
                String value = list.getFirst();
                T object = new ObjectMapper().readValue(value, returnClass);
                return object;
            }
        } catch (Exception e) {
            logger.error("[KvCache] get top value for object key: " + key, e);
            return null;
        }
    }

    // 查询键key的列表
    @Profiled(el = true, logger = KV_PERF4J_LOGGER, tag = "kv_getList<String>")
    public LinkedList<String> getList(String key) {
        return getObject(key, LinkedList.class);
    }

    @Profiled(el = true, logger = KV_PERF4J_LOGGER, tag = "kv_getList<Object>")
    public <T> LinkedList<T> getList(String key, Class returnClass) {
        try {
            LinkedList<T> listObj = new LinkedList<>();
            LinkedList<String> list = getList(key);
            if (CollectionUtils.isEmpty(list)) {
                return null;
            }
            Iterator<String> it = list.iterator();
            while (it.hasNext()) {
                String value = it.next();
                T object = (T) new ObjectMapper().readValue(value, returnClass);
                listObj.add(object);
            }
            return listObj;
        } catch (Exception e) {
            logger.error("[KvCache] get list for object key: " + key, e);
            return null;
        }
    }

    public RedisTemplate getKvTemplate() {
        return kvTemplate;
    }

    public void setKvTemplate(RedisTemplate kvTemplate) {
        this.kvTemplate = kvTemplate;
    }
}
