package com.sogou.upd.passport.manager.account.vo;

import com.sogou.upd.passport.common.utils.IpLocationUtil;
import com.sogou.upd.passport.service.account.dataobject.ActionStoreRecordDO;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-19 Time: 下午6:58 To change this template use
 * File | Settings | File Templates.
 */
public class ActionRecordVO {
    private long date;
    private String ip;
    private String city;

    public ActionRecordVO(ActionStoreRecordDO actionStoreRecordDO) {
        long date = actionStoreRecordDO.getDate();
        String ipDO = actionStoreRecordDO.getIp();
        setDate(date);
        setIp(ipDO);
        setCity(IpLocationUtil.getCity(ipDO));
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
