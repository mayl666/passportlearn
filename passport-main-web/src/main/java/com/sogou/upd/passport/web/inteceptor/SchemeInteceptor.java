package com.sogou.upd.passport.web.inteceptor;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截请求，只允许https操作，当请求是http时get请求302，其他请求直接无效
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-18
 * Time: 下午9:24
 */
public class SchemeInteceptor extends HandlerInterceptorAdapter {

    private static final String SCHEME_HTTPS = "https";

    private static final String METHOD_GET = "GET";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws java.lang.Exception {
        if (request.getScheme().equals(SCHEME_HTTPS)) {
            return true;
        }
        if(request.getMethod().equals(METHOD_GET)){
            String url= request.getRequestURL().toString();
            url=url.replace("http:","https:");
            response.sendRedirect(url);
        }
        return false;
    }

}
