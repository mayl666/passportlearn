package com.sogou.upd.passport.common.utils;


import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: chengang
 * Date: 14-8-21
 * Time: 下午4:44
 */
public class ToolUUIDUtil {


    private ToolUUIDUtil() {
    }

    /**
     * 生成原始带分隔符的uuid
     *
     * @return
     */
    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成不带分隔符的uuid
     *
     * @return
     */
    public static String genreateUUidWithOutSplit() {
        return generateUuid().replaceAll("-", "");
    }
}
