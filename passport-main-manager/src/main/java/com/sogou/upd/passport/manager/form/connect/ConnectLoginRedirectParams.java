package com.sogou.upd.passport.manager.form.connect;

import com.google.common.base.Strings;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 14-12-18
 * Time: 下午9:33
 * To change this template use File | Settings | File Templates.
 */
public class ConnectLoginRedirectParams {

    private String ru;  //搜狗产品回调url
    private int client_id;  //passport分配应用ID
    private String type; //不同终端的登录类型
    private String ts; //客户端实例ID，实现不同PC客户端登录状态分离
    private String ip;  //用户ip
    private String from; //和type搭配使用,例如:手机浏览器
    private String domain;   //非sogou.com域时需传递
    private String thirdInfo;  //是否需要个人信息
    private String user_agent;   //输入法PC客户端根据ua判断显示不同的错误页面
    private String v;   //浏览器PC客户端根据v判断显示新旧UI样式
    private String third_appid; //应用传递自己的第三方appid
    private boolean needWeixinOpenId = false; // 需要获取微信 openId

    //必须有无参的构造函数，不然servlet报错
    public ConnectLoginRedirectParams() {

    }

    public ConnectLoginRedirectParams(ConnectLoginParams connectLoginParams, String ip, String userAgent) {
        this.ru = connectLoginParams.getRu();
        this.client_id = Integer.parseInt(connectLoginParams.getClient_id());
        this.type = Strings.isNullOrEmpty(connectLoginParams.getType()) ? "web" : connectLoginParams.getType();
        this.ts = connectLoginParams.getTs();
        this.ip = ip;
        this.from = connectLoginParams.getFrom();
        this.domain = connectLoginParams.getDomain();
        this.thirdInfo = connectLoginParams.getThirdInfo();
        this.user_agent = userAgent;
        this.v = connectLoginParams.getV();
        this.third_appid = connectLoginParams.getThird_appid();
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getThirdInfo() {
        return thirdInfo;
    }

    public void setThirdInfo(String thirdInfo) {
        this.thirdInfo = thirdInfo;
    }

    public String getUser_agent() {
        return user_agent;
    }

    public void setUser_agent(String user_agent) {
        this.user_agent = user_agent;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public String getThird_appid() {
        return third_appid;
    }

    public void setThird_appid(String third_appid) {
        this.third_appid = third_appid;
    }

    public boolean isNeedWeixinOpenId() {
        return needWeixinOpenId;
    }

    public void setNeedWeixinOpenId(boolean needWeixinOpenId) {
        this.needWeixinOpenId = needWeixinOpenId;
    }

}
