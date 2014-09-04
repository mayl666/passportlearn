package com.sogou.upd.passport.model.moduleblacklist;

import java.sql.Timestamp;

/**
 * sogou module 黑名单
 * User: chengang
 * Date: 14-8-27
 * Time: 下午6:25
 */
public class ModuleBlacklist {

    /**
     * 黑名单账号
     */
    private String userid;

    /**
     * 账号类型
     */
    private int account_type;

    /**
     * 有效期
     */
    private int expire_time;

    /**
     * 创建时间
     */
    private Timestamp create_time;


    /**
     * 更新时间
     */
    private Timestamp update_time;


    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getAccount_type() {
        return account_type;
    }

    public void setAccount_type(int account_type) {
        this.account_type = account_type;
    }

    public int getExpire_time() {
        return expire_time;
    }

    public void setExpire_time(int expire_time) {
        this.expire_time = expire_time;
    }

    public Timestamp getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
    }

    public Timestamp getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Timestamp update_time) {
        this.update_time = update_time;
    }
}
