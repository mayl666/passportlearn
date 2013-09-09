package com.sogou.upd.passport.common.utils;

import com.sogou.upd.passport.common.CommonConstant;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * request/response相关工具类
 *
 * @author shipengzhi.pt
 */
public class ServletUtil {

    public static String defaultDomain = CommonConstant.SOGOU_ROOT_DOMAIN;

	/* ------------------------- cookie ------------------------- */

    public static String getCookie(HttpServletRequest request, String key) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(key)) return cookie.getValue();
        }
        return null;
    }

    public static void setCookie(HttpServletResponse response, String key, String value, int second, String domain) {
        saveCookie(response, key, value, second, "/", domain);
    }

    public static void setCookie(HttpServletResponse response, String key, String value, int second) {
        saveCookie(response, key, value, second, "/");
    }

    public static void saveCookie(HttpServletResponse response, String key, String value,
                                  int second, String path) {
        saveCookie(response, key, value, second, path, defaultDomain);
    }

    public static void saveCookie(HttpServletResponse response, String key, String value,
                                  int second, String path, String domain) {
        Cookie cookie = new Cookie(key, value);
        cookie.setDomain(domain);
        cookie.setPath(path);
        cookie.setMaxAge(second); // 默认为-1,
        response.addCookie(cookie);
    }

    public static void clearCookie(HttpServletResponse response, String key) {
        clearCookie(response, key, 0, "/", defaultDomain);
    }

    public static void clearCookie(HttpServletResponse response, String key, int second,
                                   String path, String domain) {
        Cookie cookie = new Cookie(key, null);
        cookie.setPath(path);
        cookie.setMaxAge(second);
        cookie.setDomain(domain);
        response.addCookie(cookie);
    }

}
