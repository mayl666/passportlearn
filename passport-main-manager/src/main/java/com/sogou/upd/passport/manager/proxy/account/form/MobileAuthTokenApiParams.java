package com.sogou.upd.passport.manager.proxy.account.form;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.manager.proxy.BaseApiParameters;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-6-7
 * Time: 下午8:26
 * To change this template use File | Settings | File Templates.
 */
public class MobileAuthTokenApiParams extends BaseApiParameters {

    @Min(0)
    @NotBlank(message = "type不允许为空")
    private int type;  //2：第三方登录（即将废除，请使用type=5）；5：手机应用使用的session token
    @NotBlank(message = "token不允许为空")
    private String token;  //用户登录成功之后通过302跳转传递给服务器端的token
    private String passport_id;

    @AssertTrue(message = "type类型对应的token或passport_id为空")
    private boolean isTypeNeedValueNotBlank() {
        if (type == 0) {
            return true;
        }
        if (type == 5 && Strings.isNullOrEmpty(passport_id)) {
            return false;
        } else if (type != 5 && type != 2) {
            return false;
        }
        return true;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassport_id() {
        return passport_id;
    }

    public void setPassport_id(String passport_id) {
        this.passport_id = passport_id;
    }
}
