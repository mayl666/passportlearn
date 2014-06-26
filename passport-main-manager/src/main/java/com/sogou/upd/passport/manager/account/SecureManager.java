package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.form.MobileModifyPwdParams;
import com.sogou.upd.passport.manager.form.UpdatePwdParameters;
import com.sogou.upd.passport.manager.form.UserNamePwdMappingParams;

import java.util.List;

/**
 * 账户安全相关 User: mayan Date: 13-4-15 Time: 下午4:30 To change this template use File | Settings | File
 * Templates.
 */
public interface SecureManager {

    /**
     * 发送短信验证码（至未注册未绑定手机）
     */
    public Result sendMobileCode(String mobile, int clientId, AccountModuleEnum module) throws Exception;
    /**
     * 为SOHU接口适配，发送短信验证码至原绑定手机
     *
     * @param userId
     * @param clientId
     * @return
     * @throws Exception
     */
    public Result sendMobileCodeOld(String userId, int clientId) throws Exception;

    /**
     * 发送手机验证码，不检测是否已注册或绑定，暂时供sendMobileCode*方法内部调用
     *
     * @param mobile
     * @param clientId
     * @return
     * @throws Exception
     */
    // public Result sendSmsCodeToMobile(String mobile, int clientId) throws Exception;


    /**
     * 手机用户找回密码
     *
     * @param mobile   手机号码
     * @param clientId 客户端ID
     * @return Result格式的返回值，成功则发送验证码；失败，提示失败信息
     */
    public Result findPassword(String mobile, int clientId);

    /**
     * 手机用户发短信重置密码
     *
     * @return Result格式的返回值, 成功或失败，返回提示信息
     */
    public void resetPwd(List<UserNamePwdMappingParams> list) throws Exception;

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
     * 修改用户密码（web验证码方式）
     * @param updatePwdParameters 注意passport_id需从cookie里获取后赋值、ip需赋值
     */
    public Result updateWebPwd(UpdatePwdParameters updatePwdParameters) throws Exception;


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
                                      String oldEmail, String modifyIp, String ru) throws Exception;

    /**
     * 修改密保邮箱——2.根据验证链接修改绑定邮箱
     *
     * @param userId
     * @param clientId
     * @param scode
     * @return
     * @throws Exception
     */
    public Result modifyEmailByPassportId(String userId, int clientId, String scode) throws Exception;

    /**
     * 修改密保手机——1.检查原绑定手机短信码，成功则返回secureCode记录成功标志
     *
     * @param userId
     * @param clientId
     * @param smsCode
     * @return
     * @throws Exception
     */
    public Result checkMobileCodeOldForBinding(String userId, int clientId, String smsCode) throws Exception;

    /**
     * 绑定密保手机——2.首次绑定密保手机，验证密码、新绑定手机短信码，绑定新手机号
     *
     * @param userId
     * @param clientId
     * @param newMobile
     * @param smsCode
     * @param password
     * @return
     * @throws Exception
     */
    public Result bindMobileByPassportId(String userId, int clientId, String newMobile,
                                         String smsCode, String password, String modifyIp) throws Exception;

    /**
     * 修改密保手机——2.修改密保手机，验证scode、新绑定手机短信码，绑定新手机号
     *
     * @param userId
     * @param clientId
     * @param newMobile 新绑定手机号
     * @param smsCode   新绑定手机号短信验证码
     * @param scode 验证安全码
     * @return
     * @throws Exception
     */
    public Result modifyMobileByPassportId(String userId, int clientId, String newMobile,
                                           String smsCode, String scode, String modifyIp) throws Exception;

    /**
     * 修改密保问题——验证密码，绑定新问题和答案
     *
     * @param userId
     * @param clientId
     * @param password
     * @param newQues
     * @param newAnswer
     * @return
     * @throws Exception
     */
    public Result modifyQuesByPassportId(String userId, int clientId, String password,
                                         String newQues, String newAnswer, String modifyIp) throws Exception;

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

    /**
     * 记录执行动作
     *
     * @param userId
     * @param clientId
     * @param action 记录的动作
     * @param ip
     * @param note 记录说明
     * @return
     * @throws Exception
     */
    public Result logActionRecord(String userId, int clientId, AccountModuleEnum action, String ip, String note);

    /**
     * 查询某用户某一动作的所有执行记录
     *
     * @param userId
     * @param clientId
     * @param action 查询的动作
     * @return
     * @throws Exception
     */
    public Result queryActionRecords(String userId, int clientId, AccountModuleEnum action);

    /**
     * 查询某用户的最近一次动作执行记录
     *
     * @param userId
     * @param clientId
     * @param module
     * @return
     */
    public Result queryLastActionRecord(String userId, int clientId, AccountModuleEnum module);

    /**
     * 查询某用户的所有动作执行记录
     *
     * @param userId
     * @param clientId
     * @return
     * @throws Exception
     */
    public Result queryAllActionRecords(String userId, int clientId);
}
