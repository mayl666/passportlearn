package com.sogou.upd.passport.common.utils;

import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-1-7
 * Time: 下午4:40
 * To change this template use File | Settings | File Templates.
 */
public class ConnectUtil {

    public static Map<String, String> CONNECT_INFO_MAP = Maps.newHashMap();

    static {
        //QQ第三方信息映射
        CONNECT_INFO_MAP.put("qq", "oauth_consumer_key|openid|access_token|sig");
        CONNECT_INFO_MAP.put("/internal/connect/qq/user/qzone/unread_num", "/user/get_qzoneupdates|qzone");
        CONNECT_INFO_MAP.put("/internal/connect/qq/user/weibo/unread_num", "/v3/update/get_num|weibo");
        CONNECT_INFO_MAP.put("/internal/connect/qq/user/mail/unread_num", "/user/get_mail_count|mail");
    }

    public static Map<String, String> getCONNECT_INFO_MAP() {
        return CONNECT_INFO_MAP;
    }

    public static void setCONNECT_INFO_MAP(Map<String, String> CONNECT_INFO_MAP) {
        ConnectUtil.CONNECT_INFO_MAP = CONNECT_INFO_MAP;
    }

    public static String getERR_CODE_MSG(String key) {
        return CONNECT_INFO_MAP.get(key);
    }
}
