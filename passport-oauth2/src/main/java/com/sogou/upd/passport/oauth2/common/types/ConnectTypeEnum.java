package com.sogou.upd.passport.oauth2.common.types;

import com.google.common.base.Strings;
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
    TOKEN("token"),   //桌面应用
    MAPP("mapp"),   //手机app，基于服务器端调用检验token的接口
    MOBILE("mobile"),  //仅搜狗地图的移动端使用
    WAP("wap"),  //手机wap
    PC("pc"); //pc客户端，浏览器4.2以上版本

    private String connectType;

    private static final List<String> TYPE_LIST = Lists.newArrayList();

    static {
        TYPE_LIST.add(WEB.toString());
        TYPE_LIST.add(TOKEN.toString());
        TYPE_LIST.add(MAPP.toString());
        TYPE_LIST.add(PC.toString());
        TYPE_LIST.add(MOBILE.toString());
        TYPE_LIST.add(WAP.toString());
    }

    ConnectTypeEnum(String connectType) {
        this.connectType = connectType;
    }

    public static boolean isSupportType(String type) {
        return TYPE_LIST.contains(type);
    }

    /*
     * 是否为移动客户端，type=mapp/mobile
     */
    public static boolean isMobileApp(String type) {
        return type.equals(ConnectTypeEnum.MAPP.toString());
    }
    /*
    * 是否为移动客户端，type=mapp/mobile
    */
    public static boolean isMobileWap(String type) {
        return type.equals(ConnectTypeEnum.WAP.toString());
    }
    /*
    * 是否为web端，type=web或空
    */
    public static boolean isWeb(String type) {
        return Strings.isNullOrEmpty(type) || type.equals(ConnectTypeEnum.WEB.toString());
    }

    @Override
    public String toString() {
        return connectType;
    }
}
