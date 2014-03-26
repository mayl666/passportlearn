package com.sogou.upd.passport.common.utils;

import com.sogou.upd.passport.common.CommonConstant;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * request/response相关工具类
 *
 * @author shipengzhi.pt
 */
public class ServletUtil {

    public static String defaultDomain = CommonConstant.SOGOU_ROOT_DOMAIN;

    public static String getParameterString(HttpServletRequest request) {
        StringBuilder requestParam = new StringBuilder();
        Map map = request.getParameterMap();
        for (Object key : map.keySet().toArray()) {
            requestParam.append(key.toString());
            requestParam.append(":");
            requestParam.append(request.getParameter(key.toString()));
            requestParam.append(",");
        }
        return requestParam.toString();
    }

	/* ------------------------- cookie ------------------------- */

    public static String getCookie(HttpServletRequest request, String key) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(key)) return cookie.getValue();
        }
        return null;
    }

    /*
     * 有效期默认为-1
     * 格式为：key=value; domain=.sohu.com; path=/; HttpOnly
     */
    public static void setHttpOnlyCookie(HttpServletResponse response, String key, String value, String domain) {
        StringBuffer sb = new StringBuffer();
        sb.append(key).append("=").append(value).append("; ");
        sb.append("domain=").append(domain).append("; ");
        sb.append("path=/").append("; ");
        sb.append("HttpOnly");
        String cookieValue = sb.toString();
        response.addHeader("Set-Cookie", cookieValue);
    }

    //Set-Cookie: ppinf=2|1388731545|1389941145|bG9naW5pZDowOnx1c2VyaWQ6NDQ6MzE2ODBENkE2QTY1RDMyQkYxRTkyOTY3N0U3OERFMjl
    // AcXEuc29odS5jb218c2VydmljZXVzZTozMDowMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDB8Y3J0OjA6fGVtdDoxOjB8YXBwaWQ6NDoxMTIw
    // fHRydXN0OjE6MXxwYXJ0bmVyaWQ6MTowfHJlbGF0aW9uOjA6fHV1aWQ6MTY6ODNhN2I4NzA3YjNhNDg2eHx1aWQ6MTY6ODNhN2I4NzA3YjNhNDg
    // 2eHx1bmlxbmFtZTo0OTolRTUlOEElQTAlRTclOUIlOUYxNjQyJUU1JTlDJUE4JUU2JTkwJTlDJUU3JThCJTkwfHJlZnVzZXJpZDozMjozMTY4MEQ2
    // QTZBNjVEMzJCRjFFOTI5Njc3RTc4REUyOXxyZWZuaWNrOjE4OiVFNSU4QSVBMCVFNyU5QiU5Rnw;
    //
    // domain=.hao.qq.com; path=/; expires=Fri, 17-Jan-2014 06:45:45 GMT
    public static void setHttpOnlyCookie(HttpServletResponse response, String key, String value, String domain,long expires) {
        StringBuffer sb = new StringBuffer();
        sb.append(key).append("=").append(value).append("; ");
        sb.append("domain=").append(domain).append("; ");
        sb.append("path=/").append("; ");
        sb.append("expires=").append(DateUtil.getDateByTimeStamp(expires)).append(" GMT").append("; ");
        sb.append("HttpOnly");
        String cookieValue = sb.toString();
        response.addHeader("Set-Cookie", cookieValue);
    }

    public static void setExpireCookie(HttpServletResponse response, String key, String value, String domain,long expires) {
        StringBuffer sb = new StringBuffer();
        sb.append(key).append("=").append(value).append("; ");
        sb.append("domain=").append(domain).append("; ");
        sb.append("path=/").append("; ");
        sb.append("expires=").append(DateUtil.getDateByTimeStamp(expires)).append(" GMT").append("; ");
        String cookieValue = sb.toString();
        response.addHeader("Set-Cookie", cookieValue);
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

    public static void clearCookie(HttpServletResponse response, String key,String domain) {
        clearCookie(response, key, 0, "/", domain);
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
