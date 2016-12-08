package com.sogou.upd.passport.manager.api.account.form;

import com.sogou.upd.passport.manager.api.BaseApiParams;
import org.hibernate.validator.constraints.NotBlank;

/**
 * User: wanghuaqing@sogou-inc.com
 * Date: 2016-12-7
 */
public class GetUserInfoBySgidApiparams extends BaseApiParams {
    @NotBlank(message = "sgid不能为空")
    protected String sgid;
    @NotBlank(message = "需要返回的参数列表（fields）不能为空")
    private String fields;
    private String imagesize;

    public GetUserInfoBySgidApiparams() {
    }

    public GetUserInfoBySgidApiparams(String sgid, String fields) {
        this.sgid = sgid;
        this.fields = fields;
    }

    public GetUserInfoBySgidApiparams(String sgid, int clientId, String fields) {
        this.sgid = sgid;
        this.client_id = clientId;
        this.fields = fields;
    }
    
    public String getSgid() {
        return sgid;
    }
    
    public void setSgid(String sgid) {
        this.sgid = sgid;
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
