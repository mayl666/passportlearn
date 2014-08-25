package com.sogou.upd.passport.oauth2.openresource.vo;

/**
 * 微信验证access_token是否有效返回的对象
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-8-25
 * Time: 下午4:03
 * To change this template use File | Settings | File Templates.
 */
public class WeixinOAuthTokenVO extends OAuthTokenVO {

    private String errcode;
    private String errmessage;

    public WeixinOAuthTokenVO(String errcode, String errmessage) {
        this.errcode = errcode;
        this.errmessage = errmessage;
    }

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getErrmessage() {
        return errmessage;
    }

    public void setErrmessage(String errmessage) {
        this.errmessage = errmessage;
    }
}
