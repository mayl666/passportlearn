package com.sogou.upd.passport.common.model;

/**
 * api客户端调用时需要提交的基本参数
 * User: ligang201716@sogou-inc.com
 * Date: 13-5-28
 * Time: 下午2:54
 */
public class BaseApiParams {

    //请求的appId
    private String appId;

    //long型系统时间
    private String ct;

    //通过参数的校验获得key,如mobile+ appid + key + ct的md5
    private String code;

    public BaseApiParams(){
        super();
    }

    public BaseApiParams(String appId, String ct, String code) {
        this.appId = appId;
        this.ct = ct;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getCt() {
        return ct;
    }

    public void setCt(String ct) {
        this.ct = ct;
    }
}
