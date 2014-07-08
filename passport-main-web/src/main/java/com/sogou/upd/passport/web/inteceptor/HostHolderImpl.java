package com.sogou.upd.passport.web.inteceptor;

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

    //用户passportid
    private static final String PASSPORTID ="host_holder_passport_id";

    //用户昵称
    private static final String NICKNAME="host_holder_nickname";


    @Override
    public boolean isLogin() {
        return this.getPassportId()!=null;
    }

    @Override
    public void setPassportId(String passportId) {
          this.setAttribute(PASSPORTID,passportId);
    }

    @Override
    public String getPassportId() {
          return this.getAttribute(PASSPORTID);
    }

    @Override
    public void setNickName(String nickName) {
        this.setAttribute(NICKNAME,nickName);
    }

    private void setAttribute(String name,String value){
        HttpServletRequest request =((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        request.setAttribute(name, value);
    }

    private String getAttribute(String name){
        HttpServletRequest request =((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        Object value=request.getAttribute(name);
        if(value!=null){
            return value.toString();
        }
        return null;
    }
}
