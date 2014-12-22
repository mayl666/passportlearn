package com.sogou.upd.passport.common.parameter;


import com.google.common.collect.Lists;

import java.util.List;

/**
 * 账号类型，第三方，邮箱，手机号码
 *
 * @author shipengzhi
 */
public enum MappStatReportTypeEnum {

    EXCEPTION("exception"),  //sdk异常
    INTERFACE("interface"), //接口响应
    NETFLOW("netflow"), //流量
    PRODUCT("product"), //产品
    ERROR("error"), //接口异常
    DEBUGLOG("debuglog"); //普通日志

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
        TYPE_LIST.add(ERROR.toString());
        TYPE_LIST.add(DEBUGLOG.toString());
    }

    public static boolean isSupportType(String type) {
        return TYPE_LIST.contains(type);
    }

    @Override
    public String toString() {
        return type;
    }

}
