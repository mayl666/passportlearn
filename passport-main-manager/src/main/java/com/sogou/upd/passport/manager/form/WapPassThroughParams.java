package com.sogou.upd.passport.manager.form;

import com.sogou.upd.passport.common.validation.constraints.Ru;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * wap qq透传
 * User: mayan
 * Date: 13-12-17
 * Time: 下午6:57
 */
public class WapPassThroughParams {
    @NotBlank(message = "access_token is null")
    private String access_token;
    @NotBlank(message = "openid is null")
    private String openid;
    @NotBlank(message = "expires_in is null")
    private String expires_in;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }
}
