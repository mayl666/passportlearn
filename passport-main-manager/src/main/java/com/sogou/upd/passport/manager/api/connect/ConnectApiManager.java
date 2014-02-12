package com.sogou.upd.passport.manager.api.connect;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.connect.form.BaseOpenApiParams;
import com.sogou.upd.passport.manager.form.connect.ConnectLoginParams;
import com.sogou.upd.passport.model.connect.ConnectToken;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.vo.ConnectUserInfoVO;
import com.sogou.upd.passport.oauth2.openresource.vo.OAuthTokenVO;

/**
 * 第三方授权
 * User: shipengzhi
 * Date: 13-6-18
 * Time: 上午12:20
 * To change this template use File | Settings | File Templates.
 */
public interface ConnectApiManager {

    /**
     * 第三方账户登录接口
     *
     * @param connectLoginParams OAuth2登录授权请求参数
     * @param uuid               防CRSF攻击的唯一值
     * @param provider           第三方平台
     * @param ip                 登录的ip
     * @return
     */
    public String buildConnectLoginURL(ConnectLoginParams connectLoginParams, String uuid,
                                       int provider, String ip, String httpOrHttps) throws OAuthProblemException;

    /**
     * 同步创建第三方账号的接口
     *
     * @param providerStr
     * @param oAuthTokenVO
     * @return
     */
    public Result buildConnectAccount(String appKey, String providerStr, OAuthTokenVO oAuthTokenVO);

    /**
     * 根据第三方QQ用户信息获取用户的openid及accessToken
     *
     * @param baseOpenApiParams 调用sohu接口参数类
     * @return
     */
    public Result obtainConnectToken(BaseOpenApiParams baseOpenApiParams, int clientId, String clientKey);

    /**
     * 根据account表的状态来新增/修改connect_token表和connect_relation表
     *
     * @param appKey
     * @param providerStr
     * @param oAuthTokenVO
     * @param isQueryConnectRelation
     * @return
     */
    public Result rebuildConnectAccount(String appKey, String providerStr, OAuthTokenVO oAuthTokenVO, boolean isQueryConnectRelation);


}
