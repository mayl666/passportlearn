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
    private int client_id;
    private String password;
    private String instance_id;
    private String ip;

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

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getInstance_id() {
        return instance_id;
    }

    public void setInstance_id(String instance_id) {
        this.instance_id = instance_id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
