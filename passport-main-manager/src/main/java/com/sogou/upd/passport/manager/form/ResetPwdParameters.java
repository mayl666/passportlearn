package com.sogou.upd.passport.manager.form;

import org.hibernate.validator.constraints.NotBlank;

/**
 * User: mayan Date: 13-4-15 Time: 下午5:15 To change this template use File | Settings | File
 * Templates.
 */
public class ResetPwdParameters {
  @NotBlank(message = "原密码不允许为空!")
  private String password;
  @NotBlank(message = "新密码不允许为空!")
  private String newpwd;
  @NotBlank(message = "passport_id不允许为空!")
  private String passport_id;
  private String ip;

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

  public String getPassport_id() {
    return passport_id;
  }

  public void setPassport_id(String passport_id) {
    this.passport_id = passport_id;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }
}
