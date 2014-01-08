package com.sogou.upd.passport.manager.api.connect.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.*;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.connect.ConnectProxyOpenApiManager;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.service.app.ConnectConfigService;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-1-4
 * Time: 下午4:40
 * To change this template use File | Settings | File Templates.
 */
@Component("connectProxyOpenApiManager")
public class ConnectProxyOpenApiManagerImpl extends BaseProxyManager implements ConnectProxyOpenApiManager {

    private static final Logger logger = LoggerFactory.getLogger(ConnectProxyOpenApiManagerImpl.class);

    @Autowired
    private ConnectConfigService connectConfigService;

    @Override
    public Result handleConnectOpenApi(String sgUrl, Map<String, String> tokenMap, Map<String, Object> paramsMap) {
        Result result = new APIResultSupport(false);
        try {
            String openId = tokenMap.get("open_id").toString();
            String accessToken = tokenMap.get("access_token").toString();
            //获取搜狗在第三方开放平台的appkey和appsecret
            ConnectConfig connectConfig = connectConfigService.querySpecifyConnectConfig(CommonConstant.SGPP_DEFAULT_CLIENTID, AccountTypeEnum.QQ.getValue());
            String sgAppKey = connectConfig.getAppKey();
            // 指定HTTP请求协议类型,目前代理接口走的都是HTTP请求，所以需要sig签名，如果为HTTPS请求，则不需要sig签名
            String protocol = CommonConstant.HTTPS;
            String serverName = CommonConstant.QQ_SERVER_NAME_GRAPH;
            HashMap<String, Object> sigMap = new HashMap();
            String connectInfo = ConnectUtil.getERR_CODE_MSG(sgUrl);
            String[] str = connectInfo.split("\\|");
            String apiUrl = str[0];    //搜狗封装的url请求对应真正QQ第三方的接口请求
            String platform = str[1];  //QQ第三方接口所在的平台
            //封装第三方开放平台需要的参数
            String regularParams = ConnectUtil.getERR_CODE_MSG("qq");
            String[] regularArray = regularParams.split("\\|");
            sigMap.put(regularArray[0], sgAppKey);
            sigMap.put(regularArray[1], openId);
            sigMap.put(regularArray[2], accessToken);
            if (paramsMap != null) {
                //应用传入的参数添加至map中
                Set<Map.Entry<String, Object>> entrys = paramsMap.entrySet();
                if (!CollectionUtils.isEmpty(entrys) && entrys.size() > 0) {
                    for (Map.Entry<String, Object> entry : entrys) {
                        sigMap.put(entry.getKey(), entry.getValue());
                    }
                }
            }
            String method = CommonConstant.CONNECT_METHOD_POST;
            //如果是http请求，则需要算签名
            if (protocol.equals(CommonConstant.HTTP)) {
                // 签名密钥
                String secret = CommonConstant.APP_CONNECT_SECRET + "&";
                // 计算签名
                String sig = QQSigUtil.makeSig(method, apiUrl, sigMap, secret);
                sigMap.put(regularArray[3], sig);
            }
            QQHttpClient qqHttpClient = new QQHttpClient();
            String resp = qqHttpClient.api(apiUrl, serverName, sigMap, protocol);
            result = buildCommonResult(platform, resp);
        } catch (Exception e) {
            logger.error("executeQQOpenApi Is Failed:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }

    private HashMap<String, String> buildConnectParams(String sgAppKey, String openId, String accessToken) {
        return null;
    }

    private Result buildCommonResult(String platform, String resp) {
        Result result = new APIResultSupport(false);
        try {
            if (!Strings.isNullOrEmpty(resp)) {
                ObjectMapper objectMapper = JacksonJsonMapperUtil.getMapper();
                HashMap<String, String> maps = objectMapper.readValue(resp, HashMap.class);

            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return result;

    }
}
