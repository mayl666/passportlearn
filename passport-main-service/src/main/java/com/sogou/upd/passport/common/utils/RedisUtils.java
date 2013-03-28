package com.sogou.upd.passport.common.utils;

import com.google.common.base.Strings;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * User: mayan
 * Date: 13-3-27
 * Time: 上午11:19
 * To change this template use File | Settings | File Templates.
 */
public class RedisUtils {

    /*
     * 字符串转换byte数组
     */
    public static byte[] stringToByteArry(String str) {
        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        return stringSerializer.serialize(str);
    }

    /*
   * byte数组转换字符串
   */
    public static String byteArryToString(byte[] bytes) {
        String parseResult = null;
        if (bytes != null && bytes.length > 0) {
            RedisSerializer<String> stringSerializer = new StringRedisSerializer();
            parseResult = stringSerializer.deserialize(bytes);
        }
        return parseResult;
    }

    /*
   * byte数组转换int
   */
    public static int byteArryToInteger(byte[] bytes) {
        String parseResult = null;
        if (bytes != null && bytes.length > 0) {
            RedisSerializer<String> stringSerializer = new StringRedisSerializer();
            parseResult = stringSerializer.deserialize(bytes);
        }
        return Strings.isNullOrEmpty(parseResult) ? 0 : Integer.parseInt(parseResult);
    }

    /*
 * byte数组转换long
 */
    public static long byteArryToLong(byte[] bytes) {
        String parseResult = null;
        if (bytes != null && bytes.length > 0) {
            RedisSerializer<String> stringSerializer = new StringRedisSerializer();
            parseResult = stringSerializer.deserialize(bytes);
        }
        return Strings.isNullOrEmpty(parseResult) ? 0 : Long.parseLong(parseResult);
    }
}
