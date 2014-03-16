package com.sogou.upd.passport.manager.account.impl;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.ServletUtil;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.form.CookieApiParams;
import com.sogou.upd.passport.manager.api.account.form.CreateCookieUrlApiParams;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.oauth2.common.types.ConnectDomainEnum;
import com.sogou.upd.passport.service.account.AccountService;
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

    @Autowired
    private AccountService accountService;
    @Autowired
    private LoginApiManager sgLoginApiManager;
    @Autowired
    private LoginApiManager proxyLoginApiManager;
    @Autowired
    private OperateTimesService operateTimesService;
    @Autowired
    private AppConfigService appConfigService;

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
        Result getCookieValueResult = proxyLoginApiManager.getCookieInfo(cookieApiParams);
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
    public String buildCreateSSOCookieUrl(String domain,int client_id, String passportId,String uniqname,String refnick, String ru, String ip) {
        StringBuilder urlBuilder = new StringBuilder();
        String daohangDomain = ConnectDomainEnum.DAOHANG.toString();
        String haoDomain = ConnectDomainEnum.HAO.toString();

        String shurufaDomain=ConnectDomainEnum.SHURUFA.toString();

        if (domain.equals(daohangDomain)) {
            urlBuilder.append(CommonConstant.DAOHANG_CREATE_COOKIE_URL).append("?domain=").append(daohangDomain);
        } else if (domain.equals(haoDomain)) {
            urlBuilder.append(CommonConstant.HAO_CREATE_COOKIE_URL).append("?domain=").append(haoDomain);
        }
        ///////////这块需要修改成统一的， 先加上，以后改。  add by denghua/////////////
        else if (domain.equals(shurufaDomain)) {
            urlBuilder.append(CommonConstant.SHURUFA_CREATE_COOKIE_URL).append("?domain=").append(shurufaDomain);
        }
        ////////////////////// add by denghua end///////////////

        else {
            return null;
        }

        CookieApiParams cookieApiParams = new CookieApiParams(passportId, client_id, ru,ip, uniqname, refnick);
        Result getCookieValueResult = sgLoginApiManager.getCookieInfo(cookieApiParams);
        if (!getCookieValueResult.isSuccess()) {
            return null;
        }
        String sginf = (String) getCookieValueResult.getModels().get("sginf");
        String sgrdig = (String) getCookieValueResult.getModels().get("sgrdig");

        String cookieData[] = sginf.split("\\" + CommonConstant.SEPARATOR_1);
        String createtime = cookieData[1];
        long ct = new Long(createtime);
        String code1 = getCode(sginf, CommonConstant.SGPP_DEFAULT_CLIENTID, ct);
        String code2 = getCode(sgrdig, CommonConstant.SGPP_DEFAULT_CLIENTID, ct);
        urlBuilder.append("&sginf=").append(sginf)
                .append("&sgrdig=").append(sgrdig)
                .append("&code1=").append(code1)
                .append("&code2=").append(code2)
                .append("&ru=").append(Coder.encodeUTF8(ru));
        return urlBuilder.toString();
    }

    @Override
    public boolean isCodeRight(String firstStr, int clientId, long ct, String originalCode) {
        String code = getCode(firstStr.toString(), clientId, ct);
        boolean isCodeEqual = code.equalsIgnoreCase(originalCode);
        return isCodeEqual;
    }

    @Override
    public boolean isMillCtValid(long ct){
        long currentTime = System.currentTimeMillis();
        boolean timeRight = ct > currentTime - CommonConstant.COOKIE_REQUEST_VAILD_TERM_IN_MILLI;
        return timeRight;
    }

    @Override
    public boolean isSecCtValid(long ct){
        long currentTime = System.currentTimeMillis()/1000;
        boolean timeRight = ct > currentTime - CommonConstant.COOKIE_REQUEST_VAILD_TERM;
        return timeRight;
    }

    @Override
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
