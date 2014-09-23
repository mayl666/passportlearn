package com.sogou.upd.passport.model.account;

import com.sogou.upd.passport.common.CommonConstant;

/**
 * 漫游用户在外域和搜狗域之间传递的对象
 * v:xxxx(版本号)|passportId:xxxx|status:xxxx（登录状态）|ct:xxxx(请求时间)
 * User: shipengzhi
 * Date: 14-7-30
 * Time: 下午1:59
 */
public class PcBrowerRoamDO {

    private static String KEY_SEP = "\\|"; //字符串中key和key之间的分隔符

    private String v; //值的版本
    private int clientId; //应用ID
    private String passportId;  //外域的用户ID
    private String instance_id; //实例ID
    private long ct;  //加密串生成时间，单位为秒
    private String token; //短有效期token

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getPassportId() {
        return passportId;
    }

    public void setPassportId(String passportId) {
        this.passportId = passportId;
    }

    public String getInstance_id() {
        return instance_id;
    }

    public void setInstance_id(String instance_id) {
        this.instance_id = instance_id;
    }

    public long getCt() {
        return ct;
    }

    public void setCt(long ct) {
        this.ct = ct;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    //str的格式为v=1时候的结构，1|client_id|passportId|instance_id|ct(请求时间)|token
    public static PcBrowerRoamDO getPcBrowerRoamDO(String str) {
        String[] pcBrowerRoamStr = str.split(KEY_SEP);
        if (pcBrowerRoamStr != null && pcBrowerRoamStr.length != 0) {
            try {
                if (pcBrowerRoamStr[0].equals("1") && pcBrowerRoamStr.length == 6) {
                    PcBrowerRoamDO pcRoamDO = new PcBrowerRoamDO();
                    pcRoamDO.setV(pcBrowerRoamStr[0]);
                    int clientId = Integer.parseInt(pcBrowerRoamStr[1]);
                    clientId = clientId == 30000004 ? CommonConstant.PC_CLIENTID : clientId;  //兼容浏览器PC端sohu+接口
                    pcRoamDO.setClientId(clientId);
                    pcRoamDO.setPassportId(pcBrowerRoamStr[2]);
                    pcRoamDO.setInstance_id(pcBrowerRoamStr[3]);
                    pcRoamDO.setCt(Long.parseLong(pcBrowerRoamStr[4]));
                    pcRoamDO.setToken(pcBrowerRoamStr[5]);
                    return pcRoamDO;
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
