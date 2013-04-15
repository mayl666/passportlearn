package com.sogou.upd.passport.common.parameter;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-3-24
 * Time: 下午5:26
 * To change this template use File | Settings | File Templates.
 */
public enum AccountStatusEnum {

    REGULAR(1), // 正式用户
    DISABLED(2), // 未激活
    KILLED(3); // 封杀用户

    public int value;

    AccountStatusEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
