package com.sogou.upd.passport.web.connect;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ServletUtil;
import com.sogou.upd.passport.manager.account.CommonManager;
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
    private CommonManager commonManager;

    @RequestMapping("/callback/{providerStr}")
    public String handleCallbackRedirect(HttpServletRequest req, HttpServletResponse res,
                                         @PathVariable("providerStr") String providerStr, Model model) throws IOException {
        String viewUrl;
        String ru = req.getParameter(CommonConstant.RESPONSE_RU);
        try {
            ru = URLDecoder.decode(ru, CommonConstant.DEFAULT_CONTENT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            logger.error("Url decode Exception! ru:" + ru);
            ru = CommonConstant.DEFAULT_CONNECT_REDIRECT_URL;
        }
        String type = req.getParameter("type");

        Result result = oAuthAuthLoginManager.handleConnectCallback(req, providerStr, ru, type);
        viewUrl = (String) result.getModels().get(CommonConstant.RESPONSE_RU);
        if (result.isSuccess()) {
            String passportId = (String) result.getModels().get("userid");
            //用户第三方登录log
            UserOperationLog userOperationLog = new UserOperationLog(passportId, req.getRequestURI(), req.getParameter(CommonConstant.CLIENT_ID), result.getCode(), getIp(req));
            userOperationLog.putOtherMessage("param", ServletUtil.getParameterString(req));
            UserOperationLogUtil.log(userOperationLog);

            if (ConnectTypeEnum.TOKEN.toString().equals(type)) {
                model.addAttribute("uniqname", Coder.encode((String) result.getModels().get("uniqname"), "UTF-8"));  //qq的昵称会出现特殊字符需url编码
                model.addAttribute("result", result.getModels().get("result"));
                return viewUrl;
            } else if (type.equals(ConnectTypeEnum.WAP.toString())) {
                String sgid = (String) result.getModels().get("sgid");
                ServletUtil.setCookie(res, "sgid", sgid, (int) DateAndNumTimesConstant.SIX_MONTH, CommonConstant.SOGOU_ROOT_DOMAIN);

                res.sendRedirect(viewUrl);
                return "";
            } else if (ConnectTypeEnum.PC.toString().equals(type)) {
                model.addAttribute("accesstoken", result.getModels().get("accesstoken"));
                model.addAttribute("refreshtoken", result.getModels().get("refreshtoken"));
                model.addAttribute("nick", result.getModels().get("nick"));
//                model.addAttribute("sname", result.getModels().get("sname"));
                model.addAttribute("sid", result.getModels().get("sid"));
                model.addAttribute("passport", result.getModels().get("passport"));
                model.addAttribute("result", 0);
                model.addAttribute("logintype", result.getModels().get("logintype"));
                return viewUrl;
            } else if (ConnectTypeEnum.WEB.toString().equals(type)) {
                int clientId = Integer.valueOf(req.getParameter(CommonConstant.CLIENT_ID));
                commonManager.setSogouCookie(res, passportId, clientId, getIp(req), (int) DateAndNumTimesConstant.TWO_WEEKS, ru);
                res.sendRedirect(ru);
                return "";
            } else {
                res.sendRedirect(viewUrl);
                return "";
            }
        } else {
            if (ConnectTypeEnum.TOKEN.toString().equals(type)) {
                model.addAttribute("error", result.getModels().get("error"));
                return viewUrl;
            } else if (ConnectTypeEnum.PC.toString().equals(type)) {
                return viewUrl;
            } else {
                res.sendRedirect(viewUrl);
                return "";
            }
        }

    }

}
