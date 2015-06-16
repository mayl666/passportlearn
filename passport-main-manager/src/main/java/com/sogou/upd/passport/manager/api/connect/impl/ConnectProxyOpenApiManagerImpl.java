package com.sogou.upd.passport.manager.api.connect.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.HttpConstant;
import com.sogou.upd.passport.common.exception.ConnectException;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.*;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.connect.ConnectProxyOpenApiManager;
import com.sogou.upd.passport.manager.api.connect.ConnectResultContext;
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
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private DBShardRedisUtils dbShardRedisUtils;

    @Override
    public Result handleConnectOpenApi(String sgUrl, Map<String, String> tokenMap, Map<String, Object> paramsMap, String thirdAppId) {
        Result result = new APIResultSupport(false);
        try {
            String connectInfo = ConnectUtil.getCONNECT_CODE_MSG(sgUrl);
            String[] str = connectInfo.split("\\|");
            String apiUrl = str[0];    //搜狗封装的url请求对应真正QQ第三方的接口请求
            String platform = str[1];  //QQ第三方接口所在的平台
            //封装第三方开放平台需要的参数
            HashMap<String, Object> sigMap = buildConnectParams(tokenMap, paramsMap, apiUrl, thirdAppId);
            String protocol = CommonConstant.HTTPS;
            String serverName = CommonConstant.QQ_SERVER_NAME_GRAPH;
            QQHttpClient qqHttpClient = new QQHttpClient();
            if("/v3/user/get_pinyin".equalsIgnoreCase(apiUrl)){
                String resp = dbShardRedisUtils.get("pinyinData_" + tokenMap.get("open_id").toString());
                if(Strings.isNullOrEmpty(resp)){
                    resp = qqHttpClient.api(apiUrl, serverName, sigMap, protocol);
                        if (!Strings.isNullOrEmpty(resp)) {
                            ObjectMapper objectMapper = JacksonJsonMapperUtil.getMapper();
                            HashMap<String, Object> maps = objectMapper.readValue(resp, HashMap.class);
                            if (!CollectionUtils.isEmpty(maps) && "0".equals(String.valueOf(maps.get("ret"))) && maps.containsKey("result")) {
                                HashMap<String,Object> tmp = (HashMap<String, Object>) maps.get("result");
                                if(!CollectionUtils.isEmpty(tmp) && tmp.containsKey("PinYinData") && Strings.isNullOrEmpty(String.valueOf(tmp.containsKey("PinYinData")))) {
                                    dbShardRedisUtils.setStringWithinSeconds("pinyinData_" + tokenMap.get("open_id").toString(),resp, TimeUnit.HOURS.toSeconds(8));
                                }
                            }
                        }
                }
                result = buildCommonResultByStrategy(platform, resp);
            } else {
                String resp = qqHttpClient.api(apiUrl, serverName, sigMap, protocol);
                result = buildCommonResultByStrategy(platform, resp);
            }
            //对第三方API调用失败记录log
            if (ErrorUtil.ERR_CODE_CONNECT_FAILED.equals(result.getCode()) && apiUrl.contains("get_pinyin")) {
                logger.warn("handleConnectOpenApi error. apiUrl:{},openId:{},sigMap:{},paramsMap:{}", new Object[]{apiUrl, tokenMap.get("open_id").toString(), sigMap.toString(), paramsMap.toString()});
            }
        } catch (ConnectException ce) {
            logger.error("OpenId Format Is Illegal:", ce);
            result.setCode(ErrorUtil.ERR_CODE_CONNECT_MAKE_SIGNATURE_ERROR);
        } catch (Exception e) {
            logger.error("handleConnectOpenApi Is Failed,OpenId is" + tokenMap.get("open_id").toString(), e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }

    private HashMap<String, Object> buildConnectParams(Map<String, String> tokenMap, Map<String, Object> paramsMap, String apiUrl, String thirdAppId) throws ConnectException {
        String openId = tokenMap.get("open_id").toString();
        if (!isOpenid(openId)) {
            throw new ConnectException();
        }
        String accessToken = tokenMap.get("access_token").toString();
        //获取搜狗在第三方开放平台的appkey和appsecret
        ConnectConfig connectConfig = connectConfigService.queryConnectConfigByAppId(thirdAppId, AccountTypeEnum.QQ.getValue());
        if(connectConfig == null){
            throw new ConnectException();
        }
        String sgAppKey = connectConfig.getAppKey();
        String protocol = CommonConstant.HTTPS;
        HashMap<String, Object> sigMap = new HashMap();
        String regularParams = ConnectUtil.getCONNECT_CODE_MSG("qq");
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
        String method = HttpConstant.HttpMethod.POST;
        //如果是http请求，则需要算签名
        if (protocol.equals(CommonConstant.HTTP)) {
            // 签名密钥
            String secret = CommonConstant.APP_CONNECT_SECRET + "&";
            // 计算签名
            String sig = QQSigUtil.makeSig(method, apiUrl, sigMap, secret);
            sigMap.put(regularArray[3], sig);
        }
        return sigMap;
    }

    private Result buildCommonResultByStrategy(String platform, String resp) throws IOException {
        Result result = new APIResultSupport(false);
        try {
            if (!Strings.isNullOrEmpty(resp)) {
                ObjectMapper objectMapper = JacksonJsonMapperUtil.getMapper();
                HashMap<String, Object> maps = objectMapper.readValue(resp, HashMap.class);
                if (!CollectionUtils.isEmpty(maps)) {
                    ConnectResultContext connectResultContext = new ConnectResultContext();
                    result = connectResultContext.getResultByPlatform(platform, maps);
                }
            } else {
                result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
                logger.error("handleConnectOpenApi openapi return resp is null");
            }
        } catch (IOException e) {
            throw new IOException("method[buildCommonResultByStrategy]:Transfer QQ Result To SGResult Failed:", e);
        }
        return result;
    }

    /**
     * 验证openid是否合法
     */
    private boolean isOpenid(String openid) {
        return (openid.length() == 32) && openid.matches("^[0-9A-Fa-f]+$");
    }
}
