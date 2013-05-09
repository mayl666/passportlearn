package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.dataobject.ActiveEmailDO;

import java.util.Map;

/**
 * User: mayan Date: 13-3-22 Time: 下午3:38 To change this template use File | Settings | File
 * Templates.
 */
public interface AccountService {

  /**
   * 初始化web用户账号
   */
  public Account initialWebAccount(String username)
      throws ServiceException;

  /**
   * 初始化非第三方用户账号
   */
  public Account initialAccount(String username, String password, String ip, int provider)
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
   * 验证账号的有效性，是否为正常用户
   *
   * @return 验证不通过，则返回null
   */
  public Account verifyAccountVaild(String passportId) throws ServiceException;

  /**
   * 验证用户名密码是否正确
   *
   * @return 用户名或密码不匹配，则返回null
   */
  public Account verifyUserPwdVaild(String passportId, String password) throws ServiceException;

  /**
   * 根据passportId删除Account，内部debug接口使用
   */
  public boolean deleteAccountByPassportId(String passportId) throws ServiceException;

  /**
   * 重置密码
   */
  public boolean resetPassword(String passportId, String password) throws ServiceException;

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
  public boolean sendActiveEmail(String username,String passpord,int clientId,String ip) throws Exception;

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

}
