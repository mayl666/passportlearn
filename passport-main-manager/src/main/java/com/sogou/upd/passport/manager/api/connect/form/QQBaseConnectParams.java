package com.sogou.upd.passport.manager.api.connect.form;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.manager.api.connect.form.proxy.BaseConnectParams;

/**
 * QQ第三方开放平台公共参数类
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-1-10
 * Time: 下午6:53
 * To change this template use File | Settings | File Templates.
 */
public class QQBaseConnectParams extends BaseConnectParams {
    private String appkey = "oauth_consumer_key"; //搜狗在QQ第三方开放平台的appid
    private String openid = "openid";             //搜狗在QQ第三方开放平台的openid名称
    private String server_name = CommonConstant.QQ_SERVER_NAME_GRAPH; //QQ开放平台接口域名
    private String http_type = CommonConstant.HTTPS;                  //QQ开放平台协议方式
    private String method = CommonConstant.CONNECT_METHOD_POST;//QQ开放平台请求方式

    public String getAppkey() {
        return appkey;
    }

    public String getOpenid() {
        return openid;
    }

    public String getServer_name() {
        return server_name;
    }

    public String getHttp_type() {
        return http_type;
    }

    public String getMethod() {
        return method;
    }
}
