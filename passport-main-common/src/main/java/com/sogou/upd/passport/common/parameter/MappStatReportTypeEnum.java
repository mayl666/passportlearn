package com.sogou.upd.passport.common.parameter;


import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.utils.PhoneUtil;

import java.util.List;
import java.util.Map;

/**
 * 账号类型，第三方，邮箱，手机号码
 *
 * @author shipengzhi
 */
public enum MappStatReportTypeEnum {

    EXCEPTION("exception"),         //异常
    INTERFACE("interface"), //接口响应
    NETFLOW("netflow"), //流量
    PRODUCT("product"), //产品
    COMMLOG("debuglog"); //普通日志

    private String type;
    MappStatReportTypeEnum(String type) {
        this.type = type;
    }

    private static final List<String> TYPE_LIST = Lists.newArrayList();
    static {
        TYPE_LIST.add(EXCEPTION.toString());
        TYPE_LIST.add(INTERFACE.toString());
        TYPE_LIST.add(NETFLOW.toString());
        TYPE_LIST.add(PRODUCT.toString());
        TYPE_LIST.add(COMMLOG.toString());
    }

    public static boolean isSupportType(String type) {
        return TYPE_LIST.contains(type);
    }

    @Override
    public String toString() {
        return type;
    }

}
