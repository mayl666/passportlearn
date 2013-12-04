package com.sogou.upd.passport.manager.api.connect;

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
    public Result getQQConnectUserInfo(BaseOpenApiParams baseOpenApiParams, int clientId, String clientKey);

    /**
     * 调用
     * @param openId
     * @param openKey
     * @param qqParams
     * @return
     * @throws OpensnsException
     */
    public String executeQQOpenApi(String openId, String openKey, QQLightOpenApiParams qqParams) throws OpensnsException;
}
