package com.sogou.upd.passport.common.utils;

import com.google.common.collect.Maps;

import java.util.Map;

public class ErrorUtil {

    public static Map<String, String> ERR_CODE_MSG_MAP = Maps.newHashMap();

    // 成功过
    public static final String SUCCESS = "0";

    /**
     * ************************通用的错误代码start********************************
     */
    // 系统异常错误
    public static final String SYSTEM_UNKNOWN_EXCEPTION = "10001";
    // 必填的参数错误
    public static final String ERR_CODE_COM_REQURIE = "10002";
    // code签名错误或请求超时
    public static final String INTERNAL_REQUEST_INVALID = "10003";
    // access_token错误
    public static final String ERR_ACCESS_TOKEN = "10005";
    // refresh_token错误
    public static final String ERR_REFRESH_TOKEN = "10006";
    // 签名或token验证失败
    public static final String ERR_SIGNATURE_OR_TOKEN = "10007";
    // 接口调用频次超限
    public static final String INVOKE_BEYOND_FREQUENCY_LIMIT = "10008";
    // 账号不存在
    public static final String INVALID_ACCOUNT = "10009";
    // client_id不存在
    public static final String INVALID_CLIENTID = "10010";
    // 生成token失败
    public static final String CREATE_TOKEN_FAIL = "10011";
    // 应用没有该API访问权限
    public static final String ACCESS_DENIED_CLIENT = "10012";
    // access_token过期
    public static final String ACCESS_TOKEN_EXPIRED = "10013";

    /* ============================================================================ */
    /*  OAuth2授权错误码                                                             */
    /* ============================================================================ */
    // client_id或client_secret不匹配
    public static final String INVALID_CLIENT = "101";
    //invalid_grant
//    public static final String INVALID_GRANT = "102";
    //错误的grant_type
    public static final String UNSUPPORTED_GRANT_TYPE = "103";
    //unsupported_response_type
    public static final String UNSUPPORTED_RESPONSE_TYPE = "104";
    // invalid_resource_type
    public static final String INVALID_RESOURCE_TYPE = "105";
    // insufficient_scope
//    public static final String INSUFFICIENT_SCOPE = "106";
    // expired_token
//    public static final String EXPIRED_TOKEN = "107";
    // access_token不存在或已过期
//    public static final String INVALID_ACCESS_TOKEN = "108";
    //refresh_token不存在或已过期
    public static final String INVALID_REFRESH_TOKEN = "109";
    // login/authorize fail,数据库写入失败
    public static final String AUTHORIZE_FAIL = "110";


    /* ============================================================================ */
    /*  account 服务的错误代码                                                       */
    /* ============================================================================ */
    //暂不支持邮箱注册
    public static final String ERR_CODE_REGISTER_EMAIL_NOT_ALLOWED = "20197";
    //重新发送激活邮件失败
    public static final String ERR_CODE_ACCOUNT_RESEND_ACTIVED_FAILED = "20199";
    //注册失败
    public static final String ERR_CODE_REGISTER_UNUSUAL = "20200";
    // 账号已经注册，请直接登录
    public static final String ERR_CODE_ACCOUNT_REGED = "20201";
    // 短信发送已达今天的最高上限
    public static final String ERR_CODE_ACCOUNT_CANTSENTSMS = "20202";
    // 手机号格式错误
    public static final String ERR_CODE_ACCOUNT_PHONEERROR = "20203";
    // 一分钟内只能发一条短信
    public static final String ERR_CODE_ACCOUNT_MINUTELIMIT = "20204";
    // 密码错误
    public static final String ERR_CODE_ACCOUNT_USERNAME_PWD_ERROR = "20206";
    //手机绑定的账号不存在
    public static final String ERR_CODE_ACCOUNT_BIND_NOTEXIST = "20207";
    // 验证码错误或已过期
    public static final String ERR_CODE_ACCOUNT_SMSCODE = "20208";
    // 今日验证码校验错误次数已超过上限
    public static final String ERR_CODE_ACCOUNT_CHECKSMSCODE_LIMIT = "20209";
    // 密码验证失败次数超过上限
    public static final String ERR_CODE_ACCOUNT_CHECKPWDFAIL_LIMIT = "20210";
    // 手机短信发送失败
    public static final String ERR_CODE_ACCOUNT_SMSCODE_SEND = "20213";
    //创建用户失败
    public static final String ERR_CODE_ACCOUNT_REGISTER_FAILED = "20214";
    //验证码错误或已过期
    public static final String ERR_CODE_ACCOUNT_PHONE_NOT_MATCH_SMSCODE = "20216";
    //手机账号不存在或手机号未被绑定
    public static final String ERR_CODE_ACCOUNT_PHONE_OBTAIN_FIELDS = "20217";
    //重置密码失败
    public static final String ERR_CODE_ACCOUNT_RESETPASSWORD_FAILED = "20218";
    //账号已经激活，无需再次激活
    public static final String ERR_CODE_ACCOUNT_ALREADY_ACTIVED_FAILED = "20219";
    //激活链接已失效
    public static final String ERR_CODE_ACCOUNT_ACTIVED_URL_FAILED = "20220";
    //验证码验证失败
    public static final String ERR_CODE_ACCOUNT_CAPTCHA_CODE_FAILED = "20221";
    // 当日密码修改次数已达上限
    public static final String ERR_CODE_ACCOUNT_RESETPASSWORD_LIMITED = "20222";
    // 当日邮件发送次数已达上限
    public static final String ERR_CODE_ACCOUNT_SENDEMAIL_LIMITED = "20223";
    // 手机号已注册或已被绑定
    public static final String ERR_CODE_ACCOUNT_PHONE_BINDED = "20225";
    // 登录失败
    public static final String ERR_CODE_ACCOUNT_LOGIN_FAILED = "20226";
    // 账号未登录，请先登录
    public static final String ERR_CODE_ACCOUNT_CHECKLOGIN_FAILED = "20229";
    // 登陆用户或者ip在黑名单中
    public static final String ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST = "20230";
    // 登陆账号未激活
    public static final String ERR_CODE_ACCOUNT_NO_ACTIVED_FAILED = "20231";
    // 账号已封杀
    public static final String ERR_CODE_ACCOUNT_KILLED = "20232";
    //手机号未被绑定
    public static final String ERR_CODE_ACCOUNT_PHONE_NOBIND = "20233";
    //TODO 手机绑定次数超限
    public static final String ERR_CODE_PHONE_BIND_FREQUENCY_LIMIT = "20236";
    //TODO 手机解除绑定失败
    public static final String ERR_CODE_PHONE_UNBIND_FAILED = "20237";
    //TODO 密码输入错误次数过多
    public static final String ERR_CODE_VERIFY_PASSWORD_FREQUENCY_LIMIT = "20238";
    //TODO 非法userId
    public static final String ERR_CODE_USERID_ILLEGAL = "20239";
    //生成cookie失败
    public static final String ERR_CODE_CREATE_COOKIE_FAILED = "20240";
    //暂时不支持sohu域内邮箱注册
    public static final String ERR_CODE_NOTSUPPORT_SOHU_REGISTER = "20241";
    //TODO sohu内部接口使用，以后删除，手机app校验第三方登录的token失败
    public static final String ERR_CODE_APPCONNECT_TOKEN_ERROR = "20242";
    // SOHU域不允许此操作
    public static final String ERR_CODE_ACCOUNT_SOHU_NOTALLOWED = "20243";
    // 第三方账号不允许此操作
    public static final String ERR_CODE_ACCOUNT_THIRD_NOTALLOWED = "20244";
    // 手机用户不允许此操作
    public static final String ERR_CODE_ACCOUNT_MOBILEUSER_NOTALLOWED = "20245";
    //暂时不支持sogou邮箱注册
    public static final String ERR_CODE_NOTSUPPORT_SOGOU_REGISTER = "20247";
    //用户昵称已经被使用
    public static final String ERR_CODE_UNIQNAME_ALREADY_EXISTS = "20248";
    //昵称包含限制词
    public static final String ERR_CODE_UNIQNAME_FORBID = "20249";
    //sgid生成失败
    public static final String ERR_CODE_CREATE_SGID_FAILED = "20250";
    // 删除cookie失败
    public static final String ERR_CODE_REMOVE_COOKIE_FAILED = "20255";
    //SSOAfterauth失败
    public static final String ERR_CODE_SSO_After_Auth_FAILED = "20256";
    //需要验证码
    public static final String ERR_CODE_ACCOUNT_CAPTCHA_NEED_CODE = "20257";
    //账号不允许做此操作
    public static final String ERR_CODE_ACCOUNT_NOTALLOWED = "20259";
    //搜狐接口异常
    public static final String ERR_CODE_ACCOUNT_SOHU_API_FAILED = "20260";
    //搜狗邮箱关闭
    public static final String ERR_CODE_SOGOU_MAIL_CLOSED_REG_FAILED = "20261";
    //SSO应用非法
    public static final String ERR_CODE_SSO_APP_CHECK_FAILED = "20262";
    //SSO应用未登录，登录态无效
    public static final String ERR_CODE_SSO_APP_NOT_LOGIN = "20263";
    //SSO设备非法访问
    public static final String ERR_CODE_SSO_DEVICE_INVALID = "20264";
    //其他异常导致无法换取sgid
    public static final String ERR_CODE_SSO_FAILED = "20265";
    //SSO TOKEN无效
    public static final String ERR_CODE_SSO_TOKEN_INVALID = "20266";
    //SSO参数解析错误
    public static final String ERR_CODE_SSO_PARAM_INVALID = "20267";

    /* ============================================================================ */
    /*  account secure 服务的错误代码                                                */
    /* ============================================================================ */
    /* ============================================================================ */
    /*  密保方式相关的错误代码                                                       */
    /* ============================================================================ */
    //用户有密保手机，请使用密保手机找回密码
    public static final String ERR_CODE_USER_HAVA_BIND_MOBILE = "20277";
    //请联系客服找回密码
    public static final String ERR_CODE_FIND_KEFU = "20278";
    // 未绑定邮箱
    public static final String ERR_CODE_OLDMOBILE_SECMOBILE_NOT_MATCH = "20279";
    // 未绑定邮箱
    public static final String NOTHAS_BINDINGEMAIL = "20280";
    // 密保答案错误
    public static final String ERR_CODE_ACCOUNTSECURE_CHECKANSWER_FAILED = "20282";
    // 重置密码申请链接失效
    public static final String ERR_CODE_ACCOUNTSECURE_RESETPWD_URL_FAILED = "20283";
    // 申请邮件发送失败
    public static final String ERR_CODE_ACCOUNTSECURE_SENDEMAIL_FAILED = "20284";
    // 当前密保邮箱错误
    public static final String ERR_CODE_ACCOUNTSECURE_CHECKOLDEMAIL_FAILED = "20285";
    // 绑定邮箱失败
    public static final String ERR_CODE_ACCOUNTSECURE_BINDEMAIL_FAILED = "20286";
    // 绑定邮箱申请链接失效
    public static final String ERR_CODE_ACCOUNTSECURE_BINDEMAIL_URL_FAILED = "20287";
    // 重置密码申请邮箱不可用
    public static final String ERR_CODE_ACCOUNTSECURE_RESETPWD_EMAIL_FAILED = "20288";
    // 绑定手机失败
    public static final String ERR_CODE_ACCOUNTSECURE_BINDMOBILE_FAILED = "20289";
    // 绑定密保失败
    public static final String ERR_CODE_ACCOUNTSECURE_BINDQUES_FAILED = "20291";
    //重置密码次数超限
    public static final String ERR_CODE_ACCOUNTSECURE_RESETPWD_LIMIT = "20292";
    //用户五日内成功登陆过
    public static final String ERR_CODE_ACCOUNTSECURE_USER_LOGIN_SUCC_RECENTLY = "20293";
    //用户已经存在
    public static final String ERR_CODE_USER_ID_EXIST = "20294";
    // 今日绑定次数已超过10次
    public static final String ERR_CODE_ACCOUNTSECURE_BINDNUM_LIMITED = "20295";

    /* ============================================================================ */
    /*  profile 服务的错误代码                                                       */
    /* ============================================================================ */
    //不支持的图片后缀
    public static final String ERR_CODE_PHOTO_EXT = "20296";
    //图片上传失败
    public static final String ERR_CODE_UPLOAD_PHOTO = "20298";
    //获取图像失败
    public static final String ERR_CODE_OBTAIN_PHOTO = "20299";
    //不支持的图片尺寸
    public static final String ERR_CODE_ERROR_IMAGE_SIZE = "20303";

    /* ============================================================================ */
    /*  Connect通用的错误代码                                                        */
    /* ============================================================================ */
    // 访问频率受限
    public static final String ERR_CODE_CONNECT_REQUEST_FREQUENCY_LIMIT = "30001";
    // 用户拒绝登录授权
    public static final String ERR_CODE_CONNECT_USER_DENIED_LOGIN = "30002";
    // 第三方登录账号Token过期，请重新登录
    public static final String ERR_CODE_CONNECT_TOKEN_INVALID = "30004";
    // 发送HTTP请求失败
    public static final String HTTP_CLIENT_REQEUST_FAIL = "30006";
    // 不支持指定第三方
    public static final String ERR_CODE_CONNECT_UNSUPPORT_THIRDPARTY = "30007";
    // 无效的登录授权请求
    public static final String ERR_CODE_CONNECT_INVALID_OAUTH = "30009";
    // 用户未授权
    public static final String ERR_CODE_CONNECT_REQUEST_NO_AUTHORITY = "30010";
    // 第三方自定义错误
    public static final String CONNECT_USER_DEFINED_ERROR = "30011";
    // 获取微信用户unionid失败
    public static final String ERR_CODE_CONNECT_WEIXIN_UNIONID = "30012";
    // 用户取消授权
    public static final String ERR_CODE_CONNECT_USERCANAEL = "30016";
    //不支持此类第三方账号
    public static final String ERR_CODE_CONNECT_NOT_SUPPORTED = "30017";
    // 透传失败
    public static final String ERR_CODE_CONNECT_PASSTHROUGH = "30018";
    //不支持指定编码以及不支持指定的加密方法
    public static final String ERR_CODE_CONNECT_MAKE_SIGNATURE_ERROR = "30020";
    //access_token不存在或已失效
    public static final String ERR_CODE_CONNECT_ACCESSTOKEN_NOT_FOUND = "30021";
    //第三方返回openapi调用失败
    public static final String ERR_CODE_CONNECT_OPENAPI_ERROR = "30023";
    //参数无效
    public static final String ERR_CODE_CONNECT_INVALID_PARAMETER = "30025";
    //第三方API调用失败
    public static final String ERR_CODE_CONNECT_FAILED = "30026";
    //qq用户修改密码，导致token失效
    public static final String ERR_CODE_CONNECT_TOKEN_PWDERROR = "30027";
    //token无效
    public static final String ERR_CODE_CONNECT_TOKEN_ERROR = "30028";
    //请求方式有错(GET还是POST)
    public static final String ERR_CODE_CONNECT_ERROR_HTTP = "30033";
    //需要使用HTTPS
    public static final String ERR_CODE_CONNECT_NEED_HTTPS = "30034";
    //微信返回的不合法的调用凭证
    public static final String ERR_CODE_CONNECT_INVALID_CREDENTIAL = "30035";
    //不合法的Openid
    public static final String ERR_CODE_CONNECT_INVALID_OPENID = "30036";
    //QQ用户词库大小大于5M，请特殊处理
    public static final String ERR_CODE_CONNECT_USER_DICTIONARY_LARGE_THAN_5M = "30037";

    /* ============================================================================ */
    /*  Friend 服务的错误代码                                                        */
    /* ============================================================================ */
    // 已经关注此用户
    public static final String ALREADY_FOLLOWED = "30311";

    /* ============================================================================ */
    /*  信息类API错误代码                                                            */
    /* ============================================================================ */
    //获取第三方账号用户信息失败
    public static final String ERR_CODE_CONNECT_GET_USERINFO_ERROR = "30321";
    // QQ账号未开通微博
    public static final String NO_OPEN_BLOG = "30403";
    // 请使用multpart格式上传图片
    public static final String UPDATE_MULTIPART_IMAGE = "30405";
    // 不支持的图片类型
    public static final String UNSUPPORT_IMAGE_FORMAT = "30406";
    // 不允许发送相同内容
    public static final String REPEAT_CONTENT = "30407";

    /* ============================================================================ */
    /*  反馈相关错误代码                                                            */
    /* ============================================================================ */
    //用户允许提交反馈，但提交失败
    public static final String ERR_CODE_PROBLEM_INSERT_FAILED = "30601";
    //用户提及反馈的次数超限
    public static final String ERR_CODE_PROBLEM_ADDTIMES_LIMITED = "30605";

    /* ============================================================================ */
    /*  sohu+接口相关错误代码                                                            */
    /* ============================================================================ */
    public static final String ERR_CODE_ERROR_ACCOUNT = "30701";

    /* ============================================================================ */
    /*  找回密码相关错误代码                                                            */
    /* ============================================================================ */
    //能提交找回密码请求次数超过限制
    public static final String ERR_CODE_FINDPWD_LIMITED = "30706";
    public static final String ERR_CODE_FINDPWD_SCODE_FAILED = "30702";
    public static final String ERR_CODE_FINDPWD_EMAIL_FAILED = "30705";
    /* ============================================================================ */

    //cookie值无效
    public static final String ERR_CODE_ERROR_COOKIE = "30710";
    //个人信息修改失败
    public static final String ERR_CODE_UPDATE_USERINFO = "30801";
    // RAS加解密错误
    public static final String ERR_CODE_RSA_DECRYPT = "31000";
    //漫游用户信息不存在
    public static final String ERR_CODE_ROAM_INFO_NOT_EXIST = "41001";

    public ErrorUtil() {
        super();
    }

    static {
        // 通用错误
        ERR_CODE_MSG_MAP.put(SUCCESS, "操作成功");
        ERR_CODE_MSG_MAP.put(SYSTEM_UNKNOWN_EXCEPTION, "系统繁忙，请稍后再试");
        ERR_CODE_MSG_MAP.put(ERR_CODE_COM_REQURIE, "参数错误,请输入必填的参数");
        ERR_CODE_MSG_MAP.put(INTERNAL_REQUEST_INVALID, "code签名错误或请求超时");
        ERR_CODE_MSG_MAP.put(INVALID_ACCOUNT, "账号不存在");
        ERR_CODE_MSG_MAP.put(INVOKE_BEYOND_FREQUENCY_LIMIT, "接口调用频次超限");
        ERR_CODE_MSG_MAP.put(ERR_ACCESS_TOKEN, "access_token错误");
        ERR_CODE_MSG_MAP.put(ERR_REFRESH_TOKEN, "refresh_token错误");
        ERR_CODE_MSG_MAP.put(ERR_SIGNATURE_OR_TOKEN, "签名或token验证失败");
        ERR_CODE_MSG_MAP.put(INVALID_CLIENTID, "client_id不存在");
        ERR_CODE_MSG_MAP.put(CREATE_TOKEN_FAIL, "生成token失败");
        ERR_CODE_MSG_MAP.put(ACCESS_DENIED_CLIENT, "应用没有该API访问权限");
        ERR_CODE_MSG_MAP.put(ACCESS_TOKEN_EXPIRED, "accessToken过期");

        // oauth2授权
        ERR_CODE_MSG_MAP.put(INVALID_CLIENT, "client_id or client_secret不匹配");
        ERR_CODE_MSG_MAP.put(UNSUPPORTED_GRANT_TYPE, "不支持的grant_type");
        ERR_CODE_MSG_MAP.put(INVALID_RESOURCE_TYPE, "不支持的resource_type");
        ERR_CODE_MSG_MAP.put(INVALID_REFRESH_TOKEN, "refresh_token不存在或过期");
        ERR_CODE_MSG_MAP.put(AUTHORIZE_FAIL, "授权失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_REGISTER_EMAIL_NOT_ALLOWED, "暂不支持邮箱注册");

        // account
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_RESEND_ACTIVED_FAILED, "重新发送激活邮件失败!");
        ERR_CODE_MSG_MAP.put(ERR_CODE_REGISTER_UNUSUAL, "注册失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_REGED, "账号已注册");
        ERR_CODE_MSG_MAP.put(ERR_CODE_USERID_ILLEGAL, "非法userid");
        ERR_CODE_MSG_MAP.put(ERR_CODE_USER_ID_EXIST, "用户名已经存在");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_CANTSENTSMS, "该手机号当日短信发送次数超过上限");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_PHONEERROR, "呃，地球上没有这个手机号");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_MINUTELIMIT, "一分钟内只能发一条短信");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_USERNAME_PWD_ERROR, "密码错误");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_BIND_NOTEXIST, "手机号绑定的账号不存在");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_SMSCODE, "验证码错误或已过期");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_CHECKSMSCODE_LIMIT, "今日验证码校验错误次数已超过上限");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_CHECKPWDFAIL_LIMIT, "今日密码验证失败次数超过上限");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_SMSCODE_SEND, "手机短信发送失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_REGISTER_FAILED, "创建用户失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_LOGIN_FAILED, "用户登录失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_CHECKLOGIN_FAILED, "账号未登录，请先登录");
        ERR_CODE_MSG_MAP.put(ERR_CODE_VERIFY_PASSWORD_FREQUENCY_LIMIT, "当日密码输入错误次数过多");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST, "账号操作异常");

        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_PHONE_NOT_MATCH_SMSCODE, "验证码错误或已过期");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_PHONE_OBTAIN_FIELDS, "手机账号不存在或手机号未被绑定");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_RESETPASSWORD_FAILED, "重置密码失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_ALREADY_ACTIVED_FAILED, "已经激活，无需再次激活");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_CAPTCHA_CODE_FAILED, "验证码验证失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_CAPTCHA_NEED_CODE, "请输入验证码");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_ACTIVED_URL_FAILED, "激活链接已经失效");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_PHONE_BINDED, "手机号已注册或已被绑定");

        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_NO_ACTIVED_FAILED, "账号未激活");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_KILLED, "账号已封杀");
        ERR_CODE_MSG_MAP.put(ERR_CODE_NOTSUPPORT_SOHU_REGISTER, "暂时不支持sohu域内邮箱执行此操作");
        ERR_CODE_MSG_MAP.put(ERR_CODE_NOTSUPPORT_SOGOU_REGISTER, "暂时不支持搜狗邮箱注册");
        ERR_CODE_MSG_MAP.put(ERR_CODE_UNIQNAME_ALREADY_EXISTS, "用户昵称已经被使用");
        ERR_CODE_MSG_MAP.put(ERR_CODE_UNIQNAME_FORBID, "昵称包含限制词");
        ERR_CODE_MSG_MAP.put(ERR_CODE_CREATE_SGID_FAILED, "sgid生成失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_APPCONNECT_TOKEN_ERROR, "手机app校验第三方登录的token失败");

        ERR_CODE_MSG_MAP.put(ERR_CODE_CREATE_COOKIE_FAILED, "生成cookie失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_REMOVE_COOKIE_FAILED, "删除cookie失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_SSO_After_Auth_FAILED, "SSOAfterauth失败");

        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_RESETPASSWORD_LIMITED, "当日修改或重置密码次数已达上限");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_SENDEMAIL_LIMITED, "该邮箱当日邮件发送次数已达上限");

        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_SOHU_NOTALLOWED, "SOHU域用户不允许此操作");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_THIRD_NOTALLOWED, "第三方账号不允许此操作");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_MOBILEUSER_NOTALLOWED, "手机账号不允许此操作");
        ERR_CODE_MSG_MAP.put(ERR_CODE_PHONE_UNBIND_FAILED, "手机解除绑定失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_NOTALLOWED, "账号不允许做此操作");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_SOHU_API_FAILED, "搜狐接口异常");
        ERR_CODE_MSG_MAP.put(ERR_CODE_SOGOU_MAIL_CLOSED_REG_FAILED, "搜狗邮箱已关闭");

        ERR_CODE_MSG_MAP.put(ERR_CODE_SSO_APP_CHECK_FAILED, "应用非法");
        ERR_CODE_MSG_MAP.put(ERR_CODE_SSO_APP_NOT_LOGIN, "应用未登录");
        ERR_CODE_MSG_MAP.put(ERR_CODE_SSO_DEVICE_INVALID, "设备非法访问");
        ERR_CODE_MSG_MAP.put(ERR_CODE_SSO_FAILED, "快速登录失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_SSO_TOKEN_INVALID, "快速登录令牌无效");
        ERR_CODE_MSG_MAP.put(ERR_CODE_SSO_PARAM_INVALID, "快速登录参数解析失败");

        // acount secure info
        ERR_CODE_MSG_MAP.put(ERR_CODE_USER_HAVA_BIND_MOBILE, "您的帐号已绑定手机，请使用手机找回或联系客服");
        ERR_CODE_MSG_MAP.put(ERR_CODE_FIND_KEFU, "无绑定关系，请联系客服找回您的密码");
        ERR_CODE_MSG_MAP.put(ERR_CODE_OLDMOBILE_SECMOBILE_NOT_MATCH, "原手机号与密保手机不匹配");
        ERR_CODE_MSG_MAP.put(NOTHAS_BINDINGEMAIL, "未绑定邮箱");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNTSECURE_CHECKANSWER_FAILED, "密保答案错误");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNTSECURE_RESETPWD_URL_FAILED, "重置密码链接失效");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNTSECURE_SENDEMAIL_FAILED, "申请邮件发送失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNTSECURE_CHECKOLDEMAIL_FAILED, "当前密保邮箱错误");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNTSECURE_BINDEMAIL_FAILED, "绑定密保邮箱失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNTSECURE_BINDEMAIL_URL_FAILED, "绑定密保邮箱申请链接失效");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNTSECURE_RESETPWD_EMAIL_FAILED, "重置密码申请邮箱不可用");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNTSECURE_BINDMOBILE_FAILED, "绑定密保手机失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNTSECURE_BINDQUES_FAILED, "绑定密保问题失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_PHONE_NOBIND, "手机号未绑定账号");
        ERR_CODE_MSG_MAP.put(ERR_CODE_PHONE_BIND_FREQUENCY_LIMIT, "手机绑定次数超限");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNTSECURE_RESETPWD_LIMIT, "修改密码频率过于频繁");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNTSECURE_USER_LOGIN_SUCC_RECENTLY, "用户5日内登录过");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNTSECURE_BINDNUM_LIMITED, "今日绑定次数超限，请明日再试");

        // profile
        ERR_CODE_MSG_MAP.put(ERR_CODE_PHOTO_EXT, "不支持的图片后缀");
        ERR_CODE_MSG_MAP.put(ERR_CODE_UPLOAD_PHOTO, "上传图片失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_OBTAIN_PHOTO, "获取图像失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ERROR_IMAGE_SIZE, "不支持的图片尺寸");
        ERR_CODE_MSG_MAP.put(ERR_CODE_UPDATE_USERINFO, "个人信息修改失败");

        // connect
        ERR_CODE_MSG_MAP.put(ERR_CODE_CONNECT_REQUEST_FREQUENCY_LIMIT, "超过第三方接口的访问限制");
        ERR_CODE_MSG_MAP.put(ERR_CODE_CONNECT_USER_DENIED_LOGIN, "用户拒绝登录授权");
        ERR_CODE_MSG_MAP.put(ERR_CODE_CONNECT_TOKEN_INVALID, "第三方账号Token过期，请重新登录");
        ERR_CODE_MSG_MAP.put(HTTP_CLIENT_REQEUST_FAIL, "发送HTTP请求失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_CONNECT_UNSUPPORT_THIRDPARTY, "该接口不支持指定第三方");
        ERR_CODE_MSG_MAP.put(ERR_CODE_CONNECT_INVALID_OAUTH, "无效的OAuth2.0授权验证请求");
        ERR_CODE_MSG_MAP.put(ERR_CODE_CONNECT_REQUEST_NO_AUTHORITY, "用户没有对该api进行授权");
        ERR_CODE_MSG_MAP.put(ERR_CODE_CONNECT_WEIXIN_UNIONID, "获取微信unionid失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_CONNECT_USERCANAEL, "用户取消授权");
        ERR_CODE_MSG_MAP.put(ERR_CODE_CONNECT_NOT_SUPPORTED, "不支持此类账号调用");
        ERR_CODE_MSG_MAP.put(ERR_CODE_CONNECT_PASSTHROUGH, "透传失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_CONNECT_ACCESSTOKEN_NOT_FOUND, "第三方access_token不存在或失效");
        ERR_CODE_MSG_MAP.put(ERR_CODE_CONNECT_OPENAPI_ERROR, "第三方返回openapi调用失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_CONNECT_INVALID_PARAMETER, "参数无效");
        ERR_CODE_MSG_MAP.put(ERR_CODE_CONNECT_FAILED, "第三方Api调用失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_CONNECT_TOKEN_PWDERROR, "第三方账号修改密码，导致token失效");
        ERR_CODE_MSG_MAP.put(ERR_CODE_CONNECT_TOKEN_ERROR, "token无效");
        ERR_CODE_MSG_MAP.put(ERR_CODE_CONNECT_ERROR_HTTP, "请求第三方时请求方式有误");
        ERR_CODE_MSG_MAP.put(ERR_CODE_CONNECT_NEED_HTTPS, "需要使用HTTPS");
        ERR_CODE_MSG_MAP.put(ERR_CODE_CONNECT_INVALID_CREDENTIAL, "不合法的调用凭证");
        ERR_CODE_MSG_MAP.put(ERR_CODE_CONNECT_INVALID_OPENID, "不合法的Openid");
        ERR_CODE_MSG_MAP.put(ERR_CODE_CONNECT_USER_DICTIONARY_LARGE_THAN_5M, "用户词库大小大于5M，请特殊处理");

        // info
        ERR_CODE_MSG_MAP.put(ERR_CODE_CONNECT_GET_USERINFO_ERROR, "获取第三方用户信息失败");
        ERR_CODE_MSG_MAP.put(NO_OPEN_BLOG, "还没开通微博呢");
        ERR_CODE_MSG_MAP.put(UPDATE_MULTIPART_IMAGE, "请使用multpart格式上传图片");
        ERR_CODE_MSG_MAP.put(UNSUPPORT_IMAGE_FORMAT, "不支持的图片类型");
        ERR_CODE_MSG_MAP.put(REPEAT_CONTENT, "同样的内容请勿重复发送");

        // friend
        ERR_CODE_MSG_MAP.put(ALREADY_FOLLOWED, "已经关注此用户");

        //反馈相关
        ERR_CODE_MSG_MAP.put(ERR_CODE_PROBLEM_INSERT_FAILED, "提交用户反馈失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_PROBLEM_ADDTIMES_LIMITED, "您一天内提交反馈的次数超过限制");

        //sohu+相关接口
        ERR_CODE_MSG_MAP.put(ERR_CODE_ERROR_ACCOUNT, "账号异常，请联系passportkf@sogou-inc.com找回账号");

        //SSO setcookie接口
        ERR_CODE_MSG_MAP.put(ERR_CODE_ERROR_COOKIE, "cookie值无效");

        //找回密码相关
        ERR_CODE_MSG_MAP.put(ERR_CODE_FINDPWD_LIMITED, "您一天内提交的找回密码请求次数超过限制");
        ERR_CODE_MSG_MAP.put(ERR_CODE_FINDPWD_SCODE_FAILED, "无效链接");
        ERR_CODE_MSG_MAP.put(ERR_CODE_FINDPWD_EMAIL_FAILED, "找回密码邮件已失效");

        //RSA
        ERR_CODE_MSG_MAP.put(ERR_CODE_RSA_DECRYPT, "解密错误");

        //漫游
        ERR_CODE_MSG_MAP.put(ERR_CODE_ROAM_INFO_NOT_EXIST, "漫游用户信息不存在");
    }

    public static Map<String, String> getERR_CODE_MSG_MAP() {
        return ERR_CODE_MSG_MAP;
    }

    public static String getERR_CODE_MSG(String code) {
        return ERR_CODE_MSG_MAP.get(code);
    }

}
