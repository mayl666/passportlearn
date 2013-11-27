package com.sogou.upd.passport.manager.form;

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

    private int pwdtype = CommonConstant.PWD_TYPE_CIPHER; //密码类型，1为md5后的口令，缺省为密文

    private int rememberMe=1;
    private String instanceid = "";  //客户端的实例id
    private int client_id= CommonConstant.PC_CLIENTID;

    private String captcha;//验证码
    private String token;//标识码

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

    public String getLoginname() {
        return loginname;
    }

    public void setLoginname(String loginname) {
        this.loginname = loginname;
    }

    public int getRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(int rememberMe) {
        this.rememberMe = rememberMe;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getPwdtype() {
        return pwdtype;
    }

    public void setPwdtype(int pwdtype) {
        this.pwdtype = pwdtype;
    }
}
