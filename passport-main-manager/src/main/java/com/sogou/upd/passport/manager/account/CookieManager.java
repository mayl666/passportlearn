package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.form.CookieApiParams;
import com.sogou.upd.passport.manager.form.PPCookieParams;
import com.sogou.upd.passport.manager.form.SSOCookieParams;

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
     * 种sg域cookie
     *
     * @param response
     * @param cookieApiParams
     * @param maxAge
     * @return
     */
    public Result setSGCookie(HttpServletResponse response, CookieApiParams cookieApiParams, int maxAge);



    /**
     * web端、桌面端生成cookie统一方法
     *
     * @param response
     * @param cookieApiParams
     * @return
     */
    public Result createCookie(HttpServletResponse response, CookieApiParams cookieApiParams);

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
