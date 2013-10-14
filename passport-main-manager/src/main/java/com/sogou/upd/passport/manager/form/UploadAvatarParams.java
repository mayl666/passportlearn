package com.sogou.upd.passport.manager.form;

import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * User: mayan
 * Date: 13-8-8 Time: 下午2:18
 */
public class UploadAvatarParams {
    @NotBlank(message = "client_id不允许为空!")
    @Min(0)
    private String client_id=  String.valueOf(SHPPUrlConstant.APP_ID);

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

}
