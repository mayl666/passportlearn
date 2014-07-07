package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.parameter.AccountClientEnum;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.result.Result;

import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-8 Time: 上午10:46 To change this template use
 * File | Settings | File Templates.
 */
public interface ResetPwdManager {

    /**
     * 查询密保信息
     *
     * @param username
     * @param clientId
     * @param doProcess
     * @return
     * @throws Exception
     */
    public Result queryAccountSecureInfo(String username, int clientId, boolean doProcess) throws Exception;

    /**
     * 为了获取用户绑定邮箱及用户的激活状态
     *
     * @param username
     * @return
     * @throws Exception
     */
    public Map<String, Object> getEmailAndStatus(String username) throws Exception;

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
     * @param clientEnum  web端还是wap端
     * @param useRegEmail 邮件选择方式，true为注册邮箱，false为绑定邮箱
     * @param ru          用户传递的ru参数
     * @param scode       安全码
     * @throws Exception
     */
    public Result sendEmailResetPwdByPassportId(String passportId, int clientId, AccountClientEnum clientEnum, boolean useRegEmail, String ru, String scode) throws Exception;

    /**
     * 重置密码时发送验证邮件
     *
     * @param passportId
     * @param clientId
     * @param module
     * @param email
     * @param ru
     * @param scode
     * @return
     * @throws Exception
     */
    public Result sendEmailResetPwd(String passportId, int clientId, AccountClientEnum clientEnum, AccountModuleEnum module,
                                    String email, String ru, String scode) throws Exception;


    /**
     * 重置密码（邮件方式）——2.验证重置密码申请链接
     *
     * @param uid   目前为passportId
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
     * （1.发送见sendMobileCodeByPassportId）
     *
     * @param passportId
     * @param clientId
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
     * @param ip
     * @return
     * @throws Exception
     */
    public Result resetPasswordByScode(String passportId, int clientId, String password,
                                       String secureCode, String ip) throws Exception;

    /**
     * 找回密码，发送手机验证码
     *
     * @param userId
     * @param clientId
     * @return
     * @throws Exception
     */
    public Result sendFindPwdMobileCode(String userId, int clientId, String sec_mobile) throws Exception;

    /**
     * 重置用户密码（手机验证码方式）——暂不用！！！
     */
    public Result resetPasswordByMobile(String passportId, int clientId, String password, String smsCode)
            throws Exception;

    /**
     * 统计找回密码次数
     *
     * @param passportId
     * @throws Exception
     */
    public void incFindPwdTimes(String passportId) throws Exception;

    /**
     * 检查找回密码次数
     *
     * @param passportId
     * @return
     * @throws Exception
     */
    public Result checkFindPwdTimes(String passportId) throws Exception;
}
