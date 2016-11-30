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
    public static final String CACHE_PREFIX_REGISTER_IPBLACKLIST = "SP.REGISTER:IPBLACKLIST_SET_"; // ip与注册次数映射
    public static final String CACHE_PREFIX_REGISTER_CLIENTIDBLACKLIST = "SP.REGISTER:CLIENTIDBLACKLIST_SET_"; // ip与注册次数映射
    public static final String CACHE_PREFIX_REGISTER_COOKIEBLACKLIST = "SP.REGISTER:COOKIEBLACKLIST_"; // cookie与注册次数映射

    public static final String CACHE_PREFIX_PASSPORTID_ACTIVEMAILTOKEN = "SP.PASSPORTID:ACTIVEMAILTOKEN_"; //passportId 与第三方邮件注册token有效期验证
    public static final String CACHE_PREFIX_NICKNAME_PASSPORTID = "SP.NICKNAME:PASSPORTID_"; //昵称与passportId映射

    public static final String CACHE_PREFIX_PASSPORTID_ACCOUNTINFO = "SP.PASSPORTID:ACCOUNTINFO_"; // passportId与accountInfo映射
    public static final String CACHE_PREFIX_MOBILE_CHECKSMSFAIL = "SP.MOBILE:CHECKSMSFAIL_"; // mobile与smscode错误检测次数映射
    public static final String CACHE_PREFIX_UUID_CAPTCHA = "SP.UUID:CAPTCHA_"; // 注册UUID与验证码映射
    public static final String CACHE_PREFIX_PASSPORTID_RESETPWDNUM = "SP.PASSPORTID:RESETPWDNUM_"; // passportId与当日修改密码次数映射
    public static final String CACHE_PREFIX_PASSPORTID_CHECKPWDFAIL = "SP.PASSPORTID:CHECKPWDFAIL_"; // passportId与当日验证密码失败次数映射
    // public static final String CACHE_PREFIX_PASSPORTID_RESETPWDSENDEMAILNUM = "SP.PASSPORTID:RESETPWDSENDEMAILNUM_"; // passportId与当日重置密码邮件次数
    // public static final String CACHE_PREFIX_PASSPORTID_BINDEMAILTOKEN = "SP.PASSPORTID:BINDEMAILTOKEN_MAP_"; // passportId与绑定邮件token映射
    // public static final String CACHE_PREFIX_PASSPORTID_BINDEMAILSENDNUM = "SP.PASSPORTID:BINDEMAILSENDNUM_"; // passportId与绑定邮件发送次数映射
    public static final String CACHE_PREFIX_PASSPORTID_PASSPORTID_SECURECODE = "SP.PASSPORTID:SECURESCODE_"; // passportId与安全校验码映射
    public static final String CACHE_PREFIX_PASSPORTID_RESETPWDSECURECODE = "SP.PASSPORTID:RESETPWDSECURECODE_"; // passportId与重置密码secureCode映射
    public static final String CACHE_PREFIX_PASSPORTID_MODSECINFOSECURECODE = "SP.PASSPORTID:MODSECINFOSECURECODE"; // passportId与修改密保内容secureCode映射
    public static final String CACHE_PREFIX_PASSPORTID_EMAILSCODE = "SP.PASSPORTID:EMAILSCODE_"; // passportId与email中scode的映射
    public static final String CACHE_PREFIX_PASSPORTID_SENDEMAILNUM = "SP.PASSPORTID:SENDEMAILNUM_"; // passportId与email发送次数的映射
    public static final String CACHE_PREFIX_MOBILE_SMSCODE_IPBLACKLIST = "SP.MOBILESMSCODE:IPBLACKLIST_SET_"; // ip与发送手机短信次数映射
    public static final String CACHE_PREFIX_MOBILE_SENDSMSCODENUM = "SP.MOBILESMSCODE:NUMLIMIT_SET_"; // 手机号与发送手机短信次数映射

    /*================账号黑名单、白名单缓存常量=====================*/
    public static final String CACHE_PREFIX_IP_UPDATEPWDNUM = "SP.IP:UPDATEPWDNUM_"; //IP的修改密码限制
    public static final String CACHE_PREFIX_IP_BINDNUM = "SP.IP:BINDNUM_"; //IP的设置密保限制
    // username或者ip在白名单中的set KEY
    public static final String CACHE_PREFIX_LOGIN_WHITELIST = "SP.USERNAME.IP:WHITELIST_KEY";
    // 登陆在黑名单的前缀
    public static final String CACHE_PREFIX_LOGIN_USERNAME_BLACK_ = "SP.USERNAME:BLACK_";
    public static final String CACHE_PREFIX_LOGIN_IP_BLACK_ = "SP.IP:BLACK_";
    // 检查账号是否存在在黑名单的前缀
    public static final String CACHE_PREFIX_EXIST_USERNAME_BLACK_ = "SP.EXIST.USERNAME:BLACK_";
    public static final String CACHE_PREFIX_EXIST_IP_BLACK_ = "SP.EXIST.IP:BLACK_";
    // 内部接口检查账号是否存在在黑名单的前缀
    public static final String CACHE_PREFIX_EXIST_INTERNAL_USERNAME_BLACK = "SP.EXIST.INTERNAL.USERNAME:BLACK_";
    public static final String CACHE_PREFIX_EXIST_INTERNAL_IP_BLACK = "SP.EXIST.INTERNAL.IP:BLACK_";
    //检查昵称是否存在IP黑名单
    public static final String CACHE_PREFIX_EXIST_NICKNAME_IP_BLACK = "SP.EXIST.NICKNAME.IP:BLACK_";
    public static final String CACHE_PREFIX_CHECK_NICKNAME_EXIST_IP_NUM = "SP.IP.CHECK_NICKNAME_NUM_"; //检查昵称是否存在昵称黑名单
    //cookie与检查用户昵称次数映射
    public static final String CACHE_PREFIX_CHECK_NICKNAME_COOKIE_BLACK = "SP.EXIST.NICKNAME.COOKIE:BLACK_";
    //基于cookie检查用户昵称次数限制
    public static final String CACHE_PREFIX_CHECK_NICKNAME_EXIST_COOKIE_NUM = "SP.COOKIE.CHECK_NICKNAME_NUM_";
    //内部接口 检查用户是否存在次数前缀
    public static final String CACHE_PREFIX_CHECK_USER_INTERNAL_USERNAME_NUM = "SP.EXIST.INTERNAL.USERNAME_NUM_";
    //内部接口 检查用户是否存在次数前缀
    public static final String CACHE_PREFIX_CHECK_USER_INTERNAL_IP_NUM = "SP.EXIST.INTERNAL.IP_NUM_";
    // 检查账号是否存在在黑名单的前缀
    public static final String CACHE_PREFIX_GETPAIRTOKEN_USERNAME_BLACK_ = "SP.GETPAIRTOKEN.USERNAME:BLACK_";
    public static final String CACHE_PREFIX_GETPAIRTOKEN__IP_BLACK_ = "SP.GETPAIRTOKEN.IP:BLACK_";
    //IP网段黑名单
    public static final String CACHE_PREFIX_IP_SUBIPBLACKLIST = "SP.IP:SUBIPBLACKLIST_KEY";
    //检查是否在泄露账号列表中
    public static final String CACHE_PREFIX_USER_LEAKLIST = "SP.PASSPORTID:SOGOULEAKLIST_";

    /*================绑定密保限制相关缓存常量=====================*/
    public static final String CACHE_PREFIX_PASSPORTID_BINDNUM = "SP.PASSPORTID:BINDNUM_"; // passportId与当日设置密保次数

    /*================CONNECT 相关缓存常量=====================*/
    public static final String CACHE_PREFIX_PASSPORTID_CONNECTTOKEN = "SP.PASSPORTID:CONNECTTOKEN_"; //passportId+provider+appKey与ConnectToken映射
    public static final String CACHE_PREFIX_PASSPORTID_ORIGINAL_CONNECTINFO = "SP.PASSPORTID:ORIGINAL_CONNECTINFO_"; //passportId+provider与OriginalConnectInfo映射
    public static final String CACHE_PREFIX_OPENID_CONNECTRELATION = "SP.OPENID:CONNECTRELATION_";  // openid+provider与ConnectRelation的映射
    public static final String CACHE_PREFIX_PASSPORTID_CONNECTUSERINFO = "SP.PASSPORTID:CONNECTUSERINFO_"; //passportId与ConnectUserInfoVO映射

    /*================CONFIG 相关缓存常量=====================*/
    public static final String CACHE_PREFIX_CLIENTID_APPCONFIG = "SP.CLIENTID:APPCONFIG_";     //clientid与appConfig映射
    public static final String CACHE_PREFIX_CLIENTID_CONNECTCONFIG = "SP.CLIENTID:CONNECTCONFIG_";     //clientid与connectConfig映射

    /*================用户反馈相关缓存常量=====================*/
    public static final String CACHE_PREFIX_ID_PROBLEMTYPE = "SP.ID:PROBLEMTYPE_"; // Id与problem映射 CACHE_PREFIX_PASSPORTID_PROBLEM
    public static final String CACHE_PREFIX_PASSPORTID_PROBLEMLIST = "SP.PASSPORTID:PROBLEMLIST_"; // passortId与问题列表之前的映射
    public static final String CACHE_PREFIX_PROBLEM_IPINBLACKLIST = "SP.IP_ADDPROBLEMTIMES:_"; // IP与提交反馈次数之间的映射

    /*==================token缓存常量=====================*/
    public static final String CACHE_PREFIX_SECURECODE = "SP.SECURECODE:SCODE_";

    /*-----------------------------------------   KV系统   -----------------------------------------*/
    /*==================核心kv 动作记录相关缓存常量=====================*/
    public static final String CORE_KV_PREFIX_PASSPORTID_ACTIONRECORD = "13008/action_records/SP.PASSPORTID:ACTIONRECORD_LIST_";
    /*==================核心kv token相关缓存常量========================*/
    public static final String CORE_KV_PREFIX_PASSPROTID_TOKEN = "13008/account_token/"; //kv迁移，核心kv集群
//    public static final String CORE_KV_PREFIX_PASSPROTID_TOKEN = "0/0/"; //kv测试

    /*==================登陆相关缓存常量========================*/
    public static final String CACHE_PREFIX_USERNAME_LOGINNUM = "SP.USERNAME:LOGINNUM_HASH_"; //username连续登陆的次数
    public static final String CACHE_PREFIX_IP_LOGINNUM = "SP.IP:LOGINNUM_HASH_"; //IP连续登陆的次数
    public static final String CACHE_SUCCESS_KEY = "SUCCESS"; //成功key
    public static final String CACHE_FAILED_KEY = "FAILED"; //失败key
    public static final String CACHE_PREFIX_USERNAME_EXISTNUM = "SP.USERNAME:EXISTNUM_"; //username连续登陆的次数
    public static final String CACHE_PREFIX_IP_EXISTNUM = "SP.IP:EXISTNUM_"; //IP连续登陆的次数
    public static final String CACHE_PREFIX_USERNAME_GETPAIRTOKENNUM = "SP.USERNAME:GETPAIRTOKENNUM_"; //username连续登陆的次数
    public static final String CACHE_PREFIX_IP_GETPAIRTOKENNUM = "SP.IP:GETPAIRTOKENNUM_"; //IP连续登陆的次数

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

    /*==================找回密码相关缓存常量========================*/
    public static final String CACHE_PREFIX_PASSPORTID_FINDPWDTIMES = "SP.PASSPORTID:FINDPWDTIMES_"; // passportId与找回密码次数之间的映射


    /*==================漫游相关=====================================*/
    public static final String CACHE_KEY_WEB_ROAM = "SP.RKEY:VALUE_"; //漫游key

    /*==================Module相关=====================================*/
    public static final String CACHE_KEY_MODULE_APP_REPLACE = "SP.MODULE:APP_REPLACE";   //应用module 批次替换

    /*=================黑名单列表===================================*/
    public static final String CACHE_KEY_BLACKLIST = "BLACKLIST";
    public static final int BLACKLIST_SET_SIZE = 128;
    /*=================QQ好友链===================================*/
    public static final String CACHE_KEY_QQ_FRIENDS = "SP.QQFRIENDS:RETURNVAL_";

    /*=================guava本地缓存===================================*/
    public static final int CACHE_REFRESH_INTERVAL = 10;     //guava cache 自动刷新时间间隔为10 min


    /*=================风控系统===================================*/
    //封禁IP key 前缀
    public static final String CACHE_PREFIX_DENY_IP = "SP.DENY_IP:IP_";
    //由mongo切换成redis后，风险IP key前缀
    public static final String CACHE_PREFIX_RISK_IP = "SP.RISK_IP_HASH_";
    //由mongo切换成redis后，国内出口IP key前缀
    public static final String CACHE_PREFIX_SHARDED_EXIPORT = "SP.SHARED_EXPORT_IP_HASH_";


    /*=================包签名相关缓存常量===================================*/
    public static final String CACHE_PREFIX_PACKAGENAME_PACKAGEINFO = "SP.PACKAGENAME:PACKAGEINFO_";//包名与包签名信息映射
    public static final String CACHE_PREFIX_SSO_TOKEN_KEY = "SP.SSOTOKEN:VALUE_";
    public static final String CACHE_SSO_TOKEN_VALUE = "0";

    /*=================短信登录相关===================================*/
    public static final String CACHE_PREFIX_SMS_CODE_LOGIN = "SP.MOBILE:SMS_CODE_LOGIN_";//手机短信登录，mobile与sms code 映射
    public static final String CACHE_PREFIX_SMS_CODE_GET_NUM = "SP.MOBILE:SMS_CODE_GET_NUM_";//手机短信登录，请求短信校验码次数,5次/天
    public static final String CACHE_PREFIX_SMS_CODE_CHECK_FAIL_NUM = "SP.MOBILE:SMS_CODE_CHECK_FAIL_NUM_"; //手机短信登录，尝试短信校验码次数，10次/天
    public static final String CACHE_PREFIX_SMS_CODE_LOGIN_NUM = "SP.MOBILE:SMS_CODE_LOGINNUM_HASH_";//手机短信登录，登录次数
    public static final String CACHE_PREFIX_SMSLOGIN_CAPTCHA_LIMIT = "SP.MOBILE:SMSLOGIN_CAPTCHA_LIMIT_";//手机短验登录每天超过2次就出现图片验证码


}
