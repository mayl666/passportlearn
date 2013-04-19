package com.sogou.upd.passport.common;

/**
 * Cache key汇总
 * User: mayan
 * Date: 13-4-15
 * Time: 下午3:31
 * To change this template use File | Settings | File Templates.
 */
public class CacheConstant {
    public static final String CACHE_PREFIX_MOBILE_SMSCODE = "PASSPORT:MOBILE_SMSCODE_";   //mobile与smscode映射
    public static final String CACHE_PREFIX_MOBILE_SENDNUM = "PASSPORT:MOBILE_SENDNUM_";  // mobile与发送条数映射
    public static final String CACHE_PREFIX_PASSPORTID_USERID = "PASSPORT:PASSPORTID_USERID_"; //passportid与userId映射
    public static final String CACHE_PREFIX_CLIENTID_APPCONFIG = "PASSPORT:CLIENTID_APPCONFIG_";     //clientid与appConfig映射
    public static final String CACHE_PREFIX_USERID_OPENID = "PASSPORT:USERID_OPENID_";     //第三方openId与userID映射
}
