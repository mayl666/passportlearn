package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.common.model.httpclient.RequestModelXml;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.form.AppAuthTokenApiParams;
import com.sogou.upd.passport.manager.api.account.form.AuthUserApiParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 代理搜狐Passport的登录实现
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-6
 * Time: 上午10:36
 */
@Component("proxyLoginApiManager")
public class ProxyLoginApiManagerImpl extends BaseProxyManager implements LoginApiManager {

    private static Logger log = LoggerFactory.getLogger(ProxyLoginApiManagerImpl.class);

    @Override
    public Result webAuthUser(AuthUserApiParams authUserParameters) {
        Result result = new APIResultSupport(false);
        String userId = authUserParameters.getUserid();
        if (AccountDomainEnum.PHONE.equals(AccountDomainEnum.getAccountDomain(userId))) {
            authUserParameters.setUsertype(1);
        }
        authUserParameters.setPwdtype(1);
        try {
            RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.AUTH_USER, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
            requestModelXml.addParams(authUserParameters);
            result = executeResult(requestModelXml);
        } catch (Exception e) {
            log.error("web auth user Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }

    @Override
    public Result appAuthToken(AppAuthTokenApiParams appAuthTokenApiParams) {
        Result result = new APIResultSupport(false);
        appAuthTokenApiParams.setType(2);
        try {
            RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.MOBILE_AUTH_TOKEN, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
            requestModelXml.addParams(appAuthTokenApiParams);
            result = executeResult(requestModelXml, appAuthTokenApiParams.getToken());
        } catch (Exception e) {
            log.error("App auth openLogin token Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }

}