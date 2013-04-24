package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.common.result.Result;

/**
 * Created with IntelliJ IDEA. User: shipengzhi Date: 13-4-21 Time: 下午5:56 To change this template use File | Settings |
 * File Templates.
 */
public interface MobileCodeSenderService {

    /**
     * 检查缓存中是否存在指定手机号的验证码
     */
    public boolean checkIsExistMobileCode(String cacheKey);

    /**
     * 重发验证码时更新缓存状态
     */
    public Result updateSmsCacheInfoByKeyAndClientId(String cacheKey, int clientId);

    /**
     * 注册成功后清除sms缓存信息
     */
    public boolean deleteSmsCache(final String mobile, final int clientId);

    /**
     * 手机验证码的获取与重发
     */
    public Result handleSendSms(String account, int clientId);

    /**
     * 注册时检查手机号，发送验证码是否正确
     */
    public boolean checkSmsInfoFromCache(String account, String smsCode, int clientId);

}
