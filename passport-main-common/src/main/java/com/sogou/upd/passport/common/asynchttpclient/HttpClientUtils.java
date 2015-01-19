package com.sogou.upd.passport.common.asynchttpclient;

import com.google.common.io.Closeables;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.DefaultedHttpParams;
import org.apache.http.params.HttpParams;

import java.io.*;
import java.util.Map;
import java.util.zip.DeflaterInputStream;
import java.util.zip.GZIPInputStream;
/**
 * Created with IntelliJ IDEA.
 * User: chengang
 * Date: 15-1-8
 * Time: 上午12:37
 */
public class HttpClientUtils {


    private HttpClientUtils() {
    }


    /**
     * 解压缩GZIP或者DEFLATE输入流
     *
     * @param compressFormat
     * @param inputStream    原始字节流
     * @return 解压缩后的字符串
     * @throws java.io.IOException
     */
    public static String uncompressStream(HttpClientConfig.CompressFormat compressFormat,
                                          InputStream inputStream) throws IOException {
        // FilterInputStream是GZIPInputStream和DeflaterInputStream两类流的公共父类
        FilterInputStream compressStream = null;
        InputStreamReader inputReader = null;
        StringWriter strWriter = null;
        try {
            switch (compressFormat) {
                case COMPRESS_FORMAT_GZIP:
                    compressStream = new GZIPInputStream(inputStream);
                    break;
                case COMPRESS_FORMAT_DEFLATE:
                    compressStream = new DeflaterInputStream(inputStream);
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            inputReader = new InputStreamReader(compressStream, Constants.DEFAULT_CHARSET);
            strWriter = new StringWriter();
            IOUtils.copy(inputReader, strWriter);
            return strWriter.toString();
        } finally {
            //关闭顺序 :先外后里
            Closeables.closeQuietly(strWriter);
            Closeables.closeQuietly(inputReader);
            Closeables.closeQuietly(compressStream);
        }
    }

    /**
     * 组装拼接get请求参数
     *
     * @param getMethod get方法
     * @param getParams 请求的query参数，可以为null或长度为0
     * @return
     */
    public static void assemblyGetParams(GetMethod getMethod, Map<String, String> getParams) {
        if (getParams == null || getParams.size() == 0) {
            return;
        }
        NameValuePair[] nvPairs = new NameValuePair[getParams.size()];
        int idx = 0;
        for (Map.Entry<String, String> entry : getParams.entrySet()) {
            nvPairs[idx] = new NameValuePair(entry.getKey(), entry.getValue());
            ++idx;
        }
        // 该方法默认使用UTF-8编码
        getMethod.setQueryString(nvPairs);
    }


    /**
     * 组装拼接post请求参数
     *
     * @param httpRequestBase post方法
     * @param params post参数，可以为null或长度为0
     */
    public static void assemblyAsyncParams(HttpRequestBase httpRequestBase, Map<String, String> params) {
        if (params == null || params.size() == 0) {
            return;
        }
        HttpParams httpParams = new BasicHttpParams();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            httpParams.setParameter(entry.getKey(), entry.getValue());
        }
        httpRequestBase.setParams(httpParams);
    }

    /**
     * 组装拼接post请求参数
     *
     * @param postMethod post方法
     * @param postParams post参数，可以为null或长度为0
     */
    public static void assemblyPostParams(PostMethod postMethod, Map<String, String> postParams) {
        if (postParams == null || postParams.size() == 0) {
            return;
        }
        for (Map.Entry<String, String> entry : postParams.entrySet()) {
            postMethod.addParameter(entry.getKey(), entry.getValue());
        }
        postMethod.getParams().setContentCharset(Constants.DEFAULT_CHARSET);
    }

    /**
     * 组装拼接请求头部
     *
     * @param httpMethod GET或POST等Http方法
     * @param headerMap  Http请求头部参数，可以为null或长度为0
     */
    public static void assemblyHeaders(HttpMethod httpMethod, Map<String, String> headerMap) {
        if (headerMap == null || headerMap.size() == 0) {
            return;
        }
        for (Map.Entry<String, String> entry : headerMap.entrySet()) {
            httpMethod.addRequestHeader(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 组装拼接请求头部
     *
     * @param httpRequestBase GET或POST等Http方法
     * @param headerMap  Http请求头部参数，可以为null或长度为0
     */
    public static void assemblyAsyncHeaders(HttpRequestBase httpRequestBase, Map<String, String> headerMap) {
        if (headerMap == null || headerMap.size() == 0) {
            return;
        }
        for (Map.Entry<String, String> entry : headerMap.entrySet()) {
            httpRequestBase.addHeader(entry.getKey(), entry.getValue());
        }
    }

}
