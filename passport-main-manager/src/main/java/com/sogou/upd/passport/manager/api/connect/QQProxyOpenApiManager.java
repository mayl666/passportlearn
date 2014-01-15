package com.sogou.upd.passport.manager.api.connect;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.connect.form.proxy.ConnectProxyOpenApiParams;
import com.sogou.upd.passport.manager.api.connect.form.proxy.OpenApiParams;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-1-4
 * Time: 下午7:49
 * To change this template use File | Settings | File Templates.
 */
public interface QQProxyOpenApiManager {

    /**
     * 执行QQ第三方开放平台接口调用
     *
     * @param openApiParams
     * @param params
     * @return
     */
    public Result executeQQOpenApi(OpenApiParams openApiParams, ConnectProxyOpenApiParams params);
}
