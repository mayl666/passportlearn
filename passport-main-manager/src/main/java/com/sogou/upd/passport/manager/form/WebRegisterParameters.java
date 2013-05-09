package com.sogou.upd.passport.manager.form;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Digits;

/**
 * User: mayan Date: 13-4-15 Time: 下午5:15 To change this template use File | Settings | File
 * Templates.
 */
public class WebRegisterParameters {
  @NotBlank(message = "client_id不允许为空!")
  private String client_id;
  @NotBlank(message = "邮箱不允许为空!")
  private String username;
  @NotBlank(message = "密码不允许为空!")
  private String password;
  @NotBlank(message = "验证码不允许为空!")
  private String code;//验证码

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

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }
}
