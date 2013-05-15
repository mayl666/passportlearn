package com.sogou.upd.passport.web.inteceptor;

import com.sogou.upd.passport.web.annotation.LoginRequiredResultType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.method.HandlerMethod;
import com.sogou.upd.passport.web.annotation.LoginRequired;
import org.springframework.web.servlet.resource.DefaultServletHttpRequestHandler;

/**
 * 用于拦截带有@LoginRequired注解的方法
 *
 * User: ligang201716@sogou-inc.com
 * Date: 13-5-14
 * Time: 下午8:46
 */
public class LoginRequiredInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private HostHolder hostHolder;

    public boolean preHandle(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, java.lang.Object handler) throws java.lang.Exception {
        if(!(handler instanceof HandlerMethod)){
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        LoginRequired loginRequired= handlerMethod.getMethodAnnotation(LoginRequired.class);

        if(loginRequired==null||!loginRequired.value()){
            return true;
        }

        if(hostHolder.isLogin()){
            return true;
        }

        //TODO 针对每种方式的处理，具体处理
        switch (loginRequired.resultType()){
            case json:
            case xml:
            case forward:
            case redirect:
            default:
                response.getWriter().write(loginRequired.message());
        }
        return false;
    }

}
