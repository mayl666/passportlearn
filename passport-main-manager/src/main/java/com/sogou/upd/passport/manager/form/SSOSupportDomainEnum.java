package com.sogou.upd.passport.manager.form;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * 第三方登录连接附加域类型
 * User: shipengzhi
 * Date: 13-5-2
 * Time: 下午7:22
 * To change this template use File | Settings | File Templates.
 */
public enum SSOSupportDomainEnum {
    HAO("hao.qq.com"),         //hao.qq.com
    DAOHANG("daohang.qq.com"); //daohang.qq.com

    private String connectType;
    private static final List<String> DOMAIN_LIST = Lists.newArrayList();

    static {
        DOMAIN_LIST.add(HAO.toString());
        DOMAIN_LIST.add(DAOHANG.toString());
    }

    SSOSupportDomainEnum(String connectType) {
        this.connectType = connectType;
    }

    public static String getSupportDomain(String serverName) {
        if (StringUtils.isBlank(serverName)) {
            return null;
        }

        for (String domain : DOMAIN_LIST) {
            if (serverName.contains(domain)) {
                return "." + domain;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return connectType;
    }
}
