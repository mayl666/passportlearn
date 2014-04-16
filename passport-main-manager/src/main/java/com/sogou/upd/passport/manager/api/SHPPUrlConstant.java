package com.sogou.upd.passport.manager.api;

/**
 * 搜狐passport内部接口的相关常量
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-6
 * Time: 上午10:39
 */
public class SHPPUrlConstant {


    public static final int APP_ID = 1120;
    public static final String APP_KEY = "4xoG%9>2Z67iL5]OdtBq$l#>DfW@TY";

    public static final String APPID_STRING = "appid";

    //请求SHPP时xml默认的rootNodeName
    public static final String DEFAULT_REQUEST_ROOTNODE = "info";

    //SHPP返回xml中状态码key
    public static final String RESULT_STATUS = "status";

    /* ============================================================================ */
    /*  搜狐Passport内部接口URL地址                                                  */
    /* ============================================================================ */
    // 内部接口基本url
    private static final String BASE_INTERNAL_URL = "http://internal.passport.sohu.com/interface/";
    private static final String BASE_URL = "https://passport.sohu.com/";
    // 第三方开放平台代理API基本url
    private static final String BASE_OPEN_API_URL = "http://internal.passport.sohu.com/openlogin/";

    /*================================登录相关=======================================*/
    public static final String AUTH_USER = BASE_INTERNAL_URL + "authuser"; //检查用户名密码是否正确
    public static final String MOBILE_AUTH_TOKEN = BASE_INTERNAL_URL + "token/auth"; //检查移动APP登录后token是否正确
    // 手机浏览器/authtoken接口返回结果后会302到setcookie接口，这个必须为http，所以此处也为http，得到location的也为http
    public static final String HTTPS_SET_COOKIE = BASE_URL + "act/setcookie";//用于前端设置cookie
    public static final String HTTP_SET_COOKIE = "http://passport.sohu.com/act/setcookie";//用于前端设置cookie
    public static final String CHECK_USER = BASE_INTERNAL_URL + "checkuser";//查询用户名是否注册过

    /*================================注册相关=======================================*/
    public static final String SEND_MOBILE_REG_CAPTCHA = BASE_INTERNAL_URL + "sendmobileregcaptcha"; //获取注册的手机验证码
    public static final String WEB_EMAIL_REG = BASE_INTERNAL_URL + "reguser";  //web端邮箱注册
    public static final String REG_MOBILE_NOCAPTCHA = BASE_INTERNAL_URL + "regmobiled";

    //todo 这个地方的接口名sohu接口开发完会提供，暂时命名
    public static final String GET_COOKIE_VALUE_FROM_SOHU = BASE_INTERNAL_URL + "getcookieinfo";

    /*================================手机绑定相关=======================================*/
    //绑定手机号
    public static final String BING_MOBILE = BASE_INTERNAL_URL + "wapbindmobile";
    //查询手机号绑定的账号
    public static final String QUERY_MOBILE_BING_ACCOUNT = BASE_INTERNAL_URL + "wapgetuserid";
    //解绑手机号
    public static final String UNBING_MOBILE = BASE_INTERNAL_URL + "wapunbindmobile";
    //发送手机验证码
    public static final String SEND_CAPTCHA = BASE_INTERNAL_URL + "sendcaptcha";
    //通过验证码绑定手机号
    public static final String BIND_MOBILE_CAPTCHA = BASE_INTERNAL_URL + "bindmobile";
    //通过验证码解绑手机号
    public static final String UNBIND_MOBILE_CAPTCHA = BASE_INTERNAL_URL + "unbindmobile";
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
    public static final String GET_USER_INFO = BASE_INTERNAL_URL + "getuserinfo";
    //更新用户信息
    public static final String UPDATE_USER_INFO = BASE_INTERNAL_URL + "updateuser";
    //修改用户昵称
    public static final String UPDATE_USER_UNIQNAME = BASE_INTERNAL_URL + "checkuniqname";

    /*================================安全中心操作URL=======================================*/
    // 修改密码
    public static final String SOHU_RESETPWD_URL = BASE_URL + "web/updateInfo.action?modifyType=password";
    // 修改密保邮箱
    public static final String SOHU_BINDEMAIL_URL = BASE_URL + "web/requestModifyEmailAction.action";
    // 修改密保手机
    public static final String SOHU_BINDMOBILE_URL = BASE_URL + "web/requestBindMobileAction.action";
    // 修改密保问题
    public static final String SOHU_BINDQUES_URL = BASE_URL + "web/updateInfo.action?modifyType=question";
    // 找回密码
    public static final String SOHU_FINDPWD_URL = BASE_URL + "web/RecoverPwdInput.action";

    /*================================第三方开放平台相关url=======================================*/
    //第三方发图片微博或分享
    public static final String CONNECT_SHARE_PIC = BASE_OPEN_API_URL + "api/share/add_pic";
    //第三方获取用户好友/互粉
    public static final String GET_CONNECT_FRIENDS_INFO = BASE_OPEN_API_URL + "api/friendship/friends";
}
