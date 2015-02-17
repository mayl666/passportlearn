package com.sogou.upd.passport.model.app;

import java.util.Date;

/**
 * 应用的包名和包签名
 * User: nahongxu
 * Date: 15-2-15
 * Time: 下午16:18
 * To change this template use File | Settings | File Templates.
 */
public class PackageNameSign {

    private long id;
    private int clientId;
    private String packageName;
    private String packageSign;
    private Date updateTime;

    public PackageNameSign() {

    }

    public PackageNameSign(String name, String sign) {
        this.packageName = name;
        this.packageSign = sign;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageSign() {
        return packageSign;
    }

    public void setPackageSign(String packageSign) {
        this.packageSign = packageSign;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
