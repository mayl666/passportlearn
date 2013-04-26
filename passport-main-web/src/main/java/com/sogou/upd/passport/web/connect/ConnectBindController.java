package com.sogou.upd.passport.web.connect;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.manager.app.AppConfigManager;
import com.sogou.upd.passport.manager.connect.ConnectAuthManager;
import com.sogou.upd.passport.oauth2.common.OAuthError;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthSinaSSOBindTokenRequest;
import com.sogou.upd.passport.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * SSO-SDK第三方登录授权回调接口
 * User: shipengzhi
 * Date: 13-3-24
 * Time: 下午12:07
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/v2/connect")
public class ConnectBindController extends BaseController {

    @Autowired
    private ConnectAuthManager connectAuthManager;
    @Autowired
    private AppConfigManager appConfigManager;

    @RequestMapping(value = "/ssobind/{providerStr}", method = RequestMethod.POST)
    @ResponseBody
    public Object handleSSOBind(HttpServletRequest req, HttpServletResponse res, @PathVariable("providerStr") String providerStr) throws Exception {
        Result result;
        int provider = AccountTypeEnum.getProvider(providerStr);
        OAuthSinaSSOBindTokenRequest oauthRequest = new OAuthSinaSSOBindTokenRequest(req);

        // 检查client_id和client_secret是否有效
        if (!appConfigManager.verifyClientVaild(oauthRequest.getClientId(), oauthRequest.getClientSecret())) {
            return Result.buildError(OAuthError.Response.INVALID_CLIENT, "client_id or client_secret mismatch");
        }

        result = connectAuthManager.connectAuthBind(oauthRequest, provider);
        return result;
    }


}
