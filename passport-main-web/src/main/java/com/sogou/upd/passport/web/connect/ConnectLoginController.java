package com.sogou.upd.passport.web.connect;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.ServletUtil;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.manager.connect.OAuthAuthLoginManager;
import com.sogou.upd.passport.manager.form.connect.ConnectLoginParams;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.web.BaseConnectController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    private ConfigureManager configureManager;

    @RequestMapping(value = "/connect/login")
    public void authorize(HttpServletRequest req, HttpServletResponse res, ConnectLoginParams connectLoginParams) throws IOException {

        // 校验参数
        String url;
        String type = connectLoginParams.getType();
        String ru = connectLoginParams.getRu();
        String providerStr = connectLoginParams.getProvider();
        String httpOrHttps = getProtocol(req);
        String ua = getHeaderUserAgent(req);
        try {
            String validateResult = ControllerHelper.validateParams(connectLoginParams);
            if (!Strings.isNullOrEmpty(validateResult)) {
                url = buildAppErrorRu(type, providerStr, ru, ErrorUtil.ERR_CODE_COM_REQURIE, validateResult);
                res.sendRedirect(url);
                return;
            }

            int provider = AccountTypeEnum.getProvider(providerStr);
            // 浏览器、输入法的第三方登录是搜狐nginx转发过来的，为了避免nginx层解析，所以兼容appid参数
            if (!Strings.isNullOrEmpty(connectLoginParams.getAppid()) && Strings.isNullOrEmpty(connectLoginParams.getClient_id())) {
                connectLoginParams.setClient_id(connectLoginParams.getAppid());
            }
            int clientId = Integer.parseInt(connectLoginParams.getClient_id());
            //检查client_id是否存在
            if (!configureManager.checkAppIsExist(clientId)) {
                url = buildAppErrorRu(type, providerStr, ru, ErrorUtil.INVALID_CLIENTID, null);
                res.sendRedirect(url);
                return;
            }

            String uuid = UUID.randomUUID().toString();
            url = oAuthAuthLoginManager.buildConnectLoginURL(connectLoginParams, uuid, provider, getIp(req), httpOrHttps, ua);
            res.sendRedirect(url);
            return;
        } catch (OAuthProblemException e) {
            url = buildAppErrorRu(type, providerStr, ru, e.getError(), e.getDescription());
            res.sendRedirect(url);
            return;
        } finally {
            //用户登陆log--二期迁移到callback中记录log
            UserOperationLog userOperationLog = new UserOperationLog(providerStr, req.getRequestURI(), connectLoginParams.getClient_id(), "0", getIp(req));
            userOperationLog.putOtherMessage("ref", connectLoginParams.getRu());
            userOperationLog.putOtherMessage("param", ServletUtil.getParameterString(req));
            userOperationLog.putOtherMessage(CommonConstant.USER_AGENT, ua);
            UserOperationLogUtil.log(userOperationLog);
        }
    }

}
