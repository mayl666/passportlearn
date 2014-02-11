package com.sogou.upd.passport.model.connect;

/**
 * 存储第三方账号关联关系的connect_relation表
 * User: shipengzhi
 * Date: 13-4-21 Time: 下午5:39
 */
public class ConnectRelation {

  private long id;
  private String passportId;
  private String appKey;
  private int provider;
  private String openid;

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

  public String getAppKey() {
    return appKey;
  }

  public void setAppKey(String appKey) {
    this.appKey = appKey;
  }

  public int getProvider() {
    return provider;
  }

  public void setProvider(int provider) {
    this.provider = provider;
  }

  public String getOpenid() {
    return openid;
  }

  public void setOpenid(String openid) {
    this.openid = openid;
  }
}
