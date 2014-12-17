package com.sogou.upd.passport.model.mobileoperation;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: lzy_clement
 * Date: 14-12-16
 * Time: 下午8:32
 * To change this template use File | Settings | File Templates.
 */
public class DebugLog implements MobileLog {

    private String times;
    private String level;
    private String tag;
    private String key;
    private String info;

    public DebugLog(Map data) {
        this.times = String.valueOf(data.get("times"));
        this.level = String.valueOf(data.get("level"));
        this.tag = String.valueOf(data.get("tag"));
        this.key = String.valueOf(data.get("key"));
        this.info = String.valueOf(data.get("info"));
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String toHiveString() {
        return times + "\t" + level + "\t" + tag + "\t" + key + "\t" + info;
    }
}