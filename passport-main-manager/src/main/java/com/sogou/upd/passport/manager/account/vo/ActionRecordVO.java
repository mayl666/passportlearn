package com.sogou.upd.passport.manager.account.vo;

import com.sogou.upd.passport.common.utils.IpLocationUtil;
import com.sogou.upd.passport.service.account.dataobject.ActionStoreRecordDO;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-19 Time: 下午6:58 To change this template use
 * File | Settings | File Templates.
 */
public class ActionRecordVO {
    private long time;
    private String ip;
    private String loc;
    private String type; // 默认”搜狗通行证“，需要通过Service获取再setType(...)

    public ActionRecordVO(ActionStoreRecordDO actionStoreRecordDO) {
        long date = actionStoreRecordDO.getDate();
        String ipDO = actionStoreRecordDO.getIp();
        setTime(date);
        setIp(ipDO);
        setLoc(IpLocationUtil.getCity(ipDO));
        setType("搜狗通行证");
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
