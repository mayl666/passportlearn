package com.sogou.upd.passport.manager.form;

import org.hibernate.validator.constraints.NotBlank;

/**
 * sohu桌面应用token登录流程的参数类
 * 应用于：/gettoken接口
 * User: shipengzhi
 * Date: 13-7-24
 * Time: 下午11:21
 * To change this template use File | Settings | File Templates.
 */
public class PcGetTokenParams extends PcBaseParams {

    @NotBlank(message = "password不允许为空")
    private String password;  //密码的md5

    private int authtype = 0;  //0其它，1正常登录，2记住密码登录，3输入法同步信息，4用户点击链接
    private long livetime = 0;  //token的生存期，单位为秒

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAuthtype() {
        return authtype;
    }

    public void setAuthtype(int authtype) {
        this.authtype = authtype;
    }

    public long getLivetime() {
        return livetime;
    }

    public void setLivetime(long livetime) {
        this.livetime = livetime;
    }
}
