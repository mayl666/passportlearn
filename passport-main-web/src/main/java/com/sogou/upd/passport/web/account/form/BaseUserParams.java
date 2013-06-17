package com.sogou.upd.passport.web.account.form;

import com.sogou.upd.passport.manager.api.SHPPUrlConstant;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-23 Time: 下午7:22 To change this template use
 * File | Settings | File Templates.
 */
public class BaseUserParams {

    @NotBlank(message = "账号不允许为空!")
    protected String username;
    @NotBlank(message = "client_id不允许为空!")
    @Min(0)
    protected String client_id = String.valueOf(SHPPUrlConstant.APP_ID);

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }
}
