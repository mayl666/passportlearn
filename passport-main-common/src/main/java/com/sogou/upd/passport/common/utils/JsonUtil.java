package com.sogou.upd.passport.common.utils;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;

import java.io.IOException;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-4
 * Time: 上午11:18
 */
public class JsonUtil {

    /**
     * 将json转换为bean
     * @param value json内容
     * @param type 最终要转换的类型
     * @param <T>  要转的类型
     * @return 转换的结果
     */
    public static  <T> T jsonToBean(String value,java.lang.Class<T> type){
        ObjectMapper mapper = new ObjectMapper();
        ObjectReader reader = mapper.reader(type);
        try {
            return reader.readValue(value);
        } catch (IOException e) {
            throw new RuntimeException("json to bean error",e);
        }
    }

}
