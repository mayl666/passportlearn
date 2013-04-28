package com.sogou.upd.passport.web.connect;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.manager.connect.ConnectAuthManager;
import com.sogou.upd.passport.model.OAuthConsumer;
import com.sogou.upd.passport.model.OAuthConsumerFactory;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.oauth2.common.OAuthError;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.parameters.ResponseTypeEnum;
import com.sogou.upd.passport.oauth2.openresource.request.OAuthClientRequest;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthSinaSSOTokenRequest;
import com.sogou.upd.passport.web.BaseConnectController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.manager.form.ConnectLoginParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * SSO-SDK第三方登录授权回调接口 User: shipengzhi Date: 13-3-24 Time: 下午12:07 To change this template use File
 * | Settings | File Templates.
 */
@Controller
public class ConnectLoginController extends BaseConnectController {

    private static final String DEFAULT_CONNECT_REDIRECT_URL = "http://account.sogou.com";

    @Autowired
    private ConnectAuthManager connectAuthManager;
    @Autowired
    private ConfigureManager configureManager;

    @RequestMapping(value = "/v2/connect/ssologin/{providerStr}", method = RequestMethod.POST)
    @ResponseBody
    public Object handleSSOLogin(HttpServletRequest req, HttpServletResponse res, @PathVariable("providerStr") String providerStr) throws Exception {
        Result result;
        String ip = getIp(req);
        int provider = AccountTypeEnum.getProvider(providerStr);
        OAuthSinaSSOTokenRequest oauthRequest;
        try {
            oauthRequest = new OAuthSinaSSOTokenRequest(req);
        } catch (OAuthProblemException e) {
            return Result.buildError(e.getError(), e.getDescription());
        }

        // 检查client_id和client_secret是否有效
        if (!configureManager.verifyClientVaild(oauthRequest.getClientId(), oauthRequest.getClientSecret())) {
            return Result.buildError(OAuthError.Response.INVALID_CLIENT, "client_id or client_secret mismatch");
        }

        result = connectAuthManager.connectAuthLogin(oauthRequest, provider, ip);
        return result;
    }

    @RequestMapping(value = "/connect/login")
    public ModelAndView authorize(HttpServletRequest req, HttpServletResponse res,
                                  ConnectLoginParams oauthLoginParams) throws IOException, OAuthProblemException {

        int provider = AccountTypeEnum.getProvider(oauthLoginParams.getP());
        String ru = oauthLoginParams.getRu();
        boolean force = oauthLoginParams.isForce();
        int clientId = oauthLoginParams.getClient_id();

        // TODO 判断type

        String validateResult = ControllerHelper.validateParams(oauthLoginParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            String redirectUrl = DEFAULT_CONNECT_REDIRECT_URL;
            if (!Strings.isNullOrEmpty(ru)) {
                redirectUrl = ru;
            }
            return new ModelAndView(new RedirectView(redirectUrl));
        }

        ConnectConfig connectConfig = configureManager.obtainConnectConfig(clientId, provider);
        String scope = connectConfig.getScope();
        String appkey = connectConfig.getAppKey();

        OAuthConsumer oAuthConsumer = OAuthConsumerFactory.getOAuthConsumer(provider);

        // 避免重复提交的唯一码
        String uuid = UUID.randomUUID().toString();
        OAuthClientRequest request = OAuthClientRequest
                .authorizationLocation(oAuthConsumer.getUserAuthzUrl()).setAppKey(appkey)
                .setRedirectURI(oAuthConsumer.getCallbackUrl())
                .setResponseType(ResponseTypeEnum.CODE).setScope(scope)
                .setDisplay(oauthLoginParams.getDisplay()).setForceLogin(force, provider)
                .setState(uuid)
                .buildQueryMessage(OAuthClientRequest.class);

        // 需要写入cookie中的内容，包括回调url，access_token,appid，硬件信息
        writeOAuthStateCookie(res, uuid, provider);

        return new ModelAndView(new RedirectView(request.getLocationUri()));

    }

}
