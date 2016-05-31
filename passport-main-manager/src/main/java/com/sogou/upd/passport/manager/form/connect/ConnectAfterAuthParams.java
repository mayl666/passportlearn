package com.sogou.upd.passport.manager.form.connect;

import com.sogou.upd.passport.common.validation.constraints.Ru;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created by xieyilun on 2016/5/25.
 */
public class ConnectAfterAuthParams {
    @NotBlank(message = "openid不能为空")
    private String openid;
    @NotBlank(message = "access_token不能为空")
    private String access_token;
    @Min(0)
    private long expires_in;
    @NotNull(message = "client_id不允许为空!")
    @Min(0)
    private int client_id;
    @Min(0)
    private String refresh_token;
    private String type= "wap";
    private String third_appid;
    @URL
    @Ru
    private String ru;

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(long expires_in) {
        this.expires_in = expires_in;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getThird_appid() {
        return third_appid;
    }

    public void setThird_appid(String third_appid) {
        this.third_appid = third_appid;
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }
}
