package com.sogou.upd.passport.manager.api.connect.impl;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.connect.ConnectProxyOpenApiManager;
import com.sogou.upd.passport.manager.api.connect.QQProxyOpenApiManager;
import com.sogou.upd.passport.manager.api.connect.form.proxy.ConnectProxyOpenApiParams;
import com.sogou.upd.passport.manager.api.connect.form.proxy.OpenApiParams;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.model.app.ConnectConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    private ConfigureManager configureManager;
    @Autowired
    private QQProxyOpenApiManager qqProxyOpenApiManager;

    @Override
    public Result handleConnectOpenApi(String openId, String accessToken, String providerStr, String interfaceName, ConnectProxyOpenApiParams params) {
        Result result = new APIResultSupport(false);
        try {
            //获取搜狗在第三方开放平台的appkey和appsecret
            int provider = AccountTypeEnum.getProvider(providerStr);
            ConnectConfig connectConfig = configureManager.obtainConnectConfig(CommonConstant.SGPP_DEFAULT_CLIENTID, provider);
            String sgAppKey = connectConfig.getAppKey();
            String sgAppSecret = connectConfig.getAppSecret();
            OpenApiParams openApiParams = new OpenApiParams(openId, accessToken, providerStr, interfaceName, sgAppKey, sgAppSecret, CommonConstant.QQ_SERVER_NAME_GRAPH);
            // 指定HTTP请求协议类型,目前代理接口走的都是HTTP请求，所以需要sig签名，如果为HTTPS请求，则不需要sig签名
            String protocol = CommonConstant.HTTPS;
            //封装第三方开放平台需要的参数
            Map<String, Object> sigMap = new HashMap();
            //Todo 搜狗passport负责封装的三个必填参数,这三个参数的命名需要从数据库中读出
            sigMap.put("openid", openApiParams.getOpenId());
            sigMap.put("openkey", openApiParams.getAccessToken());
            sigMap.put("oauth_consumer_key", openApiParams.getAppKey());
            ObjectMapper objectMapper = JacksonJsonMapperUtil.getMapper();
            HashMap<String, String> maps = null;
            try {
                maps = objectMapper.readValue(params.getParams().toString(), HashMap.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //应用传入的参数添加至map中
            Set<String> commonKeySet = maps.keySet();
            for (String dataKey : commonKeySet) {
                sigMap.put(dataKey, maps.get(dataKey));
            }
            //TODO 根据应用的请求方式
            String method = CommonConstant.CONNECT_METHOD_POST;
            String resp;
            try {
//                resp = qqApi(openApiParams, sigMap, protocol, method);
//                result.setDefaultModel(resp);
            } catch (Exception e) {
                logger.error("executeQQOpenApi Is Failed:", e);
                result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            }
            return result;
        } catch (ServiceException e) {
            logger.error("Service Method querySpecifyConnectConfig error.{}", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        } catch (Exception e) {
            logger.error("handleConnectOpenApi Is Failed:openId is " + openId, e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }

}
