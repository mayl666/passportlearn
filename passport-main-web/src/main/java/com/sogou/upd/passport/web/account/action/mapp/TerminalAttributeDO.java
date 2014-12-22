package com.sogou.upd.passport.web.account.action.mapp;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.utils.BeanUtil;
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
public class TerminalAttributeDO {

    private String op;
    private String pm;
    private String SdkVersion;
    private String resolution;
    private String platform;
    private String platformV;
    private String udid;

    public TerminalAttributeDO(HttpServletRequest request) {
        String data = request.getHeader(CommonConstant.MAPP_REQUEST_HEADER_SIGN);
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
        return SdkVersion;
    }

    public void setSdkVersion(String sdkVersion) {
        SdkVersion = sdkVersion;
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

    public String toHiveString() {
        return op + "\t" + pm + "\t" + SdkVersion + "\t" + resolution + "\t" + platform + "\t" + platformV + "\t" + udid + "\t";
    }

}
