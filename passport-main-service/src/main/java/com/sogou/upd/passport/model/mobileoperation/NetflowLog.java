package com.sogou.upd.passport.model.mobileoperation;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: lzy_clement
 * Date: 14-12-16
 * Time: 下午8:37
 * To change this template use File | Settings | File Templates.
 */
public class NetflowLog extends MobileBaseLog {

    private String start_time;
    private String now_time;
    private String mobile_down_http;
    private String mobile_up_http;
    private String wifi_down_http;
    private String wifi_up_http;
    private String mobile_down_tcp;
    private String mobile_up_tcp;
    private String wifi_down_tcp;
    private String wifi_up_tcp;

    public NetflowLog(Map map) {
        super(map);
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getNow_time() {
        return now_time;
    }

    public void setNow_time(String now_time) {
        this.now_time = now_time;
    }

    public String getMobile_down_http() {
        return mobile_down_http;
    }

    public void setMobile_down_http(String mobile_down_http) {
        this.mobile_down_http = mobile_down_http;
    }

    public String getMobile_up_http() {
        return mobile_up_http;
    }

    public void setMobile_up_http(String mobile_up_http) {
        this.mobile_up_http = mobile_up_http;
    }

    public String getWifi_down_http() {
        return wifi_down_http;
    }

    public void setWifi_down_http(String wifi_down_http) {
        this.wifi_down_http = wifi_down_http;
    }

    public String getWifi_up_http() {
        return wifi_up_http;
    }

    public void setWifi_up_http(String wifi_up_http) {
        this.wifi_up_http = wifi_up_http;
    }

    public String getMobile_down_tcp() {
        return mobile_down_tcp;
    }

    public void setMobile_down_tcp(String mobile_down_tcp) {
        this.mobile_down_tcp = mobile_down_tcp;
    }

    public String getMobile_up_tcp() {
        return mobile_up_tcp;
    }

    public void setMobile_up_tcp(String mobile_up_tcp) {
        this.mobile_up_tcp = mobile_up_tcp;
    }

    public String getWifi_down_tcp() {
        return wifi_down_tcp;
    }

    public void setWifi_down_tcp(String wifi_down_tcp) {
        this.wifi_down_tcp = wifi_down_tcp;
    }

    public String getWifi_up_tcp() {
        return wifi_up_tcp;
    }

    public void setWifi_up_tcp(String wifi_up_tcp) {
        this.wifi_up_tcp = wifi_up_tcp;
    }

    @Override
    public String toHiveString() {
        return start_time + "\t" + now_time + "\t" + mobile_down_http + "\t" + mobile_up_http + "\t" + wifi_down_http + "\t" + wifi_up_http + "\t" + mobile_down_tcp + "\t" + mobile_up_tcp + "\t" + wifi_up_tcp + "\t" + wifi_down_http;
    }
}
