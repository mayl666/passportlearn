package com.sogou.upd.passport.common.asynchttpclient;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.ning.http.client.*;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.perf4j.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * Created with IntelliJ IDEA.
 * User: chengang
 * Date: 15-1-8
 * Time: 上午12:14
 */
public class AsyncHttpClientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncHttpClientService.class);
    /**
     * Async HTTP 客户端
     */
    private AsyncHttpClient httpClient;

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

    private static final String GET_QQ_FRIENDS_IP_PORT = "http://203.195.155.61:80";

    private static final String GET_QQ_FRIENDS_URL = "http://203.195.155.61:80/internal/qq/friends_info";


    /**
     * 使用默认值构造HTTP客户端服务<br/>
     * </p>
     * 默认主机最大连接数：br/>
     * 默认连接超时毫秒数：<br/>
     * 默认套接字超时毫秒数：<br/>
     */
    public AsyncHttpClientService() {
        this(DEFAULT_MAX_CON_PER_HOST, DEFAULT_CON_TIME_OUT_MS, DEFAULT_SO_TIME_OUT_MS);
    }

    /**
     * 构造HTTP客户端服务，默认不开启代理，如果需要使用代理功能应调用：<br/>
     *
     * @param maxConPerHost 主机最大连接数
     * @param conTimeOutMs  连接超时毫秒数
     * @param soTimeOutMs   套接字超时毫秒数
     */
  /*  public AsyncHttpClientService(final int maxConPerHost, final int conTimeOutMs, final int soTimeOutMs) {
        // 多线程连接管理器
        AsyncHttpClientConfig config = new AsyncHttpClientConfig.Builder()
                .setMaxConnectionsPerHost(maxConPerHost)
                .setConnectTimeout(conTimeOutMs)
                .setRequestTimeout(soTimeOutMs)
                .build();
        this.httpClient = new AsyncHttpClient(config);
    }*/
    public AsyncHttpClientService(final int maxConPerHost, final int conTimeOutMs, final int soTimeOutMs) {
        // 多线程连接管理器
        AsyncHttpClientConfig config = new AsyncHttpClientConfig.Builder()
                .setMaximumConnectionsPerHost(maxConPerHost)
                .setConnectionTimeoutInMs(conTimeOutMs)
                .setRequestTimeoutInMs(soTimeOutMs)
                .build();
        this.httpClient = new AsyncHttpClient(config);
    }


    /**
     * 构造提供代理功能的HTTP客户端服务，代理参数参见
     *
     * @param maxConPerHost 主机最大连接数
     * @param conTimeOutMs  连接超时毫秒数
     * @param soTimeOutMs   套接字超时毫秒数
     * @param proxyConfig   代理配置参数
     */
   /* public AsyncHttpClientService(final int maxConPerHost, final int conTimeOutMs,
                                  final int soTimeOutMs, final HttpClientProxyConfig proxyConfig) {
        this(maxConPerHost, conTimeOutMs, soTimeOutMs);
        if (proxyConfig.isUseProxy()) {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(proxyConfig.getProxyHost()),
                    "proxy host must not be null or empty.");
            Preconditions.checkArgument(proxyConfig.getProxyPort() > 0, "proxy port must be larger than 0.");
            ProxyServer proxyServer = new ProxyServer(proxyConfig.getProxyHost(), proxyConfig.getProxyPort(), proxyConfig.getProxyAuthUser(), proxyConfig.getProxyAuthPassword());
            AsyncHttpClientConfig config = new AsyncHttpClientConfig.Builder()
                    .setMaxConnectionsPerHost(maxConPerHost)
                    .setConnectTimeout(conTimeOutMs)
                    .setRequestTimeout(soTimeOutMs)
                    .setUseProxyProperties(proxyConfig.isUseProxy())
                    .setProxyServer(proxyServer)
                    .build();
            this.httpClient = new AsyncHttpClient(config);
        }
    }*/
    public AsyncHttpClientService(final int maxConPerHost, final int conTimeOutMs,
                                  final int soTimeOutMs, final HttpClientProxyConfig proxyConfig) {
        this(maxConPerHost, conTimeOutMs, soTimeOutMs);
        if (proxyConfig.isUseProxy()) {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(proxyConfig.getProxyHost()),
                    "proxy host must not be null or empty.");
            Preconditions.checkArgument(proxyConfig.getProxyPort() > 0, "proxy port must be larger than 0.");
            ProxyServer proxyServer = new ProxyServer(proxyConfig.getProxyHost(), proxyConfig.getProxyPort(), proxyConfig.getProxyAuthUser(), proxyConfig.getProxyAuthPassword());
            AsyncHttpClientConfig config = new AsyncHttpClientConfig.Builder()
                    .setMaximumConnectionsPerHost(maxConPerHost)
                    .setConnectionTimeoutInMs(conTimeOutMs)
                    .setRequestTimeoutInMs(soTimeOutMs)
                    .setUseProxyProperties(proxyConfig.isUseProxy())
                    .setProxyServer(proxyServer)
                    .build();
            this.httpClient = new AsyncHttpClient(config);
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
    /*public String sendGet(String url, FluentStringsMap getParams, Map<String, Collection<String>> headers) throws Exception {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(url), "URL can not be empty or null.");
        LOGGER.debug("Get Request:{}", url);
        AsyncHttpClient.BoundRequestBuilder boundRequestBuilder = httpClient.prepareGet(url)
                .setBodyEncoding(Constants.DEFAULT_CHARSET)
                .setQueryParams(getParams)
                .setHeaders(headers);
        return sendRequest(boundRequestBuilder);
    }*/
    public String sendGet(String url, FluentStringsMap getParams, Map<String, Collection<String>> headers) throws Exception {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(url), "URL can not be empty or null.");
        LOGGER.debug("Get Request:{}", url);
        AsyncHttpClient.BoundRequestBuilder boundRequestBuilder = httpClient.prepareGet(url)
                .setBodyEncoding(Constants.DEFAULT_CHARSET)
                .setQueryParameters(getParams)
                .setHeaders(headers);
        return sendRequest(boundRequestBuilder);
    }

    /**
     * 发送post请求
     *
     * @param url       请求的URL
     * @param getParams POST请求参数
     * @param headers   请求头部参数
     * @return
     */
 /*   public String sendPost(String url, Map<String, List<String>> getParams, Map<String, Collection<String>> headers) throws Exception {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(url), "URL can not be empty or null.");
        LOGGER.debug("Post Request:{}", url);
        AsyncHttpClient.BoundRequestBuilder boundRequestBuilder = httpClient.preparePost(url);
        boundRequestBuilder.setBodyEncoding("UTF-8");
        boundRequestBuilder.setHeaders(headers);
        boundRequestBuilder.setQueryParams(getParams);
        return sendRequest(boundRequestBuilder);
    }
*/
    public String sendPost(String url, Map<String, Collection<String>> getParams, Map<String, Collection<String>> headers) throws Exception {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(url), "URL can not be empty or null.");
        LOGGER.debug("Post Request:{}", url);
        AsyncHttpClient.BoundRequestBuilder boundRequestBuilder = httpClient.preparePost(url);
        boundRequestBuilder.setBodyEncoding("UTF-8");
        boundRequestBuilder.setHeaders(headers);
        boundRequestBuilder.setParameters(getParams);
        return sendRequest(boundRequestBuilder);
    }

    public String sendPreparePost(String url, Map<String, String> params) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(url), "URL can not be empty or null.");
        LOGGER.debug("Post Request:{}", url);
        try {

            String userId = null;
            String tKey = null;

            if (params != null && params.size() > 0) {
                userId = params.get("userid");
                tKey = params.get("tKey");
            }
            StopWatch watch = new StopWatch();
            watch.start();

            /*Response resp = httpClient.prepareGet(GET_QQ_FRIENDS_IP_PORT + GET_QQ_FRIENDS_URL).setBody("").addQueryParam("userid", userId).addQueryParam("tKey", tKey).execute(new AsyncCompletionHandler<Response>() {

                public STATE onHeaderWriteCompleted() {
                    headerSent.set(true);
                    return STATE.CONTINUE;
                }

                public STATE onContentWriteCompleted() {
                    operationCompleted.set(true);
                    return STATE.CONTINUE;
                }

                @Override
                public Response onCompleted(Response response) throws Exception {
                    return response;
                }
            }).get();
            LOGGER.warn("user time :" + watch.getElapsedTime());
            String responseData = resp.getResponseBody();
            return responseData;*/

            AsyncHttpClient aysncHttpClient = new AsyncHttpClient();
            Response response = aysncHttpClient.preparePost(GET_QQ_FRIENDS_URL).addParameter("userid", userId).addParameter("tKey", tKey).execute(new AsyncCompletionHandler<Response>() {
                @Override
                public Response onCompleted(Response response) throws Exception {
                    return response;
                }
            }).get();

            LOGGER.warn("response.getStatusCode() :" + response.getStatusCode());
            String responseBody = response.getResponseBody();
            return responseBody;


//            InputStream inputStream = resp.getResponseBodyAsStream();
//            return IOUtils.toString(inputStream, Constants.DEFAULT_CHARSET);

//            assertNotNull(resp);
//            assertEquals(resp.getStatusCode(), HttpServletResponse.SC_OK);
//            assertEquals(resp.getResponseBody(), SIMPLE_TEXT_FILE_STRING);
//            assertTrue(operationCompleted.get());
//            assertTrue(headerSent.get());

        } catch (Exception e) {
            LOGGER.error("sendPost error.", e);
        }
        return "";
    }


    /**
     * 发送指定Http方法的请求，并接收response的字符串
     * <p/>
     * 对于服务器返回的压缩数据的情况，目前支持gzip和deflate两种解压缩方式
     *
     * @param boundRequestBuilder GET或POST方法
     * @return 返回response字符串
     * @throws java.io.IOException
     * @throws IllegalStateException 对于gzip和deflate以外压缩格式，抛出该异常
     */
    private String sendRequest(AsyncHttpClient.BoundRequestBuilder boundRequestBuilder) throws Exception {
        ListenableFuture<Response> listenableFuture = null;
        try {
            listenableFuture = boundRequestBuilder.execute();
            Response response = listenableFuture.get();
            int statusCode = response.getStatusCode();
            LOGGER.debug("Response return statusCode:{}", statusCode);

            if (statusCode != HttpStatus.SC_OK) {
                throw new IllegalStateException("Http response error. status code:" + statusCode);
            }

            /*if (LOGGER.isDebugEnabled()) {
                FluentCaseInsensitiveStringsMap resHeader = response.getHeaders();
                LOGGER.debug("response headers :");
            }
*/
            String encodingHeader = response.getHeader(HttpHeaders.Names.CONTENT_ENCODING);
            if (!Strings.isNullOrEmpty(encodingHeader)) {
                if (HttpClientConfig.CompressFormat.COMPRESS_FORMAT_GZIP.isBelong(encodingHeader)) {
                    return HttpClientUtils.uncompressStream(HttpClientConfig.CompressFormat.COMPRESS_FORMAT_GZIP, response.getResponseBodyAsStream());
                } else if (HttpClientConfig.CompressFormat.COMPRESS_FORMAT_DEFLATE.isBelong(encodingHeader)) {
                    return HttpClientUtils.uncompressStream(HttpClientConfig.CompressFormat.COMPRESS_FORMAT_DEFLATE, response.getResponseBodyAsStream());
                } else if (HttpClientConfig.CompressFormat.COMPRESS_FORMAT_IDENTITY.isBelong(encodingHeader)) {
                    return response.getResponseBody();
                } else {
                    LOGGER.error("Unsupported HTTP compressed encoding:{}", encodingHeader);
                    throw new RuntimeException("Unsupported HTTP compressed encoding:" + encodingHeader);
                }
            }
            // 如果response头部没有指示编码格式，认为非压缩，直接返回
//            InputStreamReader inputReader = new InputStreamReader(response.getResponseBodyAsStream(), Constants.DEFAULT_CHARSET);
//                     return CharStreams.toString(inputReader);
//            InputStream inputStream = response.getResponseBodyAsStream();
//            String bufferData = IOUtils.toString(inputStream, Constants.DEFAULT_CHARSET);
//            return bufferData;

            return response.getResponseBody();
//            return IOUtils.toString(inputStream, Constants.DEFAULT_CHARSET);
        } finally {
            /// 不论如何，释放连接
//            if (listenableFuture != null && listenableFuture.isDone()) {
//                listenableFuture.cancel(true);
//            }
        }
    }
}

