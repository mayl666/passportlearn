package com.sogou.upd.passport.common.utils;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.HystrixConstant;
import com.sogou.upd.passport.common.hystrix.HystrixConfigFactory;
import com.sogou.upd.passport.common.hystrix.HystrixQQCommand;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.parameter.HttpTransformat;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * 第三方使用的httpclient
 * User: shipengzhi@sogou-inc.com
 * Date: 13-5-29
 * Time: 上午10:25
 */
public class ConnectHttpClient extends SGHttpClient {

    private static final Logger hystrixLogger = LoggerFactory.getLogger("hystrixLogger");


    protected static final HttpClient httpClient;
    /**
     * 获取连接的最大等待时间
     */
    protected final static int WAIT_TIMEOUT = 3000;
    /**
     * 读取超时时间
     */
    protected final static int READ_TIMEOUT = 3000;

    /**
     * 超过500ms的请求定义为慢请求
     */
    protected final static int SLOW_TIME = 1000;

    static {
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, WAIT_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, READ_TIMEOUT);
        httpClient = WebClientDevWrapper.wrapClient(new DefaultHttpClient());
    }

    /**
     * 执行http请求，并将返回结果从HttpTransformat转换为java bean
     *
     * @param requestModel 请求参数
     * @param transformat  返回值的类型
     * @param type         要得到的对象的类
     * @param <T>          泛型最终得到的bean类型
     * @return
     */
    public static <T> T executeBean(RequestModel requestModel, HttpTransformat transformat, java.lang.Class<T> type) {
        String value = executeStr(requestModel).trim();
        T t = null;
        switch (transformat) {
            case json:
                t = JsonUtil.jsonToBean(value, type);
                break;
            case xml:
                t = XMLUtil.xmlToBean(value, type);
                break;
        }
        return t;
    }

    /**
     * 执行请求操作，返回服务器返回内容
     *
     * @param requestModel
     * @return
     */
    public static String executeStr(RequestModel requestModel) {
        HttpEntity httpEntity = execute(requestModel);
        try {
            String charset = EntityUtils.getContentCharSet(httpEntity);
            if (StringUtil.isBlank(charset)) {
                charset = CommonConstant.DEFAULT_CHARSET;
            }
            String value = EntityUtils.toString(httpEntity, charset);
            if (!StringUtil.isBlank(value)) {
                value = value.trim();
            }
            return value;
        } catch (IOException | ParseException e) {
            throw new RuntimeException("http request error ", e);
        }
    }

    /**
     * 对外提供的执行请求的方法，主要添加了性能log
     *
     * @param requestModel
     * @return
     */
    public static HttpEntity execute(RequestModel requestModel) {
        //性能分析
        StopWatch stopWatch = new Slf4JStopWatch(prefLogger);
        try {
            HttpEntity httpEntity = executePrivate(requestModel);
            stopWatch(stopWatch, requestModel.getUrl(), "success");
            return httpEntity;
        } catch (Exception e) {
            if (requestModel != null) {
                stopWatch(stopWatch, requestModel.getUrl(), "failed");
            } else {
                stopWatch(stopWatch, "requestModel is null", "failed");
            }
            throw new RuntimeException("http request error ", e);
        }
    }

    /**
     * 执行请求并返回请求结果
     *
     * @param requestModel
     * @return
     */
    protected static HttpEntity executePrivate(RequestModel requestModel) {
        if (requestModel == null) {
            throw new NullPointerException("requestModel 不能为空");
        }
        HttpRequestBase httpRequest = getHttpRequest(requestModel);

        //对QQapi调用hystrix
        hystrixLogger.warn("ConnectHttpClient executePrivate:invoke hystrix");
        String hystrixQQurl = HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_QQ_URL);
        Boolean hystrixGlobalEnabled = Boolean.parseBoolean(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_GLOBAL_ENABLED));
        if (hystrixGlobalEnabled) {
            String qqUrl = requestModel.getUrl();
            hystrixLogger.warn("ConnectHttpClient hystrix url:"+qqUrl);
            if (!Strings.isNullOrEmpty(qqUrl) && qqUrl.contains(hystrixQQurl)) {
                return new HystrixQQCommand(requestModel, httpClient).execute();
            }
        }

        InputStream in = null;
        try {
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            in = httpResponse.getEntity().getContent();
            int responseCode = httpResponse.getStatusLine().getStatusCode();
            //302如何处理
            if (responseCode == RESPONSE_SUCCESS_CODE) {
                return httpResponse.getEntity();
            }
            String params = EntityUtils.toString(requestModel.getRequestEntity(), CommonConstant.DEFAULT_CHARSET);
            String result = EntityUtils.toString(httpResponse.getEntity(), CommonConstant.DEFAULT_CHARSET);
            throw new RuntimeException("http response error code: " + responseCode + " url:" + requestModel.getUrl() + " params:" + params + "  result:" + result);
        } catch (Exception e) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ioe) {
                }
            }
            throw new RuntimeException("http request error ", e);
        }
    }

    /**
     * 记录性能log的规则
     *
     * @param stopWatch
     * @param tag
     * @param message
     */
    protected static void stopWatch(StopWatch stopWatch, String tag, String message) {
        //无论什么情况都记录下所有的请求数据
        if (stopWatch.getElapsedTime() >= SLOW_TIME) {
            tag += "(slow)";
        }
        stopWatch.stop(tag, message);
    }

}
