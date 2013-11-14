package com.sogou.upd.passport.manager.form;

import com.sogou.upd.passport.common.WapConstant;
import com.sogou.upd.passport.common.validation.constraints.Ru;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Min;

/**
 * 用于web端的登陆的参数 User: liagng201716@sogou-inc.com Date: 13-5-12 Time: 下午10:01
 */
public class WapLoginParams extends BaseLoginParams {
    @NotBlank
    @URL
    @Ru
    private String ru;//登陆来源


    @NotBlank(message = "v is null")
    private String v = WapConstant.WAP_COLOR;//wap版本:0-简易版；1-炫彩版；2-触屏版

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }
}
