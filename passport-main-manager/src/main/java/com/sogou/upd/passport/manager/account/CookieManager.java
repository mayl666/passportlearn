package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.form.CookieApiParams;
import com.sogou.upd.passport.manager.form.PPCookieParams;
import com.sogou.upd.passport.manager.form.SSOCookieParams;
import com.sogou.upd.passport.model.app.AppConfig;

import javax.servlet.http.HttpServletResponse;

/**
 * 种sogou域cookie
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-10-16
 * Time: 上午11:56
 * To change this template use File | Settings | File Templates.
 */
public interface CookieManager {

    /**
     * 根据clientId获取AppConfig对象类
     *
     * @param clientId
     * @return
     */
    public AppConfig queryAppConfigByClientId(int clientId);

    /**
     * qq导航、qq输入法需要在qq的三级域名下种搜狗cookie
     * 这里生成的cookie是sogou新cookie，sginf、sgrdig
     * @param response
     * @param ssoCookieParams
     * @return
     */
    public Result setSSOCookie(HttpServletResponse response, SSOCookieParams ssoCookieParams);

    /**
     * 调用/sso/setppcookie接口的manager方法
     * /authtoken接口成功后302到/sso/setppcookie
     * 浏览器客户端拦截302请求并获取cookie值
     * @param response
     * @param ppCookieParams
     * @return
     */
    public Result setPPCookie(HttpServletResponse response, PPCookieParams ppCookieParams);

}
