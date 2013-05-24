package com.sogou.upd.passport.manager.form;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * 获取第三方id参数校验 User: 马研 Date: 13-4-19 Time: 下午3:32 To change this template use File | Settings |
 * File Templates.
 */
public class ConnectClientObtainParams {
    @NotBlank(message = "access_token不允许为空!")
    private String access_token;
    @NotBlank(message = "client_id不允许为空!")
    @Min(0)
    private String client_id;
    @NotBlank(message = "provider不允许为空!")
    private String provider;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
