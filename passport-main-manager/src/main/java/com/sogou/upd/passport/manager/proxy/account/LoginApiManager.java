package com.sogou.upd.passport.manager.proxy.account;

import com.sogou.upd.passport.manager.proxy.account.form.AuthUserApiParams;

import java.util.Map;

/**
 * 登录相关
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-6
 * Time: 上午10:19
 */
public interface LoginApiManager {

    /**
     * 校验用户名密码是否正确
     * @param authUserApiParams
     * @return
     */
    Map<String,Object> authUser(AuthUserApiParams authUserApiParams);


}
