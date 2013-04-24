package com.sogou.upd.passport.web.account.api;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.exception.ProblemException;
import com.sogou.upd.passport.manager.account.AccountLoginManager;
import com.sogou.upd.passport.manager.app.AppConfigManager;
import com.sogou.upd.passport.oauth2.authzserver.request.OAuthTokenRequest;
import com.sogou.upd.passport.oauth2.common.OAuthError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-3-28
 * Time: 下午8:33
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/oauth2")
public class OAuth2Controller {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2Controller.class);

    @Autowired
    private AccountLoginManager accountLoginManager;
    @Autowired
    private AppConfigManager appConfigManager;

    @RequestMapping(value = "/token", method = RequestMethod.POST)
    @ResponseBody
    public Object authorize(HttpServletRequest request) throws Exception {
        OAuthTokenRequest oauthRequest;
        Result result;
        oauthRequest = new OAuthTokenRequest(request);
        int clientId = oauthRequest.getClientId();

        // 检查client_id和client_secret是否有效
        if (!appConfigManager.verifyClientVaild(clientId, oauthRequest.getClientSecret())) {
            return Result.buildError(OAuthError.Response.INVALID_CLIENT, "client_id or client_secret mismatch");
        }
        result = accountLoginManager.authorize(oauthRequest);
        return result;
    }

}
