package com.sogou.upd.passport.manager.api.connect.impl.qq;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.HttpConstant;
import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.parameter.HttpMethodEnum;
import com.sogou.upd.passport.common.parameter.HttpTransformat;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.common.utils.SGHttpClient;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.connect.QQProxyOpenApiManager;
import com.sogou.upd.passport.manager.api.connect.form.proxy.ConnectProxyOpenApiParams;
import com.sogou.upd.passport.manager.api.connect.form.proxy.OpenApiParams;
import com.sogou.upd.passport.oauth2.common.utils.qqutils.ErrorCode;
import com.sogou.upd.passport.oauth2.common.utils.qqutils.OpensnsException;
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
 * Time: 下午7:50
 * To change this template use File | Settings | File Templates.
 */
@Component("qqProxyOpenApiManager")
public class QQProxyOpenApiManagerImpl extends BaseProxyManager implements QQProxyOpenApiManager {

    private static final Logger logger = LoggerFactory.getLogger(QQProxyOpenApiManagerImpl.class);

    @Override
    public Result executeQQOpenApi(OpenApiParams openApiParams, ConnectProxyOpenApiParams params) {
        Result result = new APIResultSupport(false);
        try {
            String userIdStr = params.getUserid();
            if (AccountTypeEnum.getAccountType(userIdStr) != AccountTypeEnum.QQ) {
                result.setCode(ErrorUtil.ERR_CODE_CONNECT_NOT_SUPPORTED);
                return result;
            }
            //TODO 从数据库中根据provider读出serverName,http请求参数等
            // 指定HTTP请求协议类型,目前代理接口走的都是HTTP请求，所以需要sig签名，如果为HTTPS请求，则不需要sig签名
            String protocol = CommonConstant.HTTPS;
            openApiParams.setServerName(CommonConstant.QQ_SERVER_NAME_GRAPH);
            //封装第三方开放平台需要的参数
            Map<String, Object> sigMap = new HashMap<String, Object>();
            //Todo 搜狗passport负责封装的三个必填参数,这三个参数的命名需要从数据库中读出
            sigMap.put("openid", openApiParams.getOpenId());
            sigMap.put("openkey", openApiParams.getAccessToken());
            sigMap.put("oauth_consumer_key", openApiParams.getAppKey());
            ObjectMapper objectMapper = JacksonJsonMapperUtil.getMapper();
            HashMap<String, String> maps = null;
            try {
                maps = objectMapper.readValue(params.getParams().toString(), HashMap.class);
            } catch (IOException e) {
            }
            //应用传入的参数添加至map中
            Set<String> commonKeySet = maps.keySet();
            for (String dataKey : commonKeySet) {
                sigMap.put(dataKey, maps.get(dataKey));
            }
            //TODO 根据应用的请求方式
            String method = HttpConstant.HttpMethod.POST;
            String resp;
            try {
                resp = qqApi(openApiParams, sigMap, protocol, method);
                result.setDefaultModel(resp);
            } catch (Exception e) {
                logger.error("get qq userinfo Is Failed:", e);
                result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            }
            return result;
        } catch (Exception e) {
            logger.error("get qq userinfo Is Failed,openId is " + openApiParams.getOpenId(), e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }

    /**
     * 执行API调用
     *
     * @param openApiParams 请求第三方开放平台接口的
     * @param params        OpenApi的参数列表
     * @param protocol      HTTP请求协议 "http" / "https"
     * @return 返回服务器响应内容
     */
    public String qqApi(OpenApiParams openApiParams, Map<String, Object> params, String protocol, String method) throws OpensnsException {
        String resp = null;
        try {
            // 检查openid openkey等参数
            if (params.get("openid").toString() == null) {
                throw new OpensnsException(ErrorCode.PARAMETER_EMPTY, "openid is empty");
            }
            if (!isOpenid(params.get("openid").toString())) {
                throw new OpensnsException(ErrorCode.PARAMETER_INVALID, "openid is invalid");
            }
            StringBuilder sb = new StringBuilder(64);
            sb.append(protocol).append("://").append(openApiParams.getServerName()).append(openApiParams.getInterfaceName());
            String url = sb.toString();
            RequestModel requestModel = new RequestModel(url);
            requestModel.setHttpMethodEnum(HttpMethodEnum.POST);
//        Map<String, Object> paramsMap = convertToMap(params);
            requestModel.setParams(params);
            Map map = SGHttpClient.executeBean(requestModel, HttpTransformat.json, Map.class);
            resp = JacksonJsonMapperUtil.getMapper().writeValueAsString(map);
        } catch (IOException e) {
            logger.error("Execute Failed :", e);
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
