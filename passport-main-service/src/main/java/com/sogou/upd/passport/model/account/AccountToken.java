package com.sogou.upd.passport.model.account;

/**
 * User: mayan Date: 13-3-22 Time: 下午2:02 To change this template use File | Settings | File
 * Templates.
 */
public class AccountToken {

  private long id;
  private String passportId;
  private String accessToken;
  private String refreshToken;
  private long accessValidTime;
  private long refreshValidTime;
  private int clientId;
  private String instanceId;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getPassportId() {
    return passportId;
  }

  public void setPassportId(String passportId) {
    this.passportId = passportId;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public long getAccessValidTime() {
    return accessValidTime;
  }

  public void setAccessValidTime(long accessValidTime) {
    this.accessValidTime = accessValidTime;
  }

  public long getRefreshValidTime() {
    return refreshValidTime;
  }

  public void setRefreshValidTime(long refreshValidTime) {
    this.refreshValidTime = refreshValidTime;
  }

  public int getClientId() {
    return clientId;
  }

  public void setClientId(int clientId) {
    this.clientId = clientId;
  }

  public String getInstanceId() {
    return instanceId;
  }

  public void setInstanceId(String instanceId) {
    this.instanceId = instanceId;
  }
}