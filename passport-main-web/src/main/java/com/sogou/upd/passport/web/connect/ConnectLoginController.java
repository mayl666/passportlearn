package com.sogou.upd.passport.web.connect;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.manager.connect.OAuthAuthLoginManager;
import com.sogou.upd.passport.manager.form.connect.ConnectLoginParams;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthSinaSSOTokenRequest;
import com.sogou.upd.passport.web.BaseConnectController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * SSO-SDK第三方授权接口
 * User: shipengzhi
 * Date: 13-3-24 Time: 下午12:07
 */
@Controller
public class ConnectLoginController extends BaseConnectController {

    @Autowired
    private OAuthAuthLoginManager oAuthAuthLoginManager;
    @Autowired
    private ConnectApiManager proxyConnectApiManager;
    @Autowired
    private ConfigureManager configureManager;

    @RequestMapping(value = "/connect/ssologin/{providerStr}", method = RequestMethod.POST)
    @ResponseBody
    public Object handleSSOLogin(HttpServletRequest req, HttpServletResponse res, @PathVariable("providerStr") String providerStr) throws Exception {
        Result result = new APIResultSupport(false);
        String ip = getIp(req);
        int provider = AccountTypeEnum.getProvider(providerStr);
        OAuthSinaSSOTokenRequest oauthRequest;
        try {
            oauthRequest = new OAuthSinaSSOTokenRequest(req);
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

        result = oAuthAuthLoginManager.connectSSOLogin(oauthRequest, provider, ip);
        return result.toString();
    }

    @RequestMapping(value = "/connect/login")
    public ModelAndView authorize(HttpServletRequest req, HttpServletResponse res,
                                  ConnectLoginParams connectLoginParams) {

        String url;
        int provider = AccountTypeEnum.getProvider(connectLoginParams.getProvider());
        //验证client_id
        int clientId = Integer.parseInt(connectLoginParams.getClient_id());

        //检查client_id是否存在
        String type = connectLoginParams.getType();
        if (!configureManager.checkAppIsExist(clientId)) {
            url = buildAppErrorRu(type, ErrorUtil.INVALID_CLIENTID, null);
            return new ModelAndView(new RedirectView(url));
        }


        // 校验参数
        String validateResult = ControllerHelper.validateParams(connectLoginParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            url = buildAppErrorRu(type, ErrorUtil.ERR_CODE_COM_REQURIE, validateResult);
            return new ModelAndView(new RedirectView(url));
        }

        // 避免重复提交的唯一码
        String uuid = UUID.randomUUID().toString();
        try {
            url = proxyConnectApiManager.buildConnectLoginURL(connectLoginParams, uuid, provider, getIp(req));
//          writeOAuthStateCookie(res, uuid, provider); // TODO 第一阶段先注释掉，没用到

        } catch (OAuthProblemException e) {
            url = buildAppErrorRu(type, e.getError(), e.getDescription());

        }

        //用户登陆log--二期迁移到callback中记录log
        UserOperationLog userOperationLog = new UserOperationLog(connectLoginParams.getProvider(), req.getRequestURI(), connectLoginParams.getClient_id(), "0", getIp(req));
        userOperationLog.putOtherMessage("ref", connectLoginParams.getRu());
        userOperationLog.putOtherMessage("type", type);
        UserOperationLogUtil.log(userOperationLog);

        return new ModelAndView(new RedirectView(url));
    }

}
