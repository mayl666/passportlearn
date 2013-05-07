package com.sogou.upd.passport.manager.form;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * 获取第三方id参数校验
 * User: 马研
 * Date: 13-4-19 Time: 下午3:32
 */
public class ConnectObtainParams {

    @NotBlank(message = "passport_id不允许为空!")
    private String passport_id;
    @Min(value = 1, message = "client_id不允许为空!")
    private int client_id;
    @NotBlank(message = "provider不允许为空!")
    private String provider;

    public String getPassport_id() {
        return passport_id;
    }

    public void setPassport_id(String passport_id) {
        this.passport_id = passport_id;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
