package com.sogou.upd.passport.model.operatelog;

import java.util.Date;

/**
 * 后台操作记录历史
 * User: chengang
 * Date: 14-8-8
 * Time: 下午3:19
 */
public class OperateHistoryLog {


    /**
     * 后台操作人id
     */
    private String operate_userid;


    /**
     * 后台操作人ip
     */
    private String operate_userip;


    /**
     * 被操作账号
     */
    private String operate_user;

    /**
     * 账号类型
     */
    private int account_type;

    /**
     * 操作时间
     */
    private Date operate_time;

    /**
     * 操作类型：操作类型:目前 1、重置密码、2、解绑邮箱、3、解绑手机 4、批量删除注册手机号（仅限开发+测试使用）
     */
    private int operate_type;

    /**
     * 操作前账号状态信息、数据格式: k1:v1|k2:v2|k3:v3
     */
    private String operate_before_status;


    /**
     * 操作后账号状态信息、数据格式: k1:v1|k2:v2|k3:v3
     */
    private String operate_after_status;


    public String getOperate_userid() {
        return operate_userid;
    }

    public void setOperate_userid(String operate_userid) {
        this.operate_userid = operate_userid;
    }

    public String getOperate_userip() {
        return operate_userip;
    }

    public void setOperate_userip(String operate_userip) {
        this.operate_userip = operate_userip;
    }

    public String getOperate_user() {
        return operate_user;
    }

    public void setOperate_user(String operate_user) {
        this.operate_user = operate_user;
    }

    public int getAccount_type() {
        return account_type;
    }

    public void setAccount_type(int account_type) {
        this.account_type = account_type;
    }

    public Date getOperate_time() {
        return operate_time;
    }

    public void setOperate_time(Date operate_time) {
        this.operate_time = operate_time;
    }

    public int getOperate_type() {
        return operate_type;
    }

    public void setOperate_type(int operate_type) {
        this.operate_type = operate_type;
    }

    public String getOperate_before_status() {
        return operate_before_status;
    }

    public void setOperate_before_status(String operate_before_status) {
        this.operate_before_status = operate_before_status;
    }

    public String getOperate_after_status() {
        return operate_after_status;
    }

    public void setOperate_after_status(String operate_after_status) {
        this.operate_after_status = operate_after_status;
    }
}
