package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.manager.form.AccountSecureInfoParams;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-3 Time: 上午10:51 To change this template use
 * File | Settings | File Templates.
 *
 * 限制检测验证相关
 */
public interface AccountCheckManager {

    /**
     * 检测页面随机验证码
     *
     * @param captcha
     * @param token
     * @return
     * @throws Exception
     */
    public boolean checkCaptcha(String captcha, String token) throws Exception;

    // public boolean
}
