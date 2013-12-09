package com.sogou.upd.passport.common.utils;

import com.google.common.collect.Maps;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.params.ClientPNames;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public class HttpClientUtil {

    /**
     * 超过500ms的请求定义为慢请求
     */
    private final static int SLOW_TIME = 500;

    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    private static final Logger prefLogger = LoggerFactory.getLogger("httpClientTimingLogger");

    public static Pair<Integer, String> get(String url) {
        GetMethod get = new GetMethod(url);
        return doWget(get);
    }

    // integer 表示的返回状态吗，String表示的Content-Type的值，InputStream是内容
    public static Pair<Integer, Pair<String, byte[]>> getFile(String url) {
        GetMethod get = new GetMethod(url);
        return doWgetAsStream(get);
    }

    public static Pair<Integer, String> post(String url, Map<String, String> postdata)
            throws Exception {
        PostMethod post = new PostMethod(url);
        post.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        for (Entry<String, String> entry : postdata.entrySet()) {
            post.addParameter(entry.getKey(), entry.getValue());
//			post.addParameter(entry.getKey(), StringUtil.urlEncodeUTF8(entry.getValue()));//SignatureUtil.encUtf8(entry.getValue()));
        }
        return doWget(post);
    }

    public static Pair<Integer, String> postJson(String url, String json) throws Exception {
        PostMethod post = new PostMethod(url);

        StringRequestEntity requestEntity = new StringRequestEntity(json, "application/json",
                "UTF-8");
        post.setRequestEntity(requestEntity);
        return doWget(post);
    }


    public static Pair<Integer, String> postContent(String url, String data, String charset) {
        try {
            PostMethod post = new PostMethod(url);
            post.setRequestEntity(new StringRequestEntity(data, null, charset));
            return doWget(post, charset);
        } catch (Exception e) {
            String message = e.getClass().getName() + "|" + e.getMessage();
            return Pair.of(0, message);
        }
    }

    private static Pair<Integer, String> doWget(HttpMethod method) {
        return doWget(method, null);
    }

    public static int doHead(String url) {
        HeadMethod method = new HeadMethod(url);
        try {
            method.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
            int code = client.executeMethod(method);
            return code;
        } catch (Exception e) {
            String message = e.getClass().getName() + "|" + e.getMessage();
            System.out.println(message);
            return 0;
        } finally {
            method.releaseConnection();
        }
    }

    public static Header[] getResponseHeadersWget(String url) {
        StopWatch stopWatch = new Slf4JStopWatch(prefLogger);
        GetMethod method = new GetMethod(url);
        String[] urlArray = url.split("[?]");
        try {
            method.setFollowRedirects(false);
            method.setDoAuthentication(false);
            method.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
            method.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false);
            shClient.executeMethod(method);
            stopWatch(stopWatch, urlArray[0], "success");
            return method.getResponseHeaders();
        } catch (Exception e) {
            stopWatch(stopWatch, urlArray[0] + "(fail)", "failed");
            logger.error("http request error", e);
            return null;
        } finally {
            method.releaseConnection();
        }
    }

    public static String getgetResponseBodyWget(String url) {
        StopWatch stopWatch = new Slf4JStopWatch(prefLogger);
        GetMethod method = new GetMethod(url);
        String[] urlArray = url.split("[?]");
        try {
            method.setFollowRedirects(false);
            method.setDoAuthentication(false);
            method.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
            method.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false);
            shClient.executeMethod(method);
            stopWatch(stopWatch, urlArray[0], "success");
            return method.getResponseBodyAsString();
        } catch (Exception e) {
            stopWatch(stopWatch, urlArray[0] + "(fail)", "failed");
            logger.error("http request error", e);
            return null;
        } finally {
            method.releaseConnection();
        }
    }

    private static void stopWatch(StopWatch stopWatch, String tag, String message) {
        //无论什么情况都记录下所有的请求数据
        if (stopWatch.getElapsedTime() >= SLOW_TIME) {
            tag += "(slow)";
        }
        stopWatch.stop(tag, message);
    }

    private static Pair<Integer, String> doWget(HttpMethod method, String charset) {
        try {
            method.setFollowRedirects(false);
            method.setDoAuthentication(false);
            method.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
            method.getParams().setParameter(HttpMethodParams.USER_AGENT,
                    "Sogou Passport Center Notifier");
            method.setRequestHeader("Accept-Encoding", "gzip, deflate");
            int code = client.executeMethod(method);
            InputStream in = method.getResponseBodyAsStream();

            in = decode(in, method);
            Reader reader = read(in, method, charset);
            String body = read(reader);
            return Pair.of(code, body);
        } catch (Exception e) {
            String message = e.getClass().getName() + "|" + e.getMessage();
            return Pair.of(0, message);
        } finally {
            method.releaseConnection();
        }
    }

    private static Pair<Integer, Pair<String, byte[]>> doWgetAsStream(HttpMethod method) {
        try {
            method.setFollowRedirects(false);
            method.setDoAuthentication(false);
            method.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
            method.getParams().setParameter(HttpMethodParams.USER_AGENT,
                    "Sogou Passport Center Notifier");
            method.setRequestHeader("Accept-Encoding", "gzip, deflate");
            int code = client.executeMethod(method);
            //			InputStream in = method.getResponseBodyAsStream();
            //			Header h = method.getResponseHeader("Content-Length");
            //			byte[] bytes = new byte[Integer.parseInt(h.getValue())];
            //			in.read(bytes, 0, bytes.length);
            byte[] bytes = method.getResponseBody();
            Header respHeader = method.getResponseHeader("Content-Type");
            String mime = respHeader.getValue();
            Pair<String, byte[]> content = Pair.of(mime, bytes);
            return Pair.of(code, content);
        } catch (Exception e) {
            e.printStackTrace();
            return Pair.of(0, null);
        } finally {
            method.releaseConnection();
        }
    }

    private static InputStream decode(InputStream in, HttpMethod method) throws IOException {
        Header encodingHeader = method.getResponseHeader("Content-Encoding");
        if (encodingHeader != null) {
            String encoding = encodingHeader.getValue();
            if (encoding.contains("gzip"))
                in = new GZIPInputStream(in);
            else if (encoding.contains("deflate")) in = new InflaterInputStream(in);
        }
        return in;
    }

    private static Reader read(InputStream in, HttpMethod method, String charset)
            throws IOException {
        // check utf8 bom
        byte[] cache = new byte[3];
        int cacheSize = in.read(cache);
        if (cacheSize <= 0) return new StringReader("");

        byte[] bomUtf8 = {(byte) 0xef, (byte) 0xbb, (byte) 0xbf};
        if (Arrays.equals(bomUtf8, cache)) return new InputStreamReader(in, "utf-8");
        in = pushback(in, cache);

        // check by header
        if (charset == null) {
            charset = getCharsetFromHeader(method);
            if (charset.equals("gb2312") || charset.equals("gbk")) charset = "gb18030";
            if (charset == null) charset = "utf-8";
        }
        return new InputStreamReader(in, charset);
    }

    private static InputStream pushback(InputStream in, byte[] cache) throws IOException {
        PushbackInputStream pushback = new PushbackInputStream(in, cache.length);
        pushback.unread(cache, 0, cache.length);
        return pushback;
    }

    private static String getCharsetFromHeader(HttpMethod get) {
        Header content = get.getResponseHeader("Content-Type");
        if (content != null) {
            String contentType = content.getValue().toLowerCase();
            String charsetPrefix = "charset=";
            int idx = contentType.indexOf(charsetPrefix);
            if (idx > 0) {
                String charset = contentType.substring(idx + charsetPrefix.length()).trim();
                charset = charset.replaceAll("[^\\w\\d_-].*", "");
                if (charset.length() > 0) return charset;
            }
        }
        return null;
    }

    private static String read(Reader reader) throws IOException {
        StringBuilder cache = new StringBuilder();
        char[] buf = new char[1024];
        int l;
        while ((l = reader.read(buf)) > 0)
            cache.append(buf, 0, l);
        reader.close();
        return cache.toString();
    }

    public static HttpClient getClient() {
        return client;
    }

    private static HttpClient client;
    private static HttpClient shClient; //调用搜狐的setcookie接口

    static {
        MultiThreadedHttpConnectionManager manager = new MultiThreadedHttpConnectionManager();
        manager.getParams().setDefaultMaxConnectionsPerHost(100);
        manager.getParams().setMaxTotalConnections(500);
        manager.getParams().setConnectionTimeout(5000);
        manager.getParams().setSoTimeout(5000);
        client = new HttpClient(manager);
    }

    static {
        MultiThreadedHttpConnectionManager shManager = new MultiThreadedHttpConnectionManager();
        shManager.getParams().setDefaultMaxConnectionsPerHost(100);
        shManager.getParams().setMaxTotalConnections(500);
        shManager.getParams().setConnectionTimeout(1000);
        shManager.getParams().setSoTimeout(1000);
        shClient = new HttpClient(shManager);
    }

    public static void main(String[] args) throws Exception {
        Map<String, String> postData = Maps.newHashMap();
        postData.put("appid", "1003");
        postData.put("account", "18610017622");
        postData.put("signature", "tj6sEa2BFKeoFd1pSNFqOZVFSwZLmvldAgvOt_Ojoqs");
        postData.put("nickname", "戴菲菲");


//        Pair<Integer, String> p = HttpClientUtil.post("http://localhost/account/regexpuser",
//                postData);
//        System.out.println(p);
        String urlStr = "http://passport.sohu.com/act/setcookie?userid=wg494943628@sogou.com&appid=1120&ct=1382384218435&code=ffee354f18ef84cf73b4655a37ddd528&ru=http://profile.pinyin.sogou.com/&persistentcookie=0&domain=sogou.com";
        URL url = new URL(urlStr);
        Header[] headers = getResponseHeadersWget(urlStr);
        System.out.println("protocol:" + url.getProtocol());
        System.out.println("host:" + url.getHost());
        System.out.println("path:" + url.getPath());
        System.out.println("uri:" + url.toURI().getScheme());
    }


}
