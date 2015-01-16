package com.sogou.upd.passport.model.mobileoperation;

import com.sogou.upd.passport.common.lang.StringUtil;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: lzy_clement
 * Date: 14-12-16
 * Time: 下午8:27
 * To change this template use File | Settings | File Templates.
 */
public class ExceptionLog extends MobileBaseLog {
    private String times;
    private String start_time;
    private String last_time;
    private String exception_msg;
    private String exception_detail;

    public ExceptionLog(Map map) {
        super(map);
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getLast_time() {
        return last_time;
    }

    public void setLast_time(String last_time) {
        this.last_time = last_time;
    }

    public String getException_msg() {
        return exception_msg;
    }

    public void setException_msg(String exception_msg) {
        this.exception_msg = exception_msg;
    }

    public String getException_detail() {
        return exception_detail;
    }

    public void setException_detail(String exception_detail) {
        this.exception_detail = exception_detail;
    }

    @Override
    public String toHiveString() {
        return StringUtil.defaultIfEmpty(times, "-") + "\t" + StringUtil.defaultIfEmpty(start_time, "-") + "\t" +
                StringUtil.defaultIfEmpty(last_time, "-") + "\t" + StringUtil.defaultIfEmpty(exception_msg, "-") + "\t" +
                StringUtil.defaultIfEmpty(exception_detail, "-");
    }
}
