package com.sogou.upd.passport.manager.api.connect.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.HttpClientUtil;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.common.utils.SessionServerUtil;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.api.SessionServerUrlConstant;
import com.sogou.upd.passport.manager.api.connect.SessionServerManager;
import com.sogou.upd.passport.model.app.AppConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * User: mayan
 * Date: 13-12-4
 * Time: 下午8:44
 */
@Component("sessionServerManager")
public class SessionServerManagerImpl implements SessionServerManager {
    private static ObjectMapper jsonMapper = JacksonJsonMapperUtil.getMapper();

    private static final Logger logger = LoggerFactory.getLogger(SessionServerManagerImpl.class);

    @Override
    public Result createSession(AppConfig appConfig, String passportId) {
        Result result = new APIResultSupport(false);

        String sgid=null;
        try {
            //创建sgid
            sgid = SessionServerUtil.createSessionSid(passportId);

            int clientId = appConfig.getClientId();
            String serverSecret = appConfig.getServerSecret();
            long ct = System.currentTimeMillis();

            String code = ManagerHelper.generatorCode(sgid, clientId, serverSecret, ct);

            Map<String, String> params = Maps.newHashMap();

            params.put("client_id", String.valueOf(clientId));
            params.put("code", code);
            params.put("ct", String.valueOf(ct));
            params.put("sgid", sgid);
            params.put("user_info", jsonMapper.writeValueAsString(Maps.newHashMap().put("passport_id", passportId)));
            String resultRequest = HttpClientUtil.postRequest(SessionServerUrlConstant.CREATE_SESSION, params);

            if (!Strings.isNullOrEmpty(resultRequest)) {
                SessionResult sessionResult = jsonMapper.readValue(resultRequest, SessionResult.class);
                if (result != null) {
                    if ("0".equals(sessionResult.getStatus())) {
                        result.setSuccess(true);
                        result.getModels().put("sgid",sgid);
                        return result;
                    }
                }
            }
        } catch (Exception e) {
            if(logger.isDebugEnabled()){
                logger.debug("createSessionSid "+"passportId:"+passportId+",sid:"+sgid);
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