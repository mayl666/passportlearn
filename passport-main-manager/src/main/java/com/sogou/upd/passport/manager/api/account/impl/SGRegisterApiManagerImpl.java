package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.account.SecureManager;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.manager.api.account.form.BaseMoblieApiParams;
import com.sogou.upd.passport.manager.api.account.form.CheckUserApiParams;
import com.sogou.upd.passport.manager.api.account.form.RegEmailApiParams;
import com.sogou.upd.passport.manager.api.account.form.RegMobileCaptchaApiParams;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.AccountService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 注册
 * User: mayan
 * Date: 13-6-8
 * Time: 下午9:50
 */
@Component("sgRegisterApiManager")
public class SGRegisterApiManagerImpl implements RegisterApiManager {

    private static Logger logger = LoggerFactory.getLogger(SGRegisterApiManagerImpl.class);

    @Autowired
    private AccountService accountService;
    @Autowired
    private SecureManager secureManager;

    @Override
    public Result regMailUser(RegEmailApiParams params) {
      Result result = new APIResultSupport(false);
      try {
        String username=params.getUsername();
        String password=params.getPassword();
        String ip=params.getIp();
        int clientId=params.getClient_id();

        String captcha=params.getCaptcha();
        String token=params.getToken();

        //判断验证码
        if(!accountService.checkCaptchaCode(token,captcha)){
          result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_CODE_FAILED);
          return result;
        }

        //判断注册账号类型，外域用户还是个性用户
        AccountDomainEnum emailType = AccountDomainEnum.getAccountDomain(username);
        switch (emailType) {
          case SOGOU://个性账号直接注册
            Account account = accountService.initialAccount(username,password , false, ip, AccountTypeEnum
                .EMAIL.getValue());
            if (account != null) {
              result.setSuccess(true);
              result.setMessage("注册成功！");
            } else {
              result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_REGISTER_FAILED);
            }
            break;
          case OTHER://外域邮件注册
            boolean isSendSuccess = accountService.sendActiveEmail(username, password, clientId, ip);
            if (isSendSuccess) {
              result.setSuccess(true);
              result.setMessage("感谢注册，请立即激活账户！");
            } else {
              result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_REGISTER_FAILED);
            }
            break;
        }
        return result;
      } catch (Exception e) {
        logger.error("mail register account Fail:", e);
        result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
      }
      return result;
    }

    @Override
    public Result regMobileCaptchaUser(RegMobileCaptchaApiParams regMobileCaptchaApiParams) {
        Result result = new APIResultSupport(false);
        try {

        } catch (Exception e) {
            logger.error("mobile register phone account Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }

    @Override
    public Result checkUser(CheckUserApiParams checkUserApiParams) {
        return null;
    }

    @Override
    public Result sendMobileRegCaptcha(BaseMoblieApiParams params) {
      Result result = new APIResultSupport(false);
      try {
        result=secureManager.sendMobileCode(params.getMobile(),params.getClient_id());
      } catch (Exception e) {
        e.printStackTrace();
      }
      return result;
    }
}
