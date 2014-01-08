package com.sogou.upd.passport.manager.api.connect.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.exception.ConnectException;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.*;
import com.sogou.upd.passport.exception.ServiceException;
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
import java.rmi.server.ObjID;
import java.util.HashMap;
import java.util.List;
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
            String connectInfo = ConnectUtil.getERR_CODE_MSG(sgUrl);
            String[] str = connectInfo.split("\\|");
            String apiUrl = str[0];    //搜狗封装的url请求对应真正QQ第三方的接口请求
            String platform = str[1];  //QQ第三方接口所在的平台
            //封装第三方开放平台需要的参数
            HashMap<String, Object> sigMap = buildConnectParams(tokenMap, paramsMap, apiUrl);
            String protocol = CommonConstant.HTTPS;
            String serverName = CommonConstant.QQ_SERVER_NAME_GRAPH;
            QQHttpClient qqHttpClient = new QQHttpClient();
            String resp = qqHttpClient.api(apiUrl, serverName, sigMap, protocol);
            result = buildCommonResult(platform, resp);
        } catch (Exception e) {
            logger.error("handleConnectOpenApi Is Failed:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }

    private HashMap<String, Object> buildConnectParams(Map<String, String> tokenMap, Map<String, Object> paramsMap, String apiUrl) throws Exception {
        String openId = tokenMap.get("open_id").toString();
        if (!isOpenid(openId)) {
            logger.error(ErrorUtil.ERR_CODE_CONNECT_MAKE_SIGNATURE_ERROR, "openid is not right");
            throw new ConnectException(ErrorUtil.ERR_CODE_CONNECT_MAKE_SIGNATURE_ERROR);
        }
        String accessToken = tokenMap.get("access_token").toString();
        //获取搜狗在第三方开放平台的appkey和appsecret
        ConnectConfig connectConfig = connectConfigService.querySpecifyConnectConfig(CommonConstant.SGPP_DEFAULT_CLIENTID, AccountTypeEnum.QQ.getValue());
        String sgAppKey = connectConfig.getAppKey();
        String protocol = CommonConstant.HTTPS;
        HashMap<String, Object> sigMap = new HashMap();
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
        return sigMap;
    }

    private Result buildCommonResult(String platform, String resp) throws IOException {
        Result result = new APIResultSupport(false);
        try {
            if (!Strings.isNullOrEmpty(resp)) {
                ObjectMapper objectMapper = JacksonJsonMapperUtil.getMapper();
                HashMap<String, Object> maps = objectMapper.readValue(resp, HashMap.class);
                if (!CollectionUtils.isEmpty(maps)) {
                    //封装QQ返回请求错误的结果,请求结果包含ret，且ret不为零，表示调用不成功
                    if (maps.containsKey("ret") && !maps.get("ret").toString().equals("0")) {
                        result.setCode(maps.get("ret").toString());
                        result.setMessage((String) maps.get("msg"));
                    } else {
                        //封装QQ返回请求正确的结果，返回结果中不包含ret或者包含ret且ret值为0的结果封装
                        HashMap<String, String> data = new HashMap<>();
                        //QQ空间未读数结果封装
                        if ("qzone".equals(platform)) {
                            String ret = maps.get("ret").toString();
                            if (ret.equals("0")) {
                                result.setSuccess(true);
                                result.setMessage(ErrorUtil.getERR_CODE_MSG("0"));
                                data = convertToSGMap(maps);
                                data.remove("ret");
                                data.remove("msg");
                                result.setModels(data);
                            }
                        } else if ("weibo".equals(platform)) {
                            String ret = maps.get("ret").toString();
                            if (ret.equals("0")) {
                                if (maps.containsKey("data")) {
                                    HashMap<String, Object> mapsWeibo = (HashMap<String, Object>) maps.get("data");
                                    if (!CollectionUtils.isEmpty(mapsWeibo)) {
                                        result.setSuccess(true);
                                        result.setMessage(ErrorUtil.getERR_CODE_MSG("0"));
                                        data = convertToSGMap(mapsWeibo);
                                        result.setModels(data);
                                    }
                                }
                            }
                        } else {
                            if (maps.containsKey("result")) {
                                HashMap<String, Object> resultMap = (HashMap<String, Object>) maps.get("result");
                                if (!CollectionUtils.isEmpty(resultMap)) {
                                    List<Object> emailList = (List<Object>) resultMap.get("UnreadMailCountData");
                                    if (!CollectionUtils.isEmpty(emailList)) {
                                        HashMap<String, Object> mail = (HashMap<String, Object>) emailList.get(0);
                                        if (!CollectionUtils.isEmpty(mail)) {
                                            result.setSuccess(true);
                                            result.setMessage(ErrorUtil.getERR_CODE_MSG("0"));
                                            data = convertToSGMap(mail);
                                            data.remove("Name");
                                            result.setModels(data);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new IOException("Transfer QQ Result To SGResult Failed:", e);
        }
        return result;
    }

    private HashMap<String, String> convertToSGMap(HashMap<String, Object> map) {
        HashMap<String, String> data = new HashMap<>();
        if (!CollectionUtils.isEmpty(map)) {
            Set<Map.Entry<String, Object>> set = map.entrySet();
            if (!CollectionUtils.isEmpty(set)) {
                for (Map.Entry<String, Object> entry : set) {
                    data.put(entry.getKey(), entry.getValue().toString());
                }
            }
        }
        return data;
    }


    /**
     * 验证openid是否合法
     */
    private boolean isOpenid(String openid) {
        return (openid.length() == 32) && openid.matches("^[0-9A-Fa-f]+$");
    }
}
