package com.sogou.upd.passport.manager.api.connect.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CommonConstant;
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
import com.sogou.upd.passport.manager.form.connect.ConnectLoginParams;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.common.parameters.QueryParameterApplier;
import com.sogou.upd.passport.oauth2.openresource.vo.OAuthTokenVO;
import org.springframework.stereotype.Component;

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

    @Override
    public String buildConnectLoginURL(ConnectLoginParams connectLoginParams, String uuid, int provider, String ip) throws OAuthProblemException {
        String providerStr = AccountTypeEnum.getProviderStr(provider);

        Map params = Maps.newHashMap();
        params.put("provider", providerStr);
        params.put("appid", 9998);  // TODO 只是为了测试使用
        if (!Strings.isNullOrEmpty(connectLoginParams.getRu())) {
            params.put("ru", connectLoginParams.getRu());
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
        requestModel.addParam("expires_in", (int) oAuthTokenVO.getExpiresIn());  // 搜狐wiki里expires_in必须为int型
        requestModel.addParam("refresh_token", oAuthTokenVO.getRefreshToken());
        requestModel.addParam("openid", oAuthTokenVO.getOpenid());
//        requestModel.addParam("nick_name", oAuthTokenVO.getNickName());
        Map map = SGHttpClient.executeBean(requestModel, HttpTransformat.json, Map.class);
        if ("0".equals(map.get("status"))) {
            result.setSuccess(true);
            result.setDefaultModel("passportId", map.get("userid"));
            result.setDefaultModel("mappToken", map.get("token"));
            result.setDefaultModel("nickname", map.get("uniqname"));
        } else {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_REGISTER_FAILED);
            result.setDefaultModel("error", map.get("error"));
            result.setDefaultModel("error_description", map.get("error_description"));
        }
        return result;
    }
}
