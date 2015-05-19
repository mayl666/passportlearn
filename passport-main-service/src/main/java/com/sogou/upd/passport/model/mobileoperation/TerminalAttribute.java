package com.sogou.upd.passport.model.mobileoperation;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.utils.BeanUtil;
import com.sogou.upd.passport.exception.ServiceException;
import jodd.util.URLDecoder;
import org.apache.commons.collections.MapUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 移动端终端属性
 * User: shipengzhi
 * Date: 14-11-22
 * Time: 下午7:59
 * To change this template use File | Settings | File Templates.
 */
public class TerminalAttribute {

    private String op;  //运营商
    private String pm;  //机型
    private String sdkVersion; //操作系统版本code
    private String platform;  //操作系统
    private String platformV;  //操作系统版本string
    private String resolution;   //分辨率
    private String udid;    //唯一标示
    private String passportSdkV; //passport sdk版本
    private String appV;  //应用版本
    private String network;  //网络

    //op=&pm=Lenovo A760&sdkVersion=16&resolution=480x854&platform=android&platformV=4.1.2&udid=860227023442427SOGOUcb35e205-a936-48fa-9513-1e3c1b97e82c891872840933341&passportSdkV=1.11&clientId=1120&appV=1.0
    public TerminalAttribute(HttpServletRequest request) throws ServiceException {
        String data =  URLDecoder.decode(request.getHeader(CommonConstant.MAPP_REQUEST_HEADER_SIGN));
        if(StringUtil.isEmpty(data)){
            throw new ServiceException("解析异常");
        } else{
            Map attrMap = StringUtil.parseFormatStringToMap(data);
            if (!MapUtils.isEmpty(attrMap)) {
                Set keys = attrMap.keySet();
                Iterator it = keys.iterator();
                while (it.hasNext()) {
                    String key = String.valueOf(it.next());
                    BeanUtil.setBeanProperty(this, key, String.valueOf(attrMap.get(key)));
                }
            }
        }

    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getPm() {
        return pm;
    }

    public void setPm(String pm) {
        this.pm = pm;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    public void setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getPlatformV() {
        return platformV;
    }

    public void setPlatformV(String platformV) {
        this.platformV = platformV;
    }

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    public String getPassportSdkV() {
        return passportSdkV;
    }

    public void setPassportSdkV(String passportSdkV) {
        this.passportSdkV = passportSdkV;
    }

    public String getAppV() {
        return appV;
    }

    public void setAppV(String appV) {
        this.appV = appV;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String toHiveString() {
        return StringUtil.exchangeIfContains(StringUtil.defaultIfEmpty(op, "-"), "\t", "_") + "\t" + StringUtil.exchangeIfContains(StringUtil.defaultIfEmpty(pm, "-"), "\t", "_") + "\t" +
                StringUtil.exchangeIfContains(StringUtil.defaultIfEmpty(sdkVersion, "-"), "\t", "_") + "\t" + StringUtil.exchangeIfContains(StringUtil.defaultIfEmpty(platform, "-"), "\t", "_") + "\t" +
                StringUtil.exchangeIfContains(StringUtil.defaultIfEmpty(platformV, "-"), "\t", "_") + "\t" + StringUtil.exchangeIfContains(StringUtil.defaultIfEmpty(resolution, "-"), "\t", "_") + "\t" +
                StringUtil.exchangeIfContains(StringUtil.defaultIfEmpty(udid, "-"), "\t", "_") + "\t" + StringUtil.exchangeIfContains(StringUtil.defaultIfEmpty(passportSdkV, "-"), "\t", "_") + "\t" +
                StringUtil.exchangeIfContains(StringUtil.defaultIfEmpty(appV, "-"), "\t", "_") + "\t" + StringUtil.exchangeIfContains(StringUtil.defaultIfEmpty(network, "-"), "\t", "_");
    }

}
