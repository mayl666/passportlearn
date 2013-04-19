package com.sogou.upd.passport.web.connect;

import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.connect.ConnectAuthManager;
import com.sogou.upd.passport.manager.connect.params.ConnectParams;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthSinaSSOBindTokenRequest;
import com.sogou.upd.passport.web.BaseConnectController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
public class ConnectSSOBindController extends BaseConnectController {

    @Autowired
    private ConnectAuthManager connectAuthManager;

    @RequestMapping(value = "/ssobind/sina", method = RequestMethod.POST)
    @ResponseBody
    public Object handleSSOBind(HttpServletRequest req, HttpServletResponse res) throws Exception {
        OAuthSinaSSOBindTokenRequest oauthRequest = new OAuthSinaSSOBindTokenRequest(req);
        int accountType = AccountTypeEnum.SINA.getValue();
        int clientId = oauthRequest.getClientId();
        String connectUid = oauthRequest.getOpenid();
        String bindAccessToken = oauthRequest.getBindToken();
        ConnectParams connectParams = new ConnectParams(accountType, clientId, connectUid, bindAccessToken, null, null);
        Result result = connectAuthManager.connectAuthBind(oauthRequest, connectParams);
        return result;
    }


}
