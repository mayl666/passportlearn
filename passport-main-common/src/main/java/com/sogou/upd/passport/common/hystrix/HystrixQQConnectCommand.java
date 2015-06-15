package com.sogou.upd.passport.common.hystrix;

import com.netflix.hystrix.*;
import com.sogou.upd.passport.common.HystrixConstant;
import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: nahongxu
 * Date: 15-4-8
 * Time: 下午6:17
 * To change this template use File | Settings | File Templates.
 */
public class HystrixQQConnectCommand extends HystrixCommand<HttpEntity> {

    private static final Logger logger = LoggerFactory.getLogger("hystrixLogger");
    private static RequestModel requestModel;
    private static HttpClient httpClient;


    private static boolean requestCacheEnable = Boolean.parseBoolean(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_REQUEST_CACHE_ENABLED));
    private static boolean requestLogEnable = Boolean.parseBoolean(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_REQUEST_LOG_ENABLED));
    private static boolean breakerForceOpen = Boolean.parseBoolean(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_BREAKER_FORCE_OPEN));
    private static boolean breakerForceClose = Boolean.parseBoolean(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_BREAKER_FORCE_CLOSE));
    private static int errorThresholdPercentage = Integer.parseInt(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_ERROR_THRESHOLD_PERCENTAGE));
    private static int qqConnectPoolCoreSize = Integer.parseInt(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_QQ_CONNECT_POOL_CORESIZE));
    private static int qqTimeout = Integer.parseInt(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_QQ_TIMEOUT));
    private static int qqConnectRequestVolumeThreshold = Integer.parseInt(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_QQ_CONNECT_REQUESTVOLUME));
    private static final int fallbackSemaphoreThreshold = Integer.parseInt(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_FALLBACK_SEMAPHORE_THRESHOLD));
    private static final int breakerSleepWindow=Integer.parseInt(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_BREAKER_SLEEP_WINDOW));

    public HystrixQQConnectCommand(RequestModel requestModel, HttpClient httpClient) {


        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("HystrixHttpClient"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("QQConnectHttpClientCommand"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("QQConnectHttpClientPool"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
                        .withRequestCacheEnabled(requestCacheEnable)
                        .withRequestLogEnabled(requestLogEnable)
                        .withCircuitBreakerForceOpen(breakerForceOpen)
                        .withCircuitBreakerForceClosed(breakerForceClose)
                        .withCircuitBreakerSleepWindowInMilliseconds(breakerSleepWindow)
                        .withCircuitBreakerErrorThresholdPercentage(errorThresholdPercentage)
                        .withExecutionIsolationThreadTimeoutInMilliseconds(qqTimeout)
                        .withCircuitBreakerRequestVolumeThreshold(qqConnectRequestVolumeThreshold)
                        .withFallbackIsolationSemaphoreMaxConcurrentRequests(fallbackSemaphoreThreshold))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                        .withCoreSize(qqConnectPoolCoreSize))
        );
        this.requestModel = requestModel;
        this.httpClient = httpClient;
    }

    @Override
    protected HttpEntity run() throws Exception {
        return HystrixCommonMethod.execute(requestModel, httpClient);
    }

    @Override
    protected HttpEntity getFallback() {
        boolean isShortCircuited = isResponseShortCircuited();
        boolean isRejected = isResponseRejected();
        boolean isTimeout = isResponseTimedOut();
//        boolean isFailed = isFailedExecution();
        if (isTimeout) {
            logger.error("HystrixQQConnectCommand fallback isTimeout");
        } else if (isRejected) {
            logger.error("HystrixQQConnectCommand fallback isRejected");
        } else if (isShortCircuited) {
            logger.error("HystrixQQConnectCommand fallback isShortCircuited");
        } else {
//            logger.error("HystrixQQConnectCommand fallback unknown");
        }

        throw new UnsupportedOperationException("HystrixQQConnectCommand:No fallback available.");
    }


}