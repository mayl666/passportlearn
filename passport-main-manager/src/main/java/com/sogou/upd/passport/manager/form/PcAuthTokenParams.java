package com.sogou.upd.passport.manager.form;

import com.sogou.upd.passport.common.validation.constraints.Ru;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

/**
 * sohu桌面应用token登录流程的参数类
 * 应用于：/authtoken
 * User: shipengzhi
 * Date: 13-7-24
 * Time: 下午11:21
 * To change this template use File | Settings | File Templates.
 */
public class PcAuthTokenParams extends PcBaseParams {

    private long livetime = 0;  //token的生存期，单位为秒
    private int authtype = 0;  //0其它，1正常登录，2记住密码登录，3输入法同步信息，4用户点击链接

    @NotBlank(message = "token不允许为空")
    private String token; //用户获取到的token

    @NotBlank(message = "ru地址不允许为空")
    @URL
    @Ru
    private String ru; //token正确时的调整地址

    public long getLivetime() {
        return livetime;
    }

    public void setLivetime(long livetime) {
        this.livetime = livetime;
    }

    public int getAuthtype() {
        return authtype;
    }

    public void setAuthtype(int authtype) {
        this.authtype = authtype;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }
}
