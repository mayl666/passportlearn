package com.sogou.upd.passport.manager.form;

import com.google.common.base.Strings;

import com.sogou.upd.passport.common.CommonHelper;
import com.sogou.upd.passport.common.lang.StringUtil;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;

/**
 * User: mayan Date: 13-4-15 Time: 下午5:15 To change this template use File | Settings | File
 * Templates.
 */
public class WebRegisterParameters {
  @NotBlank(message = "client_id不允许为空!")
  @Min(0)
  private String client_id;
  @NotBlank(message = "邮箱不允许为空!")
  private String username;
  @NotBlank(message = "请输入密码!")
  private String password;
  @NotBlank(message = "验证码不允许为空!")
  private String captcha;//验证码
  @NotBlank(message = "标识码不允许为空!")
  private String token;//标识码
  private String ru;//回跳url

  @AssertTrue(message = "暂不支持sohu账号注册")
  private boolean isSohuUserName() {
    if (Strings.isNullOrEmpty(username)) {   // NotBlank已经校验过了，无需再校验
      return true;
    }
    if(username.endsWith("@sohu.com")){
      return false;
    }
    return true;
  }

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

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getCaptcha() {
    return captcha;
  }

  public void setCaptcha(String captcha) {
    this.captcha = captcha;
  }

  public String getRu() {
    return ru;
  }

  public void setRu(String ru) {
    this.ru = ru;
  }
}
