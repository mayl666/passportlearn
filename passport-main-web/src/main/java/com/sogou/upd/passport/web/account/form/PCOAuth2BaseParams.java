package com.sogou.upd.passport.web.account.form;

import com.sogou.upd.passport.common.CommonConstant;

/**
 * sohu+浏览器个人中心页
 * User: shipengzhi
 * Date: 13-7-24
 * Time: 下午11:21
 * To change this template use File | Settings | File Templates.
 */
public class PCOAuth2BaseParams {
    private String instanceid = "";  //客户端的实例id

    private String h;
    private String r;
    private String v; //浏览器版本
    private int client_id = CommonConstant.PC_CLIENTID; //用户通过token解密获取userid,所以传1044

    public String getInstanceid() {
        return instanceid;
    }

    public void setInstanceid(String instanceid) {
        this.instanceid = instanceid;
    }

    public String getH() {
        return h;
    }

    public void setH(String h) {
        this.h = h;
    }

    public String getR() {
        return r;
    }

    public void setR(String r) {
        this.r = r;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }
}
