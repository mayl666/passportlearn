package com.sogou.upd.passport.model.account;

import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.service.account.dataobject.ActionStoreRecordDO;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-18 Time: 下午5:48 To change this template use
 * File | Settings | File Templates.
 *
 * 传递动作记录的参数类
 *
 */
public class ActionRecord {
    private AccountModuleEnum action;
    private String userId;
    private int clientId;
    private String ip;
    private long date;  // 记录时间，用毫秒表示
    private String note;

    public AccountModuleEnum getAction() {
        return action;
    }

    public void setAction(AccountModuleEnum action) {
        this.action = action;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public ActionStoreRecordDO obtainStoreRecord() {
        ActionStoreRecordDO actionStoreRecord = new ActionStoreRecordDO(this);
        return actionStoreRecord;
    }
}
