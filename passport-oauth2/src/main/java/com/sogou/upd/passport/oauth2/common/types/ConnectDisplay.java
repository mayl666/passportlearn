package com.sogou.upd.passport.oauth2.common.types;

import com.google.common.collect.Lists;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.oauth2.openresource.parameters.QQOAuth;

import java.util.List;

/**
 * 第三方登录授权display参数列表
 *
 * @author shipengzhi
 */
public class ConnectDisplay {

    private static final List<String> QQ_DISPLAY = Lists.newArrayList();
    private static final List<String> SINA_DISPLAY = Lists.newArrayList();
    private static final List<String> RENREN_DISPLAY = Lists.newArrayList();
    private static final List<String> TAOBAO_DISPLAY = Lists.newArrayList();
    private static final List<String> BAIDU_DISPLAY = Lists.newArrayList();

    static {
        QQ_DISPLAY.add(QQOAuth.MOBILE_DISPLAY); //mobile端下的样式,触屏版，适用于支持html5的手机
        QQ_DISPLAY.add(QQOAuth.WML_DISPLAY);  //1：wml版本
        QQ_DISPLAY.add(QQOAuth.XHTML_DISPLAY);  //2：xhtml版本
    }

    static {
        SINA_DISPLAY.add("mobile"); //移动终端的授权页面，适用于支持html5的手机
        SINA_DISPLAY.add("client"); //客户端版本授权页面，适用于PC桌面应用。
        SINA_DISPLAY.add("wap"); //wap版授权页面，适用于非智能手机。
    }

    static {
        RENREN_DISPLAY.add("page"); //适用于Web端，最小尺寸（575px*405px），当浏览器宽度过窄时，会页面会自适应，最小尺寸（290px*580px）；
        RENREN_DISPLAY.add("iframe");  //自适应显示在一个iframe中，适用于网页中的iframe
        RENREN_DISPLAY.add("popup");  //弹框形式的授权页面，适用于桌面应用的弹出窗口
        RENREN_DISPLAY.add("mobile"); //适用于移动终端，最小尺寸（320px*480px）;
        RENREN_DISPLAY.add("touch"); //适用于不支持js的移动终端，最小尺寸（480*800px）；
    }

    static {
        TAOBAO_DISPLAY.add("web");  //普通的PC端（淘宝logo）浏览器页面样式。
        TAOBAO_DISPLAY.add("tmall");  //对应天猫的浏览器页面样式。
        TAOBAO_DISPLAY.add("wap");   //无线端的浏览器页面样式。
    }

    static {
        BAIDU_DISPLAY.add("page");  //全屏形式的授权页面(默认)，适用于web应用。
        BAIDU_DISPLAY.add("popup");  //弹框形式的授权页面，适用于桌面软件应用和web应用。
        BAIDU_DISPLAY.add("mobile");   //适用于Iphone/Android等智能移动终端上的应用。
        BAIDU_DISPLAY.add("pad");   //IPad/Android等智能平板电脑使用的授权页面。
    }

    public static boolean isSupportDisplay(String display, String provider) {
        if (provider.equals(AccountTypeEnum.QQ.toString())) {
            if (QQ_DISPLAY.contains(display)) return true;
        }
        if (provider.equals(AccountTypeEnum.SINA.toString())) {
            if (SINA_DISPLAY.contains(display)) return true;
        }
        if (provider.equals(AccountTypeEnum.RENREN.toString())) {
            if (RENREN_DISPLAY.contains(display)) return true;
        }
        if (provider.equals(AccountTypeEnum.TAOBAO.toString())) {
            if (TAOBAO_DISPLAY.contains(display)) return true;
        }

        return false;
    }

}
