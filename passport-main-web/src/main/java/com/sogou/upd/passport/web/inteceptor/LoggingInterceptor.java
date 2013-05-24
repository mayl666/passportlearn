package com.sogou.upd.passport.web.inteceptor;

import com.sogou.upd.passport.common.LoginConstant;
import com.sogou.upd.passport.common.utils.CookieUtils;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.manager.account.AccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * User: ligang201716@sogou-inc.com
 * Date: 13-5-14
 * Time: 下午9:14
 */
public class LoggingInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private AccountManager accountManager;

    @Autowired
    private HostHolder hostHolder;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, java.lang.Object handler) throws java.lang.Exception {
        String passportId= CookieUtils.getCookie(request, LoginConstant.PASSPORTID_COOKIE_ID);
        if (StringUtil.isBlank(passportId)){
            return true;
        }
        if (accountManager.isAccountExists(passportId)){
            hostHolder.setUserName(passportId);
        }
        return true;
    }
}
