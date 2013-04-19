package com.sogou.upd.passport.web.form;

import javax.validation.constraints.Min;

/**
 * 获取第三方id参数校验 User: 马研 Date: 13-4-19 Time: 下午3:32 To change this template use File | Settings |
 * File Templates.
 */
public class ConnectObtainParams {
  @Min(value = 1, message = "passport_id不允许为空!")
  String passport_id;
  @Min(value = 1, message = "client_id不允许为空!")
  int client_id;
  @Min(value = 1, message = "provider不允许为空!")
  int provider;

  public String getPassport_id() {
    return passport_id;
  }

  public void setPassport_id(String passport_id) {
    this.passport_id = passport_id;
  }

  public int getClient_id() {
    return client_id;
  }

  public void setClient_id(int client_id) {
    this.client_id = client_id;
  }

  public int getProvider() {
    return provider;
  }

  public void setProvider(int provider) {
    this.provider = provider;
  }
}
