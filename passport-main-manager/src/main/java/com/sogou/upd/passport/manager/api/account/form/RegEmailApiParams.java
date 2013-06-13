package com.sogou.upd.passport.manager.api.account.form;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.manager.api.BaseApiParams;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.AssertTrue;

/**
 * 邮箱、个性域名注册参数类
 * User: shipengzhi
 * Date: 13-6-8
 * Time: 下午2:59
 * To change this template use File | Settings | File Templates.
 */
public class RegEmailApiParams extends BaseApiParams {
    @NotBlank(message = "注册账号不允许为空")
    private String username;  //注册账号

    private String password;
    private String ip;
    private int provider;
    private String captcha;//验证码
    private String token;//标识码

    public RegEmailApiParams(String username, String password, String ip, int client_id,String captcha,String token) {
        this.username = username;
        this.password = password;
        this.ip = ip;
        setClient_id(client_id);
        this.captcha=captcha;
        this.token=token;
    }
    @AssertTrue(message = "请输入正确的手机号!")
    private boolean isSohuUserName() {
        if (Strings.isNullOrEmpty(username)) {   // NotBlank已经校验过了，无需再校验
            return true;
        }
       // TODO
        return false;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getProvider() {
        return provider;
    }

    public void setProvider(int provider) {
        this.provider = provider;
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
}
