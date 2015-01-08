package com.sogou.upd.passport.web.account.form.mapp;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.AssertTrue;

/**
 * 移动端实时获取用户信息
 * User: nahongxu
 * Date: 14-12-26
 * Time: 下午4:02
 * To change this template use File | Settings | File Templates.
 */
public class MappGetUserinfoParams extends MappBaseParams{

    @NotBlank(message = "sgid不允许为空!")
    private String sgid;

    private String fields;

    private String imagesize;


    @AssertTrue(message = "请求信息涉及用户隐私")
    private boolean isCheckInvolvingPrivacy(){
        String [] privacyFields={"username","personalid","sec_mobile","sec_email","sec_ques"};
        for(String field:privacyFields){
            if(StringUtils.contains(this.fields, field)){
                return false;
            }
        }
        return true;
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
