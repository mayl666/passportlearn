package com.sogou.upd.passport.manager.form;

import com.google.common.base.Strings;

import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.utils.PhoneUtil;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.AssertTrue;

/**
 * 用于web端的登陆的参数 User: liagng201716@sogou-inc.com Date: 13-5-12 Time: 下午10:01
 */
public class WebLoginParameters {

  /**
   * 登陆用户名
   */
  @Length(min = 1, max = 200, message = "用户名或密码错误，请重新输入！")
  @NotBlank(message = "请输入用户名！")
  private String username;


  /**
   * 登陆密码
   */
  @Length(min = 1, max = 200, message = "用户名或密码错误，请重新输入！")
  @NotBlank(message = "请输入密码！")
  private String password;


  /**
   * 是否自动登陆，自动登陆cookie时长设置两周
   */
  private int autoLogin; // 0-否  1-真

  /**
   * 验证码 用户连续3次登陆失败需要输入验证码
   */
//  @NotBlank(message = "验证码不允许为空!")
  private String captcha;//验证码
//  @NotBlank(message = "标识码不允许为空!")
  private String token;//标识码

    private String ru;//登陆来源
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    if (username != null) {
      username = username.trim();
    }
    this.username = username;
  }


  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    if (password != null) {
      password = password.trim();
    }
    this.password = password;
  }

  public int getAutoLogin() {
      return autoLogin;
  }

  public void setAutoLogin(int autoLogin) {
      this.autoLogin = autoLogin;
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

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }
}
