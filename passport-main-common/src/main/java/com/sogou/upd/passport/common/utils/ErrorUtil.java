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
    // 内部接口code签名错误或请求超时
    public static final String INTERNAL_REQUEST_INVALID = "10003";
    // 代理搜狐Passport接口HTTP请求发生异常
    public static final String PROXY_SHPP_API_EXCEPTION = "10004";
    // access_token错误
    public static final String ERR_ACCESS_TOKEN = "10005";
    // 签名或accessToken验证失败
//    public static final String ERR_OPEN_ID = "10006";
    // 接口调用频次超限
    public static final String INVOKE_BEYOND_FREQUENCY_LIMIT = "10008";
    // 账号不存在或异常或未激活
    public static final String INVALID_ACCOUNT = "10009";
    //client_id不存在
    public static final String INVALID_CLIENTID = "10010";

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
    // invalid_scope
//    public static final String INVALID_SCOPE = "105";
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
    // 用户名密码不匹配
    public static final String USERNAME_PWD_MISMATCH = "111";  // 与20206一样，因为T3历史原因，暂不删除

    /* ============================================================================ */
    /*  account 服务的错误代码                                                       */
    /* ============================================================================ */
    // 帐号已经注册，请直接登录
    public static final String ERR_CODE_ACCOUNT_REGED = "20201";
    // 短信发送已达今天的最高上限20条
    public static final String ERR_CODE_ACCOUNT_CANTSENTSMS = "20202";
    // 手机号格式错误
    public static final String ERR_CODE_ACCOUNT_PHONEERROR = "20203";
    // 一分钟内只能发一条短信
    public static final String ERR_CODE_ACCOUNT_MINUTELIMIT = "20204";
    // 帐号不存在
    public static final String ERR_CODE_ACCOUNT_NOTHASACCOUNT = "20205";
    // 用户名密码不正确
    public static final String USERNAME_PWD_ERROR = "20206";
    // 验证码错误或已过期
    public static final String ERR_CODE_ACCOUNT_SMSCODE = "20208";
    // 今日验证码校验错误次数已超过上限
    public static final String ERR_CODE_ACCOUNT_CHECKSMSCODE_LIMIT = "20209";

    // 手机验证码发送失败
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
    //注册验证码失效
    public static final String ERR_CODE_ACCOUNT_CAPTCHA_CODE_FAILED = "20221";
    // 当日密码修改次数已达上限
    public static final String ERR_CODE_ACCOUNT_RESETPASSWORD_LIMITED = "20222";
    // 当日邮件发送次数已达上限
    public static final String ERR_CODE_ACCOUNT_SENDEMAIL_LIMITED = "20223";
    // 当日注册次数已达上限
    public static final String ERR_CODE_ACCOUNT_REGISTER_LIMITED = "20224";
    // 手机号已绑定其他账号
    public static final String ERR_CODE_ACCOUNT_PHONE_BINDED = "20225";
    // 登录失败
    public static final String ERR_CODE_ACCOUNT_LOGIN_FAILED = "20226";
    // 当前登录账号与所操作账号不一致
    public static final String ERR_CODE_ACCOUNT_LOGIN_OPERACCOUNT_MISMATCH = "20228";
    // 账号未登录，请先登录
    public static final String ERR_CODE_ACCOUNT_CHECKLOGIN_FAILED = "20229";
    // 登陆用户或者ip在黑名单中
    public static final String ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST = "20230";
    // 登陆账号未激活
    public static final String ERR_CODE_ACCOUNT_NO_ACTIVED_FAILED = "20231";
    // 账号已封杀
    public static final String ERR_CODE_ACCOUNT_KILLED = "20232";
    //TODO 手机号未被绑定
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


    /* ============================================================================ */
    /*  account secure 服务的错误代码                                                */
    /* ============================================================================ */

    /* ============================================================================ */
    /*  账号绑定相关的错误代码                                                       */
    /* ============================================================================ */
    // 绑定第三方账号失败
    public static final String BIND_CONNECT_ACCOUNT_FAIL = "20250";
    // 不能绑定与主账号同一类型的账号
    public static final String CONNOT_BIND_SAME_TYPE_ACCOUNT = "20251";
    // 不允许重复绑定同一类型的账号
    public static final String NOTALLOWED_REPEAT_BIND_SAME_TYPE_ACCOUNT = "20252";
    // 此账号已经注册或绑定过
    public static final String ACCOUNT_ALREADY_REG_OR_BIND = "20253";

    /* ============================================================================ */
    /*  密保方式相关的错误代码                                                       */
    /* ============================================================================ */
    // 未绑定邮箱
    public static final String NOTHAS_BINDINGEMAIL = "20280";
    // 未设置密保问题及答案
    public static final String NOTHAS_BINDINGQUESTION = "20281";
    // 密保答案错误
    public static final String ERR_CODE_ACCOUNTSECURE_CHECKANSWER_FAILED = "20282";
    // 重置密码申请链接失效
    public static final String ERR_CODE_ACCOUNTSECURE_RESETPWD_URL_FAILED = "20283";
    // 申请邮件发送失败
    public static final String ERR_CODE_ACCOUNTSECURE_SENDEMAIL_FAILED = "20284";
    // 旧绑定邮箱错误
    public static final String ERR_CODE_ACCOUNTSECURE_CHECKOLDEMAIL_FAILED = "20285";
    // 绑定邮箱失败
    public static final String ERR_CODE_ACCOUNTSECURE_BINDEMAIL_FAILED = "20286";
    // 绑定邮箱申请链接失效
    public static final String ERR_CODE_ACCOUNTSECURE_BINDEMAIL_URL_FAILED = "20287";
    // 重置密码申请邮箱不可用
    public static final String ERR_CODE_ACCOUNTSECURE_RESETPWD_EMAIL_FAILED = "20288";
    // 绑定手机失败
    public static final String ERR_CODE_ACCOUNTSECURE_BINDMOBILE_FAILED = "20289";
    // 修改密保链接失效
    public static final String ERR_CODE_ACCOUNTSECURE_BIND_FAILED = "20290";
    // 绑定密保失败
    public static final String ERR_CODE_ACCOUNTSECURE_BINDQUES_FAILED = "20291";
    //重置密码次数超限
    public static final String ERR_CODE_ACCOUNTSECURE_RESETPWD_LIMIT = "20292";
    //用户五日内成功登陆过
    public static final String ERR_CODE_ACCOUNTSECURE_USER_LOGIN_SUCC_RECENTLY = "20293";
    //用户已经存在
    public static final String ERR_CODE_USER_ID_EXIST = "20294";
    // 手机用户不允许此操作
    public static final String ERR_CODE_ACCOUNTSECURE_MOBILEUSER_NOTALLOWED = "20295";
    // 今日绑定次数已超过10次
    public static final String ERR_CODE_ACCOUNTSECURE_BINDNUM_LIMITED = "20296";

    /* ============================================================================ */
    /*  profile 服务的错误代码                                                       */
    /* ============================================================================ */
    // 字段非法
    public static final String ERR_CODE_PROFILE_FIELD = "20301";

    /* ============================================================================ */
    /*  Connect通用的错误代码                                                        */
    /* ============================================================================ */
    // 访问频率受限
    public static final String CONNECT_REQUEST_FREQUENCY_LIMIT = "30001";
    // 用户拒绝登录授权
    public static final String CONNECT_USER_DENIED_LOGIN = "30002";
    // 第三方授权的state被篡改
    public static final String OAUTH_AUTHZ_STATE_INVALID = "30003";
    // 第三方登录帐号Token过期，请重新登录
    public static final String CONNECT_TOKEN_INVALID = "30004";
    // 刷新第三方accessToken失败
    public static final String CONNECT_REFRESH_TOKEN_FAIL = "30005";
    // 发送HTTP请求失败
    public static final String HTTP_CLIENT_REQEUST_FAIL = "30006";
    // 不支持指定第三方
    public static final String UNSUPPORT_THIRDPARTY = "30007";
    // 无效的登录授权请求
    public static final String INVALID_OPENOAUTH_REQUEST = "30009";
    // 用户未授权
    public static final String REQUEST_NO_AUTHORITY = "30010";
    // 第三方自定义错误
    public static final String CONNECT_USER_DEFINED_ERROR = "30011";
    //第三方openid获取失败,没有此用户
    public static final String ERR_CODE_CONNECT_OBTAIN_OPENID_ERROR = "30013";

    /* ============================================================================ */
    /*  Friend 服务的错误代码                                                        */
    /* ============================================================================ */
    // 关注的用户不存在
    public static final String FOLLOW_ACCOUNT_NOT_EXISTS = "30310";
    // 已经关注此用户
    public static final String ALREADY_FOLLOWED = "30311";

    /* ============================================================================ */
    /*  信息类API错误代码                                                            */
    /* ============================================================================ */
    // 图片url不能为空
    public static final String PIC_URL_NOT_NULL = "30401";
    // 发送失败
    public static final String ADD_SHARE_FAIL = "30402";
    // QQ帐号未开通微博
    public static final String NO_OPEN_BLOG = "30403";
    // 上传的图片不能为空
    public static final String PIC_NOT_NULL = "30404";
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
    //提交反馈评论失败
    public static final String ERR_CODE_PROBLEMANSWER_INSERT_FAILED = "30602";
    //关闭反馈失败
    public static final String ERR_CODE_PROBLEM_CLOSE_FAILED = "30603";
    //用户未登陆，而提及反馈
    public static final String ERR_CODE_PROBLEM_NOT_LOGIN = "30604";
    //用户提及反馈的次数超限
    public static final String ERR_CODE_PROBLEM_ADDTIMES_LIMITED = "30605";

    static {
        // 通用错误
        ERR_CODE_MSG_MAP.put(SUCCESS, "操作成功");
        ERR_CODE_MSG_MAP.put(SYSTEM_UNKNOWN_EXCEPTION, "未知错误");
        ERR_CODE_MSG_MAP.put(ERR_CODE_COM_REQURIE, "参数错误,请输入必填的参数或参数验证失败");
        ERR_CODE_MSG_MAP.put(INTERNAL_REQUEST_INVALID, "内部接口code签名错误或请求超时");
        ERR_CODE_MSG_MAP.put(INVALID_ACCOUNT, "账号不存在或异常");
        ERR_CODE_MSG_MAP.put(PROXY_SHPP_API_EXCEPTION, "代理搜狐Passport接口HTTP请求发生异常");
        ERR_CODE_MSG_MAP.put(INVOKE_BEYOND_FREQUENCY_LIMIT,"接口调用频次超限");
        ERR_CODE_MSG_MAP.put(ERR_ACCESS_TOKEN, "access_token错误");
        ERR_CODE_MSG_MAP.put(INVALID_CLIENTID, "client_id不存在");

        // oauth2授权
        ERR_CODE_MSG_MAP.put(INVALID_CLIENT, "client_id or client_secret不匹配");
        ERR_CODE_MSG_MAP.put(UNSUPPORTED_GRANT_TYPE, "不支持的grant_type");
        ERR_CODE_MSG_MAP.put(INVALID_REFRESH_TOKEN, "refresh_token不存在或过期");
        ERR_CODE_MSG_MAP.put(AUTHORIZE_FAIL, "授权失败");
        ERR_CODE_MSG_MAP.put(USERNAME_PWD_MISMATCH, "账号或密码错误");

        // account
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_REGED, "此帐号已注册，请直接登录");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_CANTSENTSMS, "今天的短信验证码已经达到上限啦");
        ERR_CODE_MSG_MAP.put(ERR_CODE_USERID_ILLEGAL, "非法userid");
        ERR_CODE_MSG_MAP.put(ERR_CODE_USER_ID_EXIST, "用户名已经存在");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_CANTSENTSMS, "手机短信发送频率超过限制");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_PHONEERROR, "呃，地球上没有这个手机号");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_MINUTELIMIT, "一分钟内只能发一条短信");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_NOTHASACCOUNT, "帐号不存在");
        ERR_CODE_MSG_MAP.put(USERNAME_PWD_ERROR, "用户名或密码不正确");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_SMSCODE, "验证码错误或已过期");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_CHECKSMSCODE_LIMIT, "今日验证码校验错误次数已超过上限");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_SMSCODE_SEND, "手机验证码发送失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_REGISTER_FAILED, "创建用户失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_LOGIN_FAILED, "用户登录失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_LOGIN_OPERACCOUNT_MISMATCH, "当前登录账号与操作账号不一致");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_CHECKLOGIN_FAILED, "账号未登录，请先登录");
        ERR_CODE_MSG_MAP.put(ERR_CODE_VERIFY_PASSWORD_FREQUENCY_LIMIT, "密码输入错误次数过多");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST, "当前账号或者IP登陆操作存在异常");

        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_PHONE_NOT_MATCH_SMSCODE, "验证码错误或已过期");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_PHONE_OBTAIN_FIELDS, "手机账号不存在或手机号未被绑定");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_RESETPASSWORD_FAILED, "重置密码失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_ALREADY_ACTIVED_FAILED, "已经激活，无需再次激活");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_CAPTCHA_CODE_FAILED, "验证码验证失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_ACTIVED_URL_FAILED, "激活链接已经失效");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_PHONE_BINDED, "手机号已绑定其他账号");

        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_REGISTER_LIMITED, "当日注册次数已达上限");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_NO_ACTIVED_FAILED, "账号未激活");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_KILLED, "账号已封杀");
        ERR_CODE_MSG_MAP.put(ERR_CODE_NOTSUPPORT_SOHU_REGISTER, "暂时不支持sohu域内邮箱注册");
        ERR_CODE_MSG_MAP.put(ERR_CODE_APPCONNECT_TOKEN_ERROR, "手机app校验第三方登录的token失败");

        ERR_CODE_MSG_MAP.put(ERR_CODE_CREATE_COOKIE_FAILED, "生成cookie失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_RESETPASSWORD_LIMITED, "当日修改或重置密码次数已达上限");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_SENDEMAIL_LIMITED, "当日邮件发送次数已达上限");

        // account bind
        ERR_CODE_MSG_MAP.put(BIND_CONNECT_ACCOUNT_FAIL, "绑定第三方账号失败");
        ERR_CODE_MSG_MAP.put(CONNOT_BIND_SAME_TYPE_ACCOUNT, "不能绑定与主账号同一类型的账号");
        ERR_CODE_MSG_MAP.put(NOTALLOWED_REPEAT_BIND_SAME_TYPE_ACCOUNT, "不允许重复绑定同一类型的账号");
        ERR_CODE_MSG_MAP.put(ACCOUNT_ALREADY_REG_OR_BIND, "此账号已经注册或绑定过，无法再次绑定");
        ERR_CODE_MSG_MAP.put(ERR_CODE_PHONE_UNBIND_FAILED, "手机解除绑定失败");


        // acount secure info
        ERR_CODE_MSG_MAP.put(NOTHAS_BINDINGEMAIL, "未绑定邮箱");
        ERR_CODE_MSG_MAP.put(NOTHAS_BINDINGQUESTION, "未设置密保问题及答案");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNTSECURE_CHECKANSWER_FAILED, "密保答案错误");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNTSECURE_RESETPWD_URL_FAILED, "重置密码链接失效");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNTSECURE_SENDEMAIL_FAILED, "申请邮件发送失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNTSECURE_CHECKOLDEMAIL_FAILED, "旧绑定邮箱错误");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNTSECURE_BINDEMAIL_FAILED, "绑定邮箱失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNTSECURE_BINDEMAIL_URL_FAILED, "绑定邮箱申请链接失效");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNTSECURE_RESETPWD_EMAIL_FAILED, "重置密码申请邮箱不可用");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNTSECURE_BINDMOBILE_FAILED, "绑定手机失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNTSECURE_BIND_FAILED, "修改密保链接失效");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNTSECURE_BINDQUES_FAILED, "绑定密保问题失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_PHONE_NOBIND, "手机号未绑定账号");
        ERR_CODE_MSG_MAP.put(ERR_CODE_PHONE_BIND_FREQUENCY_LIMIT, "手机绑定次数超限");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNTSECURE_RESETPWD_LIMIT, "修改密码频率过于频繁");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNTSECURE_USER_LOGIN_SUCC_RECENTLY, "用户5日内登录过");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNTSECURE_MOBILEUSER_NOTALLOWED, "手机账号不允许此操作");


        // profile
        ERR_CODE_MSG_MAP.put(ERR_CODE_PROFILE_FIELD, "字段非法");

        // connect
        ERR_CODE_MSG_MAP.put(CONNECT_REQUEST_FREQUENCY_LIMIT, "超过第三方接口的访问限制");
        ERR_CODE_MSG_MAP.put(CONNECT_USER_DENIED_LOGIN, "用户拒绝登录授权");
        ERR_CODE_MSG_MAP.put(OAUTH_AUTHZ_STATE_INVALID, "第三方授权的state被篡改");
        ERR_CODE_MSG_MAP.put(CONNECT_TOKEN_INVALID, "第三方帐号Token过期，请重新登录");
        ERR_CODE_MSG_MAP.put(HTTP_CLIENT_REQEUST_FAIL, "发送HTTP请求失败");
        ERR_CODE_MSG_MAP.put(UNSUPPORT_THIRDPARTY, "该接口不支持指定第三方");
        ERR_CODE_MSG_MAP.put(INVALID_OPENOAUTH_REQUEST, "无效的OAuth2.0授权验证请求");
        ERR_CODE_MSG_MAP.put(REQUEST_NO_AUTHORITY, "用户没有对该api进行授权");
        ERR_CODE_MSG_MAP.put(ERR_CODE_CONNECT_OBTAIN_OPENID_ERROR, "第三方openid获取失败");

        // info
        ERR_CODE_MSG_MAP.put(PIC_URL_NOT_NULL, "图片url不能为空");
        ERR_CODE_MSG_MAP.put(ADD_SHARE_FAIL, "发布失败");
        ERR_CODE_MSG_MAP.put(NO_OPEN_BLOG, "还没开通微博呢");
        ERR_CODE_MSG_MAP.put(PIC_NOT_NULL, "上传图片不能为空");
        ERR_CODE_MSG_MAP.put(UPDATE_MULTIPART_IMAGE, "请使用multpart格式上传图片");
        ERR_CODE_MSG_MAP.put(UNSUPPORT_IMAGE_FORMAT, "不支持的图片类型");
        ERR_CODE_MSG_MAP.put(REPEAT_CONTENT, "同样的内容请勿重复发送");

        // friend
        ERR_CODE_MSG_MAP.put(FOLLOW_ACCOUNT_NOT_EXISTS, "您关注的用户不存在或未注册过帐号");
        ERR_CODE_MSG_MAP.put(ALREADY_FOLLOWED, "已经关注此用户");

        //反馈相关
        ERR_CODE_MSG_MAP.put(ERR_CODE_PROBLEM_INSERT_FAILED, "提交用户反馈失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_PROBLEMANSWER_INSERT_FAILED, "提交反馈评论失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_PROBLEM_CLOSE_FAILED, "更新反馈状态失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_PROBLEM_NOT_LOGIN, "您还未登陆，不能提交反馈");
        ERR_CODE_MSG_MAP.put(ERR_CODE_PROBLEM_ADDTIMES_LIMITED, "您一天内提交反馈的次数超过限制");
    }

    public static Map<String, String> getERR_CODE_MSG_MAP() {
        return ERR_CODE_MSG_MAP;
    }

    public static String getERR_CODE_MSG(String code) {
        return ERR_CODE_MSG_MAP.get(code);
    }

    public static void setERR_CODE_MSG_MAP(Map<String, String> eRR_CODE_MSG_MAP) {
        ERR_CODE_MSG_MAP = eRR_CODE_MSG_MAP;
    }

}
