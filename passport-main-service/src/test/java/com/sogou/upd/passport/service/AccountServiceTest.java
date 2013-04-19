package com.sogou.upd.passport.service;

import com.sogou.upd.passport.common.exception.SystemException;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.generator.PassportIDGenerator;

import org.apache.commons.collections.MapUtils;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import javax.inject.Inject;

import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: liuling Date: 13-4-7 Time: 下午4:09 To change this template use
 * File | Settings | File Templates.
 */
@ContextConfiguration(locations = "classpath:spring-config-test.xml")
public class AccountServiceTest extends AbstractJUnit4SpringContextTests {

  @Inject
  private AccountService accountService;

  private static final String MOBILE = "13545210241";
  private static final String PASSWORD = "liuling8";
  private static final String SMSCODE = "13267";
  private static final String CLIENT_ID_STRING = "1001";
  private static final int CLIENT_ID_INT = 1001;
  private static final
  String
      PASSPORT_ID =
      PassportIDGenerator.generator(MOBILE, AccountTypeEnum.PHONE.getValue());
  private static final String IP = "127.0.0.1";
  private static final String CACHE_KEY = MOBILE + "_" + CLIENT_ID_STRING;
  private static final int PROVIDER = AccountTypeEnum.PHONE.getValue();
  private static final long USER_ID = 88;

  /**
   * 测试验证手机号码与验证码是否匹配
   */
  @Test
  public void testCheckSmsInfoFromCache() {
    boolean flag = accountService.checkSmsInfoFromCache(MOBILE, SMSCODE, CLIENT_ID_STRING);
    if (flag) {
      System.out.println("匹配...");
    } else {
      System.out.println("不匹配!!!");
    }
  }


  /**
   * 测试缓存中是否有此号码
   */
  @Test
  public void testCheckCacheKeyIsExist() {
    boolean flag = accountService.checkCacheKeyIsExist(CACHE_KEY);
    if (flag) {
      System.out.println("存在...");
    } else {
      System.out.println("不存在!!!");
    }
  }

  /**
   * 测试重发验证码时更新缓存状态
   */
  @Test
  public void testUpdateSmsInfoByCacheKeyAndClientid() {
    Map<String, Object> mapResult = null;
//                accountService.updateSmsInfoByCacheKeyAndClientid(CACHE_KEY, CLIENT_ID_INT);
    if (MapUtils.isNotEmpty(mapResult)) {
      System.out.println(mapResult.size());
    } else {
      System.out.println(ErrorUtil.buildError(ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE_SEND));
    }
  }

  /**
   * 测试手机验证码的获取与重发
   */
  @Test
  public void testHandleSendSms() {
    Map<String, Object> mapResult = null;
//        accountService.handleSendSms(MOBILE, CLIENT_ID_INT);
    if (MapUtils.isNotEmpty(mapResult)) {
      System.out.println(mapResult.size());
    } else {
      System.out.println(ErrorUtil.buildError(ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE_SEND));
    }
  }

  /**
   * 测试初始化非第三方用户账号
   */
  @Test
  public void testInitialAccount() throws Exception {
    Account account = accountService.initialAccount(MOBILE, PASSWORD, IP, PROVIDER);
    if (account != null) {
      System.out.println("插入account表成功...");
    } else {
      System.out.println("插入account表不成功!!!");
    }
  }

  /**
   * 测试根据用户名获取Account对象
   */
  @Test
  public void testGetAccountByUserName() {
    Account account = accountService.getAccountByUserName(MOBILE);
    if (account == null) {
      System.out.println("获取不成功!!!");
    } else {
      System.out.println("获取成功..");
    }
  }

  /**
   * 测试验证账号的有效性，是否为正常用户
   */
  @Test
  public void testVerifyAccountVaild() {
    Account account = accountService.verifyAccountVaild(USER_ID);
    if (account != null) {
      System.out.println("用户存在...");
    } else {
      System.out.println("用户不存在!!!");
    }
  }

  /**
   * 测试验证用户名密码是否正确
   */
  @Test
  public void testVerifyUserPwdVaild() {
    Account account = accountService.verifyUserPwdVaild(MOBILE, PASSWORD);
    if (account != null) {
      System.out.println("正确...");
    } else {
      System.out.println("不正确!!!");
    }
  }



  /**
   * 测试根据主键ID获取passportId
   */
  @Test
  public void testGetUserIdByPassportId() {

    long userId = accountService.getUserIdByPassportId("18604165373@sohu.com");
    System.out.println(userId);
  }

  /**
   * 测试注册成功后清除sms缓存信息
   */
  @Test
  public void testDeleteSmsCache() {
    boolean flag = accountService.deleteSmsCache(MOBILE, CLIENT_ID_STRING);
    if (flag) {
      System.out.println("清除成功...");
    } else {
      System.out.println("清除失败!!!");
    }
  }

  /**
   * 测试重置密码
   */
  @Test
  public void testResetPassword() throws SystemException {
    Account flag = accountService.resetPassword(MOBILE, PASSWORD);
    if (flag != null) {
      System.out.println("重置成功...");
    } else {
      System.out.println("重置失败!!!");
    }
  }
}
