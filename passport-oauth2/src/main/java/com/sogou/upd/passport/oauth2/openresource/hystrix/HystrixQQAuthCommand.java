package com.sogou.upd.passport.oauth2.openresource.hystrix;

import com.google.common.base.Strings;
import com.netflix.hystrix.*;
import com.sogou.upd.passport.common.HttpConstant;
import com.sogou.upd.passport.common.HystrixConstant;
import com.sogou.upd.passport.common.hystrix.HystrixConfigFactory;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.request.OAuthClientRequest;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthClientResponse;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: nahongxu
 * Date: 15-4-8
 * Time: 下午6:17
 * To change this template use File | Settings | File Templates.
 */
public class HystrixQQAuthCommand<T extends OAuthClientResponse> extends HystrixCommand<T> {

    private static final Logger logger = LoggerFactory.getLogger("hystrixLogger");
    private static final String COMMOND_FALLBACK_PREFIX = "HystrixQQAuthCommand fallback ";

    private static final Logger log = LoggerFactory.getLogger(HystrixQQAuthCommand.class);
    private OAuthClientRequest request;
    private String requestMethod;
    public Class<T> responseClass;
    private Map<String, String> headers;

    private HttpRequestBase httpRequestBase;
    private String fallbackReason;


    private static boolean requestCacheEnable = Boolean.parseBoolean(HystrixConfigFactory.getProperty(HystrixConfigFactory.PROPERTY_REQUEST_CACHE_ENABLED));
    private static boolean requestLogEnable = Boolean.parseBoolean(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_REQUEST_LOG_ENABLED));
    private static int errorThresholdPercentage = Integer.parseInt(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_ERROR_THRESHOLD_PERCENTAGE));
    private static int qqOAuthPoolCoreSize = Integer.parseInt(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_QQ_OAUTH_POOL_CORESIZE));
    private static int qqTimeout = Integer.parseInt(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_QQ_TIMEOUT));
    private static int qqOAuthRequestVolumeThreshold = Integer.parseInt(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_QQ_OAUTH_REQUESTVOLUME));
    private static final int fallbackSemaphoreThreshold = Integer.parseInt(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_FALLBACK_SEMAPHORE_THRESHOLD));
    private static boolean breakerForceOpen = Boolean.parseBoolean(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_BREAKER_FORCE_OPEN));
    private static boolean breakerForceClose = Boolean.parseBoolean(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_BREAKER_FORCE_CLOSE));
    private static final int breakerSleepWindow = Integer.parseInt(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_BREAKER_SLEEP_WINDOW));

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
                        .withCircuitBreakerSleepWindowInMilliseconds(breakerSleepWindow)
                        .withCircuitBreakerErrorThresholdPercentage(errorThresholdPercentage)
                        .withExecutionIsolationThreadTimeoutInMilliseconds(qqTimeout)
                        .withCircuitBreakerRequestVolumeThreshold(qqOAuthRequestVolumeThreshold)
                        .withFallbackIsolationSemaphoreMaxConcurrentRequests(fallbackSemaphoreThreshold))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                        .withCoreSize(qqOAuthPoolCoreSize))
        );
        this.request = request;
        this.requestMethod = requestMethod;
        this.responseClass = responseClass;
        this.headers = headers;

        this.httpRequestBase = null;
        this.fallbackReason = null;
    }

    @Override
    protected T run() throws Exception {
        httpRequestBase = getRequestBase(request, requestMethod);
        return HystrixQQAuthMethod.execute(request, headers, requestMethod, responseClass, httpRequestBase);

    }

    @Override
    protected T getFallback() {
        boolean isShortCircuited = isResponseShortCircuited();
        boolean isRejected = isResponseRejected();
        boolean isTimeout = isResponseTimedOut();
        boolean isFailed = isFailedExecution();

        if (isShortCircuited) {
            fallbackReason = COMMOND_FALLBACK_PREFIX + HystrixConstant.FALLBACK_REASON_SHORT_CIRCUITED;
        } else if (isRejected) {
            fallbackReason = COMMOND_FALLBACK_PREFIX + HystrixConstant.FALLBACK_REASON_REJECTED;
        } else if (isFailed) {
            Throwable e = getFailedExecutionException();
            String exceptionMsg = "";
            if (e != null) {
                exceptionMsg = e.getMessage();
            }
            fallbackReason = COMMOND_FALLBACK_PREFIX + HystrixConstant.FALLBACK_REASON_EXCUTE_FAILED + ",msg=" + exceptionMsg;
        } else if (isTimeout) {
            fallbackReason = COMMOND_FALLBACK_PREFIX + HystrixConstant.FALLBACK_REASON_TIMEOUT;
        } else {
            fallbackReason = COMMOND_FALLBACK_PREFIX + HystrixConstant.FALLBACK_REASON_UNKNOWN_REASON;
        }

        // 记录fallback原因
        if (fallbackReason != null) {
            if (isFailed) {
                logger.error(COMMOND_FALLBACK_PREFIX + HystrixConstant.FALLBACK_REASON_EXCUTE_FAILED);
            } else {
                logger.error(fallbackReason);
            }
        }

        if (httpRequestBase != null) {
            httpRequestBase.abort();
        }
        return null;
    }


    public void abortHttpRequest() {
        if (httpRequestBase != null) {
            httpRequestBase.abort();
        }
    }

    public String getFallbackReason() {
        return fallbackReason;
    }

    public HttpRequestBase getRequestBase(OAuthClientRequest request, String requestMethod) throws OAuthProblemException {
        URI location;
        String url = "";
        HttpRequestBase httpRequestResult = null;
        try {
            location = new URI(request.getLocationUri());
            url = request.getLocationUri();
        } catch (URISyntaxException e) {
            // URL表达式错误
            log.error("[HttpClient4] URL syntax error :", e);
            throw new OAuthProblemException(ErrorUtil.HTTP_CLIENT_REQEUST_FAIL);
        }

        try {
            if (!Strings.isNullOrEmpty(requestMethod) && HttpConstant.HttpMethod.POST.equals(requestMethod)) {
                httpRequestResult = new HttpPost(location);
                HttpEntity entity = new StringEntity(request.getBody());
                ((HttpPost) httpRequestResult).setEntity(entity);
            } else {
                httpRequestResult = new HttpGet(location);
                if (url.indexOf("?") > 0) {
                    url = url.substring(0, url.indexOf("?"));
                }
            }
            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    httpRequestResult.setHeader(header.getKey(), header.getValue());
                }
            }

            return httpRequestResult;

        } catch (Exception e1) {
            throw new OAuthProblemException(ErrorUtil.HTTP_CLIENT_REQEUST_FAIL);
        }
    }


}
