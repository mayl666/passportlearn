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
    @NotBlank(message = "token is null")
    private String token;
    @NotBlank(message = "openid is null")
    private String openid;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

}
