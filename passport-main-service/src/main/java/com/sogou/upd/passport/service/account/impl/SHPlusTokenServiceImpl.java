package com.sogou.upd.passport.service.account.impl;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.CommonHelper;
import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.model.httpclient.RequestModelJSON;
import com.sogou.upd.passport.common.parameter.HttpMethodEnum;
import com.sogou.upd.passport.common.parameter.OAuth2ResourceTypeEnum;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.common.utils.SGHttpClient;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.service.SHPlusConstant;
import com.sogou.upd.passport.service.account.SHPlusTokenService;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-9-10
 * Time: 上午2:49
 * To change this template use File | Settings | File Templates.
 */
@Service
public class SHPlusTokenServiceImpl implements SHPlusTokenService {

    private static Logger log = LoggerFactory.getLogger(SHPlusTokenServiceImpl.class);
    private static ObjectMapper jsonMapper = JacksonJsonMapperUtil.getMapper();

    @Override
    public String queryATokenByRToken(String passportId, String instanceId, String refreshToken) throws ServiceException {
        RequestModelJSON requestModel = new RequestModelJSON(SHPlusConstant.OAUTH2_TOKEN);
        requestModel.addParam(OAuth.OAUTH_GRANT_TYPE, "heartbeat");
        requestModel.addParam(OAuth.OAUTH_REFRESH_TOKEN, refreshToken);
        requestModel.addParam(OAuth.OAUTH_CLIENT_ID, SHPlusConstant.BROWSER_SHPLUS_CLIENTID);
        requestModel.addParam(OAuth.OAUTH_CLIENT_SECRET, SHPlusConstant.BROWSER_SHPLUS_CLIENTSECRET);
        requestModel.addParam(OAuth.OAUTH_SCOPE, "all");
        requestModel.addParam(OAuth.OAUTH_INSTANCE_ID, instanceId);
        requestModel.addParam(OAuth.OAUTH_REDIRECT_URI, "www.sohu.com");
        requestModel.addParam(OAuth.OAUTH_USERNAME, passportId);
        requestModel.setHttpMethodEnum(HttpMethodEnum.GET);
        String json = SGHttpClient.executeStr(requestModel);
        Map resultMap = null;
        try {
            resultMap = jsonMapper.readValue(json, Map.class);
        } catch (IOException e) {
            log.error("parse json to map fail,jsonString:" + json);
        }
        if (resultMap != null) {
            String result = (String) resultMap.get("result");
            if ("confirm".equals(result)) {
                long expiresTime = Long.parseLong((String) resultMap.get(OAuth.OAUTH_EXPIRES_TIME));
                long currTime = System.currentTimeMillis() / 1000;
                if (currTime < expiresTime) {
                    return (String) resultMap.get("access_token");
                }
            }
        }
        return null;
    }

    @Override
    public Map getResourceByToken(String instanceId, String accessToken, OAuth2ResourceTypeEnum resourceType) throws ServiceException {
        RequestModel requestModel = new RequestModel(SHPlusConstant.OAUTH2_RESOURCE);
        requestModel.addParam(OAuth.OAUTH_CLIENT_ID, SHPlusConstant.BROWSER_SHPLUS_CLIENTID);
        requestModel.addParam(OAuth.OAUTH_CLIENT_SECRET, SHPlusConstant.BROWSER_SHPLUS_CLIENTSECRET);
        requestModel.addParam(OAuth.OAUTH_INSTANCE_ID, instanceId);
        requestModel.addParam(OAuth.OAUTH_SCOPE, "all");
        requestModel.addParam(OAuth.OAUTH_ACCESS_TOKEN, accessToken);
        requestModel.addParam(OAuth.OAUTH_RESOURCE_TYPE, resourceType.getValue());
        requestModel.setHttpMethodEnum(HttpMethodEnum.GET);
        String json = SGHttpClient.executeStr(requestModel);
        Map resultMap = null;
        try {
            resultMap = jsonMapper.readValue(json, Map.class);
        } catch (IOException e) {
            log.error("parse json to map fail,jsonString:" + json);
        }

        return resultMap;
    }

    @Override
    public boolean copyAvatarToLocal(String passportId, String instanceId, String accessToken) {
        Map userInfoMap = getResourceByToken(instanceId, accessToken, OAuth2ResourceTypeEnum.GET_FULL_USERINFO);
        if (userInfoMap != null) {
            String result = (String) userInfoMap.get("result");
            if ("confirm".equals(result)) {
                // http://s5.suc.itc.cn/ux_sogou_member/src/asset/sogou/img_sogouAvatar175.png
                String avatar = (String) userInfoMap.get("large_avatar");
                if (!CommonHelper.isInvokeProxyApi(passportId)) {
                    // TODO 写到搜狗数据库里
                }

                return true;
            }
        }
        return false;
    }
}
