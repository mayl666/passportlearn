package com.sogou.upd.passport.model.account;

import java.util.Date;

/**
 * 昵称与passportid映射表
 * User: mayan
 * Date: 13-8-7
 * Time: 下午6:40
 */
public class UniqnamePassportMapping {

    private long id;
    private String uniqname;
    private String passportId;
    private Date updateTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUniqname() {
        return uniqname;
    }

    public void setUniqname(String uniqname) {
        this.uniqname = uniqname;
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
