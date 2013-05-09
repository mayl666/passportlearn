package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.form.ActiveEmailParameters;
import com.sogou.upd.passport.manager.form.MobileRegParams;
import com.sogou.upd.passport.manager.form.WebRegisterParameters;

import org.apache.http.HttpRequest;

import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * 注册管理 User: mayan Date: 13-4-15 Time: 下午4:43 To change this template use File | Settings | File
 * Templates.
 */
public interface AccountRegManager {

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
  public Result webRegister(WebRegisterParameters regParams, String ip) throws Exception;

  /**
   * 检查注册用户是否在黑名单中，排除恶意注册
   *
   * @param ip 参数封装的对象
   * @return Result格式的返回值，提示注册成功信息
   */
  public boolean isInAccountBlackList(String passportId,String ip) throws Exception;

  /**
   * 激活验证邮件
   *
   * @return Result格式的返回值, 成功或失败，返回提示信息
   */
  public Result activeEmail(ActiveEmailParameters activeParams) throws Exception;
  /**
   * 获取验证码
   *
   * @return 验证码
   */
  public Map<String,Object> getCaptchaCode(String code);
}
