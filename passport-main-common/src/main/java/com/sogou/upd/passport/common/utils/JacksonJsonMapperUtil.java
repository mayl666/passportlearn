package com.sogou.upd.passport.common.utils;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-10-9
 * Time: 上午11:21
 * To change this template use File | Settings | File Templates.
 */
public class JacksonJsonMapperUtil {
    static volatile ObjectMapper objectMapper = null;
    private JacksonJsonMapperUtil() {
    }

    public static ObjectMapper getMapper() {
        if (objectMapper == null) {
            synchronized (ObjectMapper.class) {
                if (objectMapper == null) {
                    objectMapper = new ObjectMapper();
                }
            }
        }
        return objectMapper;
    }
}
