package com.sogou.upd.passport.common.lang;

import java.util.*;
import java.util.Map.Entry;

/**
 * 与容器类有关的builder和util
 */
public class BuilderUtil {

    public static Map<String, String> objectsAsStringMap(Object... args) {
        if (args.length % 2 != 0)
            throw new IllegalArgumentException("args.length = " + args.length);

        Map<String, String> map = new LinkedHashMap<String, String>();
        for (int i = 0; i < args.length - 1; i += 2)
            map.put(String.valueOf(args[i]), args[i + 1] == null ? "" : String.valueOf(args[i + 1]));
        return map;
    }

    public static Map<String, Object> objectsAsObjectMap(Object... args) {
        if (args.length % 2 != 0)
            throw new IllegalArgumentException("args.length = " + args.length);

        Map<String, Object> map = new LinkedHashMap<String, Object>();
        for (int i = 0; i < args.length - 1; i += 2)
            map.put(String.valueOf(args[i]), args[i + 1]);
        return map;
    }

    /**
     * 将Map转换为字符串“a=1,b=2”形式
     *
     * @param map
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> String mapAsString(Map<K, V> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append(",");
        }
        String str = "";
        if (sb.length() > 0) {
            str = sb.deleteCharAt(sb.length() - 1).toString();
        }
        return str;
    }

    public static <T> Set<T> asSet(T... args) {
        return new HashSet<T>(Arrays.asList(args));
    }

    /**
     * 根据from更新to的内容。用于更新cacheMap，通常to为使用中的ConcurrentHashMap
     */
    public static <K, V> void update(Map<K, V> from, Map<K, V> to) {
        // mark all keys as spare
        Set<K> spareKeys = new HashSet<K>(to.keySet());

        for (Entry<K, V> entry : from.entrySet()) {
            to.put(entry.getKey(), entry.getValue());
            spareKeys.remove(entry.getKey()); // mark
        }

        // remove spare
        for (K key : spareKeys)
            to.remove(key);
    }

}
