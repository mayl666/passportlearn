package com.sogou.upd.passport.model.mobileoperation;

import com.sogou.upd.passport.common.lang.StringUtil;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: lzy_clement
 * Date: 14-12-16
 * Time: 下午8:17
 * To change this template use File | Settings | File Templates.
 */
public class InterfaceLog extends MobileBaseLog {

    private String uri;
    private String client_id;
    private String start_time;
    private String response_time;
    private String passport_code;
    private String http_code;

    public InterfaceLog(Map map) {
        super(map);
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getResponse_time() {
        return response_time;
    }

    public void setResponse_time(String response_time) {
        this.response_time = response_time;
    }

    public String getPassport_code() {
        return passport_code;
    }

    public void setPassport_code(String passport_code) {
        this.passport_code = passport_code;
    }

    public String getHttp_code() {
        return http_code;
    }

    public void setHttp_code(String http_code) {
        this.http_code = http_code;
    }

    @Override
    public String toHiveString() {
        return StringUtil.exchangeIfContains(StringUtil.defaultIfEmpty(client_id, "-"), "\t", "_") + "\t" + StringUtil.exchangeIfContains(StringUtil.defaultIfEmpty(uri, "-"), "\t", "_") + "\t" +
                StringUtil.exchangeIfContains(StringUtil.defaultIfEmpty(start_time, "-"), "\t", "_") + "\t" + StringUtil.exchangeIfContains(StringUtil.defaultIfEmpty(response_time, "-"), "\t", "_") + "\t" +
                StringUtil.exchangeIfContains(StringUtil.defaultIfEmpty(passport_code, "-"), "\t", "_") + "\t" + StringUtil.exchangeIfContains(StringUtil.defaultIfEmpty(http_code, "-"), "\t", "_");
    }

}

