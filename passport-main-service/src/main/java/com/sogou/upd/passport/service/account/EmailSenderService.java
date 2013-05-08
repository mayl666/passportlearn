package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.exception.ServiceException;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-7 Time: 下午2:00 To change this template use
 * File | Settings | File Templates.
 */
public interface EmailSenderService {
    /**
     * 发送密码重置申请邮件
     *
     * @param email,uid
     */
    public boolean sendEmailForResetPwd(String email, String uid) throws ServiceException;

    /**
     * 验证密码重置申请邮件
     *
     * @param uid,token
     */
    public boolean checkEmailForResetPwd(String uid, String token) throws ServiceException;
}
