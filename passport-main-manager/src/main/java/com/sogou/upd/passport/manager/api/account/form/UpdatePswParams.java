package com.sogou.upd.passport.manager.api.account.form;

import org.hibernate.validator.constraints.NotBlank;

/**
 * 更新密码的内部接口参数类
 * User: wanghuaqing@sogou-inc.com
 * Date: 2016-10-12
 * Time: 上午11:43
 */
public class UpdatePswParams extends BaseUserApiParams {

    @NotBlank(message = "旧密码不允许为空")
    private String password;
    @NotBlank(message = "新密码不允许为空")
    private String newpwd;
    private String ip;  //登陆用户真实IP
    
    public UpdatePswParams() {
    }
    
    public UpdatePswParams(String password, String newpwd, String ip) {
        this.password = password;
        this.newpwd = newpwd;
        this.ip = ip;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getNewpwd() {
        return newpwd;
    }
    
    public void setNewpwd(String newpwd) {
        this.newpwd = newpwd;
    }
    
    public String getIp() {
        return ip;
    }
    
    public void setIp(String ip) {
        this.ip = ip;
    }
}
