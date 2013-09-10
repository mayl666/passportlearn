package com.sogou.upd.passport.web.account.form;

import com.sogou.upd.passport.common.validation.constraints.Ru;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-23 Time: 下午6:54 To change this template use
 * File | Settings | File Templates.
 */
public class AccountScodeParams extends BaseAccountParams {
    @NotBlank(message = "scode不允许为空!")
    protected String scode;
    @NotBlank(message = "ru不允许为空!")
    @URL
    @Ru
    private String ru;     //验证邮箱成功后的回调地址

    public String getScode() {
        return scode;
    }

    public void setScode(String scode) {
        this.scode = scode;
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }
}
