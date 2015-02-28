package com.sogou.upd.passport.common.apache_asynhttpclient;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.parameter.HttpMethodEnum;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.*;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: lzy_clement
 * Date: 15-2-28
 * Time: 下午6:02
 * To change this template use File | Settings | File Templates.
 */
public class ApacheAsynHttpClient {

    protected static final CloseableHttpAsyncClient  httpClient = HttpAsyncClients.createDefault();

    /**
     * http返回成功的code
     */
    protected final static int RESPONSE_SUCCESS_CODE = 200;

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
        try {
            HttpEntity httpEntity = executePrivate(requestModel);
            return httpEntity;
        } catch (Exception e) {
            if (requestModel != null) {
            } else {

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
        InputStream in=null;
        try {
            Future<HttpResponse> future = httpClient.execute(httpRequest ,null);
            in=future.get().getEntity().getContent();
            int responseCode = future.get().getStatusLine().getStatusCode();
            //302如何处理
            if (responseCode == RESPONSE_SUCCESS_CODE) {
                return future.get().getEntity();
            }
            String params = EntityUtils.toString(requestModel.getRequestEntity(), CommonConstant.DEFAULT_CHARSET);
            String result= EntityUtils.toString(future.get().getEntity(),CommonConstant.DEFAULT_CHARSET);
            throw new RuntimeException("http response error code: " + responseCode + " url:" + requestModel.getUrl() + " params:" + params + "  result:"+result);
        } catch (Exception e) {
            if(in!=null){
                try{
                    in.close();
                }catch(IOException ioe){
                }
            }
            throw new RuntimeException("http request error ", e);
        }
    }

    /**
     * 根据请求的参数构造HttpRequestBase
     *
     * @param requestModel
     * @return
     */
    protected static HttpRequestBase getHttpRequest(RequestModel requestModel) {

        HttpRequestBase httpRequest = null;
        HttpMethodEnum method = requestModel.getHttpMethodEnum();
        switch (method) {
            case GET:
                httpRequest = new HttpGet(requestModel.getUrlWithParam());
                break;
            case POST:
                HttpPost httpPost = new HttpPost(requestModel.getUrl());
                httpPost.setEntity(requestModel.getRequestEntity());
                httpRequest = httpPost;
                break;
            case PUT:
                HttpPut httpPut = new HttpPut(requestModel.getUrl());
                httpPut.setEntity(requestModel.getRequestEntity());
                httpRequest = httpPut;
                break;
            case DELETE:
                httpRequest = new HttpDelete(requestModel.getUrl());
                break;
        }
        httpRequest.setHeaders(requestModel.getHeaders());
        return httpRequest;
    }

}
