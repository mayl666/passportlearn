package com.sogou.upd.passport.web.account.action.mapp;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.LoginConstant;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.SignatureUtils;
import com.sogou.upd.passport.manager.account.CookieManager;
import com.sogou.upd.passport.manager.api.connect.SessionServerManager;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.account.form.MappLogoutParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.TreeMap;

/**
 * 手机APP相关接口
 * User: mayan
 * Date: 14-3-3
 * Time: 下午3:46
 */
@Controller
public class MappAction extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(MappAction.class);

    @Autowired
    private SessionServerManager sessionServerManager;
    @Autowired
    private CookieManager cookieManager;

    @RequestMapping(value = {"/mapp/logout"})
    @ResponseBody
    public String logout(HttpServletRequest request, MappLogoutParams params) throws Exception {
        // 校验参数
        Result result = new APIResultSupport(false);
        String sgid = null;
        String client_id = null;
        String code = null;
        String instance_id = null;
        try {
            //参数验证
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }

            code = params.getCode();
            sgid = params.getSgid();
            client_id = params.getClient_id();
            instance_id = params.getInstance_id();

            //验证code是否有效
            result = checkCodeIsCorrect(sgid, params.getClient_id(), instance_id, code);
            if (!result.isSuccess()) {
                return result.toString();
            }

            //session server中清除cookie
            result = sessionServerManager.removeSession(sgid);
            if (result.isSuccess()) {
                result.setSuccess(true);
                result.setMessage("logout success!");
                return result.toString();
            }
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("logout " + "sgid:" + sgid + ",client_id:" + client_id);
            }
        } finally {
            //用于记录log
            UserOperationLog userOperationLog = new UserOperationLog(sgid, client_id, result.getCode(), getIp(request));
            String referer = request.getHeader("referer");
            userOperationLog.putOtherMessage("ref", referer);
            UserOperationLogUtil.log(userOperationLog);
        }
        return result.toString();
    }

    //sgid+client_id+instance_id+ client_secret
    private Result checkCodeIsCorrect(String sgid, String client_id, String instance_id, String originalCode) {
        Result result = new APIResultSupport(false);
        int clientId = Integer.parseInt(client_id);
        AppConfig appConfig = cookieManager.queryAppConfigByClientId(clientId);
        if (appConfig != null) {
            String secret = appConfig.getClientSecret();

            TreeMap map = new TreeMap();
            map.put(LoginConstant.COOKIE_SGID, sgid);
            map.put("client_id", client_id);
            map.put("instance_id", instance_id);

            //计算默认的code
            String code = "";
            try {
                code = SignatureUtils.generateSignature(map, secret);
            } catch (Exception e) {
                logger.error("calculate default code error", e);
            }

            if (code.equalsIgnoreCase(originalCode)) {
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
