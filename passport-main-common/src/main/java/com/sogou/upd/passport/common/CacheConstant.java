package com.sogou.upd.passport.common;

/**
 * Cache key汇总
 * User: mayan
 * Date: 13-4-15
 * Time: 下午3:31
 * To change this template use File | Settings | File Templates.
 */
public class CacheConstant {
    // TODO 名字需要改一下，改成mobile:smscode
    /*================ACCOUNT 相关缓存常量=====================*/
    public static final String CACHE_PREFIX_MOBILE_SMSCODE = "PASSPORT:MOBILE_SMSCODE_";   //mobile与smscode映射
    public static final String CACHE_PREFIX_MOBILE_SENDNUM = "PASSPORT:MOBILE_SENDNUM_";  // mobile与发送条数映射
    public static final String CACHE_PREFIX_PASSPORT_ACCOUNT = "PASSPORT:PASSPORTID_ACCOUNT_"; // passportId与account映射
    public static final String CACHE_PREFIX_MOBILE_PASSPORT = "PASSPORT:MOBILE_PASSPORT_";  // mobile与passportId映射
    public static final String CACHE_PREFIX_PASSPORT_ACCOUNTTOKEN = "PASSPORT:PASSPORTID_ACCOUNTTOKEN_"; // passportId与accountToken映射

    /*================CONNECT 相关缓存常量=====================*/
    public static final String CACHE_PREFIX_PASSPORTID_CONNECTTOKEN = "PASSPORT:PASSPORTID_CONNECTTOKEN_"; //passportId+provider+appKey与ConnectToken映射
    public static final String CACHE_PREFIX_OPENID_CONNECTRELATION = "PASSPORT:OPENID_CONNECTRELATION_";  // openid+provider与ConnectRelation的映射

    /*================CONFIG 相关缓存常量=====================*/
    public static final String CACHE_PREFIX_CLIENTID_APPCONFIG = "PASSPORT:CLIENTID_APPCONFIG_";     //clientid与appConfig映射
    public static final String CACHE_PREFIX_CLIENTID_CONNECTCONFIG = "PASSPORT:CLIENTID_CONNECTCONFIG_";     //clientid与connectConfig映射
}
