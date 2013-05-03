package com.sogou.upd.passport.manager.form;

/**
 * User: mayan Date: 13-4-15 Time: 下午5:15 To change this template use File | Settings | File
 * Templates.
 */
public class WebRegisterParameters {

  private int client_id;
  private String username;
  private String password;
  private String code;//验证码

  public int getClient_id() {
    return client_id;
  }

  public void setClient_id(int client_id) {
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
