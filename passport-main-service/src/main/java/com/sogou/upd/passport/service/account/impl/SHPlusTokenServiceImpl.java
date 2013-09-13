package com.sogou.upd.passport.service.account.impl;

import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.model.httpclient.RequestModelJSON;
import com.sogou.upd.passport.common.parameter.HttpMethodEnum;
import com.sogou.upd.passport.common.utils.SGHttpClient;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.service.SHPlusConstant;
import com.sogou.upd.passport.service.account.SHPlusTokenService;
import org.springframework.stereotype.Service;

import java.lang.String;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-9-10
 * Time: 上午2:49
 * To change this template use File | Settings | File Templates.
 */
@Service
public class SHPlusTokenServiceImpl implements SHPlusTokenService {

    @Override
    public boolean verifyShPlusAccessToken(String passportId, int clientId, String instanceId, String accessToken) throws ServiceException {

        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean verifyShPlusRefreshToken(String passportId, int clientId, String instanceId, String refreshToken) throws ServiceException {
        // TODO 这里是passportId还是username？
        RequestModelJSON requestModel = new RequestModelJSON(SHPlusConstant.OAUTH2_TOKEN);
        requestModel.addParam(OAuth.OAUTH_GRANT_TYPE, "heartbeat");
        requestModel.addParam(OAuth.OAUTH_REFRESH_TOKEN, refreshToken);
        requestModel.addParam(OAuth.OAUTH_CLIENT_ID, SHPlusConstant.BROWSER_SHPLUS_CLIENTID);
        requestModel.addParam(OAuth.OAUTH_CLIENT_SECRET, SHPlusConstant.BROWSER_SHPLUS_CLIENTSECRET);
        requestModel.addParam(OAuth.OAUTH_SCOPE, "all");
        requestModel.addParam(OAuth.OAUTH_USERNAME, passportId);
        requestModel.addParam(OAuth.OAUTH_INSTANCE_ID, instanceId);
        String json = SGHttpClient.executeStr(requestModel);
        return false;
    }

    @Override
    public String getResourceByToken(String instance_id,String access_token,String scope,String resource_type) throws ServiceException {
        RequestModel requestModel = new RequestModel(SHPlusConstant.OAUTH2_RESOURCE);
        requestModel.addParam(OAuth.OAUTH_CLIENT_ID, SHPlusConstant.BROWSER_SHPLUS_CLIENTID);
        requestModel.addParam(OAuth.OAUTH_INSTANCE_ID, instance_id);
        requestModel.addParam(OAuth.OAUTH_SCOPE, scope);
        requestModel.addParam(OAuth.OAUTH_ACCESS_TOKEN, access_token);
        requestModel.addParam(OAuth.OAUTH_RESOURCE_TYPE, resource_type);
        requestModel.setHttpMethodEnum(HttpMethodEnum.GET);
        String json = SGHttpClient.executeStr(requestModel);
        return json;
    }
}
