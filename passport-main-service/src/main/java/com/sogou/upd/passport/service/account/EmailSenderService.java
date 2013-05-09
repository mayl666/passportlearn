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
     * @param uid
     * @param clientId
     * @param address
     */
    public boolean sendEmailForResetPwd(String uid, int clientId, String address) throws ServiceException;

    /**
     * 验证密码重置申请邮件
     *
     * @param uid
     * @param clientId
     * @param token
     */
    public boolean checkEmailForResetPwd(String uid, int clientId, String token) throws ServiceException;

    /**
     * 删除邮件链接token缓存
     * @param uid
     * @param clientId
     * @return
     * @throws ServiceException
     */
    public boolean deleteEmailCacheResetPwd(String uid, int clientId) throws ServiceException;
}
