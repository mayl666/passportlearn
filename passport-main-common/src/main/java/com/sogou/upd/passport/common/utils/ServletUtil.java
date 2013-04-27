package com.sogou.upd.passport.common.utils;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sogou.upd.passport.common.CommonConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * request/response相关工具类
 *
 * @author shipengzhi.pt
 */
public class ServletUtil {

    static final Logger logger = LoggerFactory.getLogger(ServletUtil.class);

    public static String defaultDomain = CommonConstant.SOGOU_ROOT_DOMAIN;

    public static boolean isPost(HttpServletRequest request) {
        return request.getMethod().toLowerCase().equals("post");
    }

	/* ------------------------- session ------------------------- */

    @SuppressWarnings("unchecked")
    public static <T> T getSession(HttpServletRequest request, String name) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        return (T) session.getAttribute(name);
    }

    public static void setSession(HttpServletRequest request, String name, Object value) {
        HttpSession session = request.getSession(true);
        if (session == null) {
            logger.warn("create session failed.");
            return;
        }
        session.setAttribute(name, value);
    }

    public static void removeSession(HttpServletRequest request, String name) {
        HttpSession session = request.getSession(false);
        if (session == null) return;
        session.removeAttribute(name);
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


    public static void setJsessionidCookie(HttpServletRequest request, HttpServletResponse response) {
        //specifying name and value of the cookie
        Cookie cookie = new Cookie("JSESSIONID", request.getSession(false).getId());
        //		String domainName = request.getServerName(); //Gives www.xyz.com in our example
        //		String domainNamePrefix = domainName.substring(domainName.indexOf("."), domainName.length()); //Returns .xyz.com
        //Specifies the domain within which this cookie should be presented.
        cookie.setDomain(defaultDomain);
        response.addCookie(cookie);
    }

    public static void setCookie(HttpServletResponse response, String key, String value) {
        saveCookie(response, key, value, -1, "/");
    }

    public static void setCookie(HttpServletResponse response, String key, String value, int second) {
        saveCookie(response, key, value, second, "/");
    }

    public static void setCookie(HttpServletResponse response, String key, String value, String path) {
        saveCookie(response, key, value, -1, path);
    }

    public static void saveCookie(HttpServletResponse response, String key, String value,
                                  int second, String path) {
        saveCookie(response, key, value, second, path, defaultDomain);
    }

    public static void saveCookie(HttpServletResponse response, String key, String value,
                                  int second, String path, String domain) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath(path);
        cookie.setMaxAge(second); // 默认为-1,
        cookie.setDomain(domain);
        response.addCookie(cookie);
    }

    public static void clearCookie(HttpServletResponse response, String key) {
        clearCookie(response, key, -1, "/");
    }

    public static void clearCookie(HttpServletResponse response, String key, String domain) {
        clearCookie(response, key, -1, "/", domain);
    }

    public static void clearCookie(HttpServletResponse response, String key, int second, String path) {
        clearCookie(response, key, second, path, defaultDomain);
    }

    public static void clearCookie(HttpServletResponse response, String key, int second,
                                   String path, String domain) {
        Cookie cookie = new Cookie(key, null);
        cookie.setPath(path);
        cookie.setMaxAge(second);
        cookie.setDomain(domain);
        response.addCookie(cookie);
    }

    /**
     * 直接输出到页面
     */
    public static void writeToPage(HttpServletResponse response, String str) {
        try {
            if (response != null) {// 在junit测试的时候会不显示.
                response.setHeader("Pragma", "No-cache");
                response.setHeader("Cache-Control", "no-cache");
                response.setDateHeader("Expires", 0);
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter writer = null;
                writer = response.getWriter();
                writer.write(str);
            }
        } catch (IOException e) {
        } finally {
        }
    }

}
