package com.sogou.upd.passport.common;

/**
 * Created with IntelliJ IDEA.
 * User: nahongxu
 * Date: 15-4-9
 * Time: 上午10:51
 * To change this template use File | Settings | File Templates.
 */
public class HystrixConstant {
    //hystrix default global configurations
    public static final String DEFAULT_GLOBAL_ENABLED = "false";   //默认总开关:关闭
    public static final String DEFAULT_QQ_HYSTRIX_ENABLED="false";//默认QQ开关：关闭
    public static final String DEFAULT_KAFKA_HYSTRIX_ENABLED="false";//默认kafka开关：关闭
    public static final String DEFAULT_KAFKA_CHOOSE_THREAD_MODE="true" ;//默认采用线程池隔离kafka
    public static final String DEFAULT_REQUEST_CACHE_ENABLED = "false";    //默认不开启request cache
    public static final String DEFAULT_ERROR_THRESHOLD_PERCENTAGE = "70";  //默认错误率阈值为70%
    public static final String DEFAULT_REQUEST_LOG_ENABLED = "true";


    //QQ依赖调用
    public static final String DEFAULT_QQ_URL = "https://graph.qq.com";
    public static final String DEFAULT_QQ_HYSTRIX_THREADPOOL_CORESIZE = "20";
    public static final String DEFAULT_QQ_TIMEOUT = "7000";//ms
    public static final String DEFAULT_QQ_REQUESTVOLUME_THRESHOLD = "2000";


    //kafka依赖调用
    public static final String DEFAULT_KAFKA_HYSTRIX_THREADPOOL_CORESIZE = "10";
    public static final String DEFAULT_KAFKA_TIMEOUT = "100";// ms
    public static final String DEFAULT_KAFKA_REQUESTVOLUME_THRESHOLD = "20000";
    public static final String DEFAULT_KAFKA_SEMAPHORE_THRESHOLD = "10";
    public static final String DEFAULT_KAFKA_FALLBACK_SEMAPHORE_THRESHOLD = "10";

    //property name
    public static final String PROPERTY_GLOBAL_ENABLED = "globalEnabled";
    public static final String PROPERTY_QQ_HYSTRIX_ENABLED = "qqHystrixEnabled";
    public static final String PROPERTY_KAFKA_HYSTRIX_ENABLED = "kafkaHystrixEnabled";
    public static final String PROPERTY_KAFKA_CHOOSE_THREAD_MODE="kafkaChooseThreadMode";
    public static final String PROPERTY_REQUEST_CACHE_ENABLED = "requestCacheEnabled";
    public static final String PROPERTY_REQUEST_LOG_ENABLED = "requestLogEnabled";
    public static final String PROPERTY_ERROR_THRESHOLD_PERCENTAGE = "errorThresholdPercentage";
    public static final String PROPERTY_QQ_URL = "qqUrl";
    public static final String PROPERTY_QQ_HYSTRIX_THREADPOOL_CORESIZE = "qqHystrixThreadPoolCoreSize";
    public static final String PROPERTY_QQ_TIMEOUT = "qqTimeout";
    public static final String PROPERTY_QQ_REQUESTVOLUME_THRESHOLD = "qqRequestVolumeThreshold";
    public static final String PROPERTY_KAFKA_HYSTRIX_THREADPOOL_CORESIZE = "kafkaHystrixThreadPoolCoreSize";
    public static final String PROPERTY_KAFKA_TIMEOUT = "kafkaTimeout";
    public static final String PROPERTY_KAFKA_REQUESTVOLUME_THRESHOLD = "kafkaRequestVolumeThreshold";
    public static final String PROPERTY_KAFKA_SEMAPHORE_THRESHOLD = "kafkaSemaphoreThreshold";
    public static final String PROPERTY_KAFKA_FALLBACK_SEMAPHORE_THRESHOLD = "kafkaFallbackSemaphoreThreshold";


}
