package com.sogou.upd.passport.manager.form;

import org.hibernate.validator.constraints.NotBlank;

/**
 * sohu桌面应用token登录流程的参数类
 * 应用于：/refreshtoken
 * User: shipengzhi
 * Date: 13-7-24
 * Time: 下午11:21
 * To change this template use File | Settings | File Templates.
 */
public class PcRefreshTokenParams extends PcBaseParams {

    private int authtype = 0;  //0其它，1正常登录，2记住密码登录，3输入法同步信息，4用户点击链接
    @NotBlank(message = "refreshToken不允许为空")
    private String refresh_token; //用户获取到的refresh_token

    public int getAuthtype() {
        return authtype;
    }

    public void setAuthtype(int authtype) {
        this.authtype = authtype;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }
}
