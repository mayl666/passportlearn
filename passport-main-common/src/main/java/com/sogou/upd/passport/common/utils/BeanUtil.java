package com.sogou.upd.passport.common.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Maps;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanUtil {

    private static final Logger logger = LoggerFactory.getLogger(BeanUtil.class);

    public static void setBeanProperty(Object object, String name, String value) {
        try {
            BeanUtils.setProperty(object, name, value);
        } catch (IllegalAccessException e) {
            logger.error("Put Value To ResultDO IllegalAccessException! ParamName:" + name + ", Value:" + value, e);
        } catch (InvocationTargetException e) {
            logger.error("Put Value To ResultDO InvocationTargetException! ParamName:" + name + ", Value:" + value, e);
        }
    }

    public static String getBeanSimpleProperty(Object object, String name) {
        String value = "";
        try {
            value = BeanUtils.getSimpleProperty(object, name);
        } catch (IllegalAccessException e) {
            logger.error("Get Value From DO IllegalAccessException! ParamName:" + name, e);
        } catch (InvocationTargetException e) {
            logger.error("Get Value From DO InvocationTargetException! ParamName:" + name, e);
        } catch (NoSuchMethodException e) {
            logger.error("Get Value From DO NoSuchMethodException! ParamName:" + name, e);
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> beanDescribe(Object object) {
        Map<String, Object> map = Maps.newHashMap();
        try {
            map = BeanUtils.describe(object);
        } catch (IllegalAccessException e) {
            logger.error("Get BeanDesc IllegalAccessException! BeanName:" + object, e);
        } catch (InvocationTargetException e) {
            logger.error("Get BeanDesc InvocationTargetException! BeanName:" + object, e);
        } catch (NoSuchMethodException e) {
            logger.error("Get BeanDesc NoSuchMethodException! BeanName:" + object, e);
        }
        return map;
    }
}
