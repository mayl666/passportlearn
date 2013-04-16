package com.sogou.upd.passport.manager.connect.params;

/**
 * 第三方账户参数封装的对象
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-4-16
 * Time: 下午6:02
 * To change this template use File | Settings | File Templates.
 */
public class ConnectParams {
    int accountType;
    int clientId;
    String connectUid;
    String bindAccessToken;
    String instanceId;
    String ip;

    public ConnectParams(int accountType, int clientId, String connectUid, String bindAccessToken, String instanceId, String ip) {
        this.accountType = accountType;
        this.clientId = clientId;
        this.connectUid = connectUid;
        this.bindAccessToken = bindAccessToken;
        this.instanceId = instanceId;
        this.ip = ip;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getConnectUid() {
        return connectUid;
    }

    public void setConnectUid(String connectUid) {
        this.connectUid = connectUid;
    }

    public String getBindAccessToken() {
        return bindAccessToken;
    }

    public void setBindAccessToken(String bindAccessToken) {
        this.bindAccessToken = bindAccessToken;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
