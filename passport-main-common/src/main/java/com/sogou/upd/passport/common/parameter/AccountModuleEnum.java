package com.sogou.upd.passport.common.parameter;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-3 Time: 下午4:04 To change this template use
 * File | Settings | File Templates.
 */
public enum AccountModuleEnum {
    UNKNOWN(0, null, "其他"),
    REGISTER(1, "register", "注册"),
    LOGIN(2, "login", "登录"),
    RESETPWD(3, "findpwd", "找回密码"),
    BIND(4, "bind", "绑定密保")
    ;

    private int value;
    private String direct;
    private String description;

    AccountModuleEnum(int value, String direct, String description) {
        this.value = value;
        this.direct = direct;
        this.description = description;
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
