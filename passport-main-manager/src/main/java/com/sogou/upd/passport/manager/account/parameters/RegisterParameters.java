package com.sogou.upd.passport.manager.account.parameters;

/**
 * controller层调用manager层时传递时封装的类
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-4-15
 * Time: 下午5:15
 * To change this template use File | Settings | File Templates.
 */
public class RegisterParameters {

    private String mobile;
    private String smscode;
    private int clientId;
    private String password;
    private String instanceId;
    private String ip;

    public int getProvider() {
        return provider;
    }

    public void setProvider(int provider) {
        this.provider = provider;
    }

    private int provider;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSmscode() {
        return smscode;
    }

    public void setSmscode(String smscode) {
        this.smscode = smscode;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
}
