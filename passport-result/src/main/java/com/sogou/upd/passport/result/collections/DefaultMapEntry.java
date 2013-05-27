package com.sogou.upd.passport.result.collections;

import java.util.Map;

/**
 * <p>
 * <code>Map.Entry</code>的默认实现. 具有如下特征:
 * </p>
 * <p/>
 * <ul>
 * <li>
 * 支持值为<code>null</code>的key
 * </li>
 * <li>
 * 可以和任意<code>Map.Entry</code>的实现进行<code>equals</code>比较
 * </li>
 * <li>
 * 如果两个<code>Map.Entry</code>相同(<code>e1.equals(e2) == true</code>), 则它们的<code>hashCode()</code>也相等
 * </li>
 * </ul>
 * <p/>
 * <p/>
 * User: shipengzhi
 * Date: 13-5-25
 * Time: 下午9:18
 */
public class DefaultMapEntry implements Map.Entry {
    private final Object key;
    private Object value;

    /**
     * 创建一个<code>Map.Entry</code>.
     *
     * @param key   <code>Map.Entry</code>的key
     * @param value <code>Map.Entry</code>的value
     */
    public DefaultMapEntry(Object key, Object value) {
        this.key = key;
        this.value = value;
    }

    /**
     * 取得key.
     *
     * @return <code>Map.Entry</code>的key
     */
    public Object getKey() {
        return key;
    }

    /**
     * 取得value.
     *
     * @return <code>Map.Entry</code>的value
     */
    public Object getValue() {
        return value;
    }

    /**
     * 设置value的值.
     *
     * @param value 新的value值
     * @return 老的value值
     */
    public Object setValue(Object value) {
        Object oldValue = this.value;

        this.value = value;

        return oldValue;
    }

    /**
     * 判断两个对象是否相同.
     *
     * @param o 要比较的对象
     * @return 如果相同, 则返回<code>true</code>
     */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (o == this) {
            return true;
        }

        if (!(o instanceof Map.Entry)) {
            return false;
        }

        Map.Entry e = (Map.Entry) o;
        Object k1 = getKey();
        Object k2 = e.getKey();

        if ((k1 == k2) || ((k1 != null) && k1.equals(k2))) {
            Object v1 = getValue();
            Object v2 = e.getValue();

            if ((v1 == v2) || ((v1 != null) && v1.equals(v2))) {
                return true;
            }
        }

        return false;
    }

    /**
     * 取得<code>Map.Entry</code>的hash值. 如果两个<code>Map.Entry</code>相同, 则它们的hash值也相同.
     *
     * @return hash值
     */
    public int hashCode() {
        return ((key == null) ? 0
                : key.hashCode())
                ^ ((value == null) ? 0
                : value.hashCode());
    }

    /**
     * 将<code>Map.Entry</code>转换成字符串.
     *
     * @return 字符串形式的<code>Map.Entry</code>
     */
    public String toString() {
        return getKey() + "=" + getValue();
    }
}
