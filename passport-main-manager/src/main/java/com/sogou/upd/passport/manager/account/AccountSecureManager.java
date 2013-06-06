package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.form.MobileModifyPwdParams;
import com.sogou.upd.passport.manager.form.ResetPwdParameters;

/**
 * 账户安全相关 User: mayan Date: 13-4-15 Time: 下午4:30 To change this template use File | Settings | File
 * Templates.
 */
public interface AccountSecureManager {

    /**
     * 发送短信验证码（至未注册未绑定手机）
     */
    public Result sendMobileCode(String mobile, int clientId) throws Exception;

    /**
     * 发送短信验证码（根据passportId）
     */
    public Result sendMobileCodeByPassportId(String passportId, int clientId) throws Exception;

    /**
     * 发送手机验证码，不检测是否已注册或绑定，暂时供sendMobileCode*方法调用
     *
     * @param mobile
     * @param clientId
     * @return
     * @throws Exception
     */
    public Result sendSmsCodeToMobile(String mobile, int clientId) throws Exception;

    /**
     * 重发验证码时更新缓存状态
     */
    public Result updateSmsCacheInfo(String cacheKey, int clientId);

    /**
     * 检查发送邮件限制
     *
     * @param passportId
     * @param clientId
     * @param module
     * @param email
     * @return
     * @throws Exception
     */
    public Result checkLimitForSendEmail(String passportId, int clientId, AccountModuleEnum module,
                                         String email) throws Exception;

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
     * @param passportId
     * @param clientId
     * @param doProcess 是否模糊处理，如abcde@sogou.com转换为ab*****e@sogou.com
     * @return result.getData().get("data") // 账户安全信息
     * @throws Exception
     */
    public Result queryAccountSecureInfo(String passportId, int clientId, boolean doProcess) throws Exception;

    /**
     * 重置用户密码（web验证码方式）
     */
    public Result resetWebPassword(ResetPwdParameters resetPwdParameters) throws Exception;

    /**
     * 修改密码，包括检查修改次数
     *
     * @param passportId
     * @param clientId
     * @param password
     * @return
     * @throws Exception
     */
    public Result resetPassword(String passportId, int clientId, String password) throws Exception;

    /* ------------------------------------重置密码Begin------------------------------------ */

    /**
     * 重置密码（邮件方式）——1.发送重置密码申请验证邮件
     *
     * @param passportId
     * @param clientId
     * @param useRegEmail 邮件选择方式，true为注册邮箱，false为绑定邮箱
     * @throws Exception
     */
    public Result sendEmailResetPwdByPassportId(String passportId, int clientId, boolean useRegEmail) throws Exception;


    /**
     * 重置密码（邮件方式）——2.验证重置密码申请链接
     *
     * @param uid 目前为passportId
     * @param token
     */
    public Result checkEmailResetPwd(String uid, int clientId, String token) throws Exception;

    /**
     * 重置密码（邮件方式）——3.再一次验证token，并修改密码。目前passportId与邮件申请链接中的uid一样
     */
    public Result resetPasswordByEmail(String passportId, int clientId, String password, String token)
            throws Exception;


    /**
     * 重置密码（手机方式）——2.检查手机短信码，成功则返回secureCode记录成功标志
     *                      （1.发送见sendMobileCodeByPassportId）
     *
     * @param passportId
     * @param clientId
     * @param smsCode
     * @return
     * @throws Exception
     */
    public Result checkMobileCodeResetPwd(String passportId, int clientId, String smsCode) throws Exception;

    /**
     * 重置密码（密保方式）——1.验证密保答案及captcha，成功则返回secureCode记录成功标志。(可用于其他功能模块)
     *
     * @param passportId
     * @param clientId
     * @param answer
     * @param token
     * @param captcha
     * @return
     * @throws Exception
     */
    public Result checkAnswerByPassportId(String passportId, int clientId, String answer, String token,
            String captcha) throws Exception;

    /**
     * 重置密码（手机和密保方式）——根据secureCode修改密码（secureCode由上一步验证手机或密保问题成功获取）
     *
     * @param passportId
     * @param clientId
     * @param password
     * @param secureCode
     * @return
     * @throws Exception
     */
    public Result resetPasswordByScode(String passportId, int clientId, String password,
                                            String secureCode) throws Exception;

    /**
     * 重置用户密码（检查密保答案）——暂不用！！！
     *
     * @param passportId
     * @param clientId
     * @param password
     * @param answer
     */
    public Result resetPasswordByQues(String passportId, int clientId, String password, String answer)
            throws Exception;

    /**
     * 重置用户密码（手机验证码方式）——暂不用！！！
     */
    public Result resetPasswordByMobile(String passportId, int clientId, String password, String smsCode)
            throws Exception;

    /* ------------------------------------重置密码End------------------------------------ */

    /* ------------------------------------修改密保Begin------------------------------------ */

    /**
     * 修改密保邮箱——1.验证原绑定邮箱及发送邮件至待绑定邮箱
     *
     * @param passportId
     * @param clientId
     * @param newEmail
     * @param oldEmail
     * @return
     * @throws Exception
     */
    public Result sendEmailForBinding(String passportId, int clientId, String password, String newEmail,
                                      String oldEmail) throws Exception;

    /**
     * 修改密保邮箱——2.根据验证链接修改绑定邮箱
     *
     * @param passportId
     * @param clientId
     * @param token
     * @return
     * @throws Exception
     */
    public Result modifyEmailByPassportId(String passportId, int clientId, String token) throws Exception;

    /**
     * 修改密保手机——1.检查原绑定手机短信码，成功则返回secureCode记录成功标志
     *
     * @param passportId
     * @param clientId
     * @param smsCode
     * @return
     * @throws Exception
     */
    public Result checkMobileCodeOldForBinding(String passportId, int clientId, String smsCode) throws Exception;

    /**
     * 修改密保手机——2.验证密码或secureCode、新绑定手机短信码，绑定新手机号
     *
     * @param passportId
     * @param clientId
     * @param newMobile 新绑定手机号
     * @param smsCode   新绑定手机号短信验证码
     * @param checkCode 验证代码，即密码或安全码。取决于firstBind
     * @param firstBind 是否首次绑定。true，则checkCode为密码；false，则checkCode为安全码
     * @return
     * @throws Exception
     */
    public Result modifyMobileByPassportId(String passportId, int clientId, String newMobile, String smsCode,
                                           String checkCode, boolean firstBind) throws Exception;

    /**
     * 修改密保问题——验证密码，绑定新问题和答案
     *
     * @param passportId
     * @param clientId
     * @param password
     * @param newQues
     * @param newAnswer
     * @return
     * @throws Exception
     */
    public Result modifyQuesByPassportId(String passportId, int clientId, String password,
                                         String newQues, String newAnswer) throws Exception;

    /* ------------------------------------修改密保End------------------------------------ */

    /**
     * 验证手机短信随机码——用于新手机验证，不分业务功能
     *
     * @param mobile
     * @param clientId
     * @param smsCode
     * @return
     * @throws Exception
     */
    public Result checkMobileCodeByNewMobile(String mobile, int clientId, String smsCode) throws Exception;

    /**
     * 验证手机短信随机码——用于原绑定手机验证，不分业务功能
     *
     * @param passportId
     * @param clientId
     * @param smsCode
     * @return
     * @throws Exception
     */
    public Result checkMobileCodeByPassportId(String passportId, int clientId, String smsCode) throws Exception;

}
