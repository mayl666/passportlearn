package com.sogou.upd.passport.manager.api.account.form;

/**
 * 邮箱、个性域名注册参数类
 * User: shipengzhi
 * Date: 13-6-8
 * Time: 下午2:59
 * To change this template use File | Settings | File Templates.
 */
public class RegEmailApiParams {
  private String username;
  private String password;
  private String ip;
  private int provider;
  private int client_id;
  private String captcha;//验证码
  private String token;//标识码

  public RegEmailApiParams(String username, String password, String ip, int client_id,String captcha,String token) {
    this.username = username;
    this.password = password;
    this.ip = ip;
    this.client_id = client_id;
    this.captcha=captcha;
    this.token=token;
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

  public int getClient_id() {
    return client_id;
  }

  public void setClient_id(int client_id) {
    this.client_id = client_id;
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
