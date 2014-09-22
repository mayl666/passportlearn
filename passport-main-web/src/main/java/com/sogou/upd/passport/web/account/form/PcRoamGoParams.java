package com.sogou.upd.passport.web.account.form;

import com.sogou.upd.passport.common.validation.constraints.Ru;
import com.sogou.upd.passport.web.BaseWebParams;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 14-9-15
 * Time: 下午6:50
 * To change this template use File | Settings | File Templates.
 */
public class PcRoamGoParams extends BaseWebParams {

    @NotBlank(message = "type不允许为空!")
    private String type;    //登录态类型，iet、iec、pinyint
    @NotBlank(message = "s不允许为空!")
    private String s;   //登录态加密字符串

    @NotBlank(message = "xd不允许为空!")
    @URL
    @Ru
    private String xd;   //跨域通信所用字段，直接返回

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public String getXd() {
        return xd;
    }

    public void setXd(String xd) {
        this.xd = xd;
    }
}
