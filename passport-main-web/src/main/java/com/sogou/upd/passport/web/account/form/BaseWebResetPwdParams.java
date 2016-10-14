package com.sogou.upd.passport.web.account.form;

import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.validation.constraints.Ru;
import com.sogou.upd.passport.web.BaseWebParams;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-7-7
 * Time: 下午5:24
 * To change this template use File | Settings | File Templates.
 */
public class BaseWebResetPwdParams extends BaseWebParams {

    @NotBlank(message = "账号不允许为空!")
    protected String username;
    @URL
    @Ru
    protected String ru;
    @NotBlank(message = "安全码不能为空")
    protected String scode;
    protected boolean rtp = true; // redirect to passport 是否跳转到 passport，若否则直接跳回 ru

    public String getScode() {
        return scode;
    }

    public void setScode(String scode) {
        this.scode = scode;
    }


    public String getUsername() {
        String internalUsername = AccountDomainEnum.getInternalCase(username);
        setUsername(internalUsername);
        return username;
    }

    public void setUsername(String username) {
        if (username != null) {
            username = username.trim();
        }
        this.username = username;
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }
    
    public boolean isRtp() {
        return rtp;
    }
    
    public void setRtp(boolean rtp) {
        this.rtp = rtp;
    }
}
