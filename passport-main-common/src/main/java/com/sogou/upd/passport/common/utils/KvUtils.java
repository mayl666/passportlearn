package com.sogou.upd.passport.common.utils;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
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

/**
 * sogou kv系统 User: mayan Date: 13-6-24 Time: 下午6:34
 */
public class KvUtils {

    private static Logger logger = LoggerFactory.getLogger(KvUtils.class);
    private static String KEY_PREFIX = "20002/action_records/";
    // private static String KEY_PREFIX_TEST = "0/0/";

    private final static String KV_PERF4J_LOGGER = "rediesTimingLogger";


    private static RedisTemplate kvTemplate;

    @Profiled(el = true, logger = KV_PERF4J_LOGGER, tag = "kv_set")
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

    @Profiled(el = true, logger = KV_PERF4J_LOGGER, tag = "kv_setObject")
    public void set(String key, Object obj) throws IOException {
        set(key, new ObjectMapper().writeValueAsString(obj));
    }

    @Profiled(el = true, logger = KV_PERF4J_LOGGER, tag = "kv_get")
    public String get(String key) {
        String storeKey = KEY_PREFIX + key;
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
            String storeKey = KEY_PREFIX + key;
            kvTemplate.delete(storeKey);
        } catch (Exception e) {

        }
    }

    /**
     * @param key
     * @param value
     * @param maxLen 如果maxLen为-1，则不限制条数
     */
    @Profiled(el = true, logger = KV_PERF4J_LOGGER, tag = "kv_pushObjectToList")
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
                while (list.size() > 10) {
                    list.pollLast();
                }
            }
            set(key, new ObjectMapper().writeValueAsString(list));
        } catch (Exception e) {
            logger.error("[KvCache] lPush with maxLen fail, key:" + key, e);
        }
    }

    public void pushObjectWithMaxLen(String key, Object obj, int maxLen) {
        try {
            pushWithMaxLen(key, new ObjectMapper().writeValueAsString(obj), maxLen);
        } catch (Exception e) {
            logger.error("[KvCache] lpush object with maxlen key: " + key, e);
        }
    }

    public void pushStringToList(String key, String str) {
        try {
            pushWithMaxLen(key, str, -1);
        } catch (Exception e) {
            logger.error("[KvCache] lpush object with maxlen key: " + key, e);
        }
    }

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
    @Profiled(el = true, logger = KV_PERF4J_LOGGER, tag = "kv_getList")
    public LinkedList<String> getList(String key) {
        try {
            String strValue = get(key);
            if (Strings.isNullOrEmpty(strValue)) {
                return null;
            }
            LinkedList<String> list = new ObjectMapper().readValue(strValue, LinkedList.class);

            return list;
        } catch (Exception e) {
            logger.error("[KvCache] get list fail key: " + key, e);
            return null;
        }
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
