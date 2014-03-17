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

    public static final int Add_LIMIT = 2;
    public static final int SUCCESS_LIMIT = 1;
    public static final int FAILED_LIMIT = 0;

    public static final int SCOPE_LOGIN = 1;

    private long id;
    private int nameSort;//name字段的类型:0为username；1为ip
    private String name; //ip或者username
    private int limitSort; //限制的类型“0为失败限制；1为成功限制；2为后台添加的限制
    private Date insertTime;
    private Double durationTime; //统计次数的持续时间
    private String insertServer; //插入黑名的服务器IP
    private int scope; //限制的范围：1为登陆

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

    public int getLimitSort() {
        return limitSort;
    }

    public void setLimitSort(int limitSort) {
        this.limitSort = limitSort;
    }

    public int getNameSort() {
        return nameSort;
    }

    public void setNameSort(int nameSort) {
        this.nameSort = nameSort;
    }
}