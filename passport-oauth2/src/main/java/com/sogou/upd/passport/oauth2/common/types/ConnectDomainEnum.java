package com.sogou.upd.passport.oauth2.common.types;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.util.List;

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
    SHURUFA("shurufa.qq.com"); //shurufa.qq.com


    private String connectType;
    private static final List<String> DOMAIN_LIST = Lists.newArrayList();

    static {
        DOMAIN_LIST.add(HAO.toString());
        DOMAIN_LIST.add(DAOHANG.toString());
        DOMAIN_LIST.add(SHURUFA.toString());
    }

    ConnectDomainEnum(String connectType) {
        this.connectType = connectType;
    }

    public static boolean isSupportDomain(String domain) {
        return DOMAIN_LIST.contains(domain);
    }

    @Override
    public String toString() {
        return connectType;
    }
}
