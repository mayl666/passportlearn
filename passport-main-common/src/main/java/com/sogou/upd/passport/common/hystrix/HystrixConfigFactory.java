package com.sogou.upd.passport.common.hystrix;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.HystrixConstant;
import jodd.props.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentMap;

/**
 * Created with IntelliJ IDEA.
 * User: nahongxu
 * Date: 15-4-8
 * Time: 下午5:41
 * To change this template use File | Settings | File Templates.
 */
public class HystrixConfigFactory {
    private static String HYSTRIX_PROPERTY_FILE = "hystrix_config.properties";
    private static final Logger logger = LoggerFactory.getLogger("hystrixLogger");

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


    private static Props properties = new Props();
    protected static ConcurrentMap<String, String> hystrixConfigMap = Maps.newConcurrentMap();

    static {
        if (null == hystrixConfigMap || hystrixConfigMap.isEmpty()) {
            synchronized (HystrixConfigFactory.class) {

                InputStream input = HystrixConfigFactory.class.getClassLoader().getResourceAsStream(HYSTRIX_PROPERTY_FILE);
                try {
                    properties.load(input);
                } catch (Exception e) {
                    logger.error("load hystrix property failed", e);
                    if (input != null) {
                        try {
                            input.close();
                        } catch (IOException ioe) {
                        }
                    }
                }

                setProperties(hystrixConfigMap, properties, PROPERTY_GLOBAL_ENABLED, HystrixConstant.DEFAULT_GLOBAL_ENABLED);
                setProperties(hystrixConfigMap, properties, PROPERTY_REQUEST_CACHE_ENABLED, HystrixConstant.DEFAULT_REQUEST_CACHE_ENABLED);
                setProperties(hystrixConfigMap, properties, PROPERTY_REQUEST_LOG_ENABLED, HystrixConstant.DEFAULT_REQUEST_LOG_ENABLED);
                setProperties(hystrixConfigMap, properties, PROPERTY_ERROR_THRESHOLD_PERCENTAGE, HystrixConstant.DEFAULT_ERROR_THRESHOLD_PERCENTAGE);
                setProperties(hystrixConfigMap, properties, PROPERTY_QQ_URL, HystrixConstant.DEFAULT_QQ_URL);
                setProperties(hystrixConfigMap, properties, PROPERTY_QQ_HYSTRIX_THREADPOOL_CORESIZE, HystrixConstant.DEFAULT_QQ_HYSTRIX_THREADPOOL_CORESIZE);
                setProperties(hystrixConfigMap, properties, PROPERTY_QQ_TIMEOUT, HystrixConstant.DEFAULT_QQ_TIMEOUT);
                setProperties(hystrixConfigMap, properties, PROPERTY_QQ_REQUESTVOLUME_THRESHOLD, HystrixConstant.DEFAULT_QQ_REQUESTVOLUME_THRESHOLD);
                setProperties(hystrixConfigMap, properties, PROPERTY_KAFKA_HYSTRIX_THREADPOOL_CORESIZE, HystrixConstant.DEFAULT_KAFKA_HYSTRIX_THREADPOOL_CORESIZE);
                setProperties(hystrixConfigMap, properties, PROPERTY_KAFKA_TIMEOUT, HystrixConstant.DEFAULT_KAFKA_TIMEOUT);
                setProperties(hystrixConfigMap, properties, PROPERTY_KAFKA_REQUESTVOLUME_THRESHOLD, HystrixConstant.DEFAULT_KAFKA_REQUESTVOLUME_THRESHOLD);
                setProperties(hystrixConfigMap, properties, PROPERTY_KAFKA_SEMAPHORE_THRESHOLD, HystrixConstant.DEFAULT_KAFKA_SEMAPHORE_THRESHOLD);
                setProperties(hystrixConfigMap, properties, PROPERTY_KAFKA_FALLBACK_SEMAPHORE_THRESHOLD, HystrixConstant.DEFAULT_KAFKA_FALLBACK_SEMAPHORE_THRESHOLD);
                setProperties(hystrixConfigMap, properties,PROPERTY_QQ_HYSTRIX_ENABLED,HystrixConstant.DEFAULT_QQ_HYSTRIX_ENABLED);
                setProperties(hystrixConfigMap, properties,PROPERTY_KAFKA_HYSTRIX_ENABLED,HystrixConstant.DEFAULT_KAFKA_HYSTRIX_ENABLED);
                setProperties(hystrixConfigMap, properties,PROPERTY_KAFKA_CHOOSE_THREAD_MODE,HystrixConstant.DEFAULT_KAFKA_CHOOSE_THREAD_MODE);
                //打印参数
                logProperties();
            }
        }
    }

    private static void setProperties(ConcurrentMap map, Props properties, String propKey, String defaultValue) {
        if (!Strings.isNullOrEmpty(propKey)) {
            String propValue = properties.getValue(propKey);
            if (!Strings.isNullOrEmpty(propValue)) {
                map.putIfAbsent(propKey, propValue);
            } else {
                map.putIfAbsent(propKey, defaultValue);
            }
        }
    }

    public static String getProperty(String propertyName) {
        synchronized (hystrixConfigMap) {
            return hystrixConfigMap.get(propertyName);
        }
    }

    public static void logProperties() {
        logger.warn(PROPERTY_REQUEST_CACHE_ENABLED + ":" + getProperty(PROPERTY_REQUEST_CACHE_ENABLED));
        logger.warn(PROPERTY_REQUEST_LOG_ENABLED + ":" + getProperty(PROPERTY_REQUEST_LOG_ENABLED));
        logger.warn(PROPERTY_ERROR_THRESHOLD_PERCENTAGE + ":" + getProperty(PROPERTY_ERROR_THRESHOLD_PERCENTAGE));
        logger.warn(PROPERTY_QQ_URL + ":" + getProperty(PROPERTY_QQ_URL));
        logger.warn(PROPERTY_QQ_HYSTRIX_THREADPOOL_CORESIZE + ":" + getProperty(PROPERTY_QQ_HYSTRIX_THREADPOOL_CORESIZE));
        logger.warn(PROPERTY_QQ_TIMEOUT + ":" + getProperty(PROPERTY_QQ_TIMEOUT));
        logger.warn(PROPERTY_QQ_REQUESTVOLUME_THRESHOLD + ":" + getProperty(PROPERTY_QQ_REQUESTVOLUME_THRESHOLD));
        logger.warn(PROPERTY_KAFKA_HYSTRIX_THREADPOOL_CORESIZE + ":" + getProperty(PROPERTY_KAFKA_HYSTRIX_THREADPOOL_CORESIZE));
        logger.warn(PROPERTY_KAFKA_TIMEOUT + ":" + getProperty(PROPERTY_KAFKA_TIMEOUT));
        logger.warn(PROPERTY_KAFKA_REQUESTVOLUME_THRESHOLD + ":" + getProperty(PROPERTY_KAFKA_REQUESTVOLUME_THRESHOLD));
        logger.warn(PROPERTY_KAFKA_SEMAPHORE_THRESHOLD + ":" + getProperty(PROPERTY_KAFKA_SEMAPHORE_THRESHOLD));
        logger.warn(PROPERTY_KAFKA_FALLBACK_SEMAPHORE_THRESHOLD + ":" + getProperty(PROPERTY_KAFKA_FALLBACK_SEMAPHORE_THRESHOLD));
        logger.warn(PROPERTY_QQ_HYSTRIX_ENABLED + ":" + getProperty(PROPERTY_QQ_HYSTRIX_ENABLED));
        logger.warn(PROPERTY_KAFKA_HYSTRIX_ENABLED + ":" + getProperty(PROPERTY_KAFKA_HYSTRIX_ENABLED));
        logger.warn(PROPERTY_KAFKA_CHOOSE_THREAD_MODE + ":" + getProperty(PROPERTY_KAFKA_CHOOSE_THREAD_MODE));
    }
}
