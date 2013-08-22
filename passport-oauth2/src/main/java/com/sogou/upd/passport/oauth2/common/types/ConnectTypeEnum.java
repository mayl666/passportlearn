package com.sogou.upd.passport.oauth2.common.types;

import com.google.common.collect.Lists;

import java.util.List;

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

    private static final List<String> TYPE_LIST = Lists.newArrayList();

    static {
        TYPE_LIST.add(WEB.toString());
        TYPE_LIST.add(TOKEN.toString());
        TYPE_LIST.add(APP.toString());
        TYPE_LIST.add(MAPP.toString());
    }

    ConnectTypeEnum(String connectType) {
        this.connectType = connectType;
    }

    public static boolean isSupportType(String type) {
        return TYPE_LIST.contains(type);
    }

    @Override
    public String toString() {
        return connectType;
    }
}
