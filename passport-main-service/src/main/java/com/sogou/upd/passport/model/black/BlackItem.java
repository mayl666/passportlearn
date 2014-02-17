package com.sogou.upd.passport.model.black;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * 登陆黑名项表
 * User: chenjiameng
 * Date: 14-2-17
 * Time: 下午4:03
 * To change this template use File | Settings | File Templates.
 */
public class BlackItem {
    public static final int BLACK_IP = 1;
    public static final int BLACK_USERNAME = 0;

    public static final int SUCCESS_LIMIT = 1;
    public static final int FAILED_LIMIT = 0;

    private long id;
    private int flagIp;
    private String ipOrUsername;
    private int flagSuccessLimit;
    private Date insertTime;
    private Double durationTime;
    private String insertServer;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Double getDurationTime() {
        return durationTime;
    }

    public void setDurationTime(Double durationTime) {
        this.durationTime = durationTime;
    }

    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

    public String getIpOrUsername() {
        return ipOrUsername;
    }

    public void setIpOrUsername(String ipOrUsername) {
        this.ipOrUsername = ipOrUsername;
    }

    public String getInsertServer() {
        return insertServer;
    }

    public void setInsertServer(String insertServer) {
        this.insertServer = insertServer;
    }

    public int getFlagIp() {
        return flagIp;
    }

    public void setFlagIp(int flagIp) {
        this.flagIp = flagIp;
    }

    public int getFlagSuccessLimit() {
        return flagSuccessLimit;
    }

    public void setFlagSuccessLimit(int flagSuccessLimit) {
        this.flagSuccessLimit = flagSuccessLimit;
    }
}