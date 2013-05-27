package com.sogou.upd.passport.web.inteceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-5-14
 * Time: 下午9:39
 */
@Component
public class HostHolderImpl implements HostHolder {

    private static final String USERNAME="host_holder_user_name";

//    @Autowired
//    private HttpServletRequest request;

    @Override
    public boolean isLogin() {
        return this.getPassportId()!=null;
    }

    @Override
    public void setPassportId(String userName) {
        HttpServletRequest request =((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        request.setAttribute(USERNAME, userName);
    }

    @Override
    public String getPassportId() {
        HttpServletRequest request =((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        Object userName=request.getAttribute(USERNAME);
        if(userName!=null){
            return userName.toString();
        }
        return null;
    }
}
