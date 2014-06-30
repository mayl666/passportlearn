package com.sogou.upd.passport.manager.api.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.form.BaseMoblieApiParams;
import com.sogou.upd.passport.manager.api.account.form.BindEmailApiParams;
import com.sogou.upd.passport.manager.api.account.form.BindMobileApiParams;
import com.sogou.upd.passport.manager.api.account.form.SendCaptchaApiParams;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-7
 * Time: 下午1:57
 */
public interface BindApiManager {

    /**
     * 绑定手机接口代理
     * @param bindMobileApiParams
     * @return
     */
    Result bindMobile(BindMobileApiParams bindMobileApiParams);

    /**
     * 绑定邮箱接口
     * @param bindEmailApiParams
     * @return
     */
    Result bindEmail(BindEmailApiParams bindEmailApiParams);

    /**
     * 查询手机号绑定的账号
     * @param baseMoblieApiParams
     * @return
     */
    Result getPassportIdByMobile(BaseMoblieApiParams baseMoblieApiParams);

    /**
     * 发送验证码相关接口
     * @param sendCaptchaApiParams
     * @return
     */
    Result sendCaptcha(SendCaptchaApiParams sendCaptchaApiParams);

    /**
     * 缓存旧手机号验证码
     *
     * @param mobile
     * @param clientId
     * @param captcha
     * @return
     */
    public boolean cacheOldCaptcha(String mobile, int clientId, String captcha);

    /**
     * 提取旧手机号验证码
     *
     * @param mobile
     * @param clientId
     * @return
     */
    public String getOldCaptcha(String mobile, int clientId);

    /**
     * 直接绑定手机号
     * @param passportId
     * @param newMobile
     * @return
     */
    public Result bindMobile(String passportId,String newMobile);

    /**
     * 直接解除手机绑定
     * @param mobile
     * @return
     */
    public Result unBindMobile(String mobile);
}
