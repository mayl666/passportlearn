package com.sogou.upd.passport.web.account.form;

import com.sogou.upd.passport.common.validation.constraints.Ru;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * 支持搜狗域、搜狐域、第三方账号漫游参数
 * User: chengang
 * Date: 14-7-29
 * Time: 上午10:29
 */
public class WebRoamParams extends BaseWebRuParams{

    /**
     * 签名串
     */
    @NotBlank(message = "r_key不允许为空!")
    private String r_key;

    public String getR_key() {
        return r_key;
    }

    public void setR_key(String r_key) {
        this.r_key = r_key;
    }
}
