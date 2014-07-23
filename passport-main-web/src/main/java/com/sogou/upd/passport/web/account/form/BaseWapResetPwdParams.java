package com.sogou.upd.passport.web.account.form;

import com.sogou.upd.passport.common.WapConstant;
import com.sogou.upd.passport.common.validation.constraints.Ru;
import com.sogou.upd.passport.common.validation.constraints.Skin;
import com.sogou.upd.passport.common.validation.constraints.V;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Min;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-7-4
 * Time: 下午1:57
 * To change this template use File | Settings | File Templates.
 */
public class BaseWapResetPwdParams {

    @NotBlank(message = "client_id不允许为空!")
    @Min(0)
    protected String client_id = String.valueOf(SHPPUrlConstant.APP_ID);
    @URL
    @Ru
    protected String ru;
    @NotBlank(message = "v参数不可为空")
    @V
    protected String v = WapConstant.WAP_TOUCH;
    @Skin
    protected String skin;

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }
}
