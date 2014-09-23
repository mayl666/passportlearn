package com.sogou.upd.passport.common.utils;

import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.math.Coder;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-18
 * Time: 下午2:48
 */
public class ParseCookieUtil {

    public static final String PPINF="ppinf";

    public static final String PPINF_UNIQNAME="uniqname";

    public static final String PPINF_USERID="userid";

    /**
     * 用于解析sohu矩阵的ppinf cookie
     *
     * @param request
     * @return
     */
    public static Map<String, String> parsePpinf(HttpServletRequest request) {
        String ppinf = ServletUtil.getCookie(request, PPINF);
        return parsePpinf(ppinf);
    }

    /**
     * 用于解析sohu矩阵的ppinf cookie
     *
     * @param ppinf
     * @return
     */
    public static Map<String, String> parsePpinf(String ppinf) {
        if (StringUtil.isBlank(ppinf)) {
            return Collections.emptyMap();
        }
        String[] ppinfArray = ppinf.split("\\|");
        String userInfo = ppinfArray[ppinfArray.length - 1];
        String userInfoBase64 = Coder.decodeBASE64String(userInfo);
        String[] userInfoArray = userInfoBase64.split("\\|");
        Map<String, String> maps = new HashMap(userInfoArray.length);
        for (String item : userInfoArray) {
            if (StringUtil.isBlank(item)) {
                continue;
            }
            String[] items = item.split(":");
            if (items.length < 3) {
                continue;
            }
            String key = items[0];
            String value = items[2];
            maps.put(key, value);
        }
        return maps;
    }
}
