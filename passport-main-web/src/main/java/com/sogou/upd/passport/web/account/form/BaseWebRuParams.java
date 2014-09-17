package com.sogou.upd.passport.web.account.form;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.validation.constraints.Ru;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Min;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 14-7-30
 * Time: 上午12:46
 * To change this template use File | Settings | File Templates.
 */
public class BaseWebRuParams {

    @NotBlank(message = "client_id不允许为空!")
    @Min(0)
    protected String client_id = String.valueOf(CommonConstant.SGPP_DEFAULT_CLIENTID);

    @URL
    @Ru
    protected String ru = CommonConstant.DEFAULT_INDEX_URL;

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }
}
