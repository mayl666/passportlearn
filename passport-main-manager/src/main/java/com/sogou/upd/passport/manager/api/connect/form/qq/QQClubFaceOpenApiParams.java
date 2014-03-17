package com.sogou.upd.passport.manager.api.connect.form.qq;

import com.sogou.upd.passport.manager.api.connect.form.BaseOpenApiParams;
import org.hibernate.validator.constraints.NotBlank;

/**
 * User: Mayan
 * Date: 14-03-17
 * Time: 下午16:33
 * To change this template use File | Settings | File Templates.
 */
public class QQClubFaceOpenApiParams extends BaseOpenApiParams {
    @NotBlank(message = "平台类型不能为空")
    private String plat; //“pc”, “android”, “ios”

    @NotBlank(message = "应用请求qq的接口名称不允许为空")
    private String openApiName;    //应用请求QQ的接口名称，通用参数，需要应用指定，如调用QQ的/v3/user/get_info

    public String getOpenApiName() {
        return openApiName;
    }

    public void setOpenApiName(String openApiName) {
        this.openApiName = openApiName;
    }

    public String getPlat() {
        return plat;
    }

    public void setPlat(String plat) {
        this.plat = plat;
    }
}
