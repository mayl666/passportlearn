package com.sogou.upd.passport.manager.api.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.form.AuthPcTokenParams;
import com.sogou.upd.passport.manager.form.RefreshPcTokenParams;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-7-24
 * Time: 下午7:45
 * To change this template use File | Settings | File Templates.
 */
public interface OAuthTokenManager {
    public Result refreshToken(RefreshPcTokenParams refreshPcTokenParams);
}
