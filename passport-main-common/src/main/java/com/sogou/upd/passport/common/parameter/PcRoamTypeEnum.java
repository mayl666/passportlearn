package com.sogou.upd.passport.common.parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * 桌面端登录态类型
 * iet-短有效期token加密串；iec-cookie加密串；pinyint--输入法短有效期token加密串
 * User: shipengzhi
 * Date: 14-7-27
 * Time: 下午6:11
 * To change this template use File | Settings | File Templates.
 */
public enum PcRoamTypeEnum {
    iet("iet"),
    iec("iec"),
    pinyint("pinyint");

    private String value;

    PcRoamTypeEnum(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
