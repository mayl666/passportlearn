package com.sogou.upd.passport.manager.connect;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.model.app.ConnectConfig;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-22
 * Time: 上午12:57
 * To change this template use File | Settings | File Templates.
 */
public interface OpenAPIUsersManager {

    /**
     * 根据passportId获取第三方openid
     *
     * @param passportId
     * @return
     */
    public Result obtainOpenIdByPassportId(String passportId, int clientId, int provider);

    /**
     * 根据passportId获取第三方用户信息
     * @param accessToken
     * @param provider
     * @return
     */
    public Result obtainUserInfo(String accessToken, int provider, ConnectConfig connectConfig);
}
