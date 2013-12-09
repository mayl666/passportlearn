package com.sogou.upd.passport.manager.api.connect.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.model.httpclient.RequestModelJSON;
import com.sogou.upd.passport.common.parameter.HttpTransformat;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.ProxyErrorUtil;
import com.sogou.upd.passport.common.utils.SGHttpClient;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.manager.api.connect.ConnectManagerHelper;
import com.sogou.upd.passport.manager.api.connect.form.BaseOpenApiParams;
import com.sogou.upd.passport.manager.form.connect.ConnectLoginParams;
import com.sogou.upd.passport.model.OAuthConsumer;
import com.sogou.upd.passport.model.OAuthConsumerFactory;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.common.types.ConnectTypeEnum;
import com.sogou.upd.passport.oauth2.common.types.ResponseTypeEnum;
import com.sogou.upd.passport.oauth2.openresource.request.OAuthAuthzClientRequest;
import com.sogou.upd.passport.oauth2.openresource.vo.OAuthTokenVO;
import com.sogou.upd.passport.service.app.ConnectConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-6-18
 * Time: 上午12:22
 * To change this template use File | Settings | File Templates.
 */
@Component("sgConnectApiManager")
public class SGConnectApiManagerImpl implements ConnectApiManager {

    private static Logger logger = LoggerFactory.getLogger(SGConnectApiManagerImpl.class);

    @Autowired
    private ConnectConfigService connectConfigService;

    @Override
    public String buildConnectLoginURL(ConnectLoginParams connectLoginParams, String uuid, int provider, String ip) throws OAuthProblemException {
        OAuthConsumer oAuthConsumer;
        OAuthAuthzClientRequest request;
        ConnectConfig connectConfig;
        try {
            int clientId = Integer.parseInt(connectLoginParams.getClient_id());
            oAuthConsumer = OAuthConsumerFactory.getOAuthConsumer(provider);
            // 获取connect配置
            connectConfig = connectConfigService.queryConnectConfig(clientId, provider);
            if (connectConfig == null) {
                return CommonConstant.DEFAULT_CONNECT_REDIRECT_URL;
            }

            String redirectURI = ConnectManagerHelper.constructRedirectURI(clientId, connectLoginParams.getRu(), connectLoginParams.getType(),
                    connectLoginParams.getTs(), oAuthConsumer.getCallbackUrl(), ip, connectLoginParams.getFrom());
            String scope = connectConfig.getScope();
            String appKey = connectConfig.getAppKey();
            String connectType = connectLoginParams.getType();
            // 重新填充display，如果display为空，根据终端自动赋值；如果display不为空，则使用display
            String display = connectLoginParams.getDisplay();
            display = Strings.isNullOrEmpty(display) ? fillDisplay(connectType, connectLoginParams.getFrom(), provider) : display;

            String requestUrl;
            //判断display  xhtml\wml调用wap接口
            // 采用Authorization Code Flow流程
            requestUrl = oAuthConsumer.getWebUserAuthzUrl();
            request = OAuthAuthzClientRequest
                    .authorizationLocation(requestUrl).setAppKey(appKey)
                    .setRedirectURI(redirectURI)
                    .setResponseType(ResponseTypeEnum.CODE).setScope(scope)
                    .setDisplay(display, provider).setForceLogin(connectLoginParams.isForcelogin(), provider)
                    .setState(uuid)
                    .buildQueryMessage(OAuthAuthzClientRequest.class);
        } catch (IOException e) {
            logger.error("read oauth consumer IOException!", e);
            throw new OAuthProblemException(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "read oauth consumer IOException!");
        } catch (ServiceException se) {
            logger.error("query connect config Exception!", se);
            throw new OAuthProblemException(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "query connect config Exception!");
        }

        return request.getLocationUri();
    }

    @Override
    public Result buildConnectAccount(String providerStr, OAuthTokenVO oAuthTokenVO) {
        //To change body of implemented methods use File | Settings | File Templates.
        return null;
    }

    /**
     * 调用sohu接口获取用户的openid和accessToken等信息，只针对clientid=1120的第三方用户
     *
     * @param baseOpenApiParams
     * @return
     */
    @Override
    public Result getQQConnectUserInfo(BaseOpenApiParams baseOpenApiParams, int clientId, String clientKey) {
        Result result = new APIResultSupport(false);
        try {
            //如果是post请求，原方法
            RequestModelJSON requestModelJSON = new RequestModelJSON(SHPPUrlConstant.GET_CONNECT_QQ_LIGHT_USER_INFO);
            requestModelJSON.addParams(baseOpenApiParams);
            requestModelJSON.deleteParams(CommonConstant.CLIENT_ID);
            this.setDefaultParams(requestModelJSON, baseOpenApiParams.getUserid(), String.valueOf(clientId), clientKey);
            Map map = SGHttpClient.executeBean(requestModelJSON, HttpTransformat.json, Map.class);
            if (map.containsKey(SHPPUrlConstant.RESULT_STATUS)) {
                String status = map.get(SHPPUrlConstant.RESULT_STATUS).toString().trim();
                if ("0".equals(status)) {
                    result.setSuccess(true);
                }
                Map.Entry<String, String> entry = ProxyErrorUtil.shppErrToSgpp(requestModelJSON.getUrl(), status);
                result.setCode(entry.getKey());
                result.setMessage(entry.getValue());
                result.setModels(map);
            }
        } catch (Exception e) {
            logger.error("getQQConnectUserInfo Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }


    public RequestModelJSON setDefaultParams(RequestModelJSON requestModelJSON, String userId, String clientId, String clientKey) {
        long ct = System.currentTimeMillis();
        String code = ManagerHelper.generatorCodeGBK(userId, Integer.parseInt(clientId), clientKey, ct);
        requestModelJSON.addParam(SHPPUrlConstant.APPID_STRING, clientId);
        requestModelJSON.addParam(CommonConstant.RESQUEST_CODE, code);
        requestModelJSON.addParam(CommonConstant.RESQUEST_CT, String.valueOf(ct));
        return requestModelJSON;
    }

    /*
     * 根据type和provider重新填充display
     */
    private String fillDisplay(String type, String from, int provider) {
        String display = "";
        if (ConnectTypeEnum.isMobileApp(type) || isMobileDisplay(type, from)) {
            switch (provider) {
                case 5:  // 人人
                    display = "touch";
                    break;
                case 6:  // 淘宝
                    display = "wap";
                    break;
                default:
                    display = "mobile";
                    break;
            }
        }
        return display;
    }

    private boolean isMobileDisplay(String type, String from) {
        return type.equals(ConnectTypeEnum.TOKEN.toString()) && "mob".equalsIgnoreCase(from);
    }

}
