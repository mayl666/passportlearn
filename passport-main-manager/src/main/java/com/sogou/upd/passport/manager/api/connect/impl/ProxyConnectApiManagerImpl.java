package com.sogou.upd.passport.manager.api.connect.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.parameter.HttpTransformat;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.SGHttpClient;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.manager.api.connect.form.BaseOpenApiParams;
import com.sogou.upd.passport.manager.form.connect.ConnectLoginParams;
import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.common.parameters.QueryParameterApplier;
import com.sogou.upd.passport.oauth2.openresource.vo.OAuthTokenVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    public String buildConnectLoginURL(ConnectLoginParams connectLoginParams, String uuid, int provider, String ip) throws OAuthProblemException {
        String providerStr = AccountTypeEnum.getProviderStr(provider);

        Map params = Maps.newHashMap();
        params.put("provider", providerStr);
        params.put("appid", SHPPUrlConstant.DEFAULT_CONNECT_APP_ID);  // TODO 只是为了避免和浏览器输入法PC端冲突
        params.put("hun", "1");  //是否显示“起个更好的名字”。默认显示；为1表示 隐藏
        if (!Strings.isNullOrEmpty(connectLoginParams.getRu())) {
            params.put(CommonConstant.RESPONSE_RU, connectLoginParams.getRu());
        }
        if (!Strings.isNullOrEmpty(connectLoginParams.getDisplay())) {
            params.put("display", connectLoginParams.getDisplay());
        }
        params.put("type", connectLoginParams.getType());
        params.put("forcelogin", connectLoginParams.isForcelogin());
        if (!Strings.isNullOrEmpty(connectLoginParams.getFrom())) {
            params.put("from", connectLoginParams.getFrom());
        }
        if (!Strings.isNullOrEmpty(connectLoginParams.getTs())) {
            params.put("ts", connectLoginParams.getTs());
        }

        String url = QueryParameterApplier.applyOAuthParametersString(SHPPUrlConstant.CONNECT_LOGIN_ULR, params);
        return url;
    }

    @Override
    public Result buildConnectAccount(String providerStr, OAuthTokenVO oAuthTokenVO) {
        Result result = new APIResultSupport(false);
        String url = SHPPUrlConstant.CREATE_CONNECT_USER + providerStr;
        RequestModel requestModel = new RequestModel(url);
        requestModel.addParam("appid", CommonConstant.SGPP_DEFAULT_CLIENTID);
        requestModel.addParam("provider", providerStr);
        requestModel.addParam("access_token", oAuthTokenVO.getAccessToken());
        if(oAuthTokenVO.getExpiresIn() != 0){
            requestModel.addParam("expires_in", (int) oAuthTokenVO.getExpiresIn());  // 搜狐wiki里expires_in必须为int型
        }else{
            requestModel.addParam("expires_in", (int)DateAndNumTimesConstant.THREE_MONTH);
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
            result.setDefaultModel("userid", map.get("userid"));
            result.setDefaultModel("token", map.get("token"));
            result.setDefaultModel("uniqname", map.get("uniqname"));
        } else {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_REGISTER_FAILED);
            result.setDefaultModel(CommonConstant.RESPONSE_STATUS, map.get("error"));
            result.setDefaultModel(CommonConstant.RESPONSE_STATUS_TEXT, map.get("error_description"));
        }
        return result;
    }

    @Override
    public Result obtainConnectTokenInfo(BaseOpenApiParams baseOpenApiParams, int clientId, String clientKey) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
