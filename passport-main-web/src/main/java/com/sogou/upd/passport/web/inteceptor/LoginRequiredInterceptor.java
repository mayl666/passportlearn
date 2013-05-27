package com.sogou.upd.passport.web.inteceptor;

import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.web.annotation.LoginRequiredResultType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.method.HandlerMethod;
import com.sogou.upd.passport.web.annotation.LoginRequired;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用于拦截带有@LoginRequired注解的方法
 * 查看用户是否已经登录
 *  如果用户登录执行方法
 *  如果用户未登录，拦截方法执行，根据配置的{@link LoginRequiredResultType}做相应处理
 * User: ligang201716@sogou-inc.com
 * Date: 13-5-14
 * Time: 下午8:46
 */
public class LoginRequiredInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private HostHolder hostHolder;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws java.lang.Exception {
        //获取url对应的方法
        if(!(handler instanceof HandlerMethod)){
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        //检查是否需要登录
        LoginRequired loginRequired= handlerMethod.getMethodAnnotation(LoginRequired.class);

        if(loginRequired==null||!loginRequired.value()){
            return true;
        }
        //判断是否已经登录
        if(hostHolder.isLogin()){
            return true;
        }

        //未登陆，根据配置返回
        LoginRequiredResultType resultType = loginRequired.resultType();
        switch (resultType){
            case json:
            case xml:
            case txt:
                String msg= String.format(resultType.value(),loginRequired.message());
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write(msg);
                break;
            case forward:
                request.getRequestDispatcher(resultType.value()).forward(request, response);
                break;
            case redirect:
                response.sendRedirect(resultType.value());
                break;
        }
        return false;
    }

}
