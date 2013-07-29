package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.form.ActiveEmailParams;
import com.sogou.upd.passport.manager.form.MobileRegParams;
import com.sogou.upd.passport.manager.form.WebRegisterParams;

import java.util.Map;

/**
 * 注册管理
 * User: mayan
 * Date: 13-4-15 Time: 下午4:43
 */
public interface RegManager {

  /**
   * 手机用户正式注册接口
   *
   * @param regParams 参数封装的对象
   * @return Result格式的返回值，提示注册成功信息
   */
  public Result mobileRegister(MobileRegParams regParams, String ip);

  /**
   * 手机用户正式注册接口
   *
   * @param regParams 参数封装的对象
   * @return Result格式的返回值，提示注册成功信息
   */
  public Result webRegister(WebRegisterParams regParams, String ip) throws Exception;

  /**
   * 激活验证邮件
   *
   * @return Result格式的返回值, 成功或失败，返回提示信息
   */
  public Result activeEmail(ActiveEmailParams activeParams,String ip) throws Exception;
  /**
   * 获取验证码
   *
   * @return 验证码
   */
  public Map<String,Object> getCaptchaCode(String code);
  /**
   * 判断用户名是否被占用
   *
   * @return 验证码
   */
  public Result isAccountNotExists(String username,boolean type) throws Exception;
  /**
   *   检查一天内某ip注册次数
   */
  public Result checkRegInBlackList(String ip,String cookieStr) throws Exception;
  /**
   * 记录一天内某ip注册次数
   * @param ip
   * @return
   * @throws ServiceException
   */
  public void incRegTimes(String ip,String cookieStr) throws Exception;

}
