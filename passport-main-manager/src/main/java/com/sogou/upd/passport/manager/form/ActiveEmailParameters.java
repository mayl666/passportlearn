package com.sogou.upd.passport.manager.form;

import org.hibernate.validator.constraints.NotBlank;

/**
 * User: mayan Date: 13-4-15 Time: 下午5:15 To change this template use File | Settings | File
 * Templates.
 */
public class ActiveEmailParameters {
  @NotBlank(message = "参数错误!")
  private String client_id;
  @NotBlank(message = "参数错误!")
  private String passport_id;
  @NotBlank(message = "参数错误!")
  private String token;

  public String getClient_id() {
    return client_id;
  }

  public void setClient_id(String client_id) {
    this.client_id = client_id;
  }

  public String getPassport_id() {
    return passport_id;
  }

  public void setPassport_id(String passport_id) {
    this.passport_id = passport_id;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
