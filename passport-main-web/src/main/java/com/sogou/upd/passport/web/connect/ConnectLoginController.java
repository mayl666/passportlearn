package com.sogou.upd.passport.web.connect;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.manager.connect.OAuthAuthLoginManager;
import com.sogou.upd.passport.manager.form.ConnectLoginParams;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.request.OAuthAuthzClientRequest;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthSinaSSOTokenRequest;
import com.sogou.upd.passport.web.BaseConnectController;
import com.sogou.upd.passport.web.ControllerHelper;
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
import java.util.UUID;

/**
 * SSO-SDK第三方登录授权回调接口 User: shipengzhi Date: 13-3-24 Time: 下午12:07 To change this template use File
 * | Settings | File Templates.
 */
@Controller
public class ConnectLoginController extends BaseConnectController {


    @Autowired
    private OAuthAuthLoginManager oAuthAuthLoginManager;
    @Autowired
    private ConfigureManager configureManager;

    @RequestMapping(value = {"/v2/connect/ssologin/{providerStr}", "/connect/ssologin/sina"}, method = RequestMethod.POST)
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
            return Result.buildError(ErrorUtil.INVALID_CLIENT, "client_id or client_secret mismatch");
        }

        result = oAuthAuthLoginManager.connectSSOLogin(oauthRequest, provider, ip);
        return result;
    }

    @RequestMapping(value = "/connect/login")
    public ModelAndView authorize(HttpServletRequest req, HttpServletResponse res,
                                  ConnectLoginParams connectLoginParams) {

        ModelAndView defaultMV = new ModelAndView(new RedirectView(CommonConstant.DEFAULT_CONNECT_REDIRECT_URL));
        int provider = AccountTypeEnum.getProvider(connectLoginParams.getProvider());
        //验证client_id
        int clientId;
        try {
            clientId = Integer.parseInt(connectLoginParams.getClient_id());
        } catch (NumberFormatException e) {
            return defaultMV;
        }
        //检查client_id是否存在
        if (!configureManager.checkAppIsExist(clientId)) {
            return defaultMV;
        }

        // 校验参数
        String validateResult = ControllerHelper.validateParams(connectLoginParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            return defaultMV;
        }
        // 获取connect配置
        ConnectConfig connectConfig = configureManager.obtainConnectConfig(clientId, provider);
        if (connectConfig == null) {
            return defaultMV;
        }

        // 避免重复提交的唯一码
        String uuid = UUID.randomUUID().toString();
        try {
            OAuthAuthzClientRequest oauthRequest = oAuthAuthLoginManager.buildConnectLoginRequest(connectLoginParams, connectConfig, uuid, provider, getIp(req));
            writeOAuthStateCookie(res, uuid, provider);
            return new ModelAndView(new RedirectView(oauthRequest.getLocationUri()));
        } catch (OAuthProblemException e) {
            return defaultMV;
        }
    }


}
