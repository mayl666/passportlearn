package com.sogou.upd.passport.common.utils;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-7-25 Time: 下午5:36 To change this template use File | Settings | File Templates.
 */
public class ApiGroupUtil {
    private static Map<String, String> apiGroupMap = Maps.newHashMap();

    static {
        apiGroupMap.put("/web/security/bindques", "bindques");
        apiGroupMap.put("/web/security/bindmobilenew", "bindmobile");
        apiGroupMap.put("/web/security/bindmobile", "bindmobile");
        apiGroupMap.put("/web/security/updatepwd", "updatepwd");
        apiGroupMap.put("/internal/account/regmobileuser", "register");
        apiGroupMap.put("/web/logout_js", "logout");
        apiGroupMap.put("/web/logout_redirect", "logout");
        apiGroupMap.put("/web/reguser", "register");
        apiGroupMap.put("/internal/account/updateuserinfo", "updateinfo");
        apiGroupMap.put("/internal/account/regmobile", "register");
        apiGroupMap.put("/internal/account/reguser", "register");
        apiGroupMap.put("/connect/login", "login");
        apiGroupMap.put("/web/login", "login");
        apiGroupMap.put("/internal/account/authuser", "login");
    }

    public static String getApiGroup(String api) {
        String apiGroup = apiGroupMap.get(api);
        if (Strings.isNullOrEmpty(apiGroup)) {
            int sep = apiGroup.lastIndexOf("/");
            if (sep == -1 || sep == apiGroup.length() - 1) {
                apiGroup = api;
            } else {
                apiGroup = apiGroup.substring(sep + 1);
            }
        }
        return apiGroup;
    }
}
