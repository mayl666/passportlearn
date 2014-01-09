package com.sogou.upd.passport.oauth2.openresource.parameters;


import com.sogou.upd.passport.oauth2.common.OAuth;

public class BaiduOAuth extends OAuth {

    /* 通用参数 */
    public static final String USER = "user";
    public static final String FIELDS = "fields"; // 返回的字段列表，不同的方法值不同
    public static final String RESPONSE = "response";

    /* 通用值 */
    public static final String V1 = "1.0"; // 固定版本为1.0
    public static final String JSON = "json"; // 返回值的格式


    /* 用户类API响应参数 */
    public static final String NAME = "username"; // 用户昵称
    public static final String AVATAR = "portrait";
    public static final String SEX = "sex"; // 性别
}
