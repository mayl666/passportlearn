package com.sogou.upd.passport.manager.proxy.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.proxy.account.form.BaseMoblieApiParams;
import com.sogou.upd.passport.manager.proxy.account.form.MobileRegApiParams;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-6-7
 * Time: 下午9:09
 * To change this template use File | Settings | File Templates.
 */
public interface RegisterApiManager {

    /**
     * 可以通过此接口注册手机号@sohu.com的账号，前提是手机号既没有注册过帐号，也没有绑定过任何账号。
     * 需要用户输入1.1.1.4 获取注册的手机验证码接口下发的短信验证码。
     * @param mobileRegApiParams
     * @return
     */
    public Result mobileRegister(MobileRegApiParams mobileRegApiParams);

    /**
     * 注册手机账号，发给用户的验证码。
     * @param baseMoblieApiParams
     * @return
     */
    public Result sendMobileRegCaptcha(BaseMoblieApiParams baseMoblieApiParams);

}
