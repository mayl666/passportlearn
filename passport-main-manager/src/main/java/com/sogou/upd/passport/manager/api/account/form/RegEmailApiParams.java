package com.sogou.upd.passport.manager.api.account.form;

import com.google.common.base.Strings;
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
    @NotBlank(message = "密码不允许为空")
    private String password;  //明文密码，需要对格式校验
    @NotBlank(message = "注册IP不允许为空")
    private String createip;  //注册IP

    public RegEmailApiParams(String username, String password, String createip, int client_id) {
        this.username = username;
        this.password = password;
        this.createip = createip;
        setClient_id(client_id);
    }
    @AssertTrue(message = "请输入正确的手机号!")
    private boolean isSohuUserName() {
        if (Strings.isNullOrEmpty(username)) {   // NotBlank已经校验过了，无需再校验
            return true;
        }
       // TODO
        return false;
    }

    @AssertTrue(message = "密码格式不正确")
    public boolean isVaildUsername(){
        if (Strings.isNullOrEmpty(username)) {   // NotBlank已经校验过了，无需再校验
            return true;
        }
        //TODO username格式校验
        return false;
    }

    @AssertTrue(message = "密码格式不正确")
    public boolean isVaildPwd(){
        if (Strings.isNullOrEmpty(password)) {   // NotBlank已经校验过了，无需再校验
            return true;
        }
        //TODO password格式校验
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

    public String getCreateip() {
        return createip;
    }

    public void setCreateip(String createip) {
        this.createip = createip;
    }
}
