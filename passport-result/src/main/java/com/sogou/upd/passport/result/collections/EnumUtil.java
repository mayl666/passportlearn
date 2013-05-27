package com.sogou.upd.passport.result.collections;

import com.google.common.collect.Maps;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-5-27
 * Time: 上午11:26
 * To change this template use File | Settings | File Templates.
 */
public class EnumUtil {
    private static final Map entries = new WeakHashMap();

    /**
     * 取得<code>Enum</code>类的<code>EnumType</code>
     *
     * @param enumClass <code>Enum</code>类
     * @return <code>Enum</code>类对应的<code>EnumType</code>对象
     */
    static Enum.EnumType getEnumType(Class enumClass) {
        if (enumClass == null) {
            throw new NullPointerException("The Enum class must not be null");
        }

        synchronized (enumClass) {
            if (!Enum.class.isAssignableFrom(enumClass)) {
                throw new IllegalArgumentException(MessageFormat.format("Class \"{0}\" is not a subclass of Enum",
                        new Object[]{enumClass.getName()}));
            }

            Map entryMap = getEnumEntryMap(enumClass);
            Enum.EnumType enumType = (Enum.EnumType) entryMap.get(enumClass.getName());

            if (enumType == null) {
                Method createEnumTypeMethod = findStaticMethod(enumClass, "createEnumType", new Class[0]);

                if (createEnumTypeMethod != null) {
                    try {
                        enumType = (Enum.EnumType) createEnumTypeMethod.invoke(null, new Object[0]);
                    } catch (IllegalAccessException e) {
                    } catch (IllegalArgumentException e) {
                    } catch (InvocationTargetException e) {
                    } catch (ClassCastException e) {
                    }
                }

                if (enumType != null) {
                    entryMap.put(enumClass.getName(), enumType);

                    // 在JDK5下面，class loader完成并不意味着所有的常量被装配
                    // 下面的代码强制装配常量。
                    enumType.populateNames(enumClass);
                }
            }

            if (enumType == null) {
                throw new UnsupportedOperationException(MessageFormat.format("Could not create EnumType for class \"{0}\"",
                        new Object[]{enumClass.getName()}));
            }

            return enumType;
        }
    }

    /**
     * 取得指定类的<code>ClassLoader</code>对应的entry表.
     *
     * @param enumClass <code>Enum</code>类
     * @return entry表
     */
    static Map getEnumEntryMap(Class enumClass) {
        ClassLoader classLoader = enumClass.getClassLoader();
        Map entryMap = null;

        synchronized (entries) {
            entryMap = (Map) entries.get(classLoader);

            if (entryMap == null) {
                entryMap = Maps.newConcurrentMap();
                entries.put(classLoader, entryMap);
            }
        }

        return entryMap;
    }

    /**
     * 查找方法.
     *
     * @param enumClass  枚举类型
     * @param methodName 方法名
     * @param paramTypes 参数类型表
     * @return 方法对象, 或<code>null</code>表示未找到
     */
    private static Method findStaticMethod(Class enumClass, String methodName, Class[] paramTypes) {
        Method method = null;

        for (Class clazz = enumClass; !clazz.equals(Enum.class); clazz = clazz.getSuperclass()) {
            try {
                method = clazz.getDeclaredMethod(methodName, paramTypes);
                break;
            } catch (NoSuchMethodException e) {
            }
        }

        if ((method != null) && Modifier.isStatic(method.getModifiers())) {
            return method;
        }

        return null;
    }
}
