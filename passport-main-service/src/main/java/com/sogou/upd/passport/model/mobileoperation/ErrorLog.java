package com.sogou.upd.passport.model.mobileoperation;

import com.sogou.upd.passport.common.lang.StringUtil;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: lzy_clement
 * Date: 14-12-16
 * Time: 下午8:58
 * To change this template use File | Settings | File Templates.
 */
public class ErrorLog extends MobileBaseLog {

    private String time;
    private String key;
    private String info;

    public ErrorLog(Map map) {
        super(map);
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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
        return StringUtil.defaultIfEmpty(time, "-") + "\t" + StringUtil.defaultIfEmpty(key, "-") + "\t" +
                StringUtil.defaultIfEmpty(info, "-");
    }
}
