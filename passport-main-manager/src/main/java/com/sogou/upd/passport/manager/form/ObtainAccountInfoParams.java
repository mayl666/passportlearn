package com.sogou.upd.passport.manager.form;

import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * 获取用户信息
 * User: mayan
 * Date: 13-8-8 Time: 下午2:18
 */
public class ObtainAccountInfoParams {

    @NotBlank(message = "client_id不允许为空!")
    @Min(0)
    private String client_id = String.valueOf(SHPPUrlConstant.APP_ID);

    private String username;

    private String fields;

    private String imagesize;

    public ObtainAccountInfoParams(){}

    public ObtainAccountInfoParams(String client_id, String username, String fields) {
        this.client_id = client_id;
        this.username = username;
        this.fields=fields;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public String getImagesize() {
        return imagesize;
    }

    public void setImagesize(String imagesize) {
        this.imagesize = imagesize;
    }
}
