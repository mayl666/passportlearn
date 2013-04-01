package com.sogou.upd.passport.web.form;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Sina SSO OAuth授权的回调请求参数类
 * User: shipengzhi
 * Date: 13-3-30
 * Time: 下午4:31
 * To change this template use File | Settings | File Templates.
 */
public class SinaSSOCallbackParams {

    @Min(value = 1, message = "client_id不允许为空")
    private int client_id;
    @NotNull(message = "instance_id不允许为空！")
    private String instance_id;

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public String getInstance_id() {
        return instance_id;
    }

    public void setInstance_id(String instance_id) {
        this.instance_id = instance_id;
    }
}
