package com.sogou.upd.passport.common.apache_asynhttpclient;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.parameter.HttpMethodEnum;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.*;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.ssl.BrowserCompatHostnameVerifier;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.codecs.DefaultHttpRequestWriterFactory;
import org.apache.http.impl.nio.codecs.DefaultHttpResponseParserFactory;
import org.apache.http.impl.nio.conn.ManagedNHttpClientConnectionFactory;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.NHttpMessageParserFactory;
import org.apache.http.nio.NHttpMessageWriterFactory;
import org.apache.http.nio.conn.ManagedNHttpClientConnection;
import org.apache.http.nio.conn.NHttpConnectionFactory;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.nio.util.HeapByteBufferAllocator;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: lzy_clement
 * Date: 15-2-28
 * Time: 下午6:02
 * To change this template use File | Settings | File Templates.
 */
public class ApacheAsynHttpClient {

    /**
     * 最大连接数
     */
    protected final static int MAX_TOTAL_CONNECTIONS = 1500;
    /**
     * 获取连接的最大等待时间
     */
    protected final static int WAIT_TIMEOUT = 3000;
    /**
     * 每个路由最大连接数
     */
    protected final static int MAX_ROUTE_CONNECTIONS = 500;
    /**
     * 读取超时时间
     */
    protected final static int READ_TIMEOUT = 3000;


    protected static CloseableHttpAsyncClient httpClient = null;

    static {
//        RequestConfig requestConfig = RequestConfig.custom()
//                .setSocketTimeout(READ_TIMEOUT)
//                .setConnectTimeout(WAIT_TIMEOUT).build();
//        httpClient = HttpAsyncClients.custom().setDefaultRequestConfig(requestConfig).build();
        try {
            httpClient = init();
            httpClient.start();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static CloseableHttpAsyncClient init() throws IOReactorException, NoSuchAlgorithmException {
        NHttpMessageWriterFactory<HttpRequest> requestWriterFactory = new DefaultHttpRequestWriterFactory();
        NHttpMessageParserFactory<HttpResponse> responseParserFactory = new DefaultHttpResponseParserFactory();
        NHttpConnectionFactory<ManagedNHttpClientConnection> connFactory = new ManagedNHttpClientConnectionFactory(
                requestWriterFactory, responseParserFactory, HeapByteBufferAllocator.INSTANCE);
        IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
                .setIoThreadCount(Runtime.getRuntime().availableProcessors())
                .setConnectTimeout(READ_TIMEOUT)
                .setSoTimeout(WAIT_TIMEOUT)
                .build();
        ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);
        SSLContext sslcontext = SSLContexts.createDefault();
        // Use custom hostname verifier to customize SSL hostname verification.
        X509HostnameVerifier hostnameVerifier = new BrowserCompatHostnameVerifier();
        Registry<SchemeIOSessionStrategy> sessionStrategyRegistry = RegistryBuilder.<SchemeIOSessionStrategy>create()
                .register("http", NoopIOSessionStrategy.INSTANCE)
                .register("https", new SSLIOSessionStrategy(sslcontext, hostnameVerifier))
                .build();
        DnsResolver dnsResolver = new SystemDefaultDnsResolver();

        PoolingNHttpClientConnectionManager connManager = new PoolingNHttpClientConnectionManager(
                ioReactor, connFactory, sessionStrategyRegistry, dnsResolver);

//        MessageConstraints messageConstraints = MessageConstraints.custom()
//                .setMaxHeaderCount(200)
//                .setMaxLineLength(2000)
//                .build();
        // Create connection configuration
//        ConnectionConfig connectionConfig = ConnectionConfig.custom()
//                .setMalformedInputAction(CodingErrorAction.IGNORE)
//                .setUnmappableInputAction(CodingErrorAction.IGNORE)
//                .setCharset(Consts.UTF_8)
//                .setMessageConstraints(messageConstraints)
//                .build();
        // Configure the connection manager to use connection configuration either
        // by default or for a specific host.
//        connManager.setDefaultConnectionConfig(connectionConfig);
//        connManager.setConnectionConfig(new HttpHost("somehost", 80), ConnectionConfig.DEFAULT);

        // Configure total max or per route limits for persistent connections
        // that can be kept in the pool or leased by the connection manager.
        connManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
        connManager.setDefaultMaxPerRoute(MAX_ROUTE_CONNECTIONS);
//        connManager.setMaxPerRoute(new HttpRoute(new HttpHost("somehost", 80)), 20);

        // Use custom cookie store if necessary.
        CookieStore cookieStore = new BasicCookieStore();
        // Use custom credentials provider if necessary.
//        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        // Create global request configuration
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                .setExpectContinueEnabled(true)
                .setStaleConnectionCheckEnabled(true)
//                .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
//                .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
                .build();

        // Create an HttpClient with the given custom dependencies and configuration.
        return HttpAsyncClients.custom()
                .setConnectionManager(connManager)
                .setDefaultCookieStore(cookieStore)
//                .setDefaultCredentialsProvider(credentialsProvider)
//                .setProxy(new HttpHost("myproxy", 8080))
                .setDefaultRequestConfig(defaultRequestConfig)
                .addInterceptorFirst(new HttpRequestInterceptor() {
                    @Override
                    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
                        if (!request.containsHeader("Accept-Encoding")) {
                            request.addHeader("Accept-Encoding", "gzip");
                        }
                    }
                })
//                .addInterceptorFirst(new HttpResponseInterceptor() {
//                    @Override
//                    public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
//                        HttpEntity entity = response.getEntity();
//                        if (entity != null) {
//                            Header ceheader = entity.getContentEncoding();
//                            if (ceheader != null) {
//                                HeaderElement[] codecs = ceheader.getElements();
//                                for (int i = 0; i < codecs.length; i++) {
//                                    if(codecs[i].getName().equalsIgnoreCase("gzip")) {
//                                        response.setEntity(new GzipDecompressingEntity(response.getEntity()));
//                                        return;
//                                    }
//                                }
//                            }
//                        }
//                    }
//                })
                .build();

    }

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
        InputStream in = null;
        try {
            Future<HttpResponse> future = httpClient.execute(httpRequest, null);
            HttpEntity httpEntity = future.get().getEntity();
            System.out.print(future.get().getEntity().getContentLength());
            if (httpEntity != null) {
                Header ceheader = httpEntity.getContentEncoding();
                if (ceheader != null) {
                    HeaderElement[] codecs = ceheader.getElements();
                    for (int i = 0; i < codecs.length; i++) {
                        if (codecs[i].getName().equalsIgnoreCase("gzip")) {
                            httpEntity = new GzipDecompressingEntity(httpEntity);
                        }
                    }
                }
            }
            in = httpEntity.getContent();
            int responseCode = future.get().getStatusLine().getStatusCode();
            //302如何处理
            if (responseCode == RESPONSE_SUCCESS_CODE) {
                return httpEntity;
            }
            String params = EntityUtils.toString(requestModel.getRequestEntity(), CommonConstant.DEFAULT_CHARSET);
            String result = EntityUtils.toString(httpEntity, CommonConstant.DEFAULT_CHARSET);
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
