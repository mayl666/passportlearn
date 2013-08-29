package com.sogou.upd.passport.web.connect;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonHelper;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.ServletUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.account.PCAccountManager;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.common.types.ConnectTypeEnum;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthAuthzClientResponse;
import com.sogou.upd.passport.oauth2.openresource.response.accesstoken.OAuthAccessTokenResponse;
import com.sogou.upd.passport.oauth2.openresource.response.accesstoken.QQOpenIdResponse;
import com.sogou.upd.passport.oauth2.openresource.vo.OAuthTokenVO;
import com.sogou.upd.passport.service.app.ConnectConfigService;
import com.sogou.upd.passport.service.connect.ConnectAuthService;
import com.sogou.upd.passport.web.BaseConnectController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 第三方账号授权回调接口
 * User: shipengzhi
 * Date: 13-4-26
 * Time: 下午5:52
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/connect")
public class ConnectCallbackController extends BaseConnectController {

    @Autowired
    private ConnectAuthService connectAuthService;
    @Autowired
    private ConnectConfigService connectConfigService;
    @Autowired
    private ConnectApiManager proxyConnectApiManager;
    @Autowired
    private PCAccountManager pcAccountManager;

    @RequestMapping("/callback/{providerStr}")
    public ModelAndView handleCallbackRedirect(HttpServletRequest req, HttpServletResponse res,
                                               @PathVariable("providerStr") String providerStr, Model model) {
        String url;
        int provider = AccountTypeEnum.getProvider(providerStr);
        int clientId = Integer.valueOf(req.getParameter("client_id"));
        String ru = req.getParameter("ru");
        String ip = req.getParameter("ip");
        String type = req.getParameter("type");
        String instanceId = req.getParameter("ts");

        String state = req.getParameter("state");
        String cookieValue = ServletUtil.getCookie(req, state);
        if (!cookieValue.equals(CommonHelper.constructStateCookieKey(providerStr))) {
            url = buildAppErrorRu(type, ru, ErrorUtil.OAUTH_AUTHZ_STATE_INVALID, null);
            return new ModelAndView(new RedirectView(url));
        }

        try {
            //1.获取授权成功后返回的code值
            OAuthAuthzClientResponse oar = OAuthAuthzClientResponse.oauthCodeAuthzResponse(req);
            String code = oar.getCode();
            //2.根据code值获取access_token
            ConnectConfig connectConfig = connectConfigService.queryConnectConfig(clientId, provider);
            if (connectConfig == null) {
                url = buildAppErrorRu(type, ru, ErrorUtil.UNSUPPORT_THIRDPARTY, null);
                return new ModelAndView(new RedirectView(url));
            }
            OAuthAccessTokenResponse oauthResponse = connectAuthService.obtainAccessTokenByCode(provider, code, connectConfig);
            OAuthTokenVO oAuthTokenVO = oauthResponse.getOAuthTokenVO();
            oAuthTokenVO.setIp(ip);

            if (provider == AccountTypeEnum.QQ.getValue()) {
                //3.QQ需根据access_token获取openid
                String accessToken = oAuthTokenVO.getAccessToken();
                QQOpenIdResponse openIdResponse = connectAuthService.obtainOpenIdByAccessToken(provider, accessToken);
                String openId = openIdResponse.getOpenId();
                if (!Strings.isNullOrEmpty(openId)) oAuthTokenVO.setOpenid(openId);
            }

            // 创建第三方账号
            Result connectAccountResult = proxyConnectApiManager.buildConnectAccount(providerStr, oAuthTokenVO);
            if (connectAccountResult.isSuccess()) {
                if (type.equals(ConnectTypeEnum.TOKEN.toString())) {
                    String passportId = (String) connectAccountResult.getModels().get("userid");
                    Result tokenResult = pcAccountManager.createConnectToken(clientId, passportId, instanceId);
                    AccountToken accountToken = (AccountToken) tokenResult.getDefaultModel();
                    if (tokenResult.isSuccess()) {
                        String result = "0|" + accountToken.getAccessToken() + "|" + accountToken.getRefreshToken() + "|" +
                                accountToken.getPassportId() + "|" + oAuthTokenVO.getNickName();
                        model.addAttribute("result", result);
                        return new ModelAndView("/pcaccount/connectlogin");
                    }
                } else if (type.equals(ConnectTypeEnum.WEB.toString())) {
                    return new ModelAndView(new RedirectView(ru));
                }
            } else {
                url = buildAppErrorRu(type, ru, connectAccountResult.getCode(), null);
                return new ModelAndView(new RedirectView(url));
            }

        } catch (IOException e) {
            logger.error("read oauth consumer IOException!", e);
            url = buildAppErrorRu(type, ru, ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "read oauth consumer IOException");
            return new ModelAndView(new RedirectView(url));
        } catch (ServiceException se) {
            logger.error("query connect config Exception!", se);
            url = buildAppErrorRu(type, ru, ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "query connect config Exception");
            return new ModelAndView(new RedirectView(url));
        } catch (OAuthProblemException ope) {
            url = buildAppErrorRu(type, ru, ope.getError(), ope.getDescription());
            return new ModelAndView(new RedirectView(url));
        }

        return new ModelAndView("");
    }

    private String getAuthzType(HttpServletRequest req) {
        return Strings.isNullOrEmpty(req.getParameter(OAuth.OAUTH_CODE)) ?
                ConnectTypeEnum.TOKEN.toString() : ConnectTypeEnum.WEB.toString();
    }
}
