package com.sogou.upd.passport.manager.api.connect;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.form.connect.ConnectLoginParams;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
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
     * 创建第三方账号
     *
     * @param provider
     * @param oAuthTokenVO
     * @return
     */
    public Result buildConnectAccount(String appKey, int provider, OAuthTokenVO oAuthTokenVO);

    /**
     * 获取第三方用户的accesstoken、refreshtoken
     *
     * @param passportId 用户Id
     * @return
     */
    public Result obtainConnectToken(String passportId, int clientId, String thirdAppId);

    /**
     * 获取访问腾讯云服务需验证的connectToken加密串（t_key）
     *
     * @param passportId 用户Id
     * @return
     */
    public Result obtainTKey(String passportId, int clientId);


}
