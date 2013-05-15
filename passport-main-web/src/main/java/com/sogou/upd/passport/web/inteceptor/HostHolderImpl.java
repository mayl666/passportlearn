package com.sogou.upd.passport.web.inteceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-5-14
 * Time: 下午9:39
 */
@Component
public class HostHolderImpl implements HostHolder {

    private static final String USERNAME="host_holder_user_name";

    @Autowired
    private HttpServletRequest request;

    @Override
    public boolean isLogin() {
        return this.getUserName()!=null;
    }

    @Override
    public void setUserName(String userName) {
        request.setAttribute(USERNAME, userName);
    }

    @Override
    public String getUserName() {
        Object userName=request.getAttribute(USERNAME);
        if(userName!=null){
            return userName.toString();
        }
        return null;
    }
}
