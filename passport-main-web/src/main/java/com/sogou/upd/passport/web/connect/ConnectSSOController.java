package com.sogou.upd.passport.web.connect;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.connect.OAuthAuthLoginManager;
import com.sogou.upd.passport.manager.form.connect.AfterAuthParams;
import com.sogou.upd.passport.service.account.generator.PassportIDGenerator;
import com.sogou.upd.passport.web.BaseConnectController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * User: mayan
 * Date: 14-3-3
 * Time: 下午4:21
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/connect/sso")
public class ConnectSSOController extends BaseConnectController {

    @Autowired
    private OAuthAuthLoginManager oAuthAuthLoginManager;

    // 个别应用需要获取到特定的第三方返回结果
    private static Map<Integer, Map<String, String[]>> SPECIAL_PARAMS_MAPPING = Maps.newHashMap();

    static {
        Map gameMap = Maps.newHashMap();
        gameMap.put(AccountTypeEnum.getProviderStr(AccountTypeEnum.QQ.getValue()), new String[]{"openid", "pf", "pfkey", "pay_token"});
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

            result = oAuthAuthLoginManager.handleSSOAfterauth(req, params, providerStr);
            if (result.isSuccess()) {
                buildSpecialResultParams(req, result, params.getClient_id(), providerStr);
            }
            return result.toString();
        } finally {
            String uidStr = PassportIDGenerator.generator(params.getOpenid(), AccountTypeEnum.getProvider(providerStr));
            String userId = StringUtils.defaultIfEmpty((String) result.getModels().get("userid"), uidStr);
            UserOperationLog userOperationLog = new UserOperationLog(userId, req.getRequestURI(), String.valueOf(params.getClient_id()), result.getCode(), getIp(req));
            UserOperationLogUtil.log(userOperationLog);
        }
    }

    /*
     * 根据应用需要，构建特定返回字段
     */
    private void buildSpecialResultParams(HttpServletRequest req, Result result, int clientId, String providerStr) {
        Map<String, String[]> map = SPECIAL_PARAMS_MAPPING.get(clientId);
        if (map != null && !map.isEmpty()) {
            String[] paramArray = map.get(providerStr);
            if (paramArray != null) {
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
