package com.sogou.upd.passport.manager.form.connect;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;

/**
 * 获取第三方id参数校验
 * User: 马研
 * Date: 13-4-19 Time: 下午3:32
 */
public class ConnectObtainParams {

    @NotBlank(message = "passport_id不允许为空!")
    private String passport_id;
    @NotBlank(message = "client_id不允许为空!")
    @Min(0)
    private String client_id;
    @NotBlank(message = "provider不允许为空!")
    private String provider;

    @AssertTrue(message = "不支持的第三方")
    private boolean isSupportProvider() {
        if(Strings.isNullOrEmpty(this.provider)){
            return true;
        }
        if (this.provider != null && !CommonConstant.SUPPORT_PROVIDER_LIST.contains(this.provider)) {
            return false;
        }
        return true;
    }

    public String getPassport_id() {
        return passport_id;
    }

    public void setPassport_id(String passport_id) {
        this.passport_id = passport_id;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
