package com.sogou.upd.passport.manager.account.impl;

import com.google.common.collect.Maps;

import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.account.AccountRegManager;
import com.sogou.upd.passport.manager.form.ActiveEmailParameters;
import com.sogou.upd.passport.manager.form.MobileRegParams;
import com.sogou.upd.passport.manager.form.WebRegisterParameters;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.service.account.AccountTokenService;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.MobileCodeSenderService;

import com.sogou.upd.passport.service.account.MobilePassportMappingService;
import com.sogou.upd.passport.service.account.dataobject.ActiveEmailDO;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * User: mayan Date: 13-4-15 Time: 下午4:43 To change this template use File | Settings | File Templates.
 */
@Component
public class AccountRegManagerImpl implements AccountRegManager {

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

    private static final Logger logger = LoggerFactory.getLogger(AccountRegManagerImpl.class);



  @Override
    public Result mobileRegister(MobileRegParams regParams, String ip) {
        String mobile = regParams.getMobile();
        String smsCode = regParams.getSmscode();
        String password = regParams.getPassword();
        int clientId = regParams.getClient_id();
        String instanceId = regParams.getInstance_id();
        //验证手机号码与验证码是否匹配
        boolean checkSmsInfo = mobileCodeSenderService.checkSmsInfoFromCache(mobile, smsCode, clientId);
        if (!checkSmsInfo) {
            return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_NOT_MATCH_SMSCODE);
        }
        Account account = accountService.initialAccount(mobile, password, ip, AccountTypeEnum.PHONE.getValue());
        if (account != null) {  //     如果插入account表成功，则插入用户授权信息表
            boolean isInitialMobilePassportMapping = mobilePassportMappingService.initialMobilePassportMapping(mobile, account.getPassportId());
            if (!isInitialMobilePassportMapping) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_REGISTER_FAILED);
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
                return Result.buildSuccess("注册成功！", "mapResult", mapResult);
            } else {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_REGISTER_FAILED);
            }
        } else {
            return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_REGISTER_FAILED);
        }
    }

  @Override
  public Result webRegister(WebRegisterParameters regParams, String ip) throws Exception {
    Result result=null;
    try{
      int clientId = regParams.getClient_id();
      String username = regParams.getUsername();
      String password = regParams.getPassword();
      String code = regParams.getCode();


      //判断注册账号类型，sogou用户还是第三方用户
      int emailType=checkEmailType(username);

      //写缓存，发验证邮件
      switch (emailType){
        case 0://sougou用户，直接注册
//        accountService.initialAccount()
          break;
        case 1://外域邮件注册
          boolean isSendSuccess=accountService.sendActiveEmail(username,password,clientId,ip);
          if(isSendSuccess){
            return Result.buildSuccess("感谢注册，请立即激活账户！");
          }
          break;
      }
    }catch (ServiceException e){
         logger.error("webRegister error!",e);
         Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_REGISTER_FAILED);
    }
    return null;
  }

  @Override
  public boolean isInAccountBlackList(String passportId, String ip)
      throws Exception {
    return accountService.isInAccountBlackListByIp(passportId,ip);
  }

  @Override
  public Result activeEmail(ActiveEmailParameters activeParams) throws Exception {
    String username=activeParams.getPassport_id();
    String token=activeParams.getToken();
    int clientId=activeParams.getClient_id();
    //激活邮件
    boolean isSuccessActive=accountService.activeEmail(username,token,clientId);

    if(isSuccessActive) {
      //激活成功
      Account account=accountService.initialWebAccount(username);
      if(account !=null){
        return Result.buildSuccess("激活成功！");
      } else {
        return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
      }
    }else {
      //激活失败
      return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
    }
  }

  /*
   *判断注册账号类型，sogou用户还是第三方用户
   */
  private int checkEmailType(String username){
    //todo 搜狐域的不注册
    if (username.contains("@sogou.com")){
      return 0;
    } else{
      return 1;
    }
  }
}
