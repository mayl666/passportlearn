package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.service.account.dataobject.ActiveEmailDO;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-8 Time: 上午10:46 To change this template use
 * File | Settings | File Templates.
 */
public interface ResetPwdManager {

    /**
     * 为了获取用户绑定邮箱及用户的激活状态
     *
     * @param username
     * @param to_email
     * @return
     * @throws Exception
     * @to_email
     */
    public Result checkEmailCorrect(String username, String to_email) throws Exception;

    /* ------------------------------------重置密码Begin------------------------------------ */
    /**
     * 重置密码（邮件方式）——1.发送重置密码申请验证邮件
     *
     * @param passportId
     * @param clientId
     * @param useRegEmail 邮件选择方式，true为注册邮箱，false为绑定邮箱
     * @param ru          用户传递的ru参数
     * @param scode       安全码
     * @throws Exception
     */
    public Result sendEmailResetPwdByPassportId(String passportId, int clientId, boolean useRegEmail,
                                                String ru, String scode) throws Exception;
    
    /**
     * 重置密码（邮件方式）——1.发送重置密码申请验证邮件
     *
     * @param passportId
     * @param clientId
     * @param useRegEmail 邮件选择方式，true为注册邮箱，false为绑定邮箱
     * @param ru          用户传递的ru参数
     * @param scode       安全码
     * @param rtp  redirect to passport 是否跳转到 passport
     * @param lang  语言
     * @throws Exception
     */
    public Result sendEmailResetPwdByPassportId(String passportId, int clientId, boolean useRegEmail,
                                                String ru, String scode, Boolean rtp, String lang) throws Exception;

    /**
     * 重置密码时发送验证邮件
     *
     * @param activeEmailDO
     * @param scode
     * @return
     * @throws Exception
     */
    public Result sendEmailResetPwd(ActiveEmailDO activeEmailDO, String scode) throws Exception;


    /**
     * 重置密码（邮件方式）——2.验证重置密码申请链接
     *
     * @param uid   目前为passportId
     * @param token
     */
    public Result checkEmailResetPwd(String uid, int clientId, String token) throws Exception;

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
     * @param sec_mobile
     * @param token
     * @param captcha
     * @return
     * @throws Exception
     */
    public Result sendFindPwdMobileCode(String userId, int clientId, String sec_mobile, String token, String captcha) throws Exception;

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
