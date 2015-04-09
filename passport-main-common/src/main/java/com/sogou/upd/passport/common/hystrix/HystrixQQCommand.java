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
public class HystrixQQCommand extends HystrixCommand<HttpEntity> {

    private static final Logger logger = LoggerFactory.getLogger("hystrixLogger");
    private static RequestModel requestModel;
    private static HttpClient httpClient;

    private static int qqTimeout = Integer.parseInt(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_QQ_TIMEOUT));
    private static int qqRequestVolumeThreshold = Integer.parseInt(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_QQ_REQUESTVOLUME_THRESHOLD));

    public HystrixQQCommand(RequestModel requestModel, HttpClient httpClient) {

        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("SGHystrxiHttpClient"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("QQHystrixCommand"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("QQHystrixPool"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionIsolationThreadTimeoutInMilliseconds(qqTimeout)
                        .withCircuitBreakerRequestVolumeThreshold(qqRequestVolumeThreshold)
                ));
        this.requestModel = requestModel;
        this.httpClient = httpClient;
    }

    @Override
    protected HttpEntity run() throws Exception {
        logger.warn("invoke hystrix qq command...");
        logger.warn("hystrix qqTimeout:"+qqTimeout);
        logger.warn("hystrix qqRequestVolumeThreshold:"+qqRequestVolumeThreshold);

        return HystrixCommonMethod.execute(requestModel, httpClient);
    }

    @Override
    protected HttpEntity getFallback() {
        logger.error("HystrixQQCommand fallback!");
        throw new UnsupportedOperationException("HystrixQQCommand:No fallback available.");
    }
}
