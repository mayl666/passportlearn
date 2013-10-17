package com.sogou.upd.passport.manager.account.impl;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ServletUtil;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.account.CookieManager;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.form.CookieApiParams;
import com.sogou.upd.passport.manager.api.account.form.CreateCookieUrlApiParams;
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

    @Autowired
    private AppConfigService appConfigService;
//    @Autowired
//    private CookieApiManager proxyCookieApiManager;
    @Autowired
    private LoginApiManager proxyLoginApiManager;

    @Override
    public AppConfig queryAppConfigByClientId(int clientId) {
        AppConfig appConfig = appConfigService.queryAppConfigByClientId(clientId);
        return appConfig;
    }

    @Override
    public Result setCookie(HttpServletResponse response, Result result, CookieApiParams cookieApiParams, int maxAge) {
        String userid = cookieApiParams.getUserid();
        if (ManagerHelper.isInvokeProxyApi(userid)) {
            CreateCookieUrlApiParams createCookieUrlApiParams = new CreateCookieUrlApiParams();
            createCookieUrlApiParams.setUserid(cookieApiParams.getUserid());
            createCookieUrlApiParams.setRu(cookieApiParams.getRu());
            createCookieUrlApiParams.setDomain("sogou.com");
            //从location里拿cookie
            result = proxyLoginApiManager.getCookieValue(createCookieUrlApiParams);
        } else {
            //todo nginx module升级完成后，sogou自己生成cookie，从sohu获取cookie值，依sohu最终提供的接口
//            result = proxyCookieApiManager.getCookieValue(cookieApiParams);
            result.setSuccess(true);
        }
        //获取cookie成功，种sogou域cookie
        if (result.isSuccess()) {
            String ppinf;
            String pprdig;
            if (ManagerHelper.isInvokeProxyApi(userid)) {
                ppinf = (String) result.getModels().get("ppinf");
                pprdig = (String) result.getModels().get("pprdig");
            } else {
                ppinf = PPINF;
                pprdig = PPRDIG;
            }
            ServletUtil.setCookie(response, "ppinf", ppinf, maxAge);
            ServletUtil.setCookie(response, "pprdig", pprdig, maxAge);
            response.addHeader("Sohupp-Cookie", "ppinf,pprdig");
            result.setSuccess(true);
            result.setMessage("种cookie成功");
            result.setDefaultModel("userid", cookieApiParams.getUserid());
            result.setCode("0");
        }
        return result;
    }
}
