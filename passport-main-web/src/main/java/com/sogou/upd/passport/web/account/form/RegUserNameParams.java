package com.sogou.upd.passport.web.account.form;

import com.sogou.upd.passport.manager.form.BaseRegUserNameParams;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-6-4
 * Time: 下午4:19
 * To change this template use File | Settings | File Templates.
 */
public class RegUserNameParams extends BaseRegUserNameParams {
    private String client_id;

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }
}
