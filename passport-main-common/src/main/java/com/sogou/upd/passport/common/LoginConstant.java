package com.sogou.upd.passport.common;

/**
 * 登陆使用的常量类
 * User: ligang201716@sogou-inc.com
 * Date: 13-5-13
 * Time: 下午4:55
 */
public class LoginConstant {

    //nginx module验证完cookie之后会将用户的userid放在header中，这个是key
    public static final String USER_ID_HEADER="X-SohuPassport-UserId";


    /**
     * 连续登陆失败多少次需要用户在登陆时输入验证码 >=
     */
    public static final int LOGIN_FAILED_NEED_CAPTCHA_LIMIT_COUNT = 3;
    /**
     * 某IP连续登陆失败多少次需要用户在登陆时输入验证码 >=
     */
    public static final int LOGIN_FAILED_NEED_CAPTCHA_IP_LIMIT_COUNT = 50;
    /**
    * 一小时内用户登陆成功多少次之后，用户不能再登陆
    */
    public static final int LOGIN_SUCCESS_EXCEED_MAX_LIMIT_COUNT = 100;
    /**
     * 一小时内用户登陆失败多少次之后，用户不能再登陆
     */
    public static final int LOGIN_FAILED_EXCEED_MAX_LIMIT_COUNT = 10;
    /**
     * 一小时内IP登陆成功多少次之后，用户不能再登陆
     */
    public static final int LOGIN_IP_SUCCESS_EXCEED_MAX_LIMIT_COUNT = 1000;
    /**
     * 一小时内IP登陆失败多少次之后，用户不能再登陆
     */
    public static final int LOGIN_IP_FAILED_EXCEED_MAX_LIMIT_COUNT = 100;

    /**
     *  密码修改一天限制次数
     */
    public static final int RESETNUM_LIMITED = 10; // 密码修改一天限制次数

    /**
     * 一天内某一个ip注册次数限制
     */
    public static final int IP_LIMITED = 20; // ip一天限制次数

}
