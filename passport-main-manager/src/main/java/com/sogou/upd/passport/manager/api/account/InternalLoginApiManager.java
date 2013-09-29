package com.sogou.upd.passport.manager.api.account;

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

    /**
     *调用authuser IP 是否在黑名单中
     * @param ip
     * @return
     */
    public boolean isIPInBlackList(final String ip);
}
