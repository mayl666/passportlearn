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
 * kv操作工具类
 * Created with IntelliJ IDEA.
 * User: chengang
 * Date: 14-3-13
 * Time: 上午8:27
 */
public class CoreKvUtils {

    private static Logger logger = LoggerFactory.getLogger(CoreKvUtils.class);

    private static ObjectMapper jsonMapper = JacksonJsonMapperUtil.getMapper();

    private final static String KV_PERF4J_LOGGER = "kvTimingLogger";

    private RedisTemplate coreKvTemplate;

    public RedisTemplate getCoreKvTemplate() {
        return coreKvTemplate;
    }

    public void setCoreKvTemplate(RedisTemplate coreKvTemplate) {
        this.coreKvTemplate = coreKvTemplate;
    }

    public void set(String key, String value) {
        String storeKey = key;
        try {
            ValueOperations<String, String> valueOperations = coreKvTemplate.opsForValue();
            valueOperations.set(storeKey, value);
        } catch (Exception e) {
            logger.error("[CoreKvCache] set cache fail, key:" + key + " value:" + value, e);
            try {
                delete(key);
            } catch (Exception ex) {
                logger.error("[CoreKvCache] set and delete cache fail, key:" + key + " value:" + value, e);
                throw e;
            }
        }
    }

    @Profiled(el = true, logger = KV_PERF4J_LOGGER, tag = "coreKv_setObject", timeThreshold = 50, normalAndSlowSuffixesEnabled = true)
    public void set(String key, Object obj) throws IOException {
        set(key, JacksonJsonMapperUtil.getMapper().writeValueAsString(obj));
    }

    public String get(String key) {
        String storeKey = key;
        try {
            ValueOperations<String, String> valueOperations = coreKvTemplate.opsForValue();
            return valueOperations.get(storeKey);
        } catch (Exception e) {
            logger.error("[CoreKvCache] get cache fail, key:" + key, e);
        }
        return null;
    }

    @Profiled(el = true, logger = KV_PERF4J_LOGGER, tag = "coreKv_getObject", timeThreshold = 50, normalAndSlowSuffixesEnabled = true)
    public <T> T getObject(String key, Class<T> returnClass) {
        try {
            String strValue = get(key);
            if (!Strings.isNullOrEmpty(strValue)) {
                T object = jsonMapper.readValue(strValue, returnClass);
                return object;
            }
        } catch (Exception e) {
            logger.error("[CoreKvCache] GetObject fail, key:" + key, e);
        }
        return null;
    }

    @Profiled(el = true, logger = KV_PERF4J_LOGGER, tag = "coreKv_delete", timeThreshold = 50, normalAndSlowSuffixesEnabled = true)
    public void delete(String key) {
        try {
            String storeKey = key;
            coreKvTemplate.delete(storeKey);
        } catch (Exception e) {
            logger.error("[CoreKvCache] Delete fail, key:" + key, e);
        }
    }

    /**
     * String：Set的映射中，向Set里新增一个元素
     *
     * @param key
     * @param value
     */
    @Profiled(el = true, logger = KV_PERF4J_LOGGER, tag = "coreKv_pushStringToLinkedHashSet", timeThreshold = 50, normalAndSlowSuffixesEnabled = true)
    public void pushStringToLinkedHashSet(String key, String value) {
        try {
            Set<String> set = getObject(key, Set.class);
            if (CollectionUtils.isEmpty(set)) {
                set = Sets.newLinkedHashSet();
            }
            if (!set.contains(value)) {
                set.add(value);
                set(key, jsonMapper.writeValueAsString(set));
            }

        } catch (Exception e) {
            logger.error("[CoreKvCache] Push String To LinkedHashSet, key:" + key, e);
        }
    }

    /**
     * String：Set的映射中，获取set集合
     *
     * @param key
     */
    @Profiled(el = true, logger = KV_PERF4J_LOGGER, tag = "coreKv_pullStringFromLinkedHashSet", timeThreshold = 50, normalAndSlowSuffixesEnabled = true)
    public Set<String> pullStringFromLinkedHashSet(String key) {
        Set<String> set = Sets.newLinkedHashSet();
        try {
            set = getObject(key, Set.class);
            if (CollectionUtils.isEmpty(set)) {
                set = Sets.newLinkedHashSet();
            }
        } catch (Exception e) {
            logger.error("[CoreKvCache] Pull String From LinkedHashSet, key:" + key, e);
        }
        return set;
    }

    /**
     * 将value添加到值列表中
     * key：{value1、value2...}
     *
     * @param key
     * @param value
     * @param maxLen 如果maxLen为-1，则不限制列表长度
     */
    @Profiled(el = true, logger = KV_PERF4J_LOGGER, tag = "coreKv_pushStringToList", timeThreshold = 50, normalAndSlowSuffixesEnabled = true)
    public void pushWithMaxLen(String key, String value, int maxLen) {
        try {
            LinkedList<String> list;
            String strValue = get(key);
            if (Strings.isNullOrEmpty(strValue)) {
                list = Lists.newLinkedList();
            } else {
                list = jsonMapper.readValue(strValue, LinkedList.class);
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
            set(key, jsonMapper.writeValueAsString(list));
        } catch (Exception e) {
            logger.error("[CoreKvCache] Push with maxLen fail, key:" + key, e);
        }
    }

    @Profiled(el = true, logger = KV_PERF4J_LOGGER, tag = "coreKv_pushObjectToList", timeThreshold = 50, normalAndSlowSuffixesEnabled = true)
    public void pushObjectWithMaxLen(String key, Object obj, int maxLen) {
        try {
            pushWithMaxLen(key, jsonMapper.writeValueAsString(obj), maxLen);
        } catch (Exception e) {
            logger.error("[CoreKvCache] Push object with maxlen key: " + key, e);
        }
    }

    /*
     * 获取list中的第一个成员
     */
    @Profiled(el = true, logger = KV_PERF4J_LOGGER, tag = "coreKv_getFirstStringFromList", timeThreshold = 50, normalAndSlowSuffixesEnabled = true)
    public <T> T top(String key, Class<T> returnClass) {
        try {
            String strValue = get(key);
            if (Strings.isNullOrEmpty(strValue)) {
                return null;
            }
            LinkedList<String> list = jsonMapper.readValue(strValue, LinkedList.class);
            if (CollectionUtils.isEmpty(list)) {
                return null;
            } else {
                String value = list.getFirst();
                T object = jsonMapper.readValue(value, returnClass);
                return object;
            }
        } catch (Exception e) {
            logger.error("[CoreKvCache] Get top value for object key: " + key, e);
            return null;
        }
    }

    // 查询键key的列表
    @Profiled(el = true, logger = KV_PERF4J_LOGGER, tag = "coreKv_getList<String>", timeThreshold = 50, normalAndSlowSuffixesEnabled = true)
    public LinkedList<String> getList(String key) {
        return getObject(key, LinkedList.class);
    }

    @Profiled(el = true, logger = KV_PERF4J_LOGGER, tag = "coreKv_getList<Object>", timeThreshold = 50, normalAndSlowSuffixesEnabled = true)
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
                T object = (T) jsonMapper.readValue(value, returnClass);
                listObj.add(object);
            }
            return listObj;
        } catch (Exception e) {
            logger.error("[CoreKvCache] get list for object key: " + key, e);
            return null;
        }
    }


}
