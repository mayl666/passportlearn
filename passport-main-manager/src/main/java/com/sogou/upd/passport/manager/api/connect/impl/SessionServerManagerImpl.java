package com.sogou.upd.passport.manager.api.connect.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.parameter.HttpMethodEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.HttpClientUtil;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.common.utils.SGHttpClient;
import com.sogou.upd.passport.common.utils.SessionServerUtil;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.api.SessionServerUrlConstant;
import com.sogou.upd.passport.manager.api.connect.SessionServerManager;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.app.AppConfigService;
import org.apache.http.entity.mime.content.StringBody;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * User: mayan
 * Date: 13-12-4
 * Time: 下午8:44
 */
@Component("sessionServerManager")
public class SessionServerManagerImpl implements SessionServerManager {
    private static ObjectMapper jsonMapper = JacksonJsonMapperUtil.getMapper();

    private static final Logger logger = LoggerFactory.getLogger(SessionServerManagerImpl.class);
    @Autowired
    private AppConfigService appConfigService;

    private Map<String, String> buildHttpSessionParam(String sgid){

        AppConfig appConfig = appConfigService.queryAppConfigByClientId(CommonConstant.SGPP_DEFAULT_CLIENTID);

        int clientId = appConfig.getClientId();
        String serverSecret = appConfig.getServerSecret();
        long ct = System.currentTimeMillis();

        String code = ManagerHelper.generatorCode(sgid, clientId, serverSecret, ct);

        Map<String, String> params = Maps.newHashMap();

        params.put("client_id", String.valueOf(clientId));
        params.put("code", code);
        params.put("ct", String.valueOf(ct));
        params.put("sgid", sgid);
        return params;
    }

    @Override
    public Result createSession( String passportId) {
        Result result = new APIResultSupport(false);

        String sgid = null;
        try {
            //创建sgid
            sgid = SessionServerUtil.createSessionSid(passportId);

            Map<String,String> params=buildHttpSessionParam(sgid);

            Map<String, String> map = Maps.newHashMap();
            map.put("passport_id", passportId);
            params.put("user_info", jsonMapper.writeValueAsString(map));

//            String resultRequest = HttpClientUtil.postRequest(SessionServerUrlConstant.CREATE_SESSION, params);
            RequestModel requestModel=new RequestModel(SessionServerUrlConstant.CREATE_SESSION);

            Set<Map.Entry<String, String>> set = params.entrySet();
            for (Iterator<Map.Entry<String, String>> it = set.iterator(); it.hasNext();) {
                Map.Entry<String, String> entry = it.next();
                requestModel.addParam(entry.getKey(),entry.getValue());
            }

            requestModel.setHttpMethodEnum(HttpMethodEnum.POST);

            String resultRequest=SGHttpClient.executeStr(requestModel);
            if (!Strings.isNullOrEmpty(resultRequest)) {
                SessionResult sessionResult = jsonMapper.readValue(resultRequest, SessionResult.class);
                if (result != null) {
                    if ("0".equals(sessionResult.getStatus())) {
                        result.setSuccess(true);
                        result.getModels().put("sgid", sgid);
                        return result;
                    }
                }
            }
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("createSessionSid " + "passportId:" + passportId + ",sid:" + sgid);
            }
        }
        return result;
    }

    @Override
    public Result removeSession(String sgid) {
        Result result = new APIResultSupport(false);
        try {
            Map<String,String> params=buildHttpSessionParam(sgid);

            RequestModel requestModel=new RequestModel(SessionServerUrlConstant.REMOVE_SESSION);

            Set<Map.Entry<String, String>> set = params.entrySet();
            for (Iterator<Map.Entry<String, String>> it = set.iterator(); it.hasNext();) {
                Map.Entry<String, String> entry = it.next();
                requestModel.addParam(entry.getKey(),entry.getValue());
            }
            requestModel.setHttpMethodEnum(HttpMethodEnum.POST);

            String resultRequest = SGHttpClient.executeStr(requestModel);
            if (!Strings.isNullOrEmpty(resultRequest)) {
                SessionResult sessionResult = jsonMapper.readValue(resultRequest, SessionResult.class);
                if (result != null) {
                    if ("0".equals(sessionResult.getStatus())) {
                        result.setSuccess(true);
                        return result;
                    }
                }
            }
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("removeSession " + "sgid:" + sgid );
            }
        }
        return result;
    }

    @Override
    public Result getPassportIdBySgid(String sgid,String ip) {
        Result result = new APIResultSupport(false);
        String passportId=null;
        try {
            Map<String,String> params=buildHttpSessionParam(sgid);

            RequestModel requestModel=new RequestModel(SessionServerUrlConstant.VERIFY_SID);

            Set<Map.Entry<String, String>> set = params.entrySet();
            for (Iterator<Map.Entry<String, String>> it = set.iterator(); it.hasNext();) {
                Map.Entry<String, String> entry = it.next();
                requestModel.addParam(entry.getKey(),entry.getValue());
            }
            requestModel.addParam("user_ip",ip);
            requestModel.setHttpMethodEnum(HttpMethodEnum.POST);

            String resultRequest = SGHttpClient.executeStr(requestModel);
            if (!Strings.isNullOrEmpty(resultRequest)) {
                Map mapResult=jsonMapper.readValue(resultRequest, Map.class);
                String status= (String) mapResult.get("status");
                if("0".equals(status)){
                    Map<String,String> mapping= (Map<String, String>) mapResult.get("data");
                    passportId=mapping.get("passport_id");
                    result.setSuccess(true);
                    result.getModels().put("passport_id",passportId);
                }
                return result;
            }
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("getPassportIdBySgid error! " + "sgid:" + sgid );
            }
        }
        return result;
    }
}

class SessionResult {
    private String status;
    private String statusText;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }
}