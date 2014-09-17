package com.sogou.upd.passport.manager.api.account.form;

import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * 重新发送激活邮件的参数类
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-6-27
 * Time: 下午3:14
 * To change this template use File | Settings | File Templates.
 */
public class ResendActiveMailParams {
    @NotBlank(message = "client_id不允许为空!")
    @Min(0)
    private String client_id = String.valueOf(SHPPUrlConstant.APP_ID);

    @NotBlank(message = "账号不允许为空!")
    private String username;

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
