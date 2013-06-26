package com.sogou.upd.passport.common.utils;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * sogou kv系统 User: mayan Date: 13-6-24 Time: 下午6:34
 */
public class KvUtils {

    private static Logger logger = LoggerFactory.getLogger(KvUtils.class);
    private static String KEY_PREFIX = "20002/action_records/";
    private static String KEY_PREFIX_TEST = "0/0/";
    private static int COUNT = 0;

    private static RedisTemplate kvTemplate;



    public void setTest(String key, String value) {
        String storeKey = KEY_PREFIX_TEST + key;
        try {
            ValueOperations<String, String> valueOperations = kvTemplate.opsForValue();
            valueOperations.set(storeKey, value);
        } catch (Exception e) {
            // logger.error("[Cache] set cache fail, key:" + key + " value:" + value, e);
            System.out.println(e.getMessage());
            COUNT++;
            logger.info("出现SetKV错误!!!"+COUNT);
            try {
                delete(key);
            } catch (Exception ex) {
                logger.error("[Cache] set and delete cache fail, key:" + key + " value:" + value, e);
                throw e;
            }
        }
    }

    public String getTest(String key) {
        String storeKey = KEY_PREFIX_TEST + key;
        try {
            ValueOperations<String, String> valueOperations = kvTemplate.opsForValue();
            return valueOperations.get(storeKey);
        } catch (Exception e) {
            COUNT++;
            logger.info("出现GetKV错误!!!"+COUNT);
            logger.error("[Cache] get cache fail, key:" + key, e);
        }
        return null;
    }


    public void deleteTest(String key) {
        String storeKey = KEY_PREFIX_TEST + key;
        kvTemplate.delete(storeKey);
    }

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

    public void pushWithMaxLen(String key, String value, int maxLen) {
        try {
            synchronized (kvTemplate) {
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
                while (list.size() > 10) {
                    list.pollLast();
                }
                set(key, new ObjectMapper().writeValueAsString(list));
            }
        } catch (Exception e) {
            logger.error("[Cache] lPush with maxLen fail, key:" + key, e);
        }
    }

    public void pushObjectWithMaxLen(String key, Object obj, int maxLen) {
        try {
            pushWithMaxLen(key, new ObjectMapper().writeValueAsString(obj), maxLen);
        } catch (Exception e) {
            logger.error("[Cache] lpush object with maxlen key: " + key, e);
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
            logger.error("[Cache] get top value for object key: " + key, e);
            return null;
        }
    }

    // 查询键key的列表
    public LinkedList<String> getList(String key) {
        try {
            String strValue = get(key);
            if (Strings.isNullOrEmpty(strValue)) {
                return null;
            }
            LinkedList<String> list = new ObjectMapper().readValue(strValue, LinkedList.class);

            return list;
        } catch (Exception e) {
            logger.error("[Cache] get list fail key: " + key, e);
            return null;
        }
    }

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
            logger.error("[Cache] get list for object key: " + key, e);
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
