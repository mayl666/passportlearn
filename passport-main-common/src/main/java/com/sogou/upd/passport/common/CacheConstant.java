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
    public static final String CACHE_PREFIX_SNAME_PASSPORTID = "SP.SNAME:PASSPORTID_";  // sname与passportId映射
    public static final String CACHE_PREFIX_PASSPORT_ACCOUNTTOKEN = "SP.PASSPORTID:ACCOUNTTOKEN_"; // passportId:Map<client_id+instance_id, accountToken>
    public static final String CACHE_PREFIX_PASSPORTID_IPBLACKLIST = "SP.PASSPORTID:IPBLACKLIST_"; // passportId与ip blacklist映射
    public static final String CACHE_PREFIX_REGISTER_IPBLACKLIST = "SP.REGISTER:IPBLACKLIST_SET_"; // ip与注册次数映射
    public static final String CACHE_PREFIX_REGISTER_CLIENTIDBLACKLIST = "SP.REGISTER:CLIENTIDBLACKLIST_SET_"; // ip与注册次数映射
    public static final String CACHE_PREFIX_REGISTER_COOKIEBLACKLIST = "SP.REGISTER:COOKIEBLACKLIST_"; // cookie与注册次数映射
    public static final String CACHE_PREFIX_PASSPORTID_ACTIVEMAILTOKEN = "SP.PASSPORTID:ACTIVEMAILTOKEN_"; //passportId 与第三方邮件注册token有效期验证
    public static final String CACHE_PREFIX_NICKNAME_PASSPORTID = "SP.NICKNAME:PASSPORTID_"; //昵称与passportId映射

    // public static final String CACHE_PREFIX_PASSPORTID_RESETPWDEMAILTOKEN = "SP.PASSPORTID:RESETPWDEMAILTOKEN_"; //passportId与邮件重置密码token有效期验证
    public static final String CACHE_PREFIX_PASSPORTID_ACCOUNTINFO = "SP.PASSPORTID:ACCOUNTINFO_"; // passportId与accountInfo映射
    public static final String CACHE_PREFIX_MOBILE_CHECKSMSFAIL = "SP.MOBILE:CHECKSMSFAIL_"; // mobile与smscode错误检测次数映射
    public static final String CACHE_PREFIX_UUID_CAPTCHA = "SP.UUID:CAPTCHA_"; // 注册UUID与验证码映射
    public static final String CACHE_PREFIX_PASSPORTID_RESETPWDNUM = "SP.PASSPORTID:RESETPWDNUM_"; // passportId与当日修改密码次数映射
    public static final String CACHE_PREFIX_PASSPORTID_CHECKPWDFAIL = "SP.PASSPORTID:CHECKPWDFAIL_"; // passportId与当日验证密码失败次数映射
    // public static final String CACHE_PREFIX_PASSPORTID_RESETPWDSENDEMAILNUM = "SP.PASSPORTID:RESETPWDSENDEMAILNUM_"; // passportId与当日重置密码邮件次数
    public static final String CACHE_PREFIX_USERNAME_LOGINFAILEDNUM = "SP.USERNAME:LOGINFAILEDNUM_"; //username连续登陆失败的次数
    public static final String CACHE_PREFIX_IP_LOGINFAILEDNUM = "SP.IP:LOGINFAILEDNUM_"; //IP连续登陆失败的次数
    public static final String CACHE_PREFIX_USERNAME_LOGINSUCCESSNUM = "SP.USERNAME:LOGINNUM_"; //username登陆成功的次数
    public static final String CACHE_PREFIX_IP_LOGINSUCCESSNUM = "SP.IP:LOGINNUM_"; //IP登陆成功的次数
    // public static final String CACHE_PREFIX_PASSPORTID_BINDEMAILTOKEN = "SP.PASSPORTID:BINDEMAILTOKEN_MAP_"; // passportId与绑定邮件token映射
    // public static final String CACHE_PREFIX_PASSPORTID_BINDEMAILSENDNUM = "SP.PASSPORTID:BINDEMAILSENDNUM_"; // passportId与绑定邮件发送次数映射
    public static final String CACHE_PREFIX_PASSPORTID_RESETPWDSECURECODE = "SP.PASSPORTID:RESETPWDSECURECODE_"; // passportId与重置密码secureCode映射
    public static final String CACHE_PREFIX_PASSPORTID_MODSECINFOSECURECODE = "SP.PASSPORTID:MODSECINFOSECURECODE"; // passportId与修改密保内容secureCode映射
    public static final String CACHE_PREFIX_PASSPORTID_EMAILSCODE = "SP.PASSPORTID:EMAILSCODE_"; // passportId与email中scode的映射
    public static final String CACHE_PREFIX_PASSPORTID_SENDEMAILNUM = "SP.PASSPORTID:SENDEMAILNUM_"; // passportId与email发送次数的映射
    public static final String CACHE_PREFIX_PASSPORTID_ACCOUNT_BASE_INFO = "SP.PASSPORTID:ACCOUNT_BASE_INFO_"; // passportId与sohu+头像昵称映射
    public static final String CACHE_PREFIX_MOBILE_SMSCODE_IPBLACKLIST = "SP.MOBILESMSCODE:IPBLACKLIST_SET_"; // ip与发送手机短信次数映射

    /*================账号黑名单、白名单缓存常量=====================*/
    public static final String CACHE_PREFIX_IP_UPDATEPWDNUM = "SP.IP:UPDATEPWDNUM_"; //IP的修改密码限制
    public static final String CACHE_PREFIX_IP_BINDNUM = "SP.IP:BINDNUM_"; //IP的设置密保限制
    // username或者ip在白名单中的set KEY
    public static final String CACHE_PREFIX_LOGIN_WHITELIST = "SP.USERNAME.IP:WHITELIST_KEY";

    // username或者ip在黑名单中的set KEY
    public static final String CACHE_PREFIX_LOGIN_BLACKLIST = "SP.USERNAME.IP:BLACKLIST_KEY";

    // 登陆在黑名单的前缀
    public static final String CACHE_PREFIX_LOGIN_USERNAME_BLACK_ = "SP.USERNAME:BLACK_";
    public static final String CACHE_PREFIX_LOGIN_IP_BLACK_ = "SP.IP:BLACK_";

    // 检查账号是否存在在黑名单的前缀
    public static final String CACHE_PREFIX_EXIST_USERNAME_BLACK_ = "SP.EXIST.USERNAME:BLACK_";
    public static final String CACHE_PREFIX_EXIST_IP_BLACK_ = "SP.EXIST.IP:BLACK_";

    // 检查账号是否存在在黑名单的前缀
    public static final String CACHE_PREFIX_GETPAIRTOKEN_USERNAME_BLACK_ = "SP.GETPAIRTOKEN.USERNAME:BLACK_";
    public static final String CACHE_PREFIX_GETPAIRTOKEN__IP_BLACK_ = "SP.GETPAIRTOKEN.IP:BLACK_";

    //IP网段黑名单
    public static final String CACHE_PREFIX_IP_SUBIPBLACKLIST = "SP.IP:SUBIPBLACKLIST_KEY";

    public static final String CACHE_PREFIX_PASSPORTID_AVATARURL_MAPPING = "SP.PASSPORTID:IMAGE_HASH_"; //passportId与新旧头像映射

    /*================绑定密保限制相关缓存常量=====================*/
    public static final String CACHE_PREFIX_PASSPORTID_BINDEMAILNUM = "SP.PASSPORTID:BINDEMAILNUM_"; // passportId与当日绑定密保邮箱次数
    public static final String CACHE_PREFIX_PASSPORTID_BINDMOBILENUM = "SP.PASSPORTID:BINDMOBILENUM_"; // passportId与当日绑定密保手机次数
    public static final String CACHE_PREFIX_PASSPORTID_BINDQUESNUM = "SP.PASSPORTID:BINDQUESNUM_"; // passportId与当日绑定密保问题次数
    public static final String CACHE_PREFIX_PASSPORTID_BINDNUM = "SP.PASSPORTID:BINDNUM_"; // passportId与当日设置密保次数

    /*================CONNECT 相关缓存常量=====================*/
    public static final String CACHE_PREFIX_PASSPORTID_CONNECTTOKEN = "SP.PASSPORTID:CONNECTTOKEN_"; //passportId+provider+appKey与ConnectToken映射
    public static final String CACHE_PREFIX_OPENID_CONNECTRELATION = "SP.OPENID:CONNECTRELATION_";  // openid+provider与ConnectRelation的映射
    public static final String CACHE_PREFIX_PASSPORTID_ACCESSTOKEN = "SP.PASSPORTID:ACCESSTOKEN_"; //passportId与AccessToken映射
    public static final String CACHE_PREFIX_PASSPORTID_CONNECTUSERINFO = "SP.PASSPORTID:CONNECTUSERINFO_"; //passportId与ConnectUserInfoVO映射


    /*================CONFIG 相关缓存常量=====================*/
    public static final String CACHE_PREFIX_CLIENTID_APPCONFIG = "SP.CLIENTID:APPCONFIG_";     //clientid与appConfig映射
    public static final String CACHE_PREFIX_CLIENTID_CONNECTCONFIG = "SP.CLIENTID:CONNECTCONFIG_";     //clientid与connectConfig映射

    /*================用户反馈相关缓存常量=====================*/
    public static final String CACHE_PREFIX_ID_PROBLEMTYPE = "SP.ID:PROBLEMTYPE_"; // Id与problem映射 CACHE_PREFIX_PASSPORTID_PROBLEM
    public static final String CACHE_PREFIX_PASSPORTID_PROBLEMLIST = "SP.PASSPORTID:PROBLEMLIST_"; // passortId与问题列表之前的映射
    public static final String CACHE_PREFIX_PROBLEM_PASSPORTIDINBLACKLIST = "SP.PASSPORTID_:ADDPROBLEMTIMES_"; // passortId与提交反馈次数之间的映射
    public static final String CACHE_PREFIX_PROBLEM_IPINBLACKLIST = "SP.IP_ADDPROBLEMTIMES:_"; // IP与提交反馈次数之间的映射


    /*==================token缓存常量=====================*/
    public static final String CACHE_PREFIX_SECURECODE = "SP.SECURECODE:SCODE_";

    /*==================proxy相关缓存常量=====================*/
    // TODO:迁移后，此常量将删除
    public static final String CACHE_PREFIX_MOBILE_SMSCODE_PROXY = "SP.MOBILE:PROXY_SMSCODE_";

    /*-----------------------------------------   KV系统   -----------------------------------------*/
    /*==================动作记录相关缓存常量=====================*/
    public static final String KV_PREFIX_PASSPORTID_ACTIONRECORD = "20002/action_records/SP.PASSPORTID:ACTIONRECORD_LIST_"; // passportId与操作列表的映射

    /*==================token相关缓存常量========================*/
    public static final String KV_PREFIX_PASSPORTID_TOKEN = "20002/account_token/";
    public static final String KV_PREFIX_TEST = "0/0/";

    /*==================核心kv 动作记录相关缓存常量=====================*/
    public static final String CORE_KV_PREFIX_PASSPORTID_ACTIONRECORD = "13008/action_records/SP.PASSPORTID:ACTIONRECORD_LIST_";
    /*==================核心kv token相关缓存常量========================*/
    public static final String CORE_KV_PREFIX_PASSPROTID_TOKEN = "13008/account_token/"; //kv迁移，核心kv集群


    /*==================登陆相关缓存常量========================*/
    public static final String CACHE_PREFIX_USERNAME_LOGINNUM = "SP.USERNAME:LOGINNUM_HASH_"; //username连续登陆的次数
    public static final String CACHE_PREFIX_IP_LOGINNUM = "SP.IP:LOGINNUM_HASH_"; //IP连续登陆的次数
    public static final String CACHE_SUCCESS_KEY = "SUCCESS"; //成功key
    public static final String CACHE_FAILED_KEY = "FAILED"; //失败key
    public static final String CACHE_PREFIX_USERNAME_EXISTNUM = "SP.USERNAME:EXISTNUM_"; //username连续登陆的次数
    public static final String CACHE_PREFIX_IP_EXISTNUM = "SP.IP:EXISTNUM_"; //IP连续登陆的次数
    public static final String CACHE_PREFIX_USERNAME_GETPAIRTOKENNUM = "SP.USERNAME:GETPAIRTOKENNUM_"; //username连续登陆的次数
    public static final String CACHE_PREFIX_IP_GETPAIRTOKENNUM = "SP.IP:GETPAIRTOKENNUM_"; //IP连续登陆的次数

    /*==================内部接口登陆相关缓存常量========================*/
    public static final String CACHE_PREFIX_USERNAME_AUTHUSER_NUM = "SP.USERNAME:AUTHUSERNNUM_HASH_"; //username连续登陆的次数
    public static final String CACHE_PREFIX_IP_AUTHUSER_NNUM = "SP.IP:AUTHUSERNUM_HASH_"; //IP连续登陆的次数
    public static final String CACHE_PREFIX_USERIP_AUTHUSER_NNUM = "SP.USERIP:AUTHUSERNUM_HASH_"; //user IP连续登陆的次数
    /*==================初始化client_id限制接口的调用次数========================*/
    public static final String CACHE_PREFIX_CLIENTID_INTERFACE_LIMITED_INIT = "SP.CLIENTID:INTERFACE_LIMITED_INIT_HASH_"; //client_id与接口初始限制次数，其中包含了应用所对应的接口及其初始限制次数
    /*==================根据client_id修改限制接口的调用次数========================*/
    public static final String CACHE_PREFIX_CLIENTID_INTERFACE_LIMITED = "SP.CLIENTID:INTERFACE_LIMITED_HASH_"; //client_id与接口限制修改次数,其中包含了应用目前接口限制次数的大小
    /*==================级别与接口调用次数映射========================*/
    public static final String CACHE_PREFIX_LEVEL_INTERFACE_LIMITED = "SP.LEVEL:INTERFACE_LIMITED_HASH_"; //级别与接口的映射hash表,比如初，中，高三级别各包含哪些接口，这些接口不同级别的频次限制
    /*==================应用与级别映射==============================*/
    public static final String CACHE_PREFIX_CLIENT_LEVEL = "SP.LEVEL:CLIENTID_HASH_"; //应用与级别的映射
    /*==================client_id列表========================*/
    public static final String CACHE_PREFIX_CLIENTID = "SP.CLIENTID_SET"; //client_id列表
    /*==================接口列表=====================================*/
    public static final String CACHE_PREFIX_INTERFACE = "SP.INTERFACE_SET";  //接口列表

}
