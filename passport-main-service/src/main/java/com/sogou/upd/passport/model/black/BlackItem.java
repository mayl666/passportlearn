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

    public static final int SCOPE_LOGIN = 1;

    private long id;
    private int sort;
    private String name;
    private int flagSuccessLimit;
    private Date insertTime;
    private Double durationTime;
    private String insertServer;
    private int scope;

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

    public String getInsertServer() {
        return insertServer;
    }

    public void setInsertServer(String insertServer) {
        this.insertServer = insertServer;
    }

    public int getFlagSuccessLimit() {
        return flagSuccessLimit;
    }

    public void setFlagSuccessLimit(int flagSuccessLimit) {
        this.flagSuccessLimit = flagSuccessLimit;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getScope() {
        return scope;
    }

    public void setScope(int scope) {
        this.scope = scope;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}