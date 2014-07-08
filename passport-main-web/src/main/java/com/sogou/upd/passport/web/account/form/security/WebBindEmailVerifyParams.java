package com.sogou.upd.passport.web.account.form.security;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 14-7-1
 * Time: 下午9:17
 * To change this template use File | Settings | File Templates.
 */
public class WebBindEmailVerifyParams {

    @NotBlank(message = "token不允许为空")
    private String token;
    @NotBlank(message = "id不允许为空")
    private String id;
    @NotBlank(message = "账号不允许为空!")
    private String username;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
