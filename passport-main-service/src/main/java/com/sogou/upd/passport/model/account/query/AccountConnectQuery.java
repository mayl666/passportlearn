package com.sogou.upd.passport.model.account.query;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-3-24
 * Time: 下午4:57
 * To change this template use File | Settings | File Templates.
 */
public class AccountConnectQuery {
    private String connectUid;

    private int provider;

    private int appkey;

    private long userid;

    public String getConnectUid() {
        return connectUid;
    }

    public void setConnectUid(String connectUid) {
        this.connectUid = connectUid;
    }

    public int getProvider() {
        return provider;
    }

    public void setProvider(int provider) {
        this.provider = provider;
    }

    public int getAppkey() {
        return appkey;
    }

    public void setAppkey(int appkey) {
        this.appkey = appkey;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }
}
