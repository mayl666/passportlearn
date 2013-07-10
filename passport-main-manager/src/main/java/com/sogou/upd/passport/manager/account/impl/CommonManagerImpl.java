package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.form.CreateCookieUrlApiParams;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.MobilePassportMappingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-27
 * Time: 下午8:36
 * To change this template use File | Settings | File Templates.
 */
@Component
public class CommonManagerImpl implements CommonManager {

    private static Logger log = LoggerFactory.getLogger(CommonManagerImpl.class);
    private static final String COOKIE_URL_RUSTR = "://account.sogou.com/static/api/ru.htm";


    @Autowired
    private AccountService accountService;
    @Autowired
    private MobilePassportMappingService mobilePassportMappingService;
    @Autowired
    private LoginApiManager proxyLoginApiManager;


    @Override
    public boolean isAccountExists(String username) throws Exception {
        try {
            if (PhoneUtil.verifyPhoneNumberFormat(username)) {
                String passportId = mobilePassportMappingService.queryPassportIdByMobile(username);
                if (!Strings.isNullOrEmpty(passportId)) {
                    return true;
                }
            } else {
                Account account = accountService.queryAccountByPassportId(username);
                if (account != null) {
                    return true;
                }
            }
        } catch (ServiceException e) {
            log.error("Check account is exists Exception, username:" + username, e);
            throw new Exception(e);
        }
        return false;
    }

    @Override
    public String getPassportIdByUsername(String username) throws Exception {
        try {
            if (PhoneUtil.verifyPhoneNumberFormat(username)) {
                /*String passportId = mobilePassportMappingService.queryPassportIdByMobile(username);
                if (!Strings.isNullOrEmpty(passportId)) {
                    return passportId;
                }*/
                return username + "@sohu.com";
            } else {
                // Account account = accountService.queryAccountByPassportId(username);
                // 不查询account表
                if (username.indexOf("@") == -1) {
                    username = username + "@sogou.com";
                }
                return username;
            }
        } catch (ServiceException e) {
            log.error("Username doesn't exist Exception, username:" + username, e);
            throw new Exception(e);
        }
    }

    @Override
    public Account queryAccountByPassportId(String passportId) throws Exception {
        return  accountService.queryAccountByPassportId(passportId);
    }

    @Override
    public boolean updateState(Account account, int newState)  throws Exception{
      return  accountService.updateState(account,newState);
    }

    @Override
    public boolean resetPassword(Account account, String password, boolean needMD5) throws Exception {
      return  accountService.resetPassword(account,password,needMD5);
    }

  @Override
  public Result createCookieUrl(Result result,String passportId,int autoLogin) {
    // 种sohu域cookie

    String scheme="https";

    CreateCookieUrlApiParams createCookieUrlApiParams = new CreateCookieUrlApiParams();
    //从返回结果中获取passportId,二期待优化
    String passportIdTmp =  passportId;
    if(ManagerHelper.isInvokeProxyApi(passportId)) {
      passportIdTmp =  result.getModels().get("userid").toString();
    } else{
      Account account =  (Account)result.getDefaultModel();
      passportIdTmp =  account.getPassportId();
    }
    createCookieUrlApiParams.setUserid(passportIdTmp);
    createCookieUrlApiParams.setRu(scheme + COOKIE_URL_RUSTR);
    createCookieUrlApiParams.setPersistentcookie(autoLogin);
    Result createCookieResult  = proxyLoginApiManager.buildCreateCookieUrl(createCookieUrlApiParams);
    if (createCookieResult.isSuccess()){
      result.setDefaultModel("cookieUrl",createCookieResult.getModels().get("url"));
    } else{
      result.setCode(ErrorUtil.ERR_CODE_CREATE_COOKIE_FAILED);
    }
    return result;
  }
}
