package com.sogou.upd.passport.web.connect;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.LoginConstant;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.common.utils.ServletUtil;
import com.sogou.upd.passport.manager.account.CookieManager;
import com.sogou.upd.passport.manager.api.account.form.CookieApiParams;
import com.sogou.upd.passport.manager.connect.OAuthAuthLoginManager;
import com.sogou.upd.passport.manager.form.connect.ConnectLoginRedirectParams;
import com.sogou.upd.passport.oauth2.common.types.ConnectTypeEnum;
import com.sogou.upd.passport.web.BaseConnectController;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    @Autowired
    private RedisUtils redisUtils;

    @RequestMapping("/callback/{providerStr}")
    public String handleCallbackRedirect(HttpServletRequest req, HttpServletResponse res,
                                         @PathVariable("providerStr") String providerStr,
                                         @RequestParam(defaultValue = "") String state,
                                         ConnectLoginRedirectParams redirectParams, Model model) throws IOException {

        if (AccountTypeEnum.WEIXIN.getValue() == AccountTypeEnum.getProvider(providerStr) && !Strings.isNullOrEmpty(state)) {
            redirectParams = redisUtils.getObject(state, ConnectLoginRedirectParams.class);
            if (redirectParams == null) {
                res.sendRedirect(CommonConstant.DEFAULT_INDEX_URL);
                return "empty";
            }
        }
        String type = redirectParams.getType();
        String clientIdStr = String.valueOf(redirectParams.getClient_id());
        String ua = redirectParams.getUser_agent();
        String ru = parseRedirectUrl(redirectParams).getRu();
        if (Strings.isNullOrEmpty(clientIdStr)) {
            res.sendRedirect(ru);
            return "empty";
        }

        Result result = oAuthAuthLoginManager.handleConnectCallback(redirectParams, req, providerStr, getProtocol(req));
        String viewUrl = (String) result.getModels().get(CommonConstant.RESPONSE_RU);
        if (result.isSuccess()) {
            String passportId = (String) result.getModels().get("userid");
            //用户第三方登录log
            UserOperationLog userOperationLog = new UserOperationLog(passportId, req.getRequestURI(), clientIdStr, result.getCode(), getIp(req));
            userOperationLog.putOtherMessage("param", ServletUtil.getParameterString(req));
            userOperationLog.putOtherMessage("yyid", ServletUtil.getCookie(req, "YYID"));
            userOperationLog.putOtherMessage(CommonConstant.USER_AGENT, ua);
            UserOperationLogUtil.log(userOperationLog);

            if (ConnectTypeEnum.TOKEN.toString().equals(type)) {
                model.addAttribute("uniqname", Coder.encode((String) result.getModels().get("uniqname"), "UTF-8"));  //qq的昵称会出现特殊字符需url编码
                model.addAttribute("result", result.getModels().get("result"));
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
                String domain = redirectParams.getDomain();
                if (!Strings.isNullOrEmpty(domain)) {
                    String refnick = (String) result.getModels().get("refnick");
                    //uniqname： 对qq导航应用，传qq昵称
                    String creeateSSOCookieUrl = cookieManager.buildCreateSSOCookieUrl(domain, clientId, passportId, refnick, refnick, ru, getIp(req));
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
                if (!Strings.isNullOrEmpty(ua) && ua.contains(CommonConstant.SOGOU_IME_UA)) {     // ua=sogou_ime时，connecterr.vm不需要windows.close()
                    model.addAttribute("appname", CommonConstant.SOGOU_IME_UA); // vm没有contains函数，只能==
                }
                return viewUrl;
            } else if (ConnectTypeEnum.PC.toString().equals(type)) {
                model.addAttribute(CommonConstant.BROWER_VERSION, redirectParams.getV());
                model.addAttribute(CommonConstant.INSTANCE_ID, redirectParams.getTs());
                return viewUrl;
            } else {
                String errMsg = result.getMessage() == null ? "thirdpart custom error" : result.getMessage();
                res.sendRedirect(viewUrl + "?errorCode=" + result.getCode() + "&errorMsg=" + Coder.encodeUTF8(errMsg));
                return "empty";
            }
        }
    }

    private ConnectLoginRedirectParams parseRedirectUrl(ConnectLoginRedirectParams redirectParams) {
        String ru = redirectParams.getRu();
        try {
            ru = Strings.isNullOrEmpty(ru) ? CommonConstant.DEFAULT_INDEX_URL : ru;
            ru = URLDecoder.decode(ru, CommonConstant.DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            logger.error("Url decode Exception! ru:" + ru);
            ru = CommonConstant.DEFAULT_INDEX_URL;
        }
        redirectParams.setRu(ru);
        return redirectParams;
    }
}
