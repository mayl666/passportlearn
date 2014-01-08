package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.LoginConstant;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.common.utils.ServletUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.api.account.BindApiManager;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.form.CookieApiParams;
import com.sogou.upd.passport.manager.api.account.form.CreateCookieUrlApiParams;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.oauth2.common.types.ConnectDomainEnum;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.MobilePassportMappingService;
import com.sogou.upd.passport.service.account.OperateTimesService;
import com.sogou.upd.passport.service.app.AppConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

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
    private static final String COOKIE_URL_RU = "https://account.sogou.com/static/api/ru.htm";


    @Autowired
    private AccountService accountService;
    @Autowired
    private MobilePassportMappingService mobilePassportMappingService;
    @Autowired
    private LoginApiManager proxyLoginApiManager;
    @Autowired
    private OperateTimesService operateTimesService;
    @Autowired
    private AppConfigService appConfigService;

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
    public Account queryAccountByPassportId(String passportId) throws Exception {
        return accountService.queryAccountByPassportId(passportId);
    }

    @Override
    public boolean updateState(Account account, int newState) throws Exception {
        return accountService.updateState(account, newState);
    }

    @Override
    public boolean resetPassword(Account account, String password, boolean needMD5) throws Exception {
        return accountService.resetPassword(account, password, needMD5);
    }

    @Override
    public Result createCookieUrl(Result result, String passportId, String domain, int autoLogin) {
        // 种sohu域cookie

        String scheme = "https";

        CreateCookieUrlApiParams createCookieUrlApiParams = new CreateCookieUrlApiParams();
        //从返回结果中获取passportId,二期待优化
        String passportIdTmp = passportId;
        if (ManagerHelper.isInvokeProxyApi(passportId)) {
            passportIdTmp = result.getModels().get("userid").toString();
        } else {
            Account account = (Account) result.getDefaultModel();
            passportIdTmp = account.getPassportId();
            result.setDefaultModel("userid", passportIdTmp);
        }
        createCookieUrlApiParams.setUserid(passportIdTmp);
        createCookieUrlApiParams.setRu(scheme + COOKIE_URL_RUSTR);
        createCookieUrlApiParams.setPersistentcookie(autoLogin);
        createCookieUrlApiParams.setDomain(domain);
        Result createCookieResult = proxyLoginApiManager.buildCreateCookieUrl(createCookieUrlApiParams, true, true);
        if (createCookieResult.isSuccess()) {
            result.setDefaultModel("cookieUrl", createCookieResult.getModels().get("url"));
        } else {
            result.setCode(ErrorUtil.ERR_CODE_CREATE_COOKIE_FAILED);
        }
        return result;
    }

    @Override
    public Result createSohuCookieUrl(String passportId, String ru, int autoLogin) {
        Result result = new APIResultSupport(false);
        CreateCookieUrlApiParams createCookieUrlApiParams = new CreateCookieUrlApiParams();
        createCookieUrlApiParams.setUserid(passportId);
        createCookieUrlApiParams.setRu(ru);
        createCookieUrlApiParams.setDomain("");
        createCookieUrlApiParams.setPersistentcookie(autoLogin);
        Result createCookieResult = proxyLoginApiManager.buildCreateCookieUrl(createCookieUrlApiParams, true, true);
        if (createCookieResult.isSuccess()) {
            result.setDefaultModel("cookieUrl", createCookieResult.getModels().get("url"));
            result.setSuccess(true);
        } else {
            result.setCode(ErrorUtil.ERR_CODE_CREATE_COOKIE_FAILED);
        }
        return result;

    }

    @Override
    public boolean setSogouCookie(HttpServletResponse response, String passportId, int client_id, String ip, int maxAge, String ru) {
        CookieApiParams cookieApiParams = new CookieApiParams();
        cookieApiParams.setUserid(passportId);
        cookieApiParams.setClient_id(client_id);
        cookieApiParams.setRu(ru);
        cookieApiParams.setTrust(CookieApiParams.IS_ACTIVE);
        cookieApiParams.setPersistentcookie(String.valueOf(1));
        cookieApiParams.setIp(ip);
        Result getCookieValueResult = proxyLoginApiManager.getSHCookieValue(cookieApiParams);
        if (getCookieValueResult.isSuccess()) {
            String ppinf = (String) getCookieValueResult.getModels().get("ppinf");
            String pprdig = (String) getCookieValueResult.getModels().get("pprdig");
            ServletUtil.setCookie(response, "ppinf", ppinf, maxAge, CommonConstant.SOGOU_ROOT_DOMAIN);
            ServletUtil.setCookie(response, "pprdig", pprdig, maxAge, CommonConstant.SOGOU_ROOT_DOMAIN);
            response.addHeader("Sohupp-Cookie", "ppinf,pprdig");
            return true;
        }
        return false;
    }

    @Override
    public Result setCookie(HttpServletResponse response, String passportId, int client_id, String ip, int sogouMaxAge, String sogouRu, int sohuAutoLogin, String sohuRu) {
        Result result = new APIResultSupport(false);
        //种搜狗域cookie
        boolean setSogouCookieRes = setSogouCookie(response, passportId, client_id, ip, sogouMaxAge, sogouRu);
        if (!setSogouCookieRes) {
            result.setSuccess(false);
            result.setCode(ErrorUtil.ERR_CODE_CREATE_COOKIE_FAILED);
            result.setMessage("生成cookie失败");
            return result;
        }

        //todo 只有@sogou域 和 sohu矩阵域才种跨域cookie
        result = createSohuCookieUrl(passportId, sohuRu, sohuAutoLogin);
        return result;

    }

    @Override
    public void setSSOCookie(HttpServletResponse response, String sginf, String sgrdig, String domain, int maxAge) {
        ServletUtil.setCookie(response, LoginConstant.COOKIE_PPINF, sginf, maxAge, domain);
        ServletUtil.setCookie(response, LoginConstant.COOKIE_PPRDIG, sgrdig, maxAge, domain);
    }

    @Override
    public String buildCreateSSOCookieUrl(String domain, String passportId, String ru, String ip) {
        StringBuilder urlBuilder = new StringBuilder();
        if (domain.equals(ConnectDomainEnum.DAOHANG.toString())) {
            urlBuilder.append(CommonConstant.DAOHANG_CREATE_COOKIE_URL);
        } else if (domain.equals(ConnectDomainEnum.HAO.toString())) {
            urlBuilder.append(CommonConstant.HAO_CREATE_COOKIE_URL);
        } else {
            return null;
        }

        int client_id = CommonConstant.SGPP_DEFAULT_CLIENTID;
        CookieApiParams cookieApiParams = new CookieApiParams();
        cookieApiParams.setUserid(passportId);
        cookieApiParams.setClient_id(client_id);
        cookieApiParams.setRu(ru);
        cookieApiParams.setTrust(CookieApiParams.IS_ACTIVE);
        cookieApiParams.setPersistentcookie(String.valueOf(1));
        cookieApiParams.setIp(ip);
        Result getCookieValueResult = proxyLoginApiManager.getSHCookieValue(cookieApiParams);
        if (!getCookieValueResult.isSuccess()) {
            return null;
        }
        String ppinf = (String) getCookieValueResult.getModels().get("ppinf");
        String pprdig = (String) getCookieValueResult.getModels().get("pprdig");

        String cookieData[] = ppinf.split("\\" + CommonConstant.SEPARATOR_1);
        String createtime = cookieData[1];
        long ct = new Long(createtime);
        String code1 = getCode(ppinf, CommonConstant.SGPP_DEFAULT_CLIENTID, ct);
        String code2 = getCode(pprdig, CommonConstant.SGPP_DEFAULT_CLIENTID, ct);
        urlBuilder.append("?").append("sginf=").append(ppinf)
                .append("&sgrdig=").append(pprdig)
                .append("&code1=").append(code1)
                .append("&code2=").append(code2)
                .append("&ru=").append(Coder.encodeUTF8(ru));
        return urlBuilder.toString();
    }

    @Override
    public boolean isCodeRight(String firstStr, int clientId, long ct, String originalCode) {
        String code = getCode(firstStr.toString(), clientId, ct);
        long currentTime = System.currentTimeMillis()/1000;
        boolean isCodeEqual = code.equalsIgnoreCase(originalCode);
        boolean timeRight = ct > currentTime - CommonConstant.COOKIE_REQUEST_VAILD_TERM;
        if (isCodeEqual && timeRight) {
            return true;
        } else {
            return false;
        }
    }

    public String getCode(String firstStr, int clientId, long ct) {
        AppConfig appConfig = appConfigService.queryAppConfigByClientId(clientId);
        if (appConfig == null) {
            return null;
        }
        String secret = appConfig.getServerSecret();
        String code = ManagerHelper.generatorCode(firstStr.toString(), clientId, secret, ct);
        return code;
    }

    @Override
    public void incRegTimesForInternal(String ip) {
        operateTimesService.incRegTimesForInternal(ip);
    }

    @Override
    public void incRegTimes(String ip, String cookieStr) {
        operateTimesService.incRegTimes(ip, cookieStr);
    }
}
