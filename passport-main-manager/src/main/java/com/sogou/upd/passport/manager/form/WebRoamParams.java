package com.sogou.upd.passport.manager.form;

import com.sogou.upd.passport.common.validation.constraints.Ru;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Min;

/**
 * 支持搜狗域、搜狐域、第三方账号漫游参数
 * User: chengang
 * Date: 14-7-29
 * Time: 上午10:29
 */
public class WebRoamParams {

    /**
     * 应用client_id
     */
    @NotBlank(message = "client_id不允许为空!")
    @Min(0)
    private String client_id;

    /**
     * 签名串
     */
    @NotBlank(message = "r_key不允许为空!")
    private String r_key;

    /**
     * 回跳地址
     */
    @NotBlank(message = "ru不允许为空")
    @Ru
    private String ru;

    public String getClient_id() {
        return client_id;
    }


    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getR_key() {
        return r_key;
    }

    public void setR_key(String r_key) {
        this.r_key = r_key;
    }


    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }
}
