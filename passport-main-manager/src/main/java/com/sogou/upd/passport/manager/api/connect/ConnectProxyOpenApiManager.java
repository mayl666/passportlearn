package com.sogou.upd.passport.manager.api.connect;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.connect.form.proxy.ConnectProxyOpenApiParams;

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
     * 处理第三方接口调用请求
     *
     * @param providerStr   第三方类型
     * @param interfaceName 第三方开放平台接口
     * @param params        第三方开放平台参数
     * @return
     */
    public Result handleConnectOpenApi(String openId,String accessToken, String providerStr, String interfaceName, ConnectProxyOpenApiParams params);


}
