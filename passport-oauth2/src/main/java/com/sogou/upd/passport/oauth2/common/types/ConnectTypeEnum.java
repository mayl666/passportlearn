package com.sogou.upd.passport.oauth2.common.types;

/**
 * 第三方登录连接应用类型
 * User: shipengzhi
 * Date: 13-5-2
 * Time: 下午7:22
 * To change this template use File | Settings | File Templates.
 */
public enum ConnectTypeEnum {

    WEB("web"),
    TOKEN("token"),
    APP("app"),
    MAPP("mapp");

    private String connectType;

    ConnectTypeEnum(String connectType) {
        this.connectType = connectType;
    }

    @Override
    public String toString() {
        return connectType;
    }
}
