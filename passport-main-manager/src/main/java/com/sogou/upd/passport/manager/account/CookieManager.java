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
     * 种sogou域cookie接口
     *
     * @param response
     * @param cookieApiParams  获取cookie值必须传递的参数
     * @param persistentcookie //是否自动登录（0：否 1：是）或是否使用持久cookie 0:session级别的cookie 1:长时间有效的cookie，目前是两天
     * @return
     */
    public Result setCookie(HttpServletResponse response, CookieApiParams cookieApiParams, int persistentcookie);

    /**
     * 通过参数种cookie;
     *
     * @param response
     * @param passportId
     * @param client_id
     * @param ip
     * @param ru
     * @param maxAge
     * @return
     */
    public Result setCookie(HttpServletResponse response, String passportId, int client_id, String ip, String ru, int maxAge);

    /**
     * 生成设置sso cookie的url
     *
     * @param domain
     * @param client_id
     * @param passportId
     * @param uniqname
     * @param refnick
     * @param ru
     * @param ip
     * @return
     */
    public String buildCreateSSOCookieUrl(String domain, int client_id, String passportId, String uniqname, String refnick, String ru, String ip);

    /**
     * 种SSO cookie
     *
     * @param response
     * @param ssoCookieParams
     * @return
     */
    public Result setSSOCookie(HttpServletResponse response, SSOCookieParams ssoCookieParams);

    /**
     * @param response
     * @param ppCookieParams
     * @return
     */
    public Result setPPCookie(HttpServletResponse response, PPCookieParams ppCookieParams);


    /**
     * 清除cookie
     *
     * @param response
     */
    public void clearCookie(HttpServletResponse response);

}