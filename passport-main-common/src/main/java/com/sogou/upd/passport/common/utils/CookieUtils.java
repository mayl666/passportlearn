package com.sogou.upd.passport.common.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-5-14
 * Time: 下午9:21
 */
public class CookieUtils {

    /**
     * 从请求中获取名为key的cookie
     *
     * @param request
     * @param key
     * @return
     */
    public static String getCookie(HttpServletRequest request, String key) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(key)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static void setCookie(HttpServletResponse response, String key, String value, int maxAge) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");
        cookie.setDomain(".sogou.com");
        if (maxAge > 0) {
            cookie.setMaxAge(maxAge);
        }
        response.addCookie(cookie);
    }

    public static void deleteCookie(HttpServletResponse response, String key){
        Cookie cookie = new Cookie(key, "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
