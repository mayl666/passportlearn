package com.sogou.upd.passport.model.rishcontrol;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chengang
 * Date: 15-3-21
 * Time: 下午1:00
 */
public class RiskIpData  {

    /**
     * 风险IP
     */
    private String ip;

    /**
     * 风险IP 所在城市
     */
    private String city;

    /**
     * 风险IP 地域， 0:国内，1：国外
     */
    private String regional;

    /**
     * 风险IP，累计一周进入风险库的频次统计
     */
    private int rate;

    /**
     * 风险等级，高、中、低（2、1、0）
     */
    private int level;


    /**
     * 风险IP，入风险库时间集合
     */
    private List<String> input_times;


    /**
     * 风险IP，封禁开始时间
     */
    private String deny_startTime;

    /**
     * 风险IP，封禁结束时间
     */
    private String deny_endTime;


    /**
     * 风险IP，超越的具体识别指标
     */
    private List<String> abnormal_indicators;


    /**
     * 风险IP，超越的具体识别指标个数
     */
    private int count_indicators;


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegional() {
        return regional;
    }

    public void setRegional(String regional) {
        this.regional = regional;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }


    public String getDeny_startTime() {
        return deny_startTime;
    }

    public void setDeny_startTime(String deny_startTime) {
        this.deny_startTime = deny_startTime;
    }

    public String getDeny_endTime() {
        return deny_endTime;
    }

    public void setDeny_endTime(String deny_endTime) {
        this.deny_endTime = deny_endTime;
    }


    public int getCount_indicators() {
        return count_indicators;
    }

    public void setCount_indicators(int count_indicators) {
        this.count_indicators = count_indicators;
    }

    public List<String> getInput_times() {
        return input_times;
    }

    public void setInput_times(List<String> input_times) {
        this.input_times = input_times;
    }

    public List<String> getAbnormal_indicators() {
        return abnormal_indicators;
    }

    public void setAbnormal_indicators(List<String> abnormal_indicators) {
        this.abnormal_indicators = abnormal_indicators;
    }
}
