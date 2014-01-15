package com.sogou.upd.passport.manager.form;

import com.sogou.upd.passport.common.validation.constraints.Ru;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * wap登出参数校验
 * User: mayan
 * Date: 13-12-17
 * Time: 下午6:57
 */
public class WapLogoutParams {
    @NotBlank(message = "client_id不允许为空!")
    @Min(0)
    private String client_id;

    @NotBlank(message = "sgid不允许为空!")
    private String sgid;

    @Ru
    private String ru = "http://wap.sogou.com";  // 回调地址

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getSgid() {
        return sgid;
    }

    public void setSgid(String sgid) {
        this.sgid = sgid;
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }
}
