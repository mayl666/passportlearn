package com.sogou.upd.passport.common.parameter;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-5-13
 * Time: 下午4:48
 * To change this template use File | Settings | File Templates.
 */
public enum PasswordTypeEnum {
    ORIGINAL(0),     //原始密码
    MD5(1),           //MD5加密
    CRYPT(2);         //crypt(MD5（password）, salt )salt salt=8位随机的a-zA-Z0-9
    //todo 为了避免与搜狐账号的密码类型冲突，等搜狗账号迁移完成后，需要增加一个表示无密码的值
    private int value;

    PasswordTypeEnum(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }

}
