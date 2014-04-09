package com.sogou.upd.passport.oauth2.common.utils.qqutils;

import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.parameter.HttpMethodEnum;
import com.sogou.upd.passport.common.parameter.HttpTransformat;
import com.sogou.upd.passport.common.utils.ConnectHttpClient;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 提供访问腾讯开放平台 OpenApiV3 的接口
 *
 * @author open.qq.com
 * @version 3.0.2
 * @copyright © 2012, Tencent Corporation. All rights reserved.
 * @History: 3.0.3 | coolinchen| 2012-11-07 11:20:12 | support POST request in  "multipart/form-data" format
 * 3.0.2 | coolinchen| 2012-10-08 11:20:12 | support printing request string and result
 * 3.0.1 | nemozhang | 2012-08-28 16:40:20 | support cpay callback sig verifictaion
 * 3.0.0 | nemozhang | 2012-03-21 12:01:05 | initialization
 * @since jdk1.5
 */
public class OpenApiV3 {

    private static final Logger logger = LoggerFactory.getLogger(OpenApiV3.class);

    private String appid;
    private String appkey;
    private String serverName;

    public String getAppid() {
        return appid;
    }

    /**
     * 构造函数
     *
     * @param appid  应用的ID
     * @param appkey 应用的密钥
     */
    public OpenApiV3(String appid, String appkey) {
        this.appid = appid;
        this.appkey = appkey;

    }

    /**
     * 设置OpenApi服务器的地址
     *
     * @param serverName OpenApi服务器的地址
     */
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    /**
     * 执行API调用
     *
     * @param scriptName OpenApi CGI名字 ,如/v3/user/get_info
     * @param params     OpenApi的参数列表
     * @param protocol   HTTP请求协议 "http" / "https"
     * @return 返回服务器响应内容
     */
    public String api(String scriptName, HashMap<String, String> params, String protocol, String method) throws Exception {
        String resp;
        try {
            // 无需传sig,会自动生成
            params.remove("sig");
            // 添加固定参数
            params.put("appid", this.appid);

//        System.out.println("sig:-------------" + sig);
            StringBuilder sb = new StringBuilder(64);
            sb.append(protocol).append("://").append(this.serverName).append(scriptName);
            String url = sb.toString();
            //SGHttpClient,POST请求
            RequestModel requestModel = new RequestModel(url);
            requestModel.setHttpMethodEnum(HttpMethodEnum.POST);
            Map<String, Object> paramsMap = convertToMap(params);
            requestModel.setParams(paramsMap);
            Map map = ConnectHttpClient.executeBean(requestModel, HttpTransformat.json, Map.class);
            resp = JacksonJsonMapperUtil.getMapper().writeValueAsString(map);
        } catch (IOException ioe) {
            logger.error("api:Transfer Map To Json Is Failed :", ioe);
            throw new IOException("Transfer Map To Json Is Failed:", ioe);
        } catch (Exception e) {
            logger.error("api:Execute Api Is Failed :", e);
            throw new Exception("Execute Api Is Failed:", e);
        }
        return resp;
    }

    /**
     * 执行API调用
     *
     * @param scriptName OpenApi CGI名字 ,如/v3/user/get_info
     * @param params     OpenApi的参数列表
     * @param protocol   HTTP请求协议 "http" / "https"
     * @return 返回服务器响应内容
     */
    public String apiHttp(String scriptName, HashMap<String, String> params, String protocol, String method) throws Exception {
        String resp;
        try {
            // 无需传sig,会自动生成
            params.remove("sig");
            // 添加固定参数
            params.put("appid", this.appid);
            // 签名密钥
            String secret = this.appkey + "&";
            // 计算签名
            String sig = SnsSigCheck.makeSig(method, scriptName, params, secret);
            params.put("sig", sig);
//        System.out.println("sig:-------------" + sig);
            StringBuilder sb = new StringBuilder(64);
            sb.append(protocol).append("://").append(this.serverName).append(scriptName);
            String url = sb.toString();
            //SGHttpClient,POST请求
            RequestModel requestModel = new RequestModel(url);
            requestModel.setHttpMethodEnum(HttpMethodEnum.POST);
            Map<String, Object> paramsMap = convertToMap(params);
            requestModel.setParams(paramsMap);
            Map map = ConnectHttpClient.executeBean(requestModel, HttpTransformat.json, Map.class);
            resp = JacksonJsonMapperUtil.getMapper().writeValueAsString(map);
        } catch (IOException ioe) {
            logger.error("api:Transfer Map To Json Is Failed :", ioe);
            throw new IOException("Transfer Map To Json Is Failed:", ioe);
        } catch (Exception e) {
            logger.error("api:Execute Api Is Failed :", e);
            throw new Exception("Execute Api Is Failed:", e);
        }
        return resp;
    }

    private Map<String, Object> convertToMap(HashMap<String, String> paramsMap) {
        Map<String, Object> maps = new HashMap<>();
        if (!CollectionUtils.isEmpty(paramsMap)) {
            for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
                maps.put(entry.getKey(), entry.getValue());
            }
        }
        return maps;
    }

    /**
     * 验证openid是否合法
     */
    private boolean isOpenid(String openid) {
        return (openid.length() == 32) && openid.matches("^[0-9A-Fa-f]+$");
    }
}
