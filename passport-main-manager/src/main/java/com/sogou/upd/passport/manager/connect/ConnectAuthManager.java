package com.sogou.upd.passport.manager.connect;

import com.sogou.upd.passport.common.exception.SystemException;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.connect.params.ConnectParams;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthSinaSSOBindTokenRequest;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthSinaSSOTokenRequest;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-4-16
 * Time: 下午5:21
 * To change this template use File | Settings | File Templates.
 */
public interface ConnectAuthManager {

    /**
     * 第三方账户绑定接口
     *
     * @param oauthRequest  Sina微博采用SSO-SDK，OAuth2登录授权成功后的响应结果对象
     * @param connectParams controller传递的绑定参数
     * @return Result格式的返回值
     * @throws SystemException
     */
    public Result connectAuthBind(OAuthSinaSSOBindTokenRequest oauthRequest, ConnectParams connectParams) throws SystemException;

    /**
     * 第三方账户登录接口
     *
     * @param oauthRequest  Sina微博采用SSO-SDK，OAuth2登录授权成功后的响应结果对象
     * @param connectParams controller传递的绑定参数
     * @return Result格式的返回值
     * @throws SystemException
     */
    public Result connectAuthLogin(OAuthSinaSSOTokenRequest oauthRequest, ConnectParams connectParams) throws SystemException;


    /**
     * 根据passportId获取Uid
     *
     * @param passportId
     * @return
     */
    public Result getOpenIdByPassportId(String passportId,int clientId,int accountType);
}
