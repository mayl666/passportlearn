package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.account.Account;

import java.util.Map;

/**
 * User: mayan Date: 13-3-22 Time: 下午3:38 To change this template use File | Settings | File
 * Templates.
 */
public interface AccountService {

  /**
   * 初始化web用户账号
   */
  public Account initialWebAccount(String username,String ip)
      throws ServiceException;

  /**
   * 初始化非第三方用户账号
   */
  public Account initialAccount(String username, String password, boolean needMD5, String ip, int provider)
      throws ServiceException;

  /**
   * 初始化第三方用户账号
   */
  public Account initialConnectAccount(String passportId, String ip, int provider)
      throws ServiceException;

  /**
   * 根据passportId获取Account
   */
  public Account queryAccountByPassportId(String passportId) throws ServiceException;
  /**
   * 检测密码修改次数是否超出每天限制
   *
   * @param passportId
   * @return 不超出返回true，超出返回false
   * @throws ServiceException
   */
  public boolean checkLimitResetPwd(String passportId) throws ServiceException;
  /**
   * 验证账号的有效性，是否为正常用户
   *
   * @return 验证不通过，则返回null
   */
  public Account queryNormalAccount(String passportId) throws ServiceException;

  /**
   * 验证用户名密码是否正确
   *
   * @return 用户名或密码不匹配，则返回null
   */
  public Result verifyUserPwdVaild(String passportId, String password, boolean needMD5) throws ServiceException;

  /**
   * 根据passportId删除Account，内部debug接口使用
   */
  public boolean deleteAccountByPassportId(String passportId) throws ServiceException;

  /**
   * 重置密码
   */
  public boolean resetPassword(Account account, String password, boolean needMD5) throws ServiceException;

  /**
   * 根据ip看是否在黑名单中
   */
  public boolean isInAccountBlackListByIp(String passportId, String ip)
      throws ServiceException;

  /**
   * 激活验证邮件
   *
   * @return Result格式的返回值, 成功或失败，返回提示信息
   */
  public boolean sendActiveEmail(String username,String passpord,int clientId,String ip,String ru) throws Exception;

  /**
   * 激活验证邮件
   *
   * @return
   */
  public boolean activeEmail(String username,String token,int clientId) throws Exception;
  /**
   * 种根域和子域下的cookie
   *
   * @return
   */
  public boolean setCookie() throws Exception;

  /*
   *获取验证码
   */
  public Map<String,Object> getCaptchaCode(String code);
  /**
   * 校验验证码是否匹配
   *
   * @return 匹配结果
   */
  public boolean checkCaptchaCodeIsVaild(String token, String captchaCode);

  /**
    * 根据用户连续登陆失败次数确定用户登陆时是否需要输入验证码
    * @param username
    * @return
    */
  public boolean loginFailedNumNeedCaptcha(String username, String ip);

    /**
     * 用户登陆失败一次，增加一次
     * @param username
     * @param ip
     * @return
     */
  public long incLoginFailedNum(String username,String ip);

    /**
     *用户登陆成功，将失败次数清零
     * @param passportId
     * @return
     */
  public boolean clearLoginFailedNum(String passportId);

    /**
     * 修改绑定手机
     *
     * @param account
     * @param newMobile
     * @return
     * @throws ServiceException
     */
  public boolean modifyMobile(Account account, String newMobile);

  /**
   * 解禁或封禁用户
   *
   * @param account
   * @param newState
   * @return
   * @throws ServiceException
   */

  public boolean updateState(Account account, int newState) throws ServiceException;

  /**
   *
   * @param username
   * @param ip
   * @return
   */
  public long incLoginSuccessNum(String username,String ip);

    /**判断用户是否在黑名单中,只要username或者ip有一个在黑名单中，即返回true
     *
     * @param username
     * @param ip
     * @return
     */
  public boolean checkUserInBlackList(String username,String ip);
  /*
   *检查验证码
   */
  public boolean checkCaptchaCode(String token, String captchaCode) throws Exception;
}
