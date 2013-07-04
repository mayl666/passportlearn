package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.parameter.AccountModuleEnum;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-3 Time: 上午10:51 To change this template use
 * File | Settings | File Templates.
 *
 * 安全限制、检查验证相关
 */
public interface CheckManager {

    /**
     * 检测页面随机验证码
     *
     * @param captcha
     * @param token
     * @return
     * @throws Exception
     */
    public boolean checkCaptcha(String captcha, String token) throws Exception;

    /**
     * 检查重置密码限制
     *
     * @param passportId
     * @param clientId
     * @return
     * @throws Exception
     */
    public boolean checkLimitResetPwd(String passportId, int clientId) throws Exception;

    /**
     * 检查邮箱验证scode，并返回存储的邮箱名
     *
     * @param passportId
     * @param clientId
     * @param module
     * @param scode
     * @return
     * @throws Exception
     */
    public String checkEmailScodeReturnStr(String passportId, int clientId, AccountModuleEnum module, String scode)
            throws Exception;

    /**
     * 检查邮箱验证scode
     *
     * @param passportId
     * @param clientId
     * @param module
     * @param scode
     * @return
     * @throws Exception
     */
    public boolean checkEmailScode(String passportId, int clientId, AccountModuleEnum module, String scode)
            throws Exception;

    /**
     * 检查重置密码时的scode
     *
     * @param passportId
     * @param clientId
     * @param scode
     * @return
     * @throws Exception
     */
    public boolean checkScodeResetPwd(String passportId, int clientId, String scode) throws Exception;

    /**
     * 检查token
     *
     * @param scode
     * @param id
     * @return
     * @throws Exception
     */
    public boolean checkScode(String scode, String id) throws Exception;
}
