package com.sogou.upd.passport.manager.api.account.form;

import com.google.common.base.Strings;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import org.hibernate.validator.constraints.Email;
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
    private String imgsize;
    private String imgurl;

    public String getClient_id() {
        return client_id;
    }

    public String getImgsize() {
        return imgsize;
    }

    public void setImgsize(String imgsize) {
        this.imgsize = imgsize;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }
}
