package com.sogou.upd.passport.common.hystrix;

import com.netflix.hystrix.*;
import com.sogou.upd.passport.common.HystrixConstant;
import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: nahongxu
 * Date: 15-4-8
 * Time: 下午6:17
 * To change this template use File | Settings | File Templates.
 */
public class HystrixQQCommand extends HystrixCommand<HttpEntity> {

    private static final Logger logger = LoggerFactory.getLogger("hystrixLogger");
    private RequestModel requestModel;
    private static HttpClient httpClient;
    private InputStream in;


    private static boolean requestCacheEnable = Boolean.parseBoolean(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_REQUEST_CACHE_ENABLED));
    private static boolean requestLogEnable = Boolean.parseBoolean(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_REQUEST_LOG_ENABLED));
    private static boolean breakerForceOpen = Boolean.parseBoolean(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_BREAKER_FORCE_OPEN));
    private static boolean breakerForceClose = Boolean.parseBoolean(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_BREAKER_FORCE_CLOSE));
    private static int errorThresholdPercentage = Integer.parseInt(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_ERROR_THRESHOLD_PERCENTAGE));
    private static int qqSGPoolCoreSize = Integer.parseInt(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_QQ_SG_POOL_CORESIZE));
    private static int qqTimeout = Integer.parseInt(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_QQ_TIMEOUT));
    private static int qqSgRequestVolumeThreshold = Integer.parseInt(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_QQ_SG_REQUESTVOLUME));
    private static final int fallbackSemaphoreThreshold = Integer.parseInt(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_FALLBACK_SEMAPHORE_THRESHOLD));
    private static final int breakerSleepWindow=Integer.parseInt(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_BREAKER_SLEEP_WINDOW));

    public HystrixQQCommand(RequestModel requestModel, HttpClient httpClient) {


        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("HystrixHttpClient"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("QQSGHttpClientCommand"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("QQSGHttpClientPool"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
                        .withRequestCacheEnabled(requestCacheEnable)
                        .withRequestLogEnabled(requestLogEnable)
                        .withCircuitBreakerForceOpen(breakerForceOpen)
                        .withCircuitBreakerForceClosed(breakerForceClose)
                        .withCircuitBreakerSleepWindowInMilliseconds(breakerSleepWindow)
                        .withCircuitBreakerErrorThresholdPercentage(errorThresholdPercentage)
                        .withExecutionIsolationThreadTimeoutInMilliseconds(qqTimeout)
                        .withCircuitBreakerRequestVolumeThreshold(qqSgRequestVolumeThreshold)
                        .withFallbackIsolationSemaphoreMaxConcurrentRequests(fallbackSemaphoreThreshold))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                        .withCoreSize(qqSGPoolCoreSize))
        );
        this.requestModel = requestModel;
        this.httpClient = httpClient;
        this.in=null;
    }

    @Override
    protected HttpEntity run() throws Exception {
        HttpEntity response= HystrixCommonMethod.execute(requestModel, httpClient,in);
        logger.warn("HystrixQQCommand excute success");
        return response;
    }

    @Override
    protected HttpEntity getFallback() {
        boolean isShortCircuited = isResponseShortCircuited();
        boolean isRejected = isResponseRejected();
        boolean isTimeout = isResponseTimedOut();
//        boolean isFailed = isFailedExecution();
        if (isTimeout) {
            logger.error("HystrixQQCommand fallback isTimeout");
        } else if (isRejected) {
            logger.error("HystrixQQCommand fallback isRejected");
        } else if (isShortCircuited) {
            logger.error("HystrixQQCommand fallback isShortCircuited");
        } else {
//            logger.error("HystrixQQCommand fallback unknown");
        }


        if (in != null) {
            logger.warn("HystrixQQCommand fallback close inputStream");
            try {
                in.close();
            } catch (IOException ioe) {
               logger.error("HystrixQQCommand fallback close inputStream failed",ioe);
            }
        }

        return null;
    }


}
