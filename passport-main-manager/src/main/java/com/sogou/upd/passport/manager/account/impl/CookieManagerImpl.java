package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.LoginConstant;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.DateUtil;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.ServletUtil;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.account.CookieManager;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.form.CookieApiParams;
import com.sogou.upd.passport.manager.api.account.form.CreateCookieUrlApiParams;
import com.sogou.upd.passport.manager.form.PPCookieParams;
import com.sogou.upd.passport.manager.form.SSOCookieParams;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.app.AppConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-10-16
 * Time: 上午11:58
 * To change this template use File | Settings | File Templates.
 */
@Component
public class CookieManagerImpl implements CookieManager {

    private String PPINF = "2|1381822290|1383031890|bG9naW5pZDowOnx1c2VyaWQ6MjU6c2dwcm9jZXNzdGVzdDAxQHNvZ291LmNvbXxzZXJ2aWNldXNlOjIwOjAwMTAwMDAwMDAwMDAwMDAwMDAwfGNydDoxMDoyMDEzLTEwLTE1fGVtdDoxOjB8YXBwaWQ6NDoxMTIwfHRydXN0OjE6MXxwYXJ0bmVyaWQ6MTowfHJlbGF0aW9uOjA6fHV1aWQ6MTY6NTNkNmUwNzFmYTk5NDdic3x1aWQ6MTY6NTNkNmUwNzFmYTk5NDdic3x1bmlxbmFtZTowOnw";
    private String PPRDIG = "kOdlRIxptgVY2ZRcjKYchIY9JBWkqGM_MjQy11OTC0fB9ACztDrs0lnjzAgHenjfCb8_Bgc6RAxO15TIaR5DeJTTQQUGiz-afCzDU9dYUUfge_WJLfiXjfR7iEGBDlBIQ5eSTV0oyMdLQHE-8UlVLYjbohSOzVVUPqRfoDvLE0w";

    private static final String LOGIN_INDEX_URL = "https://account.sogou.com";
    private static final int SG_COOKIE_MIN_LEN = 3;

    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private CommonManager commonManager;
    @Autowired
    private LoginApiManager proxyLoginApiManager;

    @Override
    public AppConfig queryAppConfigByClientId(int clientId) {
        AppConfig appConfig = appConfigService.queryAppConfigByClientId(clientId);
        return appConfig;
    }

    @Override
    public Result setSSOCookie(HttpServletResponse response, SSOCookieParams ssoCookieParams){
        Result result = new APIResultSupport(false);
        //验证code
        String sginf = ssoCookieParams.getSginf();
        String sgrdig = ssoCookieParams.getSgrdig();
        String cookieData[] = sginf.split("\\" + CommonConstant.SEPARATOR_1);
        if(cookieData.length < SG_COOKIE_MIN_LEN){
            result.setCode(ErrorUtil.ERR_CODE_ERROR_COOKIE);
            result.setMessage(ErrorUtil.getERR_CODE_MSG(ErrorUtil.ERR_CODE_ERROR_COOKIE));
            return result;
        }

        String createtime = cookieData[1];
        String expiretime = cookieData[2];
        long ct = new Long(createtime);
        long et = new Long(expiretime);
        boolean code1Res = commonManager.isCodeRight(sginf, CommonConstant.SGPP_DEFAULT_CLIENTID, ct, ssoCookieParams.getCode1());
        if (!code1Res) {
            result.setCode(ErrorUtil.INTERNAL_REQUEST_INVALID);
            result.setMessage(ErrorUtil.getERR_CODE_MSG(ErrorUtil.INTERNAL_REQUEST_INVALID));
            return result;
        }
        boolean code2Res = commonManager.isCodeRight(sgrdig, CommonConstant.SGPP_DEFAULT_CLIENTID, ct, ssoCookieParams.getCode2());
        if (!code2Res) {
            result.setCode(ErrorUtil.INTERNAL_REQUEST_INVALID);
            result.setMessage(ErrorUtil.getERR_CODE_MSG(ErrorUtil.INTERNAL_REQUEST_INVALID));
            return result;
        }
        boolean isCtValid = commonManager.isSecCtValid(ct);
        if (!isCtValid) {
            result.setCode(ErrorUtil.INTERNAL_REQUEST_INVALID);
            result.setMessage(ErrorUtil.getERR_CODE_MSG(ErrorUtil.INTERNAL_REQUEST_INVALID));
            return result;
        }

        int maxAge = getMaxAge(et);
        String domain = ssoCookieParams.getDomain();
        ServletUtil.setCookie(response, LoginConstant.COOKIE_SGINF, sginf, maxAge, domain);
        ServletUtil.setCookie(response, LoginConstant.COOKIE_SGRDIG, sgrdig, maxAge, domain);
        result.setSuccess(true);
        return result;
    }

    @Override
    public Result setPPCookie(HttpServletResponse response, PPCookieParams ppCookieParams){
        Result result = new APIResultSupport(false);
        //验证code
        String ppinf = ppCookieParams.getPpinf();
        String pprdig = ppCookieParams.getPprdig();
        String passport = ppCookieParams.getPassport();
        long ct =Long.parseLong(ppCookieParams.getS());

        boolean code1Res = commonManager.isCodeRight(ppinf, CommonConstant.PC_CLIENTID, ct, ppCookieParams.getCode1());
        if (!code1Res) {
            result.setCode(ErrorUtil.INTERNAL_REQUEST_INVALID);
            result.setMessage(ErrorUtil.getERR_CODE_MSG(ErrorUtil.INTERNAL_REQUEST_INVALID));
            return result;
        }
        boolean code2Res = commonManager.isCodeRight(pprdig, CommonConstant.PC_CLIENTID, ct, ppCookieParams.getCode2());
        if (!code2Res) {
            result.setCode(ErrorUtil.INTERNAL_REQUEST_INVALID);
            result.setMessage(ErrorUtil.getERR_CODE_MSG(ErrorUtil.INTERNAL_REQUEST_INVALID));
            return result;
        }
        boolean codeRes = commonManager.isCodeRight(passport, CommonConstant.PC_CLIENTID, ct, ppCookieParams.getCode());
        if (!codeRes) {
            result.setCode(ErrorUtil.INTERNAL_REQUEST_INVALID);
            result.setMessage(ErrorUtil.getERR_CODE_MSG(ErrorUtil.INTERNAL_REQUEST_INVALID));
            return result;
        }
        boolean isCtValid = commonManager.isMillCtValid(ct);
        if (!isCtValid) {
            result.setCode(ErrorUtil.INTERNAL_REQUEST_INVALID);
            result.setMessage(ErrorUtil.getERR_CODE_MSG(ErrorUtil.INTERNAL_REQUEST_INVALID));
            return result;
        }

        if(!"0".equals(ppCookieParams.getLivetime())){
            int maxAge = (int)DateAndNumTimesConstant.TWO_WEEKS;
            long expire = DateUtil.generatorVaildTime(maxAge)/1000;
//            ServletUtil.setCookie(response, LoginConstant.COOKIE_PPINF, ppinf, maxAge, CommonConstant.SOGOU_ROOT_DOMAIN);
            ServletUtil.setExpireCookie(response,LoginConstant.COOKIE_PPINF, ppinf,CommonConstant.SOGOU_ROOT_DOMAIN,expire);
            ServletUtil.setHttpOnlyCookie(response, LoginConstant.COOKIE_PPRDIG, pprdig, CommonConstant.SOGOU_ROOT_DOMAIN,expire);
            ServletUtil.setHttpOnlyCookie(response, LoginConstant.COOKIE_PASSPORT, passport, CommonConstant.SOGOU_ROOT_DOMAIN,expire);
        }else {
            int maxAge = -1;
            ServletUtil.setCookie(response, LoginConstant.COOKIE_PPINF, ppinf, maxAge, CommonConstant.SOGOU_ROOT_DOMAIN);
            ServletUtil.setHttpOnlyCookie(response, LoginConstant.COOKIE_PPRDIG, pprdig, CommonConstant.SOGOU_ROOT_DOMAIN);
            ServletUtil.setHttpOnlyCookie(response, LoginConstant.COOKIE_PASSPORT, passport, CommonConstant.SOGOU_ROOT_DOMAIN);
        }

        result.setSuccess(true);
        return result;
    }

    //获取cookie有效期
    private int getMaxAge(long et) {
        int maxAge = -1;
        if (et > 0) {
            long currentTime = System.currentTimeMillis() / 1000;
            maxAge = DateUtil.getIntervalSec(et, currentTime);
            if (maxAge == 0) {
                maxAge = -1;
            }
        }
        return maxAge;
    }
}
