package com.sogou.upd.passport.oauth2.openresource.hystrix;

import com.netflix.hystrix.*;
import com.sogou.upd.passport.common.HystrixConstant;
import com.sogou.upd.passport.common.hystrix.HystrixConfigFactory;
import com.sogou.upd.passport.oauth2.openresource.http.HttpClient4;
import com.sogou.upd.passport.oauth2.openresource.request.OAuthClientRequest;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: nahongxu
 * Date: 15-4-8
 * Time: 下午6:17
 * To change this template use File | Settings | File Templates.
 */
public class HystrixQQAuthCommand<T extends OAuthClientResponse> extends HystrixCommand<T>  {

    private static final Logger logger = LoggerFactory.getLogger("hystrixLogger");
    private OAuthClientRequest request;
    private String requestMethod;
    public Class<T> responseClass;
    private Map<String, String> headers;


    private static boolean requestCacheEnable = Boolean.parseBoolean(HystrixConfigFactory.getProperty(HystrixConfigFactory.PROPERTY_REQUEST_CACHE_ENABLED));
    private static boolean requestLogEnable = Boolean.parseBoolean(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_REQUEST_LOG_ENABLED));
    private static int errorThresholdPercentage = Integer.parseInt(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_ERROR_THRESHOLD_PERCENTAGE));
    private static int qqOAuthPoolCoreSize = Integer.parseInt(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_QQ_OAUTH_POOL_CORESIZE));
    private static int qqTimeout = Integer.parseInt(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_QQ_TIMEOUT));
    private static int qqRequestVolumeThreshold = Integer.parseInt(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_QQ_REQUESTVOLUME_THRESHOLD));
    private static final int fallbackSemaphoreThreshold = Integer.parseInt(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_FALLBACK_SEMAPHORE_THRESHOLD));
    private static boolean breakerForceOpen= Boolean.parseBoolean(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_BREAKER_FORCE_OPEN));
    private static boolean breakerForceClose=Boolean.parseBoolean(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_BREAKER_FORCE_CLOSE));

    public HystrixQQAuthCommand(OAuthClientRequest request, String requestMethod, Class<T> responseClass, Map<String, String> headers) {


        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("HystrixHttpClient"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("QQOAuthHttpClientCommand"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("QQOAuthHttpClientPool"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
                        .withRequestCacheEnabled(requestCacheEnable)
                        .withRequestLogEnabled(requestLogEnable)
                        .withCircuitBreakerForceOpen(breakerForceOpen)
                        .withCircuitBreakerForceClosed(breakerForceClose)
                        .withCircuitBreakerErrorThresholdPercentage(errorThresholdPercentage)
                        .withExecutionIsolationThreadTimeoutInMilliseconds(qqTimeout)
                        .withCircuitBreakerRequestVolumeThreshold(qqRequestVolumeThreshold)
                        .withFallbackIsolationSemaphoreMaxConcurrentRequests(fallbackSemaphoreThreshold))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                        .withCoreSize(qqOAuthPoolCoreSize))
        );
        this.request = request;
        this.requestMethod = requestMethod;
        this.responseClass = responseClass;
        this.headers = headers;
    }

    @Override
    protected T run() throws Exception {
//        logger.warn("invoke Hystrix QQ  Auth Command...");
        return HttpClient4.execute(request, headers, requestMethod, responseClass);
    }

    @Override
    protected T getFallback() {
        boolean isShortCircuited=isResponseShortCircuited();
        boolean isRejected=isResponseRejected();
        boolean isTimeout=isResponseTimedOut();
        boolean isFailed=isFailedExecution();
        if(isFailed){
            logger.error("HystrixQQAuthCommand fallback isFailedExecution");
        }else if(isTimeout){
            logger.error("HystrixQQAuthCommand fallback isTimeout");
        }  else if(isRejected){
            logger.error("HystrixQQAuthCommand fallback isRejected");
        } else if(isShortCircuited) {
            logger.error("HystrixQQAuthCommand fallback isShortCircuited");
        } else {
            logger.error("HystrixQQAuthCommand fallback unknown");
        }
        throw new UnsupportedOperationException("HystrixQQAuthCommand:No fallback available.");
    }



}
