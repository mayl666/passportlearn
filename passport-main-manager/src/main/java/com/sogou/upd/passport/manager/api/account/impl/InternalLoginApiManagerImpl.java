package com.sogou.upd.passport.manager.api.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.model.httpclient.RequestModelXml;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.parameter.HttpMethodEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.HttpClientUtil;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.common.utils.SGHttpClient;
import com.sogou.upd.passport.manager.account.LoginManager;
import com.sogou.upd.passport.manager.account.SecureManager;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.account.InternalLoginApiManager;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.form.AppAuthTokenApiParams;
import com.sogou.upd.passport.manager.api.account.form.AuthUserApiParams;
import com.sogou.upd.passport.manager.api.account.form.CreateCookieApiParams;
import com.sogou.upd.passport.manager.api.account.form.CreateCookieUrlApiParams;
import com.sogou.upd.passport.service.account.OperateTimesService;
import org.apache.commons.httpclient.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

/**
 * 代理搜狐Passport的登录实现
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-6
 * Time: 上午10:36
 */
@Component
public class InternalLoginApiManagerImpl implements InternalLoginApiManager {

    private static Logger log = LoggerFactory.getLogger(InternalLoginApiManagerImpl.class);

    @Autowired
    private OperateTimesService operateTimesService;
    @Autowired
    private SecureManager secureManager;

    @Override
    public void doAfterAuthUserSuccess(final String username, final String ip,final String passportId, final int clientId) {
        //记录登陆次数
        operateTimesService.incAuthUserTimes(username, ip,true);
        //用户登陆记录
//        secureManager.logActionRecord(passportId, clientId, AccountModuleEnum.LOGIN, ip, null);
    }

    @Override
    public void doAfterAuthUserFailed(final String username, final String ip) {
        operateTimesService.incAuthUserTimes(username, ip,false);
    }

    @Override
    public boolean isAuthUserInBlackList(final String username, final String ip) {
        return operateTimesService.isWebAuthUserInBlackList(username,ip);
    }

    @Override
    public boolean isIPInBlackList(final String ip) {
        return operateTimesService.isWebAuthUserInBlackList("",ip);
    }


}
