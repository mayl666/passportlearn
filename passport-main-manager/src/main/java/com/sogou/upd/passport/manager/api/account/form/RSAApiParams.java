package com.sogou.upd.passport.manager.api.account.form;

import com.sogou.upd.passport.manager.api.BaseApiParams;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * ras接口调用参数
 * Created by denghua on 14-6-10.
 */
public class RSAApiParams extends BaseApiParams {

    @NotBlank(message = "密文不能为空")
    public String cipherText;

    public String getCipherText() {
        return cipherText;
    }

    public void setCipherText(String cipherText) {
        this.cipherText = cipherText;
    }
}
