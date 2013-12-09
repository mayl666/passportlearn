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
        apiGroupMap.put("/internal/account/regmobile", REGISTER);
        apiGroupMap.put("/internal/account/reguser", REGISTER);
        apiGroupMap.put("/internal/account/regmobileuser", REGISTER);
        apiGroupMap.put("/web/reguser", REGISTER);

        apiGroupMap.put("/connect/login", LOGIN);
        apiGroupMap.put("/web/login", LOGIN);
        apiGroupMap.put("/internal/account/authuser", LOGIN);
        apiGroupMap.put("/act/pclogin", LOGIN);

        // 4.2接口
        apiGroupMap.put("/oauth2/login", LOGIN);
        apiGroupMap.put("/oauth2/register", REGISTER);
        // 4.1接口
        apiGroupMap.put("/act/getpairtoken", LOGIN);

        apiGroupMap.put("/web/logout_js", LOGOUT);
        apiGroupMap.put("/web/logout_redirect", LOGOUT);

        apiGroupMap.put("/web/security/updatepwd", UPDATEPWD);

    }

    public static String getApiGroup(String api) {
        if (Strings.isNullOrEmpty(api)) {
            return null;
        }
        String apiGroup = apiGroupMap.get(api);
        return apiGroup;
    }
}
