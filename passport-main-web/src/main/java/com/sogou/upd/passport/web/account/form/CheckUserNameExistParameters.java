package com.sogou.upd.passport.web.account.form;


import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.manager.form.UsernameParams;

/**
 * User: mayan
 * Date: 13-4-15 Time: 下午5:15
 */
public class CheckUserNameExistParameters extends UsernameParams {
    private String client_id =String.valueOf(CommonConstant.PC_CLIENTID);

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }
}
