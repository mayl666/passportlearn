package com.sogou.upd.passport.common.utils;

import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.parameter.HttpMethodEnum;
import com.sogou.upd.passport.common.parameter.HttpTransformat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-1-7
 * Time: 下午7:46
 * To change this template use File | Settings | File Templates.
 */
public class QQHttpClient {

    private static final Logger logger = LoggerFactory.getLogger(QQHttpClient.class);

    public String api(String apiUrl, String serverName, HashMap<String, Object> params, String protocol) {
        String resp = null;
        try {
            StringBuilder sb = new StringBuilder(64);
            sb.append(protocol).append("://").append(serverName).append(apiUrl);
            String url = sb.toString();
            RequestModel requestModel = new RequestModel(url);
            requestModel.setHttpMethodEnum(HttpMethodEnum.POST);
            requestModel.setParams(params);
            Map map = SGHttpClient.executeBean(requestModel, HttpTransformat.json, Map.class);
            try {
                //打印出完整请求串
                printRequest(url, HttpMethodEnum.POST, params);
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            resp = JacksonJsonMapperUtil.getMapper().writeValueAsString(map);
            //打印出完整的返回串
            printRespond(resp);
        } catch (IOException e) {
            logger.error("api:Transfer Map To String Failed :", e);
        }
        return resp;
    }

    /**
     * 辅助函数，打印出完整的请求串内容
     *
     * @param url    请求cgi的url
     * @param method 请求的方式 get/post
     * @param params 参数列表
     */
    private void printRequest(String url, HttpMethodEnum method, HashMap<String, Object> params) throws Exception {
        System.out.println("==========Request Info==========\n");
        System.out.println("method:  " + method.toString());
        System.out.println("url:  " + url);
        System.out.println("params:");
        System.out.println(params);
        System.out.println("querystring:");
        StringBuilder buffer = new StringBuilder(128);
        Iterator iter = params.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            try {
                buffer.append(URLEncoder.encode((String) entry.getKey(), "UTF-8").replace("+", "%20").replace("*", "%2A")).append("=").append(URLEncoder.encode((String) entry.getValue(), "UTF-8").replace("+", "%20").replace("*", "%2A")).append("&");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String tmp = buffer.toString();
        tmp = tmp.substring(0, tmp.length() - 1);
        System.out.println(tmp);
        System.out.println();
    }

    /**
     * 辅助函数，打印出完整的执行的返回信息
     *
     * @return 返回服务器响应内容
     */
    private void printRespond(String resp) {
        System.out.println("===========Respond Info============");
        System.out.println(resp);
    }

}
