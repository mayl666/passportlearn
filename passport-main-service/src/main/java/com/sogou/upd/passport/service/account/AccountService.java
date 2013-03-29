package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.common.exception.SystemException;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountAuth;
import com.sogou.upd.passport.model.account.PostUserProfile;
import com.sogou.upd.passport.model.app.AppConfig;

import java.util.Map;

/**
 * User: mayan
 * Date: 13-3-22
 * Time: 下午3:38
 * To change this template use File | Settings | File Templates.
 */
public interface AccountService {

    /**
     * 验证用户名密码是否正确
     * @param username
     * @param password
     * @return
     */
    public boolean verifyUserVaild(String username, String password);

    public long userRegister(Account account);

    /**
     * 用户登录接口
     * @param
     * @return
     */
    public Map<String,Object> handleLogin(String mobile, String passwd, int clientId, PostUserProfile postData) throws SystemException;

    /**
     * 注册时检查手机号，发送验证码是否正确
     * @param account
     * @return
     */
    public boolean checkSmsInfoFromCache(String account,String smsCode,String clientId);

    /**
     * 检查此用户是否发送过验证码，并是否在有效期内
     *
     * @param account
     * @return
     */
    public boolean checkKeyIsExistFromCache(String account);

    /**
     * 重发验证码时更新缓存状态
     * @param cacheKey
     * @return
     */
    public Map<String, Object> updateSmsInfoByAccountFromCache(String cacheKey,int clientId);

    /**
     * 检查此用户是否注册过，从用户账号表查
     *
     * @param account
     * @return
     */
    public boolean checkIsRegisterAccount(Account account);

    /**
     * 手机验证码的获取与重发
     * @param account
     * @return
     */
    public Map<String, Object> handleSendSms(String account, int clientId);

    /**
     * 初始化非第三方用户账号
     * @param account
     * @param pwd
     * @param ip
     * @param provider
     * @return
     */
    public Account initialAccount(String account, String pwd, String ip, int provider) throws SystemException;

    /**
     * 初始化第三方用户账号
     * @param account
     * @param ip
     * @param provider
     * @return
     */
    public Account initialConnectAccount(String account, String ip, int provider) throws SystemException;

    /**
     * 初始化账号授权信息
     * @param userId
     * @param passportId
     * @param clientId
     * @return
     */
    public AccountAuth initialAccountAuth(long userId, String passportId, int clientId) throws Exception;

    /**
     *
     * @param userId
     * @param passportId
     * @param clientId
     * @return
     * @throws Exception
     */
    public AccountAuth updateAccountAuth(long userId, String passportId, int clientId) throws Exception;

    /**
     * PassportId与UserId缓存映射
     * @param passportId
     * @param userId
     */
    public boolean addPassportIdMapUserIdToCache(String passportId,String userId);

    /**
     * userId与passportId缓存映射
     * @param passportId
     * @param userId
     */
    public boolean addUserIdMapPassportIdToCache(String userId,String passportId);

    /**
     * ClientId与AppConfig缓存映射
     * @param clientId
     * @param appConfig
     */
    public boolean addClientIdMapAppConfigToCache(int clientId,AppConfig appConfig);
    /**
     * 根据PassportId 获取UserId或者mobile （缓存读取）
     * @param passportId
     * @return
     */
    public long getUserIdByPassportIdFromCache(String passportId);
    /**
     * 根据UserId 获取PassportId （缓存读取）
     * @param userId
     * @return
     */
    public String getPassportIdByUserIdFromCache(long userId);
    /**
     * 根据ClientId 获取AppConfig （缓存读取）
     * @param clientId
     * @return
     */
    public AppConfig getAppConfigByClientIdFromCache(int clientId);

    /**
     * 修改用户状态表
     * @param accountAuth
     * @return
     */
    public int updateAccountAuth(AccountAuth accountAuth);

    /**
     * 根据主键ID获取passportId
     * @param userId
     * @return
     */
    public String getPassportIdByUserId(long userId);

    /**
     * 根据主键ID获取passportId
     * @param passportId
     * @return
     */
    public long getUserIdByPassportId(String passportId);


    /**
     * 注册成功后清除sms缓存信息
     * @param mobile
     * @param clientId
     * @return
     */
    public boolean deleteSmsCache(final String mobile, final String clientId);
}
