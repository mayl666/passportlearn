package com.sogou.upd.passport.oauth2.common.types;

import com.google.common.collect.Lists;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;

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

    static {
        QQ_DISPLAY.add("mobile");
    }

    static {
        SINA_DISPLAY.add("mobile"); //移动终端的授权页面，适用于支持html5的手机
        SINA_DISPLAY.add("popup"); //弹窗类型的授权页，适用于web浏览器小窗口
        SINA_DISPLAY.add("wap1.2"); //wap1.2的授权页面。
        SINA_DISPLAY.add("wap2.0"); //wap2.0的授权页面。
        SINA_DISPLAY.add("js"); //微博JS-SDK专用授权页面，弹窗类型，返回结果为JSONP回掉函数。
    }

    static {
        RENREN_DISPLAY.add("mobile"); //适用于型号较老，没有全功能浏览器的手机使用。
        RENREN_DISPLAY.add("touch"); //适用于智能手机，拥有全功能的浏览器的手机使用。
    }

    static {
        TAOBAO_DISPLAY.add("web");  //普通的PC端（淘宝logo）浏览器页面样式。
        TAOBAO_DISPLAY.add("tmall");  //对应天猫的浏览器页面样式。
        TAOBAO_DISPLAY.add("wap");   //无线端的浏览器页面样式。
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
