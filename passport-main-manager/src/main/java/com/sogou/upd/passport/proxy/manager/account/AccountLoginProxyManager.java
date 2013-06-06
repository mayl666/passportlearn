package com.sogou.upd.passport.proxy.manager.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.form.proxy.account.AuthUserParameters;

import java.util.Map;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-6
 * Time: 上午10:19
 */
public interface AccountLoginProxyManager {

    /**
     * 校验用户名密码是否正确
     * @param authUserParameters
     * @return
     */
    Map<String,Object> authUser(AuthUserParameters authUserParameters);


}
