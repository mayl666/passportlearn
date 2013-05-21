package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.form.AccountSecureInfoParams;
import com.sogou.upd.passport.manager.form.MobileModifyPwdParams;
import com.sogou.upd.passport.manager.form.ResetPwdParameters;

/**
 * 账户安全相关 User: mayan Date: 13-4-15 Time: 下午4:30 To change this template use File | Settings | File
 * Templates.
 */
public interface AccountSecureManager {

    /**
     * 发送短信验证码
     */
    public Result sendMobileCode(String mobile, int clientId);

    /**
     * 发送短信验证码（根据passportId）
     */
    public Result sendMobileCodeByPassportId(String passportId, int clientId);

    /**
     * 重发验证码时更新缓存状态
     */
    public Result updateSmsCacheInfo(String cacheKey, int clientId);

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
     * @return Result格式的返回值, 成功或失败，返回提示信息
     */
    public Result resetPassword(MobileModifyPwdParams regParams) throws Exception;

    /**
     * 查询账户安全信息，包括邮箱、手机、密保问题，并模糊处理
     *
     * @param params
     * @return
     * @throws Exception
     */
    public Result queryAccountSecureInfo(AccountSecureInfoParams params) throws Exception;

    /**
     * 发送重置密码申请验证邮件
     *
     * @param passportId
     * @param clientId
     * @param mode 邮件选择方式，1为注册邮箱，其他为绑定邮箱
     * @throws Exception
     */
    public Result sendEmailResetPwdByPassportId(String passportId, int clientId, int mode) throws Exception;

    /**
     * 验证重置密码申请邮件
     *
     * @param uid 目前为passportId
     * @param token
     */
    public Result checkEmailResetPwd(String uid, int clientId, String token) throws Exception;

    /**
     * 重置用户密码（检查密保答案）
     *
     * @param passportId
     * @param clientId
     * @param password
     * @param answer
     */
    public Result resetPasswordByQues(String passportId, int clientId, String password, String answer)
            throws Exception;

    /**
     * 重置用户密码（手机验证码方式）
     */
    public Result resetPasswordByMobile(String passportId, int clientId, String password, String smsCode) throws Exception; /**
     * 重置用户密码（web验证码方式）
     */
    public Result resetWebPassword(ResetPwdParameters resetPwdParameters) throws Exception;

    /**
     * 重置用户密码（邮件方式）---目前passportId与邮件申请链接中的uid一样
     */
    public Result resetPasswordByEmail(String passportId, int clientId, String password, String token) throws Exception;

    /* --------------------------------------------修改密保内容-------------------------------------------- */

    /**
     * 验证手机短信随机码——用于新手机验证
     *
     * @param mobile
     * @param clientId
     * @param smsCode
     * @return
     * @throws Exception
     */
    public Result checkMobileCodeByNewMobile(String mobile, int clientId, String smsCode) throws Exception;

    /**
     * 验证手机短信随机码——用于原绑定手机验证
     *
     * @param passportId
     * @param clientId
     * @param smsCode
     * @return
     * @throws Exception
     */
    public Result checkMobileCodeByPassportId(String passportId, int clientId, String smsCode) throws Exception;

    /**
     * 修改绑定手机
     *
     * @param passportId
     * @param clientId
     * @param newMobile
     * @return
     * @throws Exception
     */
    public Result modifyMobileByPassportId(String passportId, int clientId, String newMobile) throws Exception;

    /**
     * 验证原绑定邮箱及发送邮件至待绑定邮箱
     *
     * @param passportId
     * @param clientId
     * @param newEmail
     * @param oldEmail
     * @return
     * @throws Exception
     */
    public Result sendEmailForBinding(String passportId, int clientId, String newEmail, String oldEmail)
            throws Exception;

    /**
     * 根据验证链接修改绑定邮箱
     *
     * @param passportId
     * @param clientId
     * @param token
     * @return
     * @throws Exception
     */
    public Result modifyEmailByPassportId(String passportId, int clientId, String token) throws Exception;

    /**
     * 发送手机验证码，不检测是否已注册或绑定
     *
     * @param mobile
     * @param clientId
     * @return
     * @throws Exception
     */
    public Result sendSmsCodeToMobile(String mobile, int clientId) throws Exception;
}
