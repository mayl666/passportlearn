package com.sogou.upd.passport.web.connect;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.SignatureUtils;
import com.sogou.upd.passport.manager.connect.SSOAfterauthManager;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.app.AppConfigService;
import com.sogou.upd.passport.web.BaseConnectController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.account.form.AfterAuthParams;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.TreeMap;

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
    private AppConfigService appConfigService;

    // 个别应用需要获取到特定的第三方返回结果
    private static Map<Integer, Map<String, String[]>> SPECIAL_PARAMS_MAPPING = Maps.newHashMap();

    static {
        Map gameMap = Maps.newHashMap();
        gameMap.put(AccountTypeEnum.getProviderStr(AccountTypeEnum.QQ.getValue()), new String[]{"pf", "pfkey", "pay_token"});
        SPECIAL_PARAMS_MAPPING.put(2021, gameMap);
    }

    //登陆后获取登录信息接口
    @RequestMapping("/afterauth/{providerStr}")
    @ResponseBody
    public String sso_afterauth(HttpServletRequest req, HttpServletResponse res,
                                @PathVariable("providerStr") String providerStr,
                                AfterAuthParams params) {
        Result result = new APIResultSupport(false);
        try {
            //参数验证
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            //验证code是否有效
            result = checkCodeIsCorrect(params, req);
            if (!result.isSuccess()) {
                return result.toString();
            }
            result = sSOAfterauthManager.handleSSOAfterauth(req, providerStr);
            buildSpecialResultParams(req, result, params.getClient_id(), providerStr);
            return result.toString();
        } finally {
            String uidStr = AccountTypeEnum.generateThirdPassportId(params.getOpenid(), providerStr);
            String userId = StringUtils.defaultIfEmpty((String) result.getModels().get("userid"), uidStr);
            UserOperationLog userOperationLog = new UserOperationLog(userId, req.getRequestURI(), String.valueOf(params.getClient_id()), result.getCode(), getIp(req));
            UserOperationLogUtil.log(userOperationLog);
        }
    }

    //openid+ client_id +access_token+expires_in+isthird +instance_id+ client _secret
    private Result checkCodeIsCorrect(AfterAuthParams params, HttpServletRequest req) {
        Result result = new APIResultSupport(false);

        AppConfig appConfig = appConfigService.queryAppConfigByClientId(params.getClient_id());
        if (appConfig != null) {
            String secret = appConfig.getClientSecret();

            TreeMap map = new TreeMap();
            map.put("openid", params.getOpenid());
            map.put("access_token", params.getAccess_token());
            map.put("expires_in", Long.toString(params.getExpires_in()));
            map.put("client_id", Integer.toString(params.getClient_id()));
            //处理默认值方式
            Object isthird = req.getParameterMap().get("isthird");
            if (isthird != null) {
                map.put("isthird", Integer.toString(params.getIsthird()));
            }
            String refresh_token = String.valueOf(req.getParameterMap().get("refresh_token"));
            if (!Strings.isNullOrEmpty(String.valueOf(refresh_token))|| !"null".equals(refresh_token)) {
                map.put("refresh_token", params.getRefresh_token());
            }
            map.put("instance_id", params.getInstance_id());
            String appidType = req.getParameter("appid_type");
            if (!Strings.isNullOrEmpty(appidType)) {
                map.put("appid_type", appidType);
            }
            //计算默认的code
            String code = "";
            try {
                code = SignatureUtils.generateSignature(map, secret);
            } catch (Exception e) {
                logger.error("calculate default code error", e);
            }

            if (code.equalsIgnoreCase(params.getCode())) {
                result.setSuccess(true);
                result.setMessage("接口code签名正确！");
            } else {
                result.setCode(ErrorUtil.INTERNAL_REQUEST_INVALID);
            }
        } else {
            result.setCode(ErrorUtil.INVALID_CLIENTID);
        }
        return result;
    }

    /*
     * 根据应用需要，构建应用
     */
    private void buildSpecialResultParams(HttpServletRequest req, Result result, int clientId, String providerStr) {
        if (result.isSuccess()) {
            Map<String, String[]> map = SPECIAL_PARAMS_MAPPING.get(clientId);
            if (!map.isEmpty()) {
                String[] paramArray = map.get(providerStr);
                for (String param : paramArray) {
                    String reqParamValue = req.getParameter(param);
                    if (!Strings.isNullOrEmpty(reqParamValue)) {
                        result.setDefaultModel(param, reqParamValue);
                    }
                }
            }
        }
        return;
    }
}
