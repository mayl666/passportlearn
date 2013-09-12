package com.sogou.upd.passport.web.account.form;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.common.validation.constraints.Password;
import com.sogou.upd.passport.common.validation.constraints.Ru;
import com.sogou.upd.passport.manager.form.WebRegisterParams;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

import javax.management.relation.RoleUnresolvedList;
import javax.validation.constraints.AssertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-9-12
 * Time: 上午11:03
 * To change this template use File | Settings | File Templates.
 */
public class PCOAuth2RegisterParams extends WebRegisterParams {

    private String client_id = String.valueOf(CommonConstant.PC_CLIENTID);

    private String instance_id = null;

    public String getInstance_id() {
        return instance_id;
    }

    public void setInstance_id(String instance_id) {
        this.instance_id = instance_id;
    }

    public String getClient_id() {
        return client_id;
    }
}
