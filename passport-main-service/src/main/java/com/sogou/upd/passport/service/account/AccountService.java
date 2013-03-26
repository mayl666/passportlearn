package com.sogou.upd.passport.service.account;

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
    public Map<String,Object> handleLogin(String mobile, String passwd,String access_token, int appkey, PostUserProfile postData);

    /**
     * 根据用户名密码获取用户
     * @param
     * @return
     */
    public Account getUserAccount(String account, String passwd);

    /**
     * 注册时检测手机号，验证码，应用ID
     *
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
     * 验证码重发缓存更新
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
    public Account initialAccount(String account, String pwd, String ip, int provider);

    /**
     * 初始化第三方用户账号
     * @param account
     * @param ip
     * @param provider
     * @return
     */
    public Account initialConnectAccount(String account, String ip, int provider);

    /**
     * 初始化账号授权信息
     * @param account
     * @return
     */
    public AccountAuth initialAccountAuth(Account account, int appkey) throws Exception;

}
