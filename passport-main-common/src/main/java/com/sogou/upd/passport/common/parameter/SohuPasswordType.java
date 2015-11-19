package com.sogou.upd.passport.common.parameter;

/**
 * Created by nahongxu on 2015/11/12.
 */
public enum  SohuPasswordType {
    TEXT(0),
    MD5(1);

    private int value;
    SohuPasswordType(int value){
        this.value=value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
