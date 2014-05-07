package com.sogou.upd.passport.web.account.form;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.validation.constraints.Ru;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.redis.connection.rjc.RjcUtils;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-5-6
 * Time: 下午5:52
 * To change this template use File | Settings | File Templates.
 */
public class AccountWebParams {
    @Ru
    @URL
    private String ru = CommonConstant.DEFAULT_INDEX_URL;
    @Length(min = 4, max = 4, message = "格式有误")
    private String client_id = String.valueOf(CommonConstant.SGPP_DEFAULT_CLIENTID);
    @Email(message = "邮箱格式有误")
    private String email;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
