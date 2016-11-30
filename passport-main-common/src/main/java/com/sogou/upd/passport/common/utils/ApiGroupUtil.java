package com.sogou.upd.passport.common.utils;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-7-25 Time: 下午5:36 To change this template use File | Settings | File Templates.
 */
public class ApiGroupUtil {
    private static Map<String, String> apiGroupMap = Maps.newHashMap();
    private static String LOGIN = "login";
    private static String REGISTER = "register";
    private static String UPDATEPWD = "updatepwd";
    private static String LOGOUT = "logout";

    static {
        /*-------------------注册------------------------*/
        //web注册
        apiGroupMap.put("/web/reguser", REGISTER);
        apiGroupMap.put("/oauth2/register", REGISTER);
        //内部注册
        apiGroupMap.put("/internal/account/regmobile", REGISTER);
        apiGroupMap.put("/internal/account/reguser", REGISTER);
        apiGroupMap.put("/internal/account/regmobileuser", REGISTER);
        apiGroupMap.put("/internal/account/regmobilefast", REGISTER);
        //wap注册
        apiGroupMap.put("/wap/reguser", REGISTER);
        apiGroupMap.put("/wap2/reguser", REGISTER);

        /*-------------------登录------------------------*/
        //第三方登录，参考oauth_consumer.properties，TODO：以后修改为读取配置文件
        apiGroupMap.put("/connect/callback/sina", LOGIN);
        apiGroupMap.put("/connect/callback/renren", LOGIN);
        apiGroupMap.put("/connect/callback/qq", LOGIN);
        apiGroupMap.put("/connect/callback/taobao", LOGIN);
        apiGroupMap.put("/connect/callback/baidu", LOGIN);
        apiGroupMap.put("/connect/callback/weixin", LOGIN);
        apiGroupMap.put("/connect/sso/afterauth/weixin", LOGIN);
        apiGroupMap.put("/connect/sso/afterauth/sina", LOGIN);
        apiGroupMap.put("/connect/sso/afterauth/qq", LOGIN);
        //web登录
        apiGroupMap.put("/web/login", LOGIN);
        apiGroupMap.put("/sso/web_roam", LOGIN);
        apiGroupMap.put("/sso/web_roam_go", LOGIN);
        //内部登录
        apiGroupMap.put("/internal/account/authuser", LOGIN);
        apiGroupMap.put("/internal/account/authemailuser", LOGIN);
        //pc登录
        apiGroupMap.put("/act/gettoken", LOGIN);
        apiGroupMap.put("/act/getpairtoken", LOGIN);
        apiGroupMap.put("/oauth2/login", LOGIN);
        apiGroupMap.put("/sso/pc_roam_go", LOGIN);
        //wap登录
        apiGroupMap.put("/wap/login", LOGIN);
        apiGroupMap.put("/wap2/login", LOGIN);

        /*-------------------退出------------------------*/
        apiGroupMap.put("/web/logout_js", LOGOUT);
        apiGroupMap.put("/web/logout_redirect", LOGOUT);
        apiGroupMap.put("/wap/logout_redirect", LOGOUT);
        apiGroupMap.put("/sso/logout_redirect", LOGOUT);
        apiGroupMap.put("/mapp/logout", LOGOUT);

        //修改密码
        apiGroupMap.put("/web/security/updatepwd", UPDATEPWD);
        apiGroupMap.put("/internal/security/resetpwd_batch", UPDATEPWD);
        apiGroupMap.put("/web/findpwd/reset", UPDATEPWD);
        apiGroupMap.put("/wap/findpwd/reset", UPDATEPWD);
    }

    public static String getApiGroup(String api) {
        if (Strings.isNullOrEmpty(api)) {
            return null;
        }
        String apiGroup = apiGroupMap.get(api);
        return apiGroup;
    }
}
