package com.sogou.upd.passport.model.account;

import java.util.Date;

/**
 * 手机号和PassportId映射表
 * User: shipengzhi
 * Date: 13-4-22
 * Time: 下午9:39
 * To change this template use File | Settings | File Templates.
 */
public class MobilePassportMapping {

    private long id;
    private String mobile;
    private String passportId;
    private Date updateTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassportId() {
        return passportId;
    }

    public void setPassportId(String passportId) {
        this.passportId = passportId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
