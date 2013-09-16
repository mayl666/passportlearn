package com.sogou.upd.passport.manager.form;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * sohu+访问受限资源接口
 * User: shipengzhi
 * Date: 13-9-16
 * Time: 上午11:48
 */
public class PCOAuth2ResourceParams {

    @Min(0)
    private int client_id;
    @NotBlank(message = "client_secret不允许为空")
    private String client_secret;
    private String instance_id;
    private String scope;
    @NotBlank(message = "access_token不允许为空")
    private String access_token;
    @NotBlank(message = "resource_type不允许为空")
    private String resource_type;

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }

    public String getInstance_id() {
        return instance_id;
    }

    public void setInstance_id(String instance_id) {
        this.instance_id = instance_id;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getResource_type() {
        return resource_type;
    }

    public void setResource_type(String resource_type) {
        this.resource_type = resource_type;
    }
}
