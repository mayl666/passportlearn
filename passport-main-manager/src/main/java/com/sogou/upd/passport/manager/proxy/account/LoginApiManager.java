package com.sogou.upd.passport.manager.proxy.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.proxy.account.form.AppAuthTokenApiParams;
import com.sogou.upd.passport.manager.proxy.account.form.AuthUserApiParams;

/**
 * 登录相关
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-6
 * Time: 上午10:19
 */
public interface LoginApiManager {

    /**
     * web端校验用户名和密码是否正确
     *
     * @param authUserApiParams
     * @return
     */
    public Result webAuthUser(AuthUserApiParams authUserApiParams);

    /**
     * 适用于手机应用使用第三方登录的场景，用户登录完成之后，
     * 会通过302重定向的方式将token带给产品的服务器端，
     * 产品的服务器端通过传入passportid和token验证用户的合法性，且token具有较长的有效期。
     *
     * @param appAuthTokenApiParams
     * @return
     */
    public Result appAuthToken(AppAuthTokenApiParams appAuthTokenApiParams);

}
