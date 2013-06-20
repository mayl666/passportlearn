package com.sogou.upd.passport.service.account.dataobject;

import com.sogou.upd.passport.model.account.ActionRecord;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-19 Time: 下午2:05 To change this template use
 * File | Settings | File Templates.
 *
 * <br>实际存储动作记录的字段</br><br />
 * <br>构造函数：new ActionStoreRecordDO(long date, String ip)</br>
 *
 */
public class ActionStoreRecordDO {
    private int clientId;
    private long date;      // 8字节
    private String ip;      // 15字节
    // private String note;

    public ActionStoreRecordDO(ActionRecord actionRecord) {
        if (actionRecord != null) {
            setClientId(actionRecord.getClientId());
            setDate(actionRecord.getDate());
            setIp(actionRecord.getIp());
        }
    }

    public ActionStoreRecordDO() {
        //无动作
    }

    /**
     * 构造函数，必须传入全部参数
     *
     * @param clientId
     * @param date
     * @param ip
     */
    public ActionStoreRecordDO(int clientId, long date, String ip) {
        setClientId(clientId);
        setDate(date);
        setIp(ip);
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
