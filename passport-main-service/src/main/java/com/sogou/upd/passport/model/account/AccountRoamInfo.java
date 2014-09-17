package com.sogou.upd.passport.model.account;

/**
 * 漫游用户信息
 * User: chengang
 * Date: 14-7-29
 * Time: 上午11:39
 */
public class AccountRoamInfo {

    /**
     * 版本
     */
    public String version;

    /**
     * 用户Id
     */
    public String userId;


    /**
     * 用户登录状态
     */
    public String status;

    /**
     * 请求时间
     */
    public long requestTime;


    /**
     * 用户请求真实IP
     */
    public String requestIp;


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(long requestTime) {
        this.requestTime = requestTime;
    }

    public String getRequestIp() {
        return requestIp;
    }

    public void setRequestIp(String requestIp) {
        this.requestIp = requestIp;
    }
}
