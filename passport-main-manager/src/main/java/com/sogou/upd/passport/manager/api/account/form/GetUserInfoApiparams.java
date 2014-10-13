package com.sogou.upd.passport.manager.api.account.form;

import org.hibernate.validator.constraints.NotBlank;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-13
 * Time: 上午10:31
 */
public class GetUserInfoApiparams extends BaseUserApiParams {

    @NotBlank(message = "需要返回的参数列表（fields）不能为空")
    private String fields;
    private String imagesize;

    public GetUserInfoApiparams() {
    }

    public GetUserInfoApiparams(String userId, String fields) {
        this.userid = userId;
        this.fields = fields;
    }

    public GetUserInfoApiparams(String userId, int clientId, String fields) {
        this.userid = userId;
        this.client_id = clientId;
        this.fields = fields;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public String getImagesize() {
        return imagesize;
    }

    public void setImagesize(String imagesize) {
        this.imagesize = imagesize;
    }
}
