package com.sogou.upd.passport.web.connect;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.CommonHelper;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.ServletUtil;
import com.sogou.upd.passport.manager.connect.OAuthAuthLoginManager;
import com.sogou.upd.passport.oauth2.common.types.ConnectTypeEnum;
import com.sogou.upd.passport.web.BaseConnectController;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

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
    private OAuthAuthLoginManager oAuthAuthLoginManager;

    @RequestMapping("/callback/{providerStr}")
    public ModelAndView handleCallbackRedirect(HttpServletRequest req, HttpServletResponse res,
                                               @PathVariable("providerStr") String providerStr, Model model) {
        String viewUrl;
        String ru = req.getParameter(CommonConstant.RESPONSE_RU);
        try {
            ru = URLDecoder.decode(ru, CommonConstant.DEFAULT_CONTENT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            logger.error("Url decode Exception! ru:" + ru);
            ru = CommonConstant.DEFAULT_CONNECT_REDIRECT_URL;
        }
        String type = req.getParameter("type");

        String state = req.getParameter("state");
        String cookieValue = ServletUtil.getCookie(req, state);
        if (!cookieValue.equals(CommonHelper.constructStateCookieKey(providerStr))) {
            viewUrl = buildAppErrorRu(type, ru, ErrorUtil.OAUTH_AUTHZ_STATE_INVALID, null);
            return new ModelAndView(new RedirectView(viewUrl));
        }

        Result result = oAuthAuthLoginManager.handleConnectCallback(req, providerStr, ru, type);
        viewUrl = (String) result.getModels().get(CommonConstant.RESPONSE_RU);
        if (result.isSuccess()) {
            String passportId = (String) result.getModels().get("userid");
            //用户第三方登录log
            UserOperationLog userOperationLog = new UserOperationLog(passportId, req.getRequestURI(), req.getParameter(CommonConstant.CLIENT_ID), result.getCode(), getIp(req));
            UserOperationLogUtil.log(userOperationLog);

            if (type.equals(ConnectTypeEnum.TOKEN.toString())) {
                model.addAttribute("nickname", result.getModels().get("nickname"));
                model.addAttribute("result", result.getModels().get("result"));
                return new ModelAndView(viewUrl);
            } else {
                // TODO 少了种cookie
                return new ModelAndView(new RedirectView(viewUrl));
            }
        } else {
            if (type.equals(ConnectTypeEnum.TOKEN.toString())) {
                return new ModelAndView(viewUrl);
            } else {
                return new ModelAndView(new RedirectView(viewUrl));
            }
        }

    }

}
