package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.form.CookieApiParams;
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
     * @param result
     * @param cookieApiParams  获取cookie值必须传递的参数
     * @param persistentcookie //是否自动登录（0：否 1：是）或是否使用持久cookie 0:session级别的cookie 1:长时间有效的cookie，目前是两天
     * @return
     */
    public Result setCookie(HttpServletResponse response, Result result, CookieApiParams cookieApiParams, int persistentcookie);

}
