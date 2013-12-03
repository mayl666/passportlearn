package com.sogou.upd.passport.manager.api.connect.form.qq;

import com.sogou.upd.passport.manager.api.connect.form.BaseOpenApiParams;
import org.hibernate.validator.constraints.NotBlank;

/**
 * QQ图标点亮时的请求参数类
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-11-28
 * Time: 上午10:35
 * To change this template use File | Settings | File Templates.
 */
public class QQLightOpenApiParams extends BaseOpenApiParams {

    @NotBlank(message = "应用请求qq的接口名称不允许为空")
    private String openApiName;    //应用请求QQ的接口名称，通用参数，需要应用指定，如调用QQ的/v3/user/get_info

    public String getOpenApiName() {
        return openApiName;
    }

    public void setOpenApiName(String openApiName) {
        this.openApiName = openApiName;
    }

}
