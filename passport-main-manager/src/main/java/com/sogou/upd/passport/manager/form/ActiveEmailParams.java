package com.sogou.upd.passport.manager.form;

import com.sogou.upd.passport.common.validation.constraints.Ru;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Min;

/**
 * User: mayan Date: 13-4-15 Time: 下午5:15 To change this template use File | Settings | File
 * Templates.
 */
public class ActiveEmailParams {
    @NotBlank(message = "参数错误!")
    @Min(0)
    private String client_id;
    @NotBlank(message = "参数错误!")
    private String passport_id;
    @NotBlank(message = "参数错误!")
    private String token;
    @URL
    @Ru
    private String ru;
    private boolean rtp = true; // redirect to passport 是否跳转到 passport，若否则直接跳回 ru

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getPassport_id() {
        return passport_id;
    }

    public void setPassport_id(String passport_id) {
        this.passport_id = passport_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
