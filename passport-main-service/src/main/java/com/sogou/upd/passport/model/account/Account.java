package com.sogou.upd.passport.model.account;

import java.util.Date;

/**
 * 账号主表account
 * User: mayan
 * Date: 13-3-22
 * Time: 下午2:01
 */
public class Account {

    private long id;
    private String passportId;
    private String password;
    private String mobile;
    private Date regTime;
    private String regIp;
    private int flag;   // 1-正式用户，2-未激活账号，3-锁定或封杀用户
    private int passwordtype;  //  0-原密码 1：md5 2：crypt(MD5（password）, salt )salt  salt = 8位随机的a-zA-Z0-9
    private int accountType; // 账号类型，1-email，2-phone，3-qq，4-sina，5-renren，6-taobao；7-baidu；
    private String uniqname; // 昵称
    private String avatar;  // 头像url

    public Account() {
    }

    public Account(String mobile) {
        this.mobile = mobile;
    }

    public Account(String mobile, String password) {
        this.mobile = mobile;
        this.password = password;
    }

    public Account(String password, String mobile, String regIp) {
        this.password = password;
        this.mobile = mobile;
        this.regIp = regIp;
    }

    public Account(long id, String passportId, String password, String mobile, Date regTime,
                   String regIp, int flag, int passwordtype, int accountType) {
        this.id = id;
        this.passportId = passportId;
        this.password = password;
        this.mobile = mobile;
        this.regTime = regTime;
        this.regIp = regIp;
        this.flag = flag;
        this.passwordtype = passwordtype;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getPasswordtype() {
        return passwordtype;
    }

    public void setPasswordtype(int passwordtype) {
        this.passwordtype = passwordtype;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public String getUniqname() {
        return uniqname;
    }

    public void setUniqname(String uniqname) {
        this.uniqname = uniqname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}