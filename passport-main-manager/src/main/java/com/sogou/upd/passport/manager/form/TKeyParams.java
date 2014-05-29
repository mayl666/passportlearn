package com.sogou.upd.passport.manager.form;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * Created by denghua on 14-5-13.
 */
public class TKeyParams {


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
