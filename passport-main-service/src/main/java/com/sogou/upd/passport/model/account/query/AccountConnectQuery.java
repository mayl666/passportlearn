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
    private int connectType;
    private int clientId;
    private long userid;
    
	public String getConnectUid() {
		return connectUid;
	}
	public void setConnectUid(String connectUid) {
		this.connectUid = connectUid;
	}
	public int getConnectType() {
		return connectType;
	}
	public void setConnectType(int connectType) {
		this.connectType = connectType;
	}
	public int getClientId() {
		return clientId;
	}
	public void setClientId(int clientId) {
		this.clientId = clientId;
	}
	public long getUserid() {
		return userid;
	}
	public void setUserid(long userid) {
		this.userid = userid;
	}

}
