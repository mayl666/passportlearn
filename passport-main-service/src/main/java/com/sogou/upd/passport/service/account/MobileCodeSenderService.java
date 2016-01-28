package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.result.Result;

import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: shipengzhi Date: 13-4-21 Time: 下午5:56 To change this template
 * use File | Settings | File Templates.
 */
public interface MobileCodeSenderService {

    /**
     * 检查缓存中是否存在指定手机号的验证码
     */
    public boolean checkIsExistMobileCode(String cacheKey);

    /**
     * 重发验证码时更新缓存状态
     *
     * @param cacheKeySendNum 当天发送次数更新key
     * @param cacheKeySmscode 发送手机验证码key
     * @param curtime         更新 手机验证码key 中 curtime
     */
    public boolean updateSmsCacheInfo(String cacheKeySendNum, String cacheKeySmscode,
                                      String curtime,String smsCode);

    /**
     * 注册成功后清除sms缓存信息
     */
    public boolean deleteSmsCache(final String mobile, final int clientId);

    /**
     * 校验完成后清除sms缓存信息
     * @param mobile
     * @param module
     * @return
     */
    public boolean deleteSmsCache(final String mobile, final AccountModuleEnum module);

    /**
     * 发送短信验证码
     *
     * @param mobile
     * @param clientId
     * @param module
     * @return
     */
    public Result sendSmsCode(String mobile, int clientId, AccountModuleEnum module);

    /**
     * 注册时检查手机号，发送验证码是否正确
     */
    public boolean checkSmsInfoFromCache(String mobile, int clientId, AccountModuleEnum module, String smsCode);

    /**
     * 检查是否不超过验证码验证失败次数
     *
     * @return 不超过失败次数，返回true；超过，返回false
     */
    public boolean checkLimitForSmsFail(String account, int clientId, AccountModuleEnum module);

    /**
     * 根据手机号检查验证码是否正确，正确则删除验证码
     *
     * @param mobile
     * @param clientId
     * @param smsCode
     * @return
     */
    public Result checkSmsCode(String mobile, int clientId, AccountModuleEnum module, String smsCode);
}
