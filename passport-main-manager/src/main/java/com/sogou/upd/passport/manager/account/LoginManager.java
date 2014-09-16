package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.form.WebLoginParams;

/**
 * 手机号登录，邮箱登录
 * User: mayan
 * Date: 13-4-15
 * Time: 下午4:33
 */
public interface LoginManager {

    /**
     * 登录时检查用户名是否存在
     *
     * @param username
     * @param clientId
     * @return
     */
    public Result checkUser(String username, int clientId) throws Exception;

    /**
     * 登录验证
     * 根据module有密码验证、快速验证
     * @param parameters
     * @param ip
     * @param scheme
     * @return
     */
    public Result accountLogin(WebLoginParams parameters, String ip, String scheme);

    /**
     * 获取username登陆的时候是否需要登陆验证码
     * 目前策略如果连续3次登陆失败就需要输入验证码
     * 或者IP超过一个量就输入验证码
     *
     * @param username
     * @param ip
     * @return
     */
    public boolean needCaptchaCheck(String client_id, String username, String ip);

    public void doAfterLoginSuccess(final String username, final String ip, final String passportId, final int clientId);

    public void doAfterLoginFailed(final String username, final String ip, String errCode);

    /**
     * 检查username ip是否在黑名单中
     *
     * @param username
     * @param ip
     * @return
     */
    public boolean isLoginUserInBlackList(final String username, final String ip);

    /**
     * 通过username获取passportId
     *
     * @param username
     * @return
     */
    public String getIndividPassportIdByUsername(String username);

    /**
     * 判断用户名是否在黑名单中，并校验用户名、密码
     *
     * @param username
     * @param ip
     * @param pwdMD5
     * @return
     */
    public Result authUser(String username, String ip, String pwdMD5);

}