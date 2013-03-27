package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.common.exception.SystemException;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountAuth;
import com.sogou.upd.passport.model.account.PostUserProfile;

import java.util.Map;

/**
 * User: mayan
 * Date: 13-3-22
 * Time: 下午3:38
 * To change this template use File | Settings | File Templates.
 */
public interface AccountService {

    public long userRegister(Account account);

    /**
     * 用户登录接口
     * @param
     * @return
     */
    public Map<String,Object> handleLogin(String mobile, String passwd, int appkey, PostUserProfile postData) throws SystemException;

    /**
     * 注册时检查手机号，发送验证码是否正确
     * @param account
     * @return
     */
    public boolean checkSmsInfoFromCache(String account,String smsCode,String appkey);

    /**
     * 检查此用户是否发送过验证码，并是否在有效期内
     *
     * @param account
     * @return
     */
    public boolean checkIsExistFromCache(String account);

    /**
     * 重发验证码时更新缓存状态
     * @param cacheKey
     * @return
     */
    public Map<String, Object> updateCacheStatusByAccount(String cacheKey);

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
    public Map<String, Object> handleSendSms(String account, int appkey);

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
     * @param appKey
     * @return
     */
    public AccountAuth initialAccountAuth(long userId, String passportId, int appKey) throws Exception;

    /**
     * PassportId与UserId缓存映射
     * @param passportId
     * @param userId
     */
    public boolean addPassportIdMapUserId(String passportId,String userId);

    /**
     * userId与passportId缓存映射
     * @param passportId
     * @param userId
     */
    public boolean addUserIdMapPassportId(String passportId,String userId);

    /**
     * 根据PassportId 获取UserId （缓存读取）
     * @param passportId
     * @return
     */
    public long getUserIdByPassportId(long passportId);
    /**
     * 根据UserId 获取PassportId （缓存读取）
     * @param userId
     * @return
     */
    public long getPassportIdByUserId(long userId);


    /**
     * 修改用户状态表
     * @param accountAuth
     * @return
     */
    public int updateAccountAuth(AccountAuth accountAuth);

}
