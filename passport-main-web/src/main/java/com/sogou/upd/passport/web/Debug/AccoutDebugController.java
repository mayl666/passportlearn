package com.sogou.upd.passport.web.debug;

import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.AccountAuthService;
import com.sogou.upd.passport.service.account.AccountService;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;

/**
 * 账号相关的内部调试接口 User: shipengzhi Date: 13-4-1 Time: 下午5:30 To change this template use File |
 * Settings | File Templates.
 */
@Controller
public class AccoutDebugController {

  @Inject
  private AccountService accountService;
  @Inject
  private AccountAuthService accountAuthService;
  @Inject
  private RedisTemplate redisTemplate;

  /**
   * 手机账号获取，重发手机验证码接口
   */
  @RequestMapping(value = "/internal/debug/deleteAccount", method = RequestMethod.GET)
  @ResponseBody
  public Object deleteAccount(@RequestParam(defaultValue = "") String mobile) throws Exception {
    if (PhoneUtil.verifyPhoneNumberFormat(mobile)) {
      Account account = accountService.getAccountByUserName(mobile);
      if (account != null) {
        accountService.deleteAccountByPassportId(account.getPassportId());
        accountAuthService.deleteAccountAuthByUserId(account.getId());
        String cacheKey = "PASSPORT:ACCOUNT_PASSPORTID_" + mobile + "@sohu.com";
        redisTemplate.delete(cacheKey);
        return "delete success!";
      } else {
        return "accout is not exist, not require delete";
      }
    } else {
      return "is not phone number! ";
    }
  }
}
