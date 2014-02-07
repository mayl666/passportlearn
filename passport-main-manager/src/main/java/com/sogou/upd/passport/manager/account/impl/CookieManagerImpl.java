package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
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
    public Result setCookie(HttpServletResponse response, CookieApiParams cookieApiParams, int maxAge) {
        Result result = new APIResultSupport(false);
        if (ManagerHelper.isUsedSohuProxyApiToGetCookie()) {  //使用sohu新提供的getcookieinfo接口
            result = proxyLoginApiManager.getCookieInfo(cookieApiParams);
        } else {       //使用之前的从location里拿的cookie的接口，为回滚做准备
            CreateCookieUrlApiParams createCookieUrlApiParams = new CreateCookieUrlApiParams();
            createCookieUrlApiParams.setUserid(cookieApiParams.getUserid());
            String ru = Strings.isNullOrEmpty(cookieApiParams.getRu()) ? LOGIN_INDEX_URL : cookieApiParams.getRu();
            createCookieUrlApiParams.setRu(ru);
            createCookieUrlApiParams.setDomain("sogou.com");
            result = proxyLoginApiManager.getCookieValue(createCookieUrlApiParams);
        }
        //获取cookie成功，种sogou域cookie
        if (result.isSuccess()) {
            String ppinf = (String) result.getModels().get("ppinf");
            String pprdig = (String) result.getModels().get("pprdig");
            ServletUtil.setCookie(response, "ppinf", ppinf, maxAge, CommonConstant.SOGOU_ROOT_DOMAIN);
            ServletUtil.setCookie(response, "pprdig", pprdig, maxAge, CommonConstant.SOGOU_ROOT_DOMAIN);
            result.setSuccess(true);
            result.setDefaultModel("userid", cookieApiParams.getUserid());
        }
        return result;
    }

    @Override
    public Result setSSOCookie(HttpServletResponse response, SSOCookieParams ssoCookieParams){
        Result result = new APIResultSupport(false);
        //验证code
        String sginf = ssoCookieParams.getSginf();
        String sgrdig = ssoCookieParams.getSgrdig();
        String cookieData[] = sginf.split("\\" + CommonConstant.SEPARATOR_1);
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
        int maxAge = getMaxAge(et);
        String domain = ssoCookieParams.getDomain();
        ServletUtil.setCookie(response, LoginConstant.COOKIE_SGINF, sginf, maxAge, domain);
        ServletUtil.setCookie(response, LoginConstant.COOKIE_SGRDIG, sgrdig, maxAge, domain);
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
