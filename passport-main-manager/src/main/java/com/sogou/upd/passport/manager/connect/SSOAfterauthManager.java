package com.sogou.upd.passport.manager.connect;

import com.sogou.upd.passport.common.result.Result;

import javax.servlet.http.HttpServletRequest;

/**
 * User: mayan
 * Date: 14-3-3
 * Time: 下午5:19
 * To change this template use File | Settings | File Templates.
 */
public interface SSOAfterauthManager {

    public Result handleSSOAfterauth(HttpServletRequest req, String providerStr);
}
