package com.sogou.upd.passport.model.account;

import java.util.Date;

/**
 * sohu+切换前注册的个性账号和手机号与PassportId映射表
 * User: shipengzhi
 * Date: 13-4-22
 * Time: 下午9:39
 * To change this template use File | Settings | File Templates.
 */
public class SnamePassportMapping {

    private long id;
    private String sid;
    private String sname;
    private String passportId;
    private Date updateTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }
}
