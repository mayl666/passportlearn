package com.sogou.upd.passport.model.mobileoperation;

import com.sogou.upd.passport.common.lang.StringUtil;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: lzy_clement
 * Date: 14-12-16
 * Time: 下午8:32
 * To change this template use File | Settings | File Templates.
 */
public class DebugLog extends MobileBaseLog {

    private String times;
    private String level;
    private String tag;
    private String key;
    private String info;

    public DebugLog(Map map) {
        super(map);
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
        return StringUtil.exchangeIfContains(StringUtil.defaultIfEmpty(times, "-"), "\t", "_") + "\t" + StringUtil.exchangeIfContains(StringUtil.defaultIfEmpty(level, "-"), "\t", "_") + "\t" +
                StringUtil.exchangeIfContains(StringUtil.defaultIfEmpty(tag, "-"), "\t", "_") + "\t" + StringUtil.exchangeIfContains(StringUtil.defaultIfEmpty(key, "-"), "\t", "_") + "\t" +
                StringUtil.exchangeIfContains(StringUtil.defaultIfEmpty(info, "-"), "\t", "_");
    }
}
