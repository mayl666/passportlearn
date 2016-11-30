package com.sogou.upd.passport.model.mobileoperation;

import com.sogou.upd.passport.common.lang.StringUtil;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: lzy_clement
 * Date: 14-12-16
 * Time: 下午8:56
 * To change this template use File | Settings | File Templates.
 */
public class ProductLog extends MobileBaseLog {

    private String client_id;
    private String page;
    private String event_id;
    private String time;

    public ProductLog(Map map) {
        super(map);
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    @Override
    public String toHiveString() {
        return StringUtil.exchangeIfContains(StringUtil.defaultIfEmpty(client_id, "-"), "\t", "_") + "\t" + StringUtil.exchangeIfContains(StringUtil.defaultIfEmpty(page, "-"), "\t", "_") + "\t" +
                StringUtil.exchangeIfContains(StringUtil.defaultIfEmpty(event_id, "-"), "\t", "_") + "\t" + StringUtil.exchangeIfContains(StringUtil.defaultIfEmpty(time, "-"), "\t", "_");
    }
}
