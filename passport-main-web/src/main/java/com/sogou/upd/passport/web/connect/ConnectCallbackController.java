package com.sogou.upd.passport.web.connect;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.LoginConstant;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ServletUtil;
import com.sogou.upd.passport.manager.account.CookieManager;
import com.sogou.upd.passport.manager.api.account.form.CookieApiParams;
import com.sogou.upd.passport.manager.connect.OAuthAuthLoginManager;
import com.sogou.upd.passport.oauth2.common.types.ConnectTypeEnum;
import com.sogou.upd.passport.web.BaseConnectController;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    @Autowired
    private CookieManager cookieManager;

    @RequestMapping("/callback/{providerStr}")
    public String handleCallbackRedirect(HttpServletRequest req, HttpServletResponse res,
                                         @PathVariable("providerStr") String providerStr, Model model) throws IOException {
        String viewUrl;
        String ru = req.getParameter(CommonConstant.RESPONSE_RU);
        String type = Strings.isNullOrEmpty(req.getParameter("type")) ? "web" : req.getParameter("type");
        String clientIdStr = req.getParameter(CommonConstant.CLIENT_ID);
        String ua = req.getParameter(CommonConstant.USER_AGENT);
        if (Strings.isNullOrEmpty(clientIdStr)) {
            res.sendRedirect(ru);
            return "empty";
        }

        String httpOrHttps = getProtocol(req);
        try {
            ru = Strings.isNullOrEmpty(ru) ? CommonConstant.DEFAULT_CONNECT_REDIRECT_URL : ru;
            ru = URLDecoder.decode(ru, CommonConstant.DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            logger.error("Url decode Exception! ru:" + ru);
            ru = CommonConstant.DEFAULT_CONNECT_REDIRECT_URL;
        }

        Result result = oAuthAuthLoginManager.handleConnectCallback(req, providerStr, ru, type, httpOrHttps);
        viewUrl = (String) result.getModels().get(CommonConstant.RESPONSE_RU);
        if (result.isSuccess()) {
            String passportId = (String) result.getModels().get("userid");
            //用户第三方登录log
            UserOperationLog userOperationLog = new UserOperationLog(passportId, req.getRequestURI(), req.getParameter(CommonConstant.CLIENT_ID), result.getCode(), getIp(req));
            userOperationLog.putOtherMessage("param", ServletUtil.getParameterString(req));
            userOperationLog.putOtherMessage("yyid", ServletUtil.getCookie(req, "YYID"));
            UserOperationLogUtil.log(userOperationLog);

            if (ConnectTypeEnum.TOKEN.toString().equals(type)) {
                model.addAttribute("uniqname", Coder.encode((String) result.getModels().get("uniqname"), "UTF-8"));  //qq的昵称会出现特殊字符需url编码
                model.addAttribute("result", result.getModels().get("result"));
                if(!Strings.isNullOrEmpty(ua)){     // ua=sogou_ime时，connecterr.vm不需要windows.close()
                    model.addAttribute("appname", ua);
                }
                return viewUrl;
            } else if (ConnectTypeEnum.WAP.toString().equals(type)) {
                String sgid = (String) result.getModels().get(LoginConstant.COOKIE_SGID);
                ServletUtil.setCookie(res, LoginConstant.COOKIE_SGID, sgid, (int) DateAndNumTimesConstant.SIX_MONTH, CommonConstant.SOGOU_ROOT_DOMAIN);

                res.sendRedirect(viewUrl);
                return "empty";
            } else if (ConnectTypeEnum.PC.toString().equals(type)) {
                model.addAttribute("accesstoken", result.getModels().get("accesstoken"));
                model.addAttribute("refreshtoken", result.getModels().get("refreshtoken"));
                model.addAttribute("nick", result.getModels().get("nick"));
                model.addAttribute("sid", result.getModels().get("sid"));
                model.addAttribute("passport", result.getModels().get("passport"));
                model.addAttribute("result", 0);
                model.addAttribute("logintype", result.getModels().get("logintype"));
                return viewUrl;
            } else if (ConnectTypeEnum.WEB.toString().equals(type)) {
                int clientId = Integer.valueOf(clientIdStr);

                //最初版本
//                cookieManager.setCookie(res, passportId, clientId, getIp(req), ru, (int) DateAndNumTimesConstant.TWO_WEEKS);

                //module 替换
                CookieApiParams cookieApiParams = new CookieApiParams();
                cookieApiParams.setUserid(passportId);
                cookieApiParams.setClient_id(clientId);
                cookieApiParams.setRu(ru);
                cookieApiParams.setTrust(CookieApiParams.IS_ACTIVE);
                cookieApiParams.setPersistentcookie(String.valueOf(1));
                cookieApiParams.setIp(getIp(req));
                cookieApiParams.setMaxAge((int) DateAndNumTimesConstant.TWO_WEEKS);
                cookieApiParams.setCreateAndSet(CommonConstant.CREATE_COOKIE_AND_SET);
                cookieApiParams.setUniqname((String) result.getModels().get("refnick"));
                cookieApiParams.setRefnick((String) result.getModels().get("refnick"));

                cookieManager.createCookie(res, cookieApiParams);

                String domain = req.getParameter("domain");
                if (!Strings.isNullOrEmpty(domain)) {
                    String refnick = (String) result.getModels().get("refnick");
                    //uniqname： 对qq导航应用，传qq昵称
                    String creeateSSOCookieUrl = cookieManager.buildCreateSSOCookieUrl(domain, clientId, passportId, refnick, refnick, ru, getIp(req));
                    logger.debug("create sso cookie url:" + creeateSSOCookieUrl);
                    res.sendRedirect(creeateSSOCookieUrl);
                } else {
                    res.sendRedirect(ru);
                }
                return "empty";
            } else {
                res.sendRedirect(viewUrl);
                return "empty";
            }
        } else {
            if (ConnectTypeEnum.TOKEN.toString().equals(type)) {
                model.addAttribute("error", result.getModels().get("error"));
                return viewUrl;
            } else if (ConnectTypeEnum.PC.toString().equals(type)) {
                return viewUrl;
            } else {
                res.sendRedirect(viewUrl + "?errorCode=" + result.getCode() + "&errorMsg=" + Coder.encodeUTF8(result.getMessage()));
                return "empty";
            }
        }
    }

}
