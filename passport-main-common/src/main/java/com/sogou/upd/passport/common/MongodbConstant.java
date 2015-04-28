package com.sogou.upd.passport.common;

/**
 * Created with IntelliJ IDEA.
 * User: lzy_clement
 * Date: 15-3-23
 * Time: 下午6:01
 * To change this template use File | Settings | File Templates.
 */
public final class MongodbConstant {

    //风险测试表
    public static final String RISK_CONTROL_COLLECTION_TEST = "risk_database_test";

    //风险库
    public static final String RISK_CONTROL_COLLECTION = "risk_database";

    //共用出口数据 collection
    public static final String IP_SHARED_EXPORT_DATABASE = "ip_shared_export_database";

    //风险IP
    public static final String IP = "ip";

    //风险IP 具体城市
    public static final String CITY = "city";

    //风险IP 具体地域
    public static final String REGIONAL = "regional";

    //风险IP，进入风险库频次统计
    public static final String RATE = "rate";

    //风险IP，风险等级
    public static final String LEVEL = "level";

    //风险IP，进入风险库时间集合
    public static final String INPUT_TIMES = "input_times";

    //风险IP，封禁开始时间
    public static final String DENY_START_TIME = "deny_startTime";

    //风险IP，封禁结束时间
    public static final String DENY_END_TIME = "deny_endTime";

    //风险IP，超越具体识别指标
    public static final String ABNORMAL_INDICATORS = "abnormal_indicators";

    //风险IP，超越具体识别指标个数
    public static final String COUNT_INDICATORS = "count_indicators";

    //最新入库时间
    public static final String LATEST_TIME = "latest_time";

    //国外IP
    public static final String FOREIGN_IP = "1";

    //国内IP
    public static final String CHINA_IP = "0";

}
