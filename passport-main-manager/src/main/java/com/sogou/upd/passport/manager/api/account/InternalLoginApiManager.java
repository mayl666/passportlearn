package com.sogou.upd.passport.manager.api.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.form.AppAuthTokenApiParams;
import com.sogou.upd.passport.manager.api.account.form.AuthUserApiParams;
import com.sogou.upd.passport.manager.api.account.form.CreateCookieApiParams;
import com.sogou.upd.passport.manager.api.account.form.CreateCookieUrlApiParams;

/**
 * 登录相关
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-6
 * Time: 上午10:19
 */
public interface InternalLoginApiManager {
    /**
     * authuser成功后操作
     * @param username
     * @param ip
     * @param passportId
     * @param clientId
     */
    public void doAfterAuthUserSuccess(final String username, final String ip, final String passportId, final int clientId);

    /**
     * authuser失败后操作
     * @param username
     * @param ip
     */
    public void doAfterAuthUserFailed(final String username, final String ip);

    /**
     * 判断authuser是否在黑名单中
     * @param username
     * @param ip
     * @return
     */
    public boolean isAuthUserInBlackList(final String username, final String ip);
}
