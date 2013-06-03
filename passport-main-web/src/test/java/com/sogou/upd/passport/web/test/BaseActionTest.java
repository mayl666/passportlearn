package com.sogou.upd.passport.web.test;

import com.sogou.upd.passport.common.result.Result;

import net.sf.json.JSONException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-24 Time: 下午2:15 To change this template use
 * File | Settings | File Templates.
 */
public class BaseActionTest {

    protected Result sendPost(String sendUrl, Map<String, String> params)
            throws IOException {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost method = new HttpPost(sendUrl);
            List<NameValuePair> pairs = new ArrayList<>();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            HttpEntity entity = new UrlEncodedFormEntity(pairs, "UTF-8");
            method.setEntity(entity);

            HttpResponse response = client.execute(method);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                System.out.println("请求错误，错误码：" + statusCode + " - " +
                                   response.getStatusLine().getReasonPhrase());
                return null;
            }

            InputStream is = response.getEntity().getContent();
            BufferedReader bf = new BufferedReader(new InputStreamReader(is));

            String resultStr = "";
            String res;
            while ((res = bf.readLine())!= null) {
                resultStr += res + "\n";
            }

            Result result = new ObjectMapper().readValue(resultStr, Result.class);

            return result;
        } catch (HttpHostConnectException e) {
            System.out.println("HOST连接错误，请检查是否启动服务器！！");
            return null;
        } catch (JsonProcessingException e) {
            System.out.println("返回结果不是Result类型！！");
            return null;
        }
    }

    protected Result sendGet(String sendUrl, Map<String, String> params)
            throws IOException {
        try {
            HttpClient client = new DefaultHttpClient();
            String sendUrlWithParams = new String(sendUrl);
            sendUrlWithParams += "?";
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String sch = entry.getKey() + "=" + entry.getValue();
                sendUrlWithParams += sch;
            }
            HttpGet method = new HttpGet(sendUrl);

            HttpResponse response = client.execute(method);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                System.out.println("请求错误，错误码：" + statusCode + " - " +
                                   response.getStatusLine().getReasonPhrase());
                return null;
            }

            InputStream is = response.getEntity().getContent();
            BufferedReader bf = new BufferedReader(new InputStreamReader(is));

            String resultStr = "";
            String res;
            while ((res = bf.readLine())!= null) {
                resultStr += res + "\n";
            }

            Result result = new ObjectMapper().readValue(resultStr, Result.class);

            return result;
        } catch (HttpHostConnectException e) {
            System.out.println("HOST连接错误，请检查是否启动服务器！！");
            return null;
        } catch (JsonProcessingException e) {
            System.out.println("返回结果不是Result类型！！");
            return null;
        }
    }

    protected Result sendPostLocal(String sendUrl, Map<String, String> params)
            throws IOException {
        String sendUrlFull = "http://localhost/";
        sendUrlFull += sendUrl;
        return sendPost(sendUrlFull, params);
    }

    protected Result sendGetLocal(String sendUrl, Map<String, String> params)
            throws IOException {
        String sendUrlFull = "http://localhost/";
        sendUrlFull += sendUrl;
        return sendGet(sendUrlFull, params);
    }

}
