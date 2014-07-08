package com.sogou.upd.passport.common.utils;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-4
 * Time: 上午11:18
 */
public class JsonUtil {


    private JsonUtil() {
    }


    /**
     * 将json转换为bean
     *
     * @param value json内容
     * @param type  最终要转换的类型
     * @param <T>   要转的类型
     * @return 转换的结果
     */
    public static <T> T jsonToBean(String value, java.lang.Class<T> type) {
        ObjectMapper mapper = JacksonJsonMapperUtil.getMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        ObjectReader reader = mapper.reader(type);
        try {
            return reader.readValue(value);
        } catch (IOException e) {
            throw new RuntimeException("json to bean error", e);
        }
    }


    /**
     * Object 转换为 JSON字符串
     *
     * @param obj
     * @return
     */
    public static String obj2Json(Object obj) {
        try {
            return getInstance().writeValueAsString(obj);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * JSON字符串 转换为 Object
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T json2Obj(String json, Class<T> clazz) {
        try {
            return getInstance().readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * JSON字符串 转换为 Object
     *
     * @param json
     * @param typeReference
     * @param <T>
     * @return
     */
    public static <T> T json2Obj(String json, TypeReference<T> typeReference) {
        try {
            return getInstance().readValue(json, typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static ObjectMapper getInstance() {
        return SingletonHolder.instance;
    }

    private static final class SingletonHolder {
        private static final ObjectMapper instance = new ObjectMapper();

        static {
            // 忽略JSON字符串中存在而Java对象实际没有的属性
            instance.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }
    }


}
