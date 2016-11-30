package com.sogou.upd.passport.common.parameter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-3 Time: 下午4:04 To change this template use
 * File | Settings | File Templates.
 */
public enum AccountModuleEnum {
    UNKNOWN(0, null, "其他"),
    REGISTER(1, "register", "注册"),
    LOGIN(2, "login", "登录"),
    RESETPWD(3, "findpwd", "找回密码"),
    SECURE(4, "security", "安全"),
    USERINFO(5, "userinfo", "用户信息");

    private int value;
    private String direct;
    private String description;

    AccountModuleEnum(int value, String direct, String description) {
        this.value = value;
        this.direct = direct;
        this.description = description;
    }


    public static Map<AccountModuleEnum, String> buildEmailSubjects() {
        Map<AccountModuleEnum, String> subjects = new HashMap<>();
        subjects.put(AccountModuleEnum.RESETPWD, "搜狗通行证找回密码服务");
        subjects.put(AccountModuleEnum.SECURE, "搜狗通行证绑定邮箱服务");
        subjects.put(AccountModuleEnum.REGISTER, "搜狗通行证新用户注册服务");
        subjects.put(AccountModuleEnum.LOGIN, "搜狗通行证用户登录服务");
        subjects.put(AccountModuleEnum.USERINFO, "搜狗通行证用户信息服务");
        subjects.put(AccountModuleEnum.UNKNOWN, "搜狗通行证其他服务");

        return subjects;
    }
    
    public static Map<AccountModuleEnum, String> buildEnEmailSubjects() {
        Map<AccountModuleEnum, String> subjects = new HashMap<>();
        subjects.put(AccountModuleEnum.RESETPWD, "Sogou Pass find password service");
        return subjects;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getDirect() {
        return direct;
    }

    public void setDirect(String direct) {
        this.direct = direct;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
