package com.sogou.upd.passport.service.account.dataobject;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-5-7
 * Time: 下午1:59
 * To change this template use File | Settings | File Templates.
 */
public class SecureSignDO {

    private long ts;  // 时间戳，从1970年1月1日00:00:00至今的秒数
    private String nonce;   // 随机字符串
    private String uri; // request URI
    private String serverName; // request host

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}
