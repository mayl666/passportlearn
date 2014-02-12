package com.sogou.upd.passport.manager.api.connect.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.model.httpclient.RequestModelJSON;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.parameter.HttpTransformat;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.ProxyErrorUtil;
import com.sogou.upd.passport.common.utils.SGHttpClient;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.manager.api.connect.form.BaseOpenApiParams;
import com.sogou.upd.passport.manager.form.connect.ConnectLoginParams;
import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.vo.OAuthTokenVO;
import com.sogou.upd.passport.service.connect.AccessTokenService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-6-18
 * Time: 上午12:28
 * To change this template use File | Settings | File Templates.
 */
@Component("proxyConnectApiManager")
public class ProxyConnectApiManagerImpl extends BaseProxyManager implements ConnectApiManager {

    private static final Logger log = LoggerFactory.getLogger(ProxyConnectApiManagerImpl.class);

    @Override
    public String buildConnectLoginURL(ConnectLoginParams connectLoginParams, String uuid, int provider, String ip, String httpOrHttps) throws OAuthProblemException {
        return null;
    }

    @Override
    public Result buildConnectAccount(String appKey, String providerStr, OAuthTokenVO oAuthTokenVO) {
        Result result = new APIResultSupport(false);
        String url = SHPPUrlConstant.CREATE_CONNECT_USER + providerStr;
        RequestModel requestModel = new RequestModel(url);
        requestModel.addParam("appid", CommonConstant.SGPP_DEFAULT_CLIENTID);
        requestModel.addParam("provider", providerStr);
        requestModel.addParam("access_token", oAuthTokenVO.getAccessToken());
        long expires = DateAndNumTimesConstant.THREE_MONTH;
        if (oAuthTokenVO.getExpiresIn() != 0) {
            expires = oAuthTokenVO.getExpiresIn();
            requestModel.addParam("expires_in", expires);  // 搜狐wiki里expires_in必须为int型
        } else {
            requestModel.addParam("expires_in", (int) DateAndNumTimesConstant.THREE_MONTH);
        }
        if (!Strings.isNullOrEmpty(oAuthTokenVO.getRefreshToken())) {
            requestModel.addParam(OAuth.OAUTH_REFRESH_TOKEN, oAuthTokenVO.getRefreshToken());
        }
        if (!Strings.isNullOrEmpty(oAuthTokenVO.getOpenid())) {
            requestModel.addParam("openid", oAuthTokenVO.getOpenid());
        }
        if (!Strings.isNullOrEmpty(oAuthTokenVO.getNickName())) {
            String nickName = oAuthTokenVO.getNickName();
            if (AccountTypeEnum.TAOBAO.toString().equals(providerStr)) {    // taobao注册账号昵称返回乱码
                try {
                    nickName = URLEncoder.encode(nickName, CommonConstant.DEFAULT_CONTENT_CHARSET);
                } catch (UnsupportedEncodingException e) {
                    log.error("encoder taobao nickname exception,nickName:" + nickName, e);
                }
            }
            requestModel.addParam("nick_name", nickName);
        }
        Map map = SGHttpClient.executeBean(requestModel, HttpTransformat.json, Map.class);
        if ("0".equals(map.get("status"))) {
            result.setSuccess(true);
            String userid = map.get("userid").toString();
            result.setDefaultModel("userid", userid);
            result.setDefaultModel("token", map.get("token").toString());
            result.setDefaultModel("uniqname", map.get("uniqname"));
        } else {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_REGISTER_FAILED);
            result.setDefaultModel(CommonConstant.RESPONSE_STATUS, map.get("error"));
            result.setDefaultModel(CommonConstant.RESPONSE_STATUS_TEXT, map.get("error_description"));
        }
        return result;
    }

    /**
     * 调用sohu接口获取用户的openid和accessToken等信息，只针对clientid=1120的第三方用户
     *
     * @param baseOpenApiParams
     * @return
     */
    @Override
    public Result obtainConnectToken(BaseOpenApiParams baseOpenApiParams, int clientId, String clientKey) {
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
                map.remove(SHPPUrlConstant.RESULT_STATUS);
                result.setModels(map);
            }
        } catch (Exception e) {
            log.error("getConnectTokenInfo Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }

    @Override
    public Result rebuildConnectAccount(String appKey, String providerStr, OAuthTokenVO oAuthTokenVO, boolean isQueryConnectRelation) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public RequestModelJSON setDefaultParams(RequestModelJSON requestModelJSON, String userId, String clientId, String clientKey) {
        long ct = System.currentTimeMillis();
        String code = ManagerHelper.generatorCodeGBK(userId, Integer.parseInt(clientId), clientKey, ct);
        requestModelJSON.addParam(SHPPUrlConstant.APPID_STRING, clientId);
        requestModelJSON.addParam(CommonConstant.RESQUEST_CODE, code);
        requestModelJSON.addParam(CommonConstant.RESQUEST_CT, String.valueOf(ct));
        return requestModelJSON;
    }
}
