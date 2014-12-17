package com.sogou.upd.passport.model.mobileoperation;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: lzy_clement
 * Date: 14-12-16
 * Time: 下午8:56
 * To change this template use File | Settings | File Templates.
 */
public class ProductLog implements MobileLog {

    private String page;
    private String event_id;
    private String client_id;

    public ProductLog(Map map) {
        this.page = String.valueOf(map.get("page"));
        this.event_id = String.valueOf(map.get("event_id"));
        this.client_id = String.valueOf(map.get("client_id"));
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
        return page + "\t" + event_id + "\t" + client_id;
    }
}
