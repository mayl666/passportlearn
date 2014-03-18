package com.sogou.upd.passport.manager.api.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.form.*;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-6-7
 * Time: 下午9:09
 * To change this template use File | Settings | File Templates.
 */
public interface RegisterApiManager {

    /**
     * 注册邮箱、个性域名（昵称+@Sogou）账号
     * 外域邮箱发送激活邮件，其他账号直接注册
     * @param regEmailApiParams
     * @return
     */
    public Result regMailUser(RegEmailApiParams regEmailApiParams);

    /**
     * 可以通过此接口注册手机号@sohu.com的账号，前提是手机号既没有注册过帐号，也没有绑定过任何账号。
     * 需要用户输入sendMobileRegCaptcha 获取注册的手机验证码接口下发的短信验证码。
     * TODO 这种涉及到流程的接口就不要在SG流程Manager里调用了，这里只是为了历史原因冗余一份
     * @param regMobileCaptchaApiParams
     * @return
     */
    public Result regMobileCaptchaUser(RegMobileCaptchaApiParams regMobileCaptchaApiParams);

    /**
     * 注册手机账号，发给用户的验证码。
     * TODO 在SG流程里可以直接调用SG实现类，无需先调用Proxy，以后改SG
     * @param baseMobileApiParams
     * @return
     */
    public Result sendMobileRegCaptcha(BaseMobileApiParams baseMobileApiParams);

    /**
     * 检查用户名是否已经被注册
     * userid可以为xxx@sogou.com、xxx@sohu.com、xxx@126.com、xxx@qq.sohu.com
     * 不允许为13621009174@sohu.com，如果是手机账号需要调用wapgetuserid接口
     * @param checkUserApiParams
     * @return
     */
    public Result checkUser(CheckUserApiParams checkUserApiParams);

    /**
     * 手机号直接注册，不经验证码——地图专用接口
     *
     * @param regMobileApiParams
     * @return
     */
    public Result regMobileUser(RegMobileApiParams regMobileApiParams);
}
