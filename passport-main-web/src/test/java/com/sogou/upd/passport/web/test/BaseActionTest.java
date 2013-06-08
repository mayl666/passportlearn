package com.sogou.upd.passport.web.test;

import com.google.common.base.Strings;

import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;

import junit.framework.TestCase;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-24 Time: 下午2:15 To change this template use
 * File | Settings | File Templates.
 */
//@ContextConfiguration(locations = "classpath:spring-config-test.xml")
public class BaseActionTest extends TestCase {

    protected String sendPostXml(String sendUrl, String xmlStr) throws IOException {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost method = new HttpPost(sendUrl);

            HttpEntity entity = new StringEntity(xmlStr, "UTF-8");
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

            return resultStr;
        } catch (HttpHostConnectException e) {
            System.out.println("HOST连接错误，请检查是否启动服务器！！");
            return null;
        }
    }

    protected String sendPost(String sendUrl, Map<String, String> params)
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

            return resultStr;
        } catch (HttpHostConnectException e) {
            System.out.println("HOST连接错误，请检查是否启动服务器！！");
            return null;
        }
    }

    protected String sendGet(String sendUrl, Map<String, String> params)
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

            return resultStr;
        } catch (HttpHostConnectException e) {
            System.out.println("HOST连接错误，请检查是否启动服务器！！");
            return null;
        }
    }

    protected String sendPostLocal(String sendUrl, Map<String, String> params)
            throws IOException {
        Result result;
        try {
            String sendUrlFull = "http://localhost/";
            sendUrlFull += sendUrl;
            String resultStr = sendPost(sendUrlFull, params);

            return resultStr;
        } catch (JsonProcessingException e) {
            System.out.println("返回结果不是Result类型！！");
            return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    protected String sendGetLocal(String sendUrl, Map<String, String> params)
            throws IOException {
        Result result;
        try {
            String sendUrlFull = "http://localhost/";
            sendUrlFull += sendUrl;
            String resultStr = sendGet(sendUrlFull, params);

            return resultStr;
        } catch (JsonProcessingException e) {
            System.out.println("返回结果不是Result类型！！");
            return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private static final String appId="1100";

    private static final String key="yRWHIkB$2.9Esk>7mBNIFEcr:8\\[Cv";

    public void testPostXml() throws Exception {

        long ct=System.currentTimeMillis();
        String code= "shipengzhi1986@sogou.com" +appId+ key+ ct;
        code= Coder.encryptMD5(code);



        String url = "http://internal.passport.sohu.com/interface/getuserinfo";
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        sb.append("<register>\n"
                  + "    <userid>shipengzhi1986@sogou.com</userid>\n"
                  + "    <appid>1100</appid>\n"
                  + "    <ct>"+ct+"</ct>\n"
                  + "    <code>"+code+"</code>\n"
                  + "    <password></password>\n"
                  + "    <passwordtype></passwordtype>\n"
                  + "    <question></question>\n"
                  + "    <answer></answer>\n"
                  + "    <email></email>\n"
                  + "    <mobile></mobile>\n"
                  + "    <createip></createip>\n"
                  + "    <uniqname></uniqname>\n"
                  /*+ "    <avatarurl></avatarurl>\n"*/
                  + "    <regappid></regappid>\n"
                  + "</register>");
        String result = sendPostXml(url, sb.toString());
        System.out.println(result);
    }
}