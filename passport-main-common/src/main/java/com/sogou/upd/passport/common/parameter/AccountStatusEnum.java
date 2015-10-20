package com.sogou.upd.passport.common.parameter;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-3-24
 * Time: 下午5:26
 * To change this template use File | Settings | File Templates.
 */
public enum AccountStatusEnum {

    DISABLED(0), // 未激活
    REGULAR(1), // 正式用户
    KILLED(2), // 封杀用户
    LEAKED(3);//风险账号

    public int value;

    AccountStatusEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
