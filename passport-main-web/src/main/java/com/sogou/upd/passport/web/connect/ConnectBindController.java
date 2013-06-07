package com.sogou.upd.passport.web.connect;

import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.manager.connect.OAuthAuthBindManager;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthSinaSSOBindTokenRequest;
import com.sogou.upd.passport.web.BaseConnectController;
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
public class ConnectBindController extends BaseConnectController {

    @Autowired
    private OAuthAuthBindManager oAuthAuthBindManager;
    @Autowired
    private ConfigureManager configureManager;

    @RequestMapping(value = "/ssobind/{providerStr}", method = RequestMethod.POST)
    @ResponseBody
    public Object handleSSOBind(HttpServletRequest req, HttpServletResponse res, @PathVariable("providerStr") String providerStr) throws Exception {
        Result result = new APIResultSupport(false);
        int provider = AccountTypeEnum.getProvider(providerStr);
        OAuthSinaSSOBindTokenRequest oauthRequest;
        try {
            oauthRequest = new OAuthSinaSSOBindTokenRequest(req);
        } catch (OAuthProblemException e) {
            result.setCode(e.getError());
            result.setMessage(e.getDescription());
            return result.toString();
        }

        // 检查client_id和client_secret是否有效
        if (!configureManager.verifyClientVaild(oauthRequest.getClientId(), oauthRequest.getClientSecret())) {
            result.setCode(ErrorUtil.INVALID_CLIENT);
            result.setMessage("client_id or client_secret mismatch");
            return result.toString();
        }

        result = oAuthAuthBindManager.connectSSOBind(oauthRequest, provider);
        return result.toString();
    }


}
