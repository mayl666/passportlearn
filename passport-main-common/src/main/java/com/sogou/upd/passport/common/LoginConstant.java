package com.sogou.upd.passport.common;

/**
 * 登陆使用的常量类 User: ligang201716@sogou-inc.com Date: 13-5-13 Time: 下午4:55
 */
public class LoginConstant {

    //nginx module验证完cookie之后会将用户的userid放在header中，这个是key
    public static final String USER_ID_HEADER = "X-SohuPassport-UserId";

    //用户登陆的信息
    public static final String COOKIE_PPINF = "ppinf";
    //用户登陆的信息
    public static final String COOKIE_SGID = "sgid";

    //PPINF 的数字签名
    public static final String COOKIE_PPRDIG = "pprdig";
    //passport cookie
    public static final String COOKIE_PASSPORT = "passport";
    //ppinfo cookie
    public static final String COOKIE_PPINFO = "ppinfo";

    public static final String COOKIE_SGRDIG = "sgrdig";

    public static final String COOKIE_SGINF = "sginf";


    /**
     * 连续登陆失败多少次需要用户在登陆时输入验证码 >=
     */
    public static final int LOGIN_FAILED_NEED_CAPTCHA_LIMIT_COUNT = 3;
    /**
     * 某IP连续登陆失败多少次需要用户在登陆时输入验证码 >=
     */
    public static final int LOGIN_FAILED_NEED_CAPTCHA_IP_LIMIT_COUNT = 50;

    /**
     * 某IP连续登陆失败多少次需要用户在登陆时输入验证码 >=
     */
    public static final int LOGIN_FAILED_SUB_IP_LIMIT_COUNT = 5;
    /**
     * 一小时内用户登陆成功多少次之后，用户不能再登陆
     */
    public static final int LOGIN_SUCCESS_EXCEED_MAX_LIMIT_COUNT = 20;
    /**
     * 一小时内用户登陆失败多少次之后，用户不能再登陆
     */
    public static final int LOGIN_FAILED_EXCEED_MAX_LIMIT_COUNT = 10;
    /**
     * 一小时内IP登陆成功多少次之后，用户不能再登陆
     */
    public static final int LOGIN_IP_SUCCESS_EXCEED_MAX_LIMIT_COUNT = 500;

    /**
     * 内部接口一小时内某IP验证失败多少次之后，用户不能再登陆
     */
    public static final int AUTHUSER_IP_FAILED_EXCEED_MAX_LIMIT_COUNT = 50;
    /**
     * 密码修改一天限制次数
     */
    public static final int RESETNUM_LIMITED = 10; // 密码修改一天限制次数

    /**
     * 密码修改一个ip一天限制次数
     */
    public static final int UPDATENUM_IP_LIMITED = 50; // 密码修改一个ip一天限制次数

    /**
     * 设置密保一个ip一天限制次数
     */
    public static final int BINDNUM_IP_LIMITED = 100; // 密码修改一个ip一天限制次数

    /**
     * 内部接口安全限制50天一次
     */
    public static final int REGISTER_IP_COOKIE_LIMITED_FOR_INTERNAL = 25;//内部接口ip限制为50次一天

    /**
     * 一天内某一个ip注册次数限制
     */
    public static final int REGISTER_IP_LIMITED = 100; // ip一天限制次数

    /**
     * 一天内某一个ip注册时调用发送手机验证码接口次数限制
     */
    public static final int MOBILE_SEND_SMSCODE_LIMITED = 1000; //ip一天限制次数

    /**
     * 一天内某一个cookie注册次数限制
     */
    public static final int REGISTER_COOKIE_LIMITED = 5; // cookie一天限制次数

    /**
     * 一天内某一个ip+cookie注册次数限制
     */
    public static final int REGISTER_IP_COOKIE_LIMITED = 10; // cookie加ip一天限制次数

    /**
     * 一天内某一个ip提及反馈次数限制
     */
    public static final int ADDPROBLEM_IP_LIMITED = 100; // ip一天限制次数

    /**
     * 当username或者ip存在白名单时，存为1
     */
    public static final int IS_IN_WHITE_LIST = 1;

}
