package com.sogou.upd.passport.web.account.action.mapp;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.lang.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 移动端终端属性
 * User: shipengzhi
 * Date: 14-11-22
 * Time: 下午7:59
 * To change this template use File | Settings | File Templates.
 */
public class TerminalAttributeDO {

    private String carrier;  //运营商
    private String resolution; //分辨率
    private String udid; //终端唯一标识
    private String userIp;  //用户ip

    public String getUdid() {
        return udid;
    }
    //op=&pm=Lenovo A760&SdkVersion=16&resolution=480x854&platform=android&platformV=4.1.2&udid=860227023442427SOGOUcb35e205-a936-48fa-9513-1e3c1b97e82c891872840933341&passportSdkV=1.11&clientId=1120&appV=1.0
    public TerminalAttributeDO(HttpServletRequest request) {
        String data = request.getHeader(CommonConstant.MAPP_REQUEST_HEADER_SIGN);
        Map attrMap = StringUtil.parseFormatStringToMap(data);
        this.udid = (String) attrMap.get("udid");
    }

}
