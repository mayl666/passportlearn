package com.sogou.upd.passport.web.connect;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonHelper;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.utils.ServletUtil;
import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.types.ConnectTypeEnum;
import com.sogou.upd.passport.oauth2.openresource.request.OAuthAuthzClientRequest;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthAuthzClientResponse;
import com.sogou.upd.passport.oauth2.openresource.response.accesstoken.OAuthAccessTokenResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-26
 * Time: 下午5:52
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/connect")
public class ConnectCallbackController {

    @RequestMapping("/callback/{providerStr}")
    public Object handleCallbackRedirect(HttpServletRequest req, HttpServletResponse res,
                                         @PathVariable("providerStr") String providerStr) throws Exception {
        int provider = AccountTypeEnum.getProvider(providerStr);
        int clientId = Integer.valueOf(req.getParameter("client_id"));
        String ru = req.getParameter("ru");
        String ip = req.getParameter("ip");
        String authzType = getAuthzType(req);

        String state = req.getParameter("state");
        String cookieState = ServletUtil.getCookie(req, CommonHelper.constructStateCookieKey(provider));
        if (!state.equals(cookieState)) {
            // todo return;
        }


        //1.获取授权成功后返回的code值
        OAuthAuthzClientResponse oar = OAuthAuthzClientResponse.oauthCodeAuthzResponse(req);
        String code = oar.getCode();
        if (!Strings.isNullOrEmpty(code)) {  // web端
            //2.根据code值获取access_token
//            OAuthAccessTokenResponse oauthResponse = authService.obtainAccessTokenByCode(
//                    connectName, code, config, consumer);
//
//            OAuthToken oauthToken = oauthResponse.getOAuthToken();

        } else {
            oar = OAuthAuthzClientResponse.oauthTokenAuthzResponse(req);

        }


        return new ModelAndView("");
    }

    private String getAuthzType(HttpServletRequest req) {
        return Strings.isNullOrEmpty(req.getParameter(OAuth.OAUTH_CODE)) ?
                ConnectTypeEnum.TOKEN.toString() : ConnectTypeEnum.WEB.toString();
    }
}
