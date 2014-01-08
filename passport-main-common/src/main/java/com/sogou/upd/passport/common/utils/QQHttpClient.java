package com.sogou.upd.passport.common.utils;

import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.parameter.HttpMethodEnum;
import com.sogou.upd.passport.common.parameter.HttpTransformat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.HashMap;
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
            resp = JacksonJsonMapperUtil.getMapper().writeValueAsString(map);
        } catch (IOException e) {
            logger.error("Transfer Map To String Failed :", e);
            e.printStackTrace();
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
