package com.sogou.upd.passport.web.account.form;

import com.sogou.upd.passport.common.CommonConstant;
import org.hibernate.validator.constraints.NotBlank;

/**
 * sohu+浏览器登陆参数
 * User: shipengzhi
 * Date: 13-7-24
 * Time: 下午11:21
 * To change this template use File | Settings | File Templates.
 */
public class PCOAuth2LoginParams {
    @NotBlank(message = "登陆名不能为空")
    private String loginname;
    @NotBlank(message = "密码不能为空")
    private String pwd;

    private int rememberMe=0;
    private String instanceid = "";  //客户端的实例id
    private int client_id= CommonConstant.BROWSER_CLIENTID;

    public String getInstanceid() {
        return instanceid;
    }

    public void setInstanceid(String instanceid) {
        this.instanceid = instanceid;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }
}
