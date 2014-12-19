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

    private Map attrMap = null;


    //op=&pm=Lenovo A760&SdkVersion=16&resolution=480x854&platform=android&platformV=4.1.2&udid=860227023442427SOGOUcb35e205-a936-48fa-9513-1e3c1b97e82c891872840933341&passportSdkV=1.11&clientId=1120&appV=1.0
    public TerminalAttributeDO(HttpServletRequest request) {
        String data = request.getHeader(CommonConstant.MAPP_REQUEST_HEADER_SIGN);
        attrMap = StringUtil.parseFormatStringToMap(data);
    }

    public String getUdid() {
        return (String) attrMap.get("udid");
    }

    @Override
    public String toString() {
        return String.valueOf(attrMap.get("op")) + "\t" + String.valueOf(attrMap.get("pm")) + "\t" + String.valueOf(attrMap.get("SdkVersion")) + "\t" + String.valueOf(attrMap.get("resolution")) + "\t" + String.valueOf(attrMap.get("platform")) + "\t" + String.valueOf(attrMap.get("platformV")) + "\t" + String.valueOf(attrMap.get("udid"));
    }

}
