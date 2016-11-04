package com.sogou.upd.passport.manager.api.account.impl;

import com.google.common.base.Strings;

import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.form.AuthUserApiParams;
import com.sogou.upd.passport.manager.api.account.form.CookieApiParams;
import com.sogou.upd.passport.manager.api.account.form.CreateCookieUrlApiParams;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.TokenService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.sogou.upd.passport.common.parameter.AccountDomainEnum.THIRD;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-4-21
 * Time: 下午7:47
 * To change this template use File | Settings | File Templates.
 */
@Component("loginApiManager")
public class LoginApiManagerImpl extends BaseProxyManager implements LoginApiManager {

    private static final Logger logger = LoggerFactory.getLogger(LoginApiManagerImpl.class);
    private static final Logger sohuSpecialLogger= LoggerFactory.getLogger("sohuSpecialLogger");


    @Autowired
    private LoginApiManager sgLoginApiManager;
    @Autowired
    private LoginApiManager proxyLoginApiManager;
    @Autowired
    private CommonManager commonManager;
    @Autowired
    private AccountService accountService;
    @Autowired
    private TokenService tokenService;

    @Override
    public Result webAuthUser(AuthUserApiParams authUserApiParams) {
        Result result = new APIResultSupport(false);
        try {
            String userId = authUserApiParams.getUserid();
            
            // 用户名的所属域
            AccountDomainEnum accountDomainEnum = AccountDomainEnum.getAccountDomain(userId);
            if(THIRD.equals(accountDomainEnum) && !userId.matches(".+@qq\\.sohu\\.com$")) {   // 第三方登陆
                // 非 QQ 第三方账号不允许此操作
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_THIRD_NOTALLOWED);
                return result;
            }
            
            String passportId = commonManager.getPassportIdByUsername(userId);
            if (Strings.isNullOrEmpty(passportId)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_NOBIND);
                return result;
            }
            AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(passportId);
            //搜狐账号也在搜狗校验
            result = sgLoginApiManager.webAuthUser(authUserApiParams);
//            if(result.isSuccess()){
//                return result;
//            }

            //停止去搜狐校验
//            if((!result.isSuccess())&&(AccountDomainEnum.SOHU.equals(domain))) {
//                //停止新的搜狐账号登录,若存在，去搜狐校验，若不存在直接返回10009
//                Account account = accountService.queryAccountByPassportId(passportId);
//                if (null == account) {
//                    result.setCode(ErrorUtil.INVALID_ACCOUNT);
//                    return result;
//                }
//
//                String passwordStored=account.getPassword();
//                if(Strings.isNullOrEmpty(passwordStored) && ManagerHelper.authUserBySOHUSwitcher()){
//                    result = proxyLoginApiManager.webAuthUser(authUserApiParams);
//                    String pwdParam = authUserApiParams.getPassword();
//                    if(result.isSuccess()){
//                        accountService.updatePwd(passportId,account, pwdParam, false);
//                        sohuSpecialLogger.warn(passportId+"\t"+pwdParam);
//                    }
//                }
//
//            }


        } catch (Exception e) {
            logger.error("bothAuthUser Exception", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }

    @Override
    public Result appAuthToken(String token) {
        Result result = new APIResultSupport(false);
        try {
            String passportId = tokenService.getPassprotIdByWapToken(token);
            if (!Strings.isNullOrEmpty(passportId)) {
                result.setSuccess(true);
                result.setMessage("操作成功");
                result.setDefaultModel("userid", passportId);
                return result;
            }
            result.setCode(ErrorUtil.ERR_SIGNATURE_OR_TOKEN);
            return result;
        } catch (Exception e) {
            result.setSuccess(false);
            return result;
        }
    }

    @Override
    public Result buildCreateCookieUrl(CreateCookieUrlApiParams createCookieUrlApiParams, boolean isRuEncode, boolean isHttps) {
        return null;
    }

    @Override
    public Result getCookieInfo(CookieApiParams cookieApiParams) {
        return null;
    }

    @Override
    public Result getSGCookieInfoForAdapter(CookieApiParams cookieApiParams) {
        return null;
    }
}
