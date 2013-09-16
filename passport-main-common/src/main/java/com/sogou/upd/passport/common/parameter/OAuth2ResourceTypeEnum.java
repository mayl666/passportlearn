package com.sogou.upd.passport.common.parameter;

import com.google.common.base.Strings;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-9-16
 * Time: 下午3:02
 * To change this template use File | Settings | File Templates.
 */
public enum OAuth2ResourceTypeEnum {
    GET_COOKIE("cookie.get"),     // 获取cookie值
    GET_FULL_USERINFO("full.get");  // 获取完整个人资料

    private String value;

    OAuth2ResourceTypeEnum(String value) {
        this.value = value;
    }

    public static boolean isEqual(String value, OAuth2ResourceTypeEnum resourceTypeEnum) {
        if (!Strings.isNullOrEmpty(value)) {
            return value.equals(resourceTypeEnum.toString());
        }
        return false;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
