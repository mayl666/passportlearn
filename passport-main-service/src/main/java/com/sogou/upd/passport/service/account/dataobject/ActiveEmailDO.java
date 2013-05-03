package com.sogou.upd.passport.service.account.dataobject;

/**
 * User: mayan Date: 13-4-15 Time: 下午5:15 To change this template use File | Settings | File
 * Templates.
 */
public class ActiveEmailDO {

  private int client_id;
  private String password_id;
  private String token;

  public int getClient_id() {
    return client_id;
  }

  public void setClient_id(int client_id) {
    this.client_id = client_id;
  }

  public String getPassword_id() {
    return password_id;
  }

  public void setPassword_id(String password_id) {
    this.password_id = password_id;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
