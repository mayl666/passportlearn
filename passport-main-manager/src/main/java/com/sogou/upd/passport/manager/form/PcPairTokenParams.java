package com.sogou.upd.passport.manager.form;

import com.google.common.base.Strings;

import javax.validation.constraints.AssertTrue;

/**
 * sohu桌面应用token登录流程的参数类
 * 应用于：/getpairtoken接口
 * User: shipengzhi
 * Date: 13-7-24
 * Time: 下午11:21
 * To change this template use File | Settings | File Templates.
 */
public class PcPairTokenParams extends PcBaseParams {

    private String password;  //密码的md5
    private String timestamp; //用于sig的时间戳
    private String sig;  //用于用refresh刷新token，userid + appid + refresh_token + timestamp + key 的md5

    @AssertTrue(message = "password和sig不能同时为空")
    private boolean isCheckPwdAndSig() {
        if (Strings.isNullOrEmpty(password) && (Strings.isNullOrEmpty(sig) || Strings.isNullOrEmpty(timestamp))) {
            return false;
        }
        return true;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSig() {
        return sig;
    }

    public void setSig(String sig) {
        this.sig = sig;
    }
}
