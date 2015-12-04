package com.sogou.upd.passport.common.parameter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * 第三方登录连接附加域类型
 * User: shipengzhi
 * Date: 13-5-2
 * Time: 下午7:22
 * To change this template use File | Settings | File Templates.
 */
public enum ConnectDomainEnum {
    HAO("hao.qq.com"),         //hao.qq.com
    DAOHANG("daohang.qq.com"), //daohang.qq.com
    SHURUFA("shurufa.qq.com"), //shurufa.qq.com
    PY("py.qq.com"), //py.qq.com
    PINYIN("pinyin.qq.com"), //pinyin.qq.com
    WUBI("wubi.qq.com"), // wubi.qq.com
    SHURU("shuru.qq.com"), //shuru.qq.com
    PINYIN_CN("qq.pinyin.cn"), //qq.pinyin.cn
    TEEMO("teemo.cn"); // www.teemo.cn

    private String connectType;
    private static final List<String> DOMAIN_LIST = Lists.newArrayList();
    private static final Map<String, String> DOMAIN_SSOCOOKIE_URL_MAP = Maps.newHashMap();

    static {
        DOMAIN_LIST.add(HAO.toString());
        DOMAIN_LIST.add(DAOHANG.toString());
        DOMAIN_LIST.add(SHURUFA.toString());
        DOMAIN_LIST.add(PY.toString());
        DOMAIN_LIST.add(PINYIN.toString());
        DOMAIN_LIST.add(WUBI.toString());
        DOMAIN_LIST.add(SHURU.toString());
        DOMAIN_LIST.add(PINYIN_CN.toString());
        DOMAIN_LIST.add(TEEMO.toString());

        DOMAIN_SSOCOOKIE_URL_MAP.put(HAO.toString(), "http://account.hao.qq.com/sso/setcookie");
        DOMAIN_SSOCOOKIE_URL_MAP.put(DAOHANG.toString(), "https://account.daohang.qq.com/sso/setcookie");
        DOMAIN_SSOCOOKIE_URL_MAP.put(SHURUFA.toString(), "http://account.shurufa.qq.com/sso/setcookie");
        DOMAIN_SSOCOOKIE_URL_MAP.put(PINYIN_CN.toString(), "http://account.qq.pinyin.cn/sso/setcookie");
        DOMAIN_SSOCOOKIE_URL_MAP.put(TEEMO.toString(), "https://account.teemo.cn/sso/setcookie");
    }

    ConnectDomainEnum(String connectType) {
        this.connectType = connectType;
    }

    public static boolean isSupportDomain(String domain) {
        return DOMAIN_LIST.contains(domain);
    }

    public static String getSSOCookieUrl(String domain){
        return  DOMAIN_SSOCOOKIE_URL_MAP.get(domain);
    }

    @Override
    public String toString() {
        return connectType;
    }
}
