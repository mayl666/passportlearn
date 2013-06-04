package com.sogou.upd.passport.common.model.httpclient;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-2
 * Time: 下午10:59
 */
public class ResponseModel {

    //返回码
    private int statusCode;

    //
    private String body;


    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
