package com.sogou.upd.passport.manager.account.impl;

import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountStatusEnum;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.parameter.PasswordTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.account.RegManager;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.manager.api.account.form.RegEmailApiParams;
import com.sogou.upd.passport.manager.form.ActiveEmailParameters;
import com.sogou.upd.passport.manager.form.MobileRegParams;
import com.sogou.upd.passport.manager.form.WebRegisterParameters;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.AccountTokenService;
import com.sogou.upd.passport.service.account.MobileCodeSenderService;
import com.sogou.upd.passport.service.account.MobilePassportMappingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 注册管理
 * User: mayan
 * Date: 13-4-15 Time: 下午4:43
 */
@Component
public class RegManagerImpl implements RegManager {

    @Autowired
    private AccountService accountService;
    @Autowired
    private MobileCodeSenderService mobileCodeSenderService;
    @Autowired
    private AccountTokenService accountTokenService;
    @Autowired
    private MobilePassportMappingService mobilePassportMappingService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private RegisterApiManager sgRegisterApiManager;

    private static final Logger logger = LoggerFactory.getLogger(RegManagerImpl.class);

  @Override
  public Result webRegister(WebRegisterParameters regParams, String ip) throws Exception {

    Result result = new APIResultSupport(false);
    try {
      int clientId = Integer.parseInt(regParams.getClient_id());
      String username = regParams.getUsername();
      String password = regParams.getPassword();

      //判断注册账号类型，sogou用户还是手机用户
      AccountDomainEnum emailType = AccountDomainEnum.getAccountDomain(username);

      switch (emailType) {
        case SOGOU://个性账号直接注册
        case OTHER://外域邮件注册
          String captchaCode = regParams.getCaptcha();
          String token = regParams.getToken();

          result = sgRegisterApiManager.regMailUser(new RegEmailApiParams(username,password , ip, clientId,captchaCode,token));
          return result;
        case PHONE://手机号
//          result=sgRegisterApiManager.regMobileCaptchaUser();
          break;
      }


    } catch (ServiceException e) {
      logger.error("webRegister fail,passportId:" + regParams.getUsername(), e);
      result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_REGISTER_FAILED);
      return result;
    }
    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_REGISTER_FAILED);
    return result;
  }

    @Override
    public Result mobileRegister(MobileRegParams regParams, String ip) {
        String mobile = regParams.getMobile();
        String smsCode = regParams.getSmscode();
        String password = regParams.getPassword();
        int clientId = Integer.parseInt(regParams.getClient_id());
        String instanceId = regParams.getInstance_id();
        int pwdType = regParams.getPwd_type();
        boolean needMD5 = pwdType == PasswordTypeEnum.Plaintext.getValue() ? true : false;

        Result result = new APIResultSupport(false);
        //验证手机号码与验证码是否匹配
        boolean checkSmsInfo = mobileCodeSenderService.checkSmsInfoFromCache(mobile, smsCode, clientId);
        if (!checkSmsInfo) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_NOT_MATCH_SMSCODE);
            return result;
        }
        Account account =
                accountService.initialAccount(mobile, password, needMD5, ip, AccountTypeEnum.PHONE.getValue());
        if (account != null) {  //     如果插入account表成功，则插入用户授权信息表
            boolean
                    isInitialMobilePassportMapping =
                    mobilePassportMappingService
                            .initialMobilePassportMapping(mobile, account.getPassportId());
            if (!isInitialMobilePassportMapping) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_REGISTER_FAILED);
                return result;
            }
            //生成token并向account_auth表里插一条用户状态记录
            AccountToken accountToken = accountTokenService.initialAccountToken(account.getPassportId(),
                    clientId, instanceId);
            if (accountToken != null) {   //如果用户授权信息表插入也成功，则说明注册成功
                //清除验证码的缓存
                mobileCodeSenderService.deleteSmsCache(mobile, clientId);
                String accessToken = accountToken.getAccessToken();
                long accessValidTime = accountToken.getAccessValidTime();
                String refreshToken = accountToken.getRefreshToken();
                Map<String, Object> mapResult = Maps.newHashMap();
                mapResult.put("access_token", accessToken);
                mapResult.put("expires_time", accessValidTime);
                mapResult.put("refresh_token", refreshToken);

                result.setSuccess(true);
                result.setMessage("注册成功！");
                result.setModels(mapResult);
                return result;
            } else {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_REGISTER_FAILED);
                return result;
            }
        } else {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_REGISTER_FAILED);
            return result;
        }
    }


    @Override
    public Result activeEmail(ActiveEmailParameters activeParams, String ip) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            String username = activeParams.getPassport_id();
            String token = activeParams.getToken();
            int clientId = Integer.parseInt(activeParams.getClient_id());
            //激活邮件
            boolean isSuccessActive = accountService.activeEmail(username, token, clientId);

            if (isSuccessActive) {
                //激活成功
                Account account = accountService.initialWebAccount(username, ip);
                if (account != null) {
                    //更新缓存
                    result.setSuccess(true);
                    result.setMessage("激活成功！");
                    return result;
                } else {
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
                    return result;
                }
            } else {
                //激活失败
                Account account = accountService.queryAccountByPassportId(username);
                if (account != null) {
                    if (account.getStatus() == AccountStatusEnum.REGULAR.getValue()) {
                        //已经激活，无需再次激活
                        result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_ALREADY_ACTIVED_FAILED);
                        return result;
                    } else {
                        result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_ACTIVED_URL_FAILED);
                        return result;
                    }
                } else {
                    //无此账号
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
                    return result;
                }
            }
        } catch (ServiceException e) {
            logger.error("activeEmail fail, passportId:" + activeParams.getPassport_id() + " clientId:" + activeParams.getClient_id(), e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    @Override
    public Map<String, Object> getCaptchaCode(String code) {
        return accountService.getCaptchaCode(code);
    }
}
