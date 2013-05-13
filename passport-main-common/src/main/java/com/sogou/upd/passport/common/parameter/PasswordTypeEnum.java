package com.sogou.upd.passport.common.parameter;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-5-13
 * Time: 下午4:48
 * To change this template use File | Settings | File Templates.
 */
public enum PasswordTypeEnum {
    MD5(0),     // MD5(明文密码)
    Plaintext(1);  // 明文密码

    private int value;

    PasswordTypeEnum(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }

}
