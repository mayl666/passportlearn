package com.sogou.upd.passport.model.account;

import java.util.Date;

/**
 * 账号主表
 * User: mayan Date: 13-3-22 Time: 下午2:01 To change this template use File | Settings | File
 * Templates.
 */
public class
        Account {

    public static final int NEW_ACCOUNT_VERSION = 1; // sogou-passport新生成的账号
    public static final int OLD_ACCOUNT_VERSION = 2; // sohu-passport迁移过来的账号

    private long id;
    private String passportId;
    private String passwd;
    private String mobile;
    private Date regTime;
    private String regIp;
    private int status;   // 1-正式用户，2-未激活账号，3-锁定或封杀用户
    private int version;  // 0-sohu老用户，1-sogou新用户
    private int accountType; // 账号类型，1-email，2-phone，3-qq，4-sina，5-renren，6-

    public Account() {
    }

    public Account(String mobile) {
        this.mobile = mobile;
    }

    public Account(String mobile, String passwd) {
        this.mobile = mobile;
        this.passwd = passwd;
    }

    public Account(String passwd, String mobile, String regIp) {
        this.passwd = passwd;
        this.mobile = mobile;
        this.regIp = regIp;
    }

    public Account(long id, String passportId, String passwd, String mobile, Date regTime,
                   String regIp, int status, int version, int accountType) {
        this.id = id;
        this.passportId = passportId;
        this.passwd = passwd;
        this.mobile = mobile;
        this.regTime = regTime;
        this.regIp = regIp;
        this.status = status;
        this.version = version;
        this.accountType = accountType;
    }

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

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Date getRegTime() {
        return regTime;
    }

    public void setRegTime(Date regTime) {
        this.regTime = regTime;
    }

    public String getRegIp() {
        return regIp;
    }

    public void setRegIp(String regIp) {
        this.regIp = regIp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }
}