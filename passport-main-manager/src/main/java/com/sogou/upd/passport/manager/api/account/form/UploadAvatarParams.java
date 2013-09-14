package com.sogou.upd.passport.manager.api.account.form;

import com.google.common.base.Strings;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

/**
 * User: mayan
 * Date: 13-8-8 Time: 下午2:18
 */
public class UploadAvatarParams extends BaseUserApiParams {

    @NotBlank(message = "注册账号不允许为空")
    @Email
    private String userid;  //注册账号

    private String type;

    private byte [] byteArr;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if(Strings.isNullOrEmpty(type)){
            type="0";
        }
        this.type = type;
    }

    public byte[] getByteArr() {
        return byteArr;
    }

    public void setByteArr(byte[] byteArr) {
        this.byteArr = byteArr;
    }
}
