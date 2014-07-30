package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.form.WapLoginParams;
import com.sogou.upd.passport.manager.form.WapPassThroughParams;
import com.sogou.upd.passport.manager.form.WebLoginParams;

/**
 * wap登录
 * User: chenjiameng
 * Date: 13-4-15
 * Time: 下午4:33
 */
public interface WapLoginManager {
    /**
     * wap端校验用户名和密码
     * @param parameters
     * @param ip
     * @return
     */
    public Result accountLogin(WapLoginParams parameters, String ip);

    /**
     * wap端应用检查验证码是否正确
     * @param username
     * @param ip
     * @param clientId
     * @param captchaCode
     * @param token
     * @return
     */
    public Result checkCaptchaVaild(String username, String ip, String clientId,String captchaCode,String token );

    /**
     * wap端移除session
     * @return
     */
    public Result passThroughQQ(int client_id,String sgid,String accessToken,String openId,String ip,String expires_in);
    /**
     * QQ wap透传
     * @return
     */
    public Result removeSession(String sgid);

    /**
     * 判断wap端应用是否需要输入验证码
     * @param client_id
     * @param username
     * @param ip
     * @return
     */
    public boolean needCaptchaCheck(String client_id, String username, String ip);

    /**
     * wap用户登录后操作
     * @param username
     * @param ip
     * @param passportId
     * @param clientId
     */
    public void doAfterLoginSuccess(final String username, final String ip, final String passportId, final int clientId);
}
