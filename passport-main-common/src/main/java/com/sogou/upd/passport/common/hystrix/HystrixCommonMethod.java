package com.sogou.upd.passport.common.hystrix;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.parameter.HttpMethodEnum;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: nahongxu
 * Date: 15-4-8
 * Time: 下午6:19
 * To change this template use File | Settings | File Templates.
 */
public class HystrixCommonMethod {

    public final static int RESPONSE_SUCCESS_CODE = 200;

    /**
     * 根据请求的参数构造HttpRequestBase
     *
     * @param requestModel
     * @return
     */
    public static HttpRequestBase getHttpRequest(RequestModel requestModel) {

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

    public static HttpEntity execute(RequestModel requestModel, HttpClient httpClient) throws Exception {

        HttpRequestBase httpRequest = getHttpRequest(requestModel);
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
}
