package com.sogou.upd.passport.web;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-9 Time: 下午3:02 To change this template use
 * File | Settings | File Templates.
 *
 * 基本参数类，只有client_id，userid从登录cookie中获取
 */
public class BaseWebParams {
    @NotBlank(message = "client_id不允许为空!")
    @Min(0)
    protected String client_id = String.valueOf(CommonConstant.SGPP_DEFAULT_CLIENTID);

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }
}
