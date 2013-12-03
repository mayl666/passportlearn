package com.sogou.upd.passport.manager.api.connect;

import com.qq.open.OpenApiV3;
import com.qq.open.OpensnsException;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.connect.form.BaseOpenApiParams;
import com.sogou.upd.passport.manager.api.connect.form.qq.QQLightOpenApiParams;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-11-28
 * Time: 上午10:10
 * To change this template use File | Settings | File Templates.
 */
public interface QQLightOpenApiManager {
    /**
     * 根据用户信息获取用户的openid及accessToken
     *
     * @param baseOpenApiParams 调用sohu接口参数类
     * @return
     */
    public Result getProxyConnectUserInfo(BaseOpenApiParams baseOpenApiParams,int clientId,String clientKey);

    /**
     * @param sdk                  QQ开放平台接口类
     * @param openid               用户的openid
     * @param openkey              用户的accessToken
     * @param qqLightOpenApiParams 代理接口参数类
     * @throws OpensnsException QQ开放平台异常类
     */
    public String executeQQOpenApi(OpenApiV3 sdk, String openid, String openkey, QQLightOpenApiParams qqLightOpenApiParams) throws OpensnsException;
}
