package com.sogou.upd.passport.web.account.form;

import org.hibernate.validator.constraints.NotBlank;

/**
 * sohu+浏览器个人中心页
 * User: shipengzhi
 * Date: 13-7-24
 * Time: 下午11:21
 * To change this template use File | Settings | File Templates.
 */
public class PCOAuth2ResetPwdParams {
    @NotBlank(message = "accesstoken illegal")
    private String accesstoken="";   //获取的访问token
    @NotBlank(message = "老密码不能为空")
    private String oldpwd;
    @NotBlank(message = "新密码不能为空")
    private String newpwd = "";
    public String getAccesstoken() {
        return accesstoken;
    }

    public void setAccesstoken(String accesstoken) {
        this.accesstoken = accesstoken;
    }

    public String getOldpwd() {
        return oldpwd;
    }

    public void setOldpwd(String oldpwd) {
        this.oldpwd = oldpwd;
    }

    public String getNewpwd() {
        return newpwd;
    }

    public void setNewpwd(String newpwd) {
        this.newpwd = newpwd;
    }
}
