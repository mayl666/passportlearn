package com.sogou.upd.passport.model.account;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * 漫游用户在外域和搜狗域之间传递的对象
 * v:xxxx(版本号)|passportId:xxxx|status:xxxx（登录状态）|ct:xxxx(请求时间)
 * User: shipengzhi
 * Date: 14-7-30
 * Time: 下午1:59
 */
public class WebRoamDO {

    private static String KEY_SEP = "|"; //字符串中key和key之间的分隔符
    private static String VALUE_SEP = ":"; //字符串中key和value之间的分隔符
    public static String V = "v";
    public static String PASSPORTID = "passportId";
    public static String STATUS = "status";
    public static String CT = "ct";


    private String v; //值的版本
    private String passportId;  //外域的用户ID
    private int status; //状态，目前只有1--登录
    private long ct;  //漫游起始时刻点

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public String getPassportId() {
        return passportId;
    }

    public void setPassportId(String passportId) {
        this.passportId = passportId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getCt() {
        return ct;
    }

    public void setCt(long ct) {
        this.ct = ct;
    }

    //str的格式为v:xxxx(版本号)|passportId:xxxx|status:xxxx（登录状态）|ct:xxxx(请求时间)
    public static WebRoamDO getWebRoamDO(String str) {
//        Map<String, String> webRoamMap = strToMap(str);
        Map<String, String> webRoamMap = Splitter.on(KEY_SEP).withKeyValueSeparator(VALUE_SEP).split(str);
        if (!webRoamMap.isEmpty()) {
            try {
                WebRoamDO webRoamDO = new WebRoamDO();
                webRoamDO.setV(webRoamMap.get(V));
                webRoamDO.setPassportId(webRoamMap.get(PASSPORTID));
                webRoamDO.setStatus(Integer.parseInt(webRoamMap.get(STATUS)));
                webRoamDO.setCt(Long.parseLong(webRoamMap.get(CT)));
                return webRoamDO;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(V).append(VALUE_SEP).append(this.v).append(KEY_SEP);
        sb.append(PASSPORTID).append(VALUE_SEP).append(this.passportId).append(KEY_SEP);
        sb.append(STATUS).append(VALUE_SEP).append(this.status).append(KEY_SEP);
        sb.append(CT).append(VALUE_SEP).append(this.ct);
        return sb.toString();
    }

    private static Map<String, String> strToMap(String str) {
        Map<String, String> keyValueMap = Maps.newHashMap();
        String[] keyArray = str.split(KEY_SEP);
        if (keyArray.length >= 4) {
            for (String keyValueStr : keyArray) {
                String[] keyValueArray = keyValueStr.split(VALUE_SEP);
                if (keyValueArray.length >= 2) {
                    keyValueMap.put(keyValueArray[0], keyValueArray[1]);
                }
            }
        }
        return keyValueMap;
    }
}
