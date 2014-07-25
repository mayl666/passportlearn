package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.common.parameter.AccountClientEnum;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.exception.ServiceException;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-7 Time: 下午2:00 To change this template use
 * File | Settings | File Templates.
 */
public interface EmailSenderService {

    /**
     * 发送邮件链接至email
     *
     * @param passportId
     * @param clientId
     * @param module
     * @param email
     * @param saveEmail  是否在缓存中存储email，绑定新邮箱需要存储新邮箱地址，找回密码不需要
     * @return
     * @throws ServiceException
     */
    public boolean sendEmail(String passportId, int clientId, AccountClientEnum clientEnum, AccountModuleEnum module, String email, boolean saveEmail, String ru)
            throws ServiceException;

    /**
     * wap端发送邮件链接至email
     *
     * @param passportId
     * @param clientId
     * @param module
     * @param email
     * @param saveEmail  是否在缓存中存储email，绑定新邮箱需要存储新邮箱地址，找回密码不需要
     * @return
     * @throws ServiceException
     */
    public boolean sendWapEmail(String passportId, int clientId, AccountClientEnum clientEnum, AccountModuleEnum module, String email, boolean saveEmail, String ru, String skin, String v)
            throws ServiceException;

    /**
     * @param passportId
     * @param clientId
     * @param module
     * @param address
     * @param ru
     * @return
     * @throws ServiceException
     */
    public boolean sendBindEmail(String passportId, int clientId, AccountModuleEnum module, String address, String ru)
            throws ServiceException;

    /**
     * 检查邮件链接中的scode
     *
     * @param passportId
     * @param clientId
     * @param module
     * @param scode
     * @param saveEmail
     * @return <p>绑定邮箱时saveEmail为true，成功则返回存储的email；
     *         找回密码时saveEmail为false，成功则返回passportId。
     *         <br/>失败返回null</p>
     * @throws ServiceException
     */
    public String checkScodeForEmail(String passportId, int clientId, AccountModuleEnum module, String scode, boolean saveEmail)
            throws ServiceException;

    /**
     * 计数邮件发送次数
     *
     * @param userId
     * @param clientId
     * @param module
     * @param email
     * @return
     * @throws ServiceException
     */
    public boolean incLimitForSendEmail(String userId, int clientId, AccountModuleEnum module, String email)
            throws ServiceException;

    /**
     * 检查邮件发送限制次数
     *
     * @param passportId
     * @param clientId
     * @param module
     * @param email
     * @return
     * @throws ServiceException
     */
    public boolean checkLimitForSendEmail(String passportId, int clientId, AccountModuleEnum module, String email)
            throws ServiceException;

    /**
     * 删除邮件链接scode缓存
     *
     * @param passportId
     * @param clientId
     * @param module
     * @return
     * @throws ServiceException
     */
    public boolean deleteScodeCacheForEmail(String passportId, int clientId, AccountModuleEnum module)
            throws ServiceException;

}
