package com.sogou.upd.passport.oauth2.common.utils.qqutils;

import com.sogou.upd.passport.common.CommonConstant;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * 发送HTTP网络请求类
 *
 * @author open.qq.com
 * @version 3.0.0
 * @copyright © 2012, Tencent Corporation. All rights reserved.
 * @History: 3.0.1 | coolinchen| 2012-11-07 11:20:12 | support POST request in  "multipart/form-data" format
 * 3.0.0 | nemozhang | 2012-03-21 12:01:05 | initialization
 * @since jdk1.5
 */


public class SnsNetwork {

    // 编码方式
    private static final String CONTENT_CHARSET = "UTF-8";
    // 每个路由最大连接数
    protected final static int MAX_ROUTE_CONNECTIONS = 100;
    // 最大连接数
    protected final static int MAX_TOTAL_CONNECTIONS = 500;
    // 连接超时时间
    private static final int CONNECTION_TIMEOUT = 3000;
    // 读数据超时时间
    private static final int READ_DATA_TIMEOUT = 3000;

    private static HttpClient client;

    static {
        MultiThreadedHttpConnectionManager manager = new MultiThreadedHttpConnectionManager();
        manager.getParams().setDefaultMaxConnectionsPerHost(MAX_ROUTE_CONNECTIONS);
        manager.getParams().setMaxTotalConnections(MAX_TOTAL_CONNECTIONS);
        manager.getParams().setConnectionTimeout(CONNECTION_TIMEOUT);
        manager.getParams().setSoTimeout(READ_DATA_TIMEOUT);
        client = new HttpClient(manager);
    }

    /**
     * 发送POST请求
     *
     * @param url      请求URL地址
     * @param params   请求参数
     * @param protocol 请求协议 "http" / "https"
     * @return 服务器响应的请求结果
     * @throws OpensnsException 网络故障时抛出异常。
     */
    public static String postRequest(
            String url,
            HashMap<String, String> params,
            HashMap<String, String> cookies,
            String protocol) throws OpensnsException {


        PostMethod postMethod = new PostMethod(url);


        // 设置请求参数
        if (params != null && !params.isEmpty()) {
            NameValuePair[] data = new NameValuePair[params.size()];

            Iterator iter = params.entrySet().iterator();
            int i = 0;
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                data[i] = new NameValuePair((String) entry.getKey(), (String) entry.getValue());
                ++i;
            }

            postMethod.setRequestBody(data);
        }

        // 设置cookie
        if (cookies != null && !cookies.isEmpty()) {
            Iterator iter = cookies.entrySet().iterator();
            StringBuilder buffer = new StringBuilder(128);
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                buffer.append((String) entry.getKey()).append("=").append((String) entry.getValue()).append("; ");
            }
            // 设置cookie策略
            postMethod.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);

            // 设置cookie内容
            postMethod.setRequestHeader("Cookie", buffer.toString());
        }

        // 设置User-Agent
        postMethod.setRequestHeader(CommonConstant.USER_AGENT, "Java OpenApiV3 SDK Client");

/*        // 设置每个路由最大连接数
        httpClient.getHttpConnectionManager().getParams().setDefaultMaxConnectionsPerHost(MAX_ROUTE_CONNECTIONS);

        // 设置最大连接数
        httpClient.getHttpConnectionManager().getParams().setMaxTotalConnections(MAX_TOTAL_CONNECTIONS);

        // 设置建立连接超时时间
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECTION_TIMEOUT);

        // 设置读数据超时时间
        httpClient.getHttpConnectionManager().getParams().setSoTimeout(READ_DATA_TIMEOUT);*/

        // 设置编码
        postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,
                CONTENT_CHARSET);

        //使用系统提供的默认的恢复策略
        postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                new DefaultHttpMethodRetryHandler());

        try {
            int statusCode = client.executeMethod(postMethod);

            if (statusCode != HttpStatus.SC_OK) {
                throw new OpensnsException(ErrorCode.NETWORK_ERROR, "Request [" + url + "] failed:" + postMethod.getStatusLine());
            }

            //读取内容
            byte[] responseBody = postMethod.getResponseBody();

            return new String(responseBody, CONTENT_CHARSET);
        } catch (HttpException e) {
            //发生致命的异常，可能是协议不对或者返回的内容有问题
            throw new OpensnsException(ErrorCode.NETWORK_ERROR, "Request [" + url + "] failed:" + e.getMessage());
        } catch (IOException e) {
            //发生网络异常
            throw new OpensnsException(ErrorCode.NETWORK_ERROR, "Request [" + url + "] failed:" + e.getMessage());
        } finally {
            //释放链接
            postMethod.releaseConnection();
        }
    }

}
