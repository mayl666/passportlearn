package com.sogou.upd.passport.manager.form;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * 支持搜狗域、搜狐域、第三方账号漫游参数
 * User: chengang
 * Date: 14-7-29
 * Time: 上午10:29
 */
public class AccountRoamParams {


    public String getSgid() {
        return sgid;
    }

    @NotBlank(message = "client_id不允许为空!")
    @Min(0)
    private int client_id;

    private String sgid;

    public void setSgid(String sgid) {
        this.sgid = sgid;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }
}
