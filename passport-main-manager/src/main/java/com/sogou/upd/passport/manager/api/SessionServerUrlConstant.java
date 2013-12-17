package com.sogou.upd.passport.manager.api;

/**
 * User: mayan
 * Date: 13-12-4
 * Time: 下午9:11
 */
public class SessionServerUrlConstant {
    private static final String BASE_URL = "http://session.account.sogou.com.z.sogou-op.org/";
    public static final String CREATE_SESSION = BASE_URL + "set_session";
    public static final String REMOVE_SESSION = BASE_URL + "del_session";

}
