package com.sogou.upd.passport.common.asynchttpclient;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.io.CharStreams;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chengang
 * Date: 15-1-9
 * Time: 下午12:49
 */
public class HttpClientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientService.class);
    /**
     * Apache HTTP 客户端
     */
    private HttpClient httpClient;

    /**
     * 默认主机最大连接数
     */
    private static final int DEFAULT_MAX_CON_PER_HOST = 100;
    /**
     * 默认连接超时毫秒数
     */
    private static final int DEFAULT_CON_TIME_OUT_MS = 3000;
    /**
     * 默认套接字超时毫秒数
     */
    private static final int DEFAULT_SO_TIME_OUT_MS = 3000;

    /**
     * 使用默认值构造HTTP客户端服务<br/>
     * </p>
     * 默认主机最大连接数：{@link HttpClientService#DEFAULT_MAX_CON_PER_HOST} <br/>
     * 默认连接超时毫秒数：{@link HttpClientService#DEFAULT_CON_TIME_OUT_MS} <br/>
     * 默认套接字超时毫秒数：{@link HttpClientService#DEFAULT_SO_TIME_OUT_MS}  <br/>
     */
    public HttpClientService() {
        this(DEFAULT_MAX_CON_PER_HOST, DEFAULT_CON_TIME_OUT_MS, DEFAULT_SO_TIME_OUT_MS);
    }

    /**
     * 构造HTTP客户端服务，默认不开启代理，如果需要使用代理功能应调用：<br/>
     *
     * @param maxConPerHost 主机最大连接数
     * @param conTimeOutMs  连接超时毫秒数
     * @param soTimeOutMs   套接字超时毫秒数
     */
    public HttpClientService(final int maxConPerHost, final int conTimeOutMs, final int soTimeOutMs) {
        // 多线程连接管理器
        MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        HttpConnectionManagerParams params = connectionManager.getParams();
        params.setDefaultMaxConnectionsPerHost(maxConPerHost);
        params.setConnectionTimeout(conTimeOutMs);
        params.setSoTimeout(soTimeOutMs);

        HttpClientParams clientParams = new HttpClientParams();
        clientParams.setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
        httpClient = new HttpClient(clientParams, connectionManager);
    }

    /**
     * 构造提供代理功能的HTTP客户端服务，代理参数参见{@link HttpClientProxyConfig}
     *
     * @param maxConPerHost 主机最大连接数
     * @param conTimeOutMs  连接超时毫秒数
     * @param soTimeOutMs   套接字超时毫秒数
     * @param proxyConfig   代理配置参数
     */
    public HttpClientService(final int maxConPerHost, final int conTimeOutMs,
                             final int soTimeOutMs, final HttpClientProxyConfig proxyConfig) {
        this(maxConPerHost, conTimeOutMs, soTimeOutMs);
        if (proxyConfig.isUseProxy()) {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(proxyConfig.getProxyHost()),
                    "proxy host must not be null or empty.");
            Preconditions.checkArgument(proxyConfig.getProxyPort() > 0, "proxy port must be larger than 0.");
            httpClient.getHostConfiguration().setProxy(proxyConfig.getProxyHost(), proxyConfig.getProxyPort());
            LOGGER.info("Proxy host: {}", proxyConfig.getProxyHost());
            LOGGER.info("Proxy port: {}", proxyConfig.getProxyPort());
            //代理授权用户名和密码可以为空，即该代理不需要用户名密码认证
            if (!Strings.isNullOrEmpty(proxyConfig.getProxyAuthUser())) {
                httpClient.getParams().setAuthenticationPreemptive(true);
                httpClient.getState().setProxyCredentials(AuthScope.ANY,
                        new UsernamePasswordCredentials(
                                proxyConfig.getProxyAuthUser(), proxyConfig.getProxyAuthPassword()));
                LOGGER.info("Proxy auth user name: {}", proxyConfig.getProxyAuthUser());
                LOGGER.info("Proxy auth password: {}", proxyConfig.getProxyAuthPassword());
            }
        }
    }


    /**
     * 发送get请求
     *
     * @param url       请求的URL
     * @param getParams GET请求参数
     * @param headers   请求头部参数
     * @return
     */
    public String sendGet(String url, Map<String, String> getParams, Map<String, String> headers) throws IOException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(url), "URL can not be empty or null.");
        LOGGER.info("Get Request:{}", url);
        GetMethod get = new GetMethod(url);
        HttpClientUtils.assemblyGetParams(get, getParams);
        HttpClientUtils.assemblyHeaders(get, headers);
        return sendRequest(get);
    }

    /**
     * 发送post请求
     *
     * @param url        请求的URL
     * @param postParams POST请求参数
     * @param headers    请求头部参数
     * @return
     */

    public String sendPost(String url, Map<String, String> postParams, Map<String, String> headers) throws IOException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(url), "URL can not be empty or null.");
        LOGGER.info("Post Request:{}", url);
        PostMethod post = new PostMethod(url);
        HttpClientUtils.assemblyPostParams(post, postParams);
        HttpClientUtils.assemblyHeaders(post, headers);
        return sendRequest(post);
    }

    /**
     * 发送指定Http方法的请求，并接收response的字符串
     * <p/>
     * 对于服务器返回的压缩数据的情况，目前支持gzip和deflate两种解压缩方式
     *
     * @param method GET或POST方法
     * @return 返回response字符串
     * @throws IOException
     * @throws IllegalStateException 对于gzip和deflate以外压缩格式，抛出该异常
     */
    private String sendRequest(HttpMethod method) throws IOException, IllegalStateException {
        try {
            method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                    new DefaultHttpMethodRetryHandler(3, false));
            httpClient.executeMethod(method);

            int statusCode = method.getStatusCode();
            LOGGER.debug("Response return statusCode:{}", statusCode);

            if (statusCode != HttpStatus.SC_OK) {
                throw new IllegalStateException("Http response error. status code:" + statusCode);
            }

            if (LOGGER.isDebugEnabled()) {
                Header[] resHeader = method.getResponseHeaders();
                LOGGER.debug("response headers :");
                for (Header header : resHeader) {
                    LOGGER.debug(header.getName() + ":" + header.getValue());
                }
            }

            Header encodingHeader = method.getResponseHeader(HttpHeaders.Names.CONTENT_ENCODING);
            if (encodingHeader != null) {
                if (HttpClientConfig.CompressFormat.COMPRESS_FORMAT_GZIP.isBelong(encodingHeader.getValue())) {
                    return HttpClientUtils.uncompressStream(HttpClientConfig.CompressFormat.COMPRESS_FORMAT_GZIP, method.getResponseBodyAsStream());
                } else if (HttpClientConfig.CompressFormat.COMPRESS_FORMAT_DEFLATE.isBelong(encodingHeader.getValue())) {
                    return HttpClientUtils.uncompressStream(HttpClientConfig.CompressFormat.COMPRESS_FORMAT_DEFLATE, method.getResponseBodyAsStream());
                } else if (HttpClientConfig.CompressFormat.COMPRESS_FORMAT_IDENTITY.isBelong(encodingHeader.getValue())) {
                    return method.getResponseBodyAsString();
                } else {
                    LOGGER.error("Unsupported HTTP compressed encoding:{}", encodingHeader.getValue());
                    throw new RuntimeException("Unsupported HTTP compressed encoding:" + encodingHeader.getValue());
                }
            }
            // 如果response头部没有指示编码格式，认为非压缩，直接返回
            InputStreamReader inputReader = new InputStreamReader(method.getResponseBodyAsStream(), Constants.DEFAULT_CHARSET);
            return CharStreams.toString(inputReader);
        } finally {
            // 不论如何，释放连接
            method.releaseConnection();
        }
    }


    /**
     * 发送指定Http方法的请求，并接收response的二进制流,
     * <p/>
     * 务必记住处理完毕后，关闭数据流
     *
     * @param url
     * @param getParams
     * @return
     * @throws IOException
     */
    public InputStream sendGetForStream(String url, Map<String, String> getParams) throws IOException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(url), "URL can not be empty or null.");
        LOGGER.info("Get Request:{}", url);
        GetMethod method = new GetMethod(url);
        HttpClientUtils.assemblyGetParams(method, getParams);
        HttpClientUtils.assemblyHeaders(method, null);
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                new DefaultHttpMethodRetryHandler(3, false));
        httpClient.executeMethod(method);

        int statusCode = method.getStatusCode();
        LOGGER.debug("Response return statusCode:{}", statusCode);

        if (statusCode != HttpStatus.SC_OK) {
            throw new IllegalStateException("Http response error. status code:" + statusCode);
        }

        if (LOGGER.isDebugEnabled()) {
            Header[] resHeader = method.getResponseHeaders();
            LOGGER.debug("response headers :");
            for (Header header : resHeader) {
                LOGGER.debug(header.getName() + ":" + header.getValue());
            }
        }

        return method.getResponseBodyAsStream();
    }
}
