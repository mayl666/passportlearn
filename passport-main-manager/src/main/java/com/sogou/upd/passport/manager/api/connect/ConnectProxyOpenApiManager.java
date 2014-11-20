package com.sogou.upd.passport.manager.api.connect;

import com.sogou.upd.passport.common.result.Result;

import java.util.Map;

/**
 * 第三方开放平台接口代理
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-1-4
 * Time: 下午4:39
 * To change this template use File | Settings | File Templates.
 */
public interface ConnectProxyOpenApiManager {

    /**
     * 处理QQ第三方接口调用请求
     *
     * @param sgUrl     应用请求passport接口的url
     * @param tokenMap  sohu返回的openid和token信息
     * @param paramsMap 应用传递进来的参数信息
     * @return
     */
    public Result handleConnectOpenApi(String sgUrl, Map<String, String> tokenMap, Map<String, Object> paramsMap, String thirdAppId);


}
