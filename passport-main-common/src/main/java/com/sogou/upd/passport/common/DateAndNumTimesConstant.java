package com.sogou.upd.passport.common;

/**
 * User: mayan Date: 13-4-28 Time: 下午3:34 To change this template use
 * File | Settings | File Templates.
 */
public class DateAndNumTimesConstant {

    public static final long TIME_ONEDAY = 24 * 60 * 60; // 时间 一天 1440分钟 ,单位s
    public static final long TIME_TWODAY = 2 * 24 * 60 * 60; // 时间 两天 2880分钟 ,单位s
    public static final long IP_LIMITED = 20; // ip一天限制次数
    public static final int RESETPWD_NUM = 10; // 密码修改一天限制次数
    public static final int CHECKPWD_NUM = 10; // 密码检测一天限制次数
    public static final long CAPTCHA_INTERVAL = 60; // 注册验证码1分钟，单位s
    public static final long SECURECODE_VALID = 15 * 60;
    public static final long TIME_ONEHOUR = 60 * 60; // 时间 一小时,单位s
    public static final int BIND_LIMIT = 10;   // 每日绑定限制次数
    public static final int ACTIONRECORD_NUM = 10; // 动作记录条数
}
