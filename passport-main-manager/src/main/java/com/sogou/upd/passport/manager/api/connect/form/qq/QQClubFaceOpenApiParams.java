package com.sogou.upd.passport.manager.api.connect.form.qq;

import org.hibernate.validator.constraints.NotBlank;

/**
 * User: Mayan
 * Date: 14-03-17
 * Time: 下午16:33
 * To change this template use File | Settings | File Templates.
 */
public class QQClubFaceOpenApiParams extends QQLightOpenApiParams {
    @NotBlank(message = "平台类型不能为空")
    private String plat; //“pc”, “android”, “ios”

    public String getPlat() {
        return plat;
    }

    public void setPlat(String plat) {
        this.plat = plat;
    }
}
