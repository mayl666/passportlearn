package com.sogou.upd.passport.model.account.query;

/**
 * Created with IntelliJ IDEA. User: shipengzhi Date: 13-3-24 Time: 下午4:57 To change this template
 * use File | Settings | File Templates.
 */
public class AccountConnectQuery {

  private String connectUid;
  private int clientId;
  private int accountType;
  private long userId;

  public AccountConnectQuery() {
  }

  public AccountConnectQuery(String connectUid, int accountType) {
    this.connectUid = connectUid;
    this.accountType = accountType;
  }

  public AccountConnectQuery(String connectUid, int accountType, int clientId) {
    this(connectUid, accountType);
    this.clientId = clientId;
  }

  public AccountConnectQuery(String connectUid, int accountType, int clientId, long userId) {
    this(connectUid, accountType, clientId);
    this.userId = userId;
  }

  public AccountConnectQuery(long userId,int accountType, int clientId) {
    this.clientId = clientId;
    this.accountType = accountType;
    this.userId = userId;
  }

  public String getConnectUid() {
    return connectUid;
  }

  public void setConnectUid(String connectUid) {
    this.connectUid = connectUid;
  }

  public int getClientId() {
    return clientId;
  }

  public void setClientId(int clientId) {
    this.clientId = clientId;
  }

  public int getAccountType() {
    return accountType;
  }

  public void setAccountType(int accountType) {
    this.accountType = accountType;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }
}
