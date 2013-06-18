package com.sogou.upd.passport.manager.api.connect;

import com.sogou.upd.passport.manager.form.connect.ConnectLoginParams;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;

/**
 * Created with IntelliJ IDEA.
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
     * @param uuid                防CRSF攻击的唯一值
     * @param provider           第三方平台
     * @param ip                  登录的ip
     * @return
     */
    public String buildConnectLoginURL(ConnectLoginParams connectLoginParams, String uuid,
                                       int provider, String ip) throws OAuthProblemException;
}
