package com.sogou.upd.passport.result.resourcebundle;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * 将一个集合和一个<code>Enumeration</code>结合的<code>Enumeration</code>, 用来遍历resource bundle及其父bundle中的所有内容.
 *
 * @author shiprngzhi
 */
public class ResourceBundleEnumeration implements Enumeration {
    private Set         set;
    private Iterator    iterator;
    private Enumeration enumeration; // 可以为null
    private Object      next = null;

    /**
     * 创建一个<code>Enumeration</code>.
     *
     * @param set 集合
     * @param enumeration <code>Enumeration</code>对象, 可以为<code>null</code>
     */
    public ResourceBundleEnumeration(Set set, Enumeration enumeration) {
        this.set             = set;
        this.iterator        = set.iterator();
        this.enumeration     = enumeration;
    }

    /**
     * 判断是否有下一个元素.
     *
     * @return 如果还有下一个元素, 则返回<code>true</code>
     */
    public boolean hasMoreElements() {
        if (next == null) {
            if (iterator.hasNext()) {
                next = iterator.next();
            } else if (enumeration != null) {
                while ((next == null) && enumeration.hasMoreElements()) {
                    next = enumeration.nextElement();

                    if (set.contains(next)) {
                        next = null;
                    }
                }
            }
        }

        return next != null;
    }

    /**
     * 取得下一个元素.
     *
     * @return 下一个元素的值
     */
    public Object nextElement() {
        if (hasMoreElements()) {
            Object result = next;

            next = null;
            return result;
        } else {
            throw new NoSuchElementException();
        }
    }
}

