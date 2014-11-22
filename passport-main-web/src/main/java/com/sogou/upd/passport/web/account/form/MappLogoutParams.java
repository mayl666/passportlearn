package com.sogou.upd.passport.web.account.form;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * User: mayan
 * Date: 14-3-3
 * Time: 下午3:51
 * To change this template use File | Settings | File Templates.
 */
public class MappLogoutParams {
    @NotBlank(message = "client_id不允许为空!")
    @Min(0)
    private String client_id;

    @NotBlank(message = "sgid不允许为空!")
    private String sgid;

    @NotBlank(message = "客户端唯一标识不允许为空！")
    private String instance_id;

    @NotBlank(message = "code不允许为空！")
    private String code;

    //验证code是否有效

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

    public String getInstance_id() {
        return instance_id;
    }

    public void setInstance_id(String instance_id) {
        this.instance_id = instance_id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
