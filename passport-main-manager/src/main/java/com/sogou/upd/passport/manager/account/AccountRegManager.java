package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.account.parameters.RegisterParameters;
import com.sogou.upd.passport.model.account.Account;

/**
 * 注册管理
 * User: mayan
 * Date: 13-4-15
 * Time: 下午4:43
 * To change this template use File | Settings | File Templates.
 */
public interface AccountRegManager {

    /**
     * 手机用户正式注册接口
     *
     * @param regParams 参数封装的对象
     * @return Result格式的返回值，提示注册成功信息
     */
    public Result mobileRegister(RegisterParameters regParams) throws Exception;

    /**
     * 手机用户找回密码
     *
     * @param mobile   手机号码
     * @param clientId 客户端ID
     * @return Result格式的返回值，成功则发送验证码；失败，提示失败信息
     */
    public Result findPassword(String mobile, int clientId);

    /**
     * 手机用户重置密码
     *
     * @param regParams
     * @return Result格式的返回值, 成功或失败，返回提示信息
     */
    public Result resetPassword(RegisterParameters regParams) throws Exception;

}
