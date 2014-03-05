package com.sogou.upd.passport.web.connect;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.CookieManager;
import com.sogou.upd.passport.manager.connect.SSOAfterauthManager;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.web.BaseConnectController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.account.form.AfterAuthParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: mayan
 * Date: 14-3-3
 * Time: 下午4:21
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/connect/sso")
public class ConnectSSOController extends BaseConnectController {

    private static final Logger logger = LoggerFactory.getLogger(ConnectSSOController.class);
    @Autowired
    private SSOAfterauthManager sSOAfterauthManager;
    @Autowired
    private CookieManager cookieManager;

    //登陆后获取登录信息接口
    @RequestMapping("/afterauth/{providerStr}")
    @ResponseBody
    public String sso_afterauth(HttpServletRequest req, HttpServletResponse res,
                                         @PathVariable("providerStr") String providerStr,
                                         AfterAuthParams params) {
        Result result = new APIResultSupport(false);

        //参数验证
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }

        //验证code是否有效
        result = checkCodeIsCorrect(params);
        if (!result.isSuccess()) {
            return result.toString();
        }

        result = sSOAfterauthManager.handleSSOAfterauth(req, providerStr);

        return result.toString();
    }
    //openid+ client_id +access_token+expires_in+isthird +instance_id+ client _secret
    private Result checkCodeIsCorrect(AfterAuthParams params) {
        Result result = new APIResultSupport(false);

        AppConfig appConfig = cookieManager.queryAppConfigByClientId(params.getClient_id());
        if (appConfig != null) {
            String secret = appConfig.getClientSecret();

            //计算默认的code
            String code = "";
            try {
                code = params.getOpenid() + params.getClient_id() + params.getAccess_token() + params.getExpires_in() + params.getIsthird() + params.getInstance_id() +secret;
                code = Coder.encryptMD5GBK(code);
            } catch (Exception e) {
                logger.error("calculate default code error", e);
            }

            if (code.equalsIgnoreCase(params.getCode())) {
                result.setSuccess(true);
                result.setMessage("内部接口code签名正确！");
            } else {
                result.setCode(ErrorUtil.INTERNAL_REQUEST_INVALID);
            }
        } else {
            result.setCode(ErrorUtil.INVALID_CLIENTID);
        }
        return result;
    }
}
