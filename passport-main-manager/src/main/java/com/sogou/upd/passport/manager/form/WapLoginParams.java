package com.sogou.upd.passport.manager.form;

import com.sogou.upd.passport.common.validation.constraints.Ru;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

/**
 * 用于web端的登陆的参数 User: liagng201716@sogou-inc.com Date: 13-5-12 Time: 下午10:01
 */
public class WapLoginParams extends BaseLoginParams {
    @NotBlank
    @URL
    @Ru
    private String ru;//登陆来源

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }
}
