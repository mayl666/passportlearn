package com.sogou.upd.passport.common.utils;

import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CommonConstant;

import java.util.Map;

public class ErrorUtil {

    public static Map<String, String> ERR_CODE_MSG_MAP = Maps.newHashMap();

    /**
     * ************************通用的错误代码start********************************
     */
    // 系统异常错误
    public static final String SYSTEM_UNKNOWN_EXCEPTION = "10001";
    // 必填的参数错误
    public static final String ERR_CODE_COM_REQURIE = "10002";
    // 签名错误
//    public static final String ERR_CODE_COM_SING = "10003";
    // access_token错误
    public static final String ERR_ACCESS_TOKEN = "10005";
    // 签名或accessToken验证失败
//    public static final String ERR_OPEN_ID = "10006";
    // 账号不存在或异常或未激活
    public static final String INVALID_ACCOUNT = "10009";
    //client_id不存在
    public static final String INVALID_CLIENTID = "10010";
    //client_id格式不正确
    public static final String ERR_FORMAT_CLIENTID = "10011";
    //***************************通用的错误代码end*********************************

    //***************************OAuth2授权错误码start******************************
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
    public static final String USERNAME_PWD_MISMATCH = "111";
    //***************************OAuth2授权错误码end******************************

    //***************************account 服务的错误代码start*********************************
    // 帐号已经注册，请直接登录
    public static final String ERR_CODE_ACCOUNT_REGED = "20201";
    // 短信发送已达今天的最高上限20条
    public static final String ERR_CODE_ACCOUNT_CANTSENTSMS = "20202";
    // 手机号格式错误
    public static final String ERR_CODE_ACCOUNT_PHONEERROR = "20203";
    // 一分钟内只能发一条短信
    public static final String ERR_CODE_ACCOUNT_MINUTELIMIT = "20204";
    // 没有这个用户
    public static final String ERR_CODE_ACCOUNT_NOTHASACCOUNT = "20205";
    // 验证码不正确，或已过期
    public static final String ERR_CODE_ACCOUNT_SMSCODE = "20208";
    // 当日短信验证错误次数已超过上限
    public static final String ERR_CODE_ACCOUNT_CHECKSMSCODE_LIMIT = "20209";
    // 昵称验证失败
//    public static final String ERR_CODE_ACCOUNT_VERIFY_FIELDS = "20212";
    // 手机验证码发送失败
    public static final String ERR_CODE_ACCOUNT_SMSCODE_SEND = "20213";
    //用户允许注册，但注册失败
    public static final String ERR_CODE_ACCOUNT_REGISTER_FAILED = "20214";
    //验证码错误或已过期
    public static final String ERR_CODE_ACCOUNT_PHONE_NOT_MATCH_SMSCODE = "20216";
    //手机号获取失败,没有此用户
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
    // 手机号已注册或绑定
    public static final String ERR_CODE_ACCOUNT_PHONE_BINDED = "20225";
    //登录失败
    public static final String ERR_CODE_ACCOUNT_LOGIN_FAILED = "20226";
    //密码必须为字母和数字且长度大于6位!
    public static final String ERR_CODE_ACCOUNT_PWDERROR = "20227";
    // 当前登录账号与所操作账号不一致
    public static final String ERR_CODE_ACCOUNT_LOGIN_OPERACCOUNT_MISMATCH = "20228";
    // 账号未登录，请先登录
    public static final String ERR_CODE_ACCOUNT_CHECKLOGIN_FAILED = "20229";
    // 登陆用户或者ip在黑名单中
    public static final String ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST = "20230";
    // 登陆账号未激活
    public static final String ERR_CODE_ACCOUNT_NO_ACTIVED_FAILED = "20231";
    // 登陆账号被封杀
    public static final String ERR_CODE_ACCOUNT_KILLED = "20232";
    //***************************account 服务的错误代码end*********************************


    //***************************账号绑定相关的错误代码start*********************************
    // 绑定第三方账号失败
    public static final String BIND_CONNECT_ACCOUNT_FAIL = "20250";
    // 不能绑定与主账号同一类型的账号
    public static final String CONNOT_BIND_SAME_TYPE_ACCOUNT = "20251";
    // 不允许重复绑定同一类型的账号
    public static final String NOTALLOWED_REPEAT_BIND_SAME_TYPE_ACCOUNT = "20252";
    // 此账号已经注册或绑定过
    public static final String ACCOUNT_ALREADY_REG_OR_BIND = "20253";

    //***************************账号绑定相关的错误代码end*********************************

    //***************************密保方式相关的错误代码start*********************************
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

    //***************************密保方式相关的错误代码end*********************************


    //***************************profile 服务的错误代码start*********************************
    // 字段非法
    public static final String ERR_CODE_PROFILE_FIELD = "20301";


    //***************************profile 服务的错误代码end*********************************

    //***************************IMAGE 服务的错误代码start*********************************
    // 上传头像的文件扩展名不对
    public static final String ERR_CODE_PROFILE_IMGEXT = "20401";
    // 上传错误，没有找到上传的文件
    public static final String ERR_CODE_PROFILE_FILE = "20402";
    // 上传头像不成功，更新数据库错误
    public static final String ERR_CODE_PROFILE_UPDATE = "20403";
    // 获取图片的URL出错或者服务器连不上
    public static final String ERR_CODE_ACHIEVE_PROFILE = "20404";

    //***************************IMAGE 服务的错误代码end*********************************

    /**
     * ************************Connect通用的错误代码start********************************
     */
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

    //***************************通用的错误代码end*********************************

    //***************************Friend 服务的错误代码start************************
    // 无法读入上传文件
    public static final String READ_FILE_FAILED = "30301";
    // 上传文件失败
    public static final String UPDATE_FILE_FAILED = "30302";
    // 用户没有手机帐号，无法获取通讯录好友
    public static final String PHONE_ACCOUNT_NOT_EXISTS = "30303";
    // 关注的用户不存在
    public static final String FOLLOW_ACCOUNT_NOT_EXISTS = "30310";
    // 已经关注此用户
    public static final String ALREADY_FOLLOWED = "30311";
    //***************************Friend 服务的错误代码end*******************************

    //***************************信息类API错误代码start*********************************
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

    //***************************信息类API错误代码end*********************************

    //***************************好友类API错误代码start********************************
    // 上传的通讯录file不能为空
    public static final String UPDATE_CONATCT_NOT_NULL = "30501";

    //***************************好友类API错误代码end********************************

    static {
        // 通用错误
        ERR_CODE_MSG_MAP.put(SYSTEM_UNKNOWN_EXCEPTION, "未知错误");
        ERR_CODE_MSG_MAP.put(ERR_CODE_COM_REQURIE, "参数错误,请输入必填的参数或参数验证失败");
        ERR_CODE_MSG_MAP.put(INVALID_ACCOUNT, "账号不存在或异常");
        ERR_CODE_MSG_MAP.put(ERR_ACCESS_TOKEN, "access_token错误");
        ERR_CODE_MSG_MAP.put(INVALID_CLIENTID, "client_id不存在");
        ERR_CODE_MSG_MAP.put(ERR_FORMAT_CLIENTID, "client_id格式不正确");

        // oauth2授权
        ERR_CODE_MSG_MAP.put(INVALID_CLIENT, "client_id or client_secret不匹配");
        ERR_CODE_MSG_MAP.put(UNSUPPORTED_GRANT_TYPE, "不支持的grant_type");
        ERR_CODE_MSG_MAP.put(INVALID_REFRESH_TOKEN, "refresh_token不存在或过期");
        ERR_CODE_MSG_MAP.put(AUTHORIZE_FAIL, "授权失败");
        ERR_CODE_MSG_MAP.put(USERNAME_PWD_MISMATCH, "账号或密码错误");

        // account
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_REGED, "此帐号已注册，请直接登录");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_CANTSENTSMS, "今天的短信已经到20条上限啦");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_PHONEERROR, "呃，地球上没有这个手机号");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_MINUTELIMIT, "一分钟内只能发一条短信");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_NOTHASACCOUNT, "帐号不存在");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_SMSCODE, "验证码错误或已过期");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_CHECKSMSCODE_LIMIT, "今日短信验证错误次数已超过上限");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_SMSCODE_SEND, "手机验证码发送失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_REGISTER_FAILED, "用户注册失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_LOGIN_FAILED, "用户登录失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_PWDERROR, "密码必须为字母和数字且长度大于6位!");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_LOGIN_OPERACCOUNT_MISMATCH, "当前登录账号与操作账号不一致");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_CHECKLOGIN_FAILED, "账号未登录，请先登录");

        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_PHONE_NOT_MATCH_SMSCODE, "验证码错误或已过期");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_PHONE_OBTAIN_FIELDS, "手机号获取失败，或没有此用户");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_RESETPASSWORD_FAILED, "重置密码失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_ALREADY_ACTIVED_FAILED, "已经激活，无需再次激活");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_CAPTCHA_CODE_FAILED, "验证码验证失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_ACTIVED_URL_FAILED, "激活链接已经失效");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_PHONE_BINDED, "手机号已注册或绑定");

        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_REGISTER_LIMITED, "当日注册次数已达上限");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST, "当前账号或者IP存在异常");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_NO_ACTIVED_FAILED, "登陆账号未激活");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_KILLED, "登陆账号被封杀");

        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_RESETPASSWORD_LIMITED, "当日修改或重置密码次数已达上限");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_SENDEMAIL_LIMITED, "当日邮件发送次数已达上限");

        // account bind
        ERR_CODE_MSG_MAP.put(BIND_CONNECT_ACCOUNT_FAIL, "绑定第三方账号失败");
        ERR_CODE_MSG_MAP.put(CONNOT_BIND_SAME_TYPE_ACCOUNT, "不能绑定与主账号同一类型的账号");
        ERR_CODE_MSG_MAP.put(NOTALLOWED_REPEAT_BIND_SAME_TYPE_ACCOUNT, "不允许重复绑定同一类型的账号");
        ERR_CODE_MSG_MAP.put(ACCOUNT_ALREADY_REG_OR_BIND, "此账号已经注册或绑定过，无法再次绑定");

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

        // profile
        ERR_CODE_MSG_MAP.put(ERR_CODE_PROFILE_FIELD, "字段非法");

        // image
        ERR_CODE_MSG_MAP.put(ERR_CODE_PROFILE_IMGEXT, "图片扩展名不对，支持的格式为：bmp, gif, jpg, jpeg, png");
        ERR_CODE_MSG_MAP.put(ERR_CODE_PROFILE_FILE, "没有找到上传的文件");
        ERR_CODE_MSG_MAP.put(ERR_CODE_PROFILE_UPDATE, "上传头像失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACHIEVE_PROFILE, "URL或服务器出错，待会儿再试试吧");

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
        ERR_CODE_MSG_MAP.put(UPDATE_CONATCT_NOT_NULL, "通讯录是空的哦");
        ERR_CODE_MSG_MAP.put(READ_FILE_FAILED, "无法读取上传文件");
        ERR_CODE_MSG_MAP.put(UPDATE_FILE_FAILED, "上传通讯录失败");
        ERR_CODE_MSG_MAP.put(PHONE_ACCOUNT_NOT_EXISTS, "没找到你的通讯录呢");
        ERR_CODE_MSG_MAP.put(FOLLOW_ACCOUNT_NOT_EXISTS, "您关注的用户不存在或未注册过帐号");
        ERR_CODE_MSG_MAP.put(ALREADY_FOLLOWED, "已经关注此用户");

    }

    public static Map<String, Object> buildError(String code) {
        Map<String, Object> retMap = Maps.newHashMap();
        retMap.put(CommonConstant.RESPONSE_STATUS, code);
        retMap.put(CommonConstant.RESPONSE_STATUS_TEXT, ERR_CODE_MSG_MAP.get(code));
        return retMap;
    }

    public static Map<String, Object> buildError(String code, String msg) {
        Map<String, Object> retMap = Maps.newHashMap();
        retMap.put(CommonConstant.RESPONSE_STATUS, code);
        retMap.put(CommonConstant.RESPONSE_STATUS_TEXT, msg);
        return retMap;
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
