package com.sogou.upd.passport.model.account;

/**
 * sohu+ 头像、昵称临时表
 * User: mayan
 * Date: 13-11-27
 * Time: 下午2:02
 */
public class AccountBaseInfo {
    private long id;
    private String uniqname;
    private String passportId;
    private String avatar;

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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
