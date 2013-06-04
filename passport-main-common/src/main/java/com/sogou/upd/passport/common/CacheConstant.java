package com.sogou.upd.passport.common;

/**
 * Cache key汇总
 * User: mayan
 * Date: 13-4-15
 * Time: 下午3:31
 * To change this template use File | Settings | File Templates.
 */
public class CacheConstant {
    /*================ACCOUNT 相关缓存常量=====================*/
    public static final String CACHE_PREFIX_MOBILE_SMSCODE = "SP.MOBILE:SMSCODE_MAP_";   //mobile与smscode映射
    public static final String CACHE_PREFIX_MOBILE_SENDNUM = "SP.MOBILE:SENDNUM_MAP_";  // mobile与发送条数映射
    public static final String CACHE_PREFIX_PASSPORT_ACCOUNT = "SP.PASSPORTID:ACCOUNT_"; // passportId与account映射
    public static final String CACHE_PREFIX_MOBILE_PASSPORTID = "SP.MOBILE:PASSPORTID_";  // mobile与passportId映射
    public static final String CACHE_PREFIX_PASSPORT_ACCOUNTTOKEN = "SP.PASSPORTID:ACCOUNTTOKEN_"; // passportId:Map<client_id+instance_id, accountToken>
    public static final String CACHE_PREFIX_PASSPORTID_IPBLACKLIST = "SP.PASSPORTID:IPBLACKLIST_"; // passportId与ip blacklist映射
    public static final String CACHE_PREFIX_PASSPORTID_ACTIVEMAILTOKEN = "SP.PASSPORTID:ACTIVEMAILTOKEN_"; //passportId 与第三方邮件注册token有效期验证
    public static final String CACHE_PREFIX_PASSPORTID_RESETPWDEMAILTOKEN = "SP.PASSPORTID:RESETPWDEMAILTOKEN_"; //passportId与邮件重置密码token有效期验证
    public static final String CACHE_PREFIX_PASSPORTID_ACCOUNTINFO = "SP.PASSPORTID:ACCOUNTINFO_"; // passportId与accountInfo映射
    public static final String CACHE_PREFIX_MOBILE_CHECKSMSFAIL = "SP.MOBILE:CHECKSMSFAIL_"; // mobile与smscode错误检测次数映射
    public static final String CACHE_PREFIX_UUID_CAPTCHA = "SP.UUID:CAPTCHA_"; // 注册UUID与验证码映射
    public static final String CACHE_PREFIX_PASSPORTID_RESETPWDNUM = "SP.PASSPORTID:RESETPWDNUM_"; // passportId与当日修改密码次数映射
    public static final String CACHE_PREFIX_PASSPORTID_RESETPWDSENDEMAILNUM = "SP.PASSPORTID:RESETPWDSENDEMAILNUM_"; // passportId与当日重置密码邮件次数
    public static final String CACHE_PREFIX_PASSPORTID_LOGINFAILEDNUM = "SP.PASSPORTID:LOGINFAILEDNUM_"; //passportId连续登陆失败的次数
    public static final String CACHE_PREFIX_PASSPORTID_BINDEMAILTOKEN = "SP.PASSPORTID:BINDEMAILTOKEN_MAP_"; // passportId与绑定邮件token映射
    public static final String CACHE_PREFIX_PASSPORTID_BINDEMAILSENDNUM = "SP.PASSPORTID:BINDEMAILSENDNUM_"; // passportId与绑定邮件发送次数映射
    public static final String CACHE_PREFIX_PASSPORTID_RESETPWDSECURECODE = "SP.PASSPORTID:RESETPWDSECURECODE_"; // passportId与重置密码secureCode映射
    public static final String CACHE_PREFIX_PASSPORTID_MODSECINFOSECURECODE = "SP.PASSPORTID:MODSECINFOSECURECODE"; // passportId与修改密保内容secureCode映射
    public static final String CACHE_PREFIX_PASSPORTID_EMAILSCODE = "SP.PASSPORTID:EMAILSCODE_"; // passportId与email中scode的映射
    public static final String CACHE_PREFIX_PASSPORTID_SENDEMAILNUM = "SP.PASSPORTID:SENDEMAILNUM_"; // passportId与email发送次数的映射

  /*================CONNECT 相关缓存常量=====================*/
    public static final String CACHE_PREFIX_PASSPORTID_CONNECTTOKEN = "SP.PASSPORTID:CONNECTTOKEN_"; //passportId+provider+appKey与ConnectToken映射
    public static final String CACHE_PREFIX_OPENID_CONNECTRELATION = "SP.OPENID:CONNECTRELATION_";  // openid+provider与ConnectRelation的映射

    /*================CONFIG 相关缓存常量=====================*/
    public static final String CACHE_PREFIX_CLIENTID_APPCONFIG = "SP.CLIENTID:APPCONFIG_";     //clientid与appConfig映射
    public static final String CACHE_PREFIX_CLIENTID_CONNECTCONFIG = "SP.CLIENTID:CONNECTCONFIG_";     //clientid与connectConfig映射
}
