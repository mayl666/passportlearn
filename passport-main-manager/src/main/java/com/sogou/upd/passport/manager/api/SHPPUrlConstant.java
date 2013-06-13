package com.sogou.upd.passport.manager.api;

/**
 * 搜狐passport内部接口的相关常量
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-6
 * Time: 上午10:39
 */
public class SHPPUrlConstant {


    public static final int APP_ID = 1100;

    public static final String APP_KEY = "yRWHIkB$2.9Esk>7mBNIFEcr:8\\[Cv";

    //请求SHPP时xml默认的rootNodeName
    public static final String DEFAULT_REQUEST_ROOTNODE = "info";

    //SHPP返回xml中状态码key
    public static final String RESULT_STATUS="status";

    /* ============================================================================ */
    /*  搜狐Passport内部接口URL地址                                                  */
    /* ============================================================================ */
    // 内部接口基本url
    private static final String BASE_INTERNAL_URL = "http://internal.passport.sohu.com/interface/";

    /*================================登录相关=======================================*/
    public static final String AUTH_USER = BASE_INTERNAL_URL + "authuser"; //检查用户名密码是否正确
    public static final String MOBILE_AUTH_TOKEN = BASE_INTERNAL_URL + "token/auth"; //检查用户名密码是否正确

    /*================================注册相关=======================================*/
    public static final String SEND_MOBILE_REG_CAPTCHA = BASE_INTERNAL_URL + "sendmobileregcaptcha"; //获取注册的手机验证码
    public static final String REG_MOBILE_CAPTCHA = BASE_INTERNAL_URL + "register/mobilecaptcha";  //手机号验证码注册

    /*================================手机绑定相关=======================================*/
    //绑定手机号
    public static final String BING_MOBILE = BASE_INTERNAL_URL + "wapbindmobile";
    //查询手机号绑定的账号
    public static final String QUERY_MOBILE_BING_ACCOUNT = BASE_INTERNAL_URL + "wapgetuserid";
    //解绑手机号
    public static final String UNBING_MOBILE = BASE_INTERNAL_URL + "wapunbindmobile";
    //查询手机号绑定的账号
    public static final String MOBILE_GET_USERID = BASE_INTERNAL_URL + "wapgetuserid";


    /*================================邮箱绑定相关=======================================*/
    //绑定邮箱
    public static final String BIND_EMAIL = BASE_INTERNAL_URL + "bindemail";

    /*================================修改密码=======================================*/
    //根据老密码修改新密码
    public static final String UPDATE_PWD = BASE_INTERNAL_URL + "updatepwd";

    //根据密保答案重置密码
    public static final String RESET_PWD_BY_QUES = BASE_INTERNAL_URL + "recoverpwd";

    //获取用户信息
    public static final String USER_INFO = BASE_INTERNAL_URL + "getuserinfo";
}
