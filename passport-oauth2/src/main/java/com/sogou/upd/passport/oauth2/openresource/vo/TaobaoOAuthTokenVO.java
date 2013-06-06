package com.sogou.upd.passport.oauth2.openresource.vo;

/**
 * 淘宝OAuth授权返回的token对象
 * User: shipengzhi
 * Date: 13-5-28
 * Time: 上午11:42
 */
public class TaobaoOAuthTokenVO {

    private String access_token;
    private String token_type;   //Access token的类型目前只支持bearer
    private String expires_in;   //Access token过期时间
    private String refresh_token;  //可选
    private String re_expires_in;   //可选，Refresh token过期时间
    private String taobao_user_nick;   //淘宝账号昵称
    private String taobao_user_id;  //淘宝帐号对应id

    /*=============可选参数================*/
    private String sub_taobao_user_nick; //淘宝子账号
    private String sub_taobao_user_id; //淘宝子账号对应id
    private long r1_expires_in;  //r1级别API或字段的访问过期时间
    private long r2_expires_in;  //r2级别API或字段的访问过期时间
    private long w1_expires_in;  //w1级别API或字段的访问过期时间

    public long getW2_expires_in() {
        return w2_expires_in;
    }

    public void setW2_expires_in(long w2_expires_in) {
        this.w2_expires_in = w2_expires_in;
    }

    public long getW1_expires_in() {
        return w1_expires_in;
    }

    public void setW1_expires_in(long w1_expires_in) {
        this.w1_expires_in = w1_expires_in;
    }

    public long getR2_expires_in() {
        return r2_expires_in;
    }

    public void setR2_expires_in(long r2_expires_in) {
        this.r2_expires_in = r2_expires_in;
    }

    public long getR1_expires_in() {
        return r1_expires_in;
    }

    public void setR1_expires_in(long r1_expires_in) {
        this.r1_expires_in = r1_expires_in;
    }

    public String getSub_taobao_user_id() {
        return sub_taobao_user_id;
    }

    public void setSub_taobao_user_id(String sub_taobao_user_id) {
        this.sub_taobao_user_id = sub_taobao_user_id;
    }

    public String getSub_taobao_user_nick() {
        return sub_taobao_user_nick;
    }

    public void setSub_taobao_user_nick(String sub_taobao_user_nick) {
        this.sub_taobao_user_nick = sub_taobao_user_nick;
    }

    public String getTaobao_user_id() {
        return taobao_user_id;
    }

    public void setTaobao_user_id(String taobao_user_id) {
        this.taobao_user_id = taobao_user_id;
    }

    public String getTaobao_user_nick() {
        return taobao_user_nick;
    }

    public void setTaobao_user_nick(String taobao_user_nick) {
        this.taobao_user_nick = taobao_user_nick;
    }

    public String getRe_expires_in() {
        return re_expires_in;
    }

    public void setRe_expires_in(String re_expires_in) {
        this.re_expires_in = re_expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    private long w2_expires_in;  //w2级别API或字段的访问过期时间

}
