package com.sogou.upd.passport.web.internal.connect.qq;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.manager.api.connect.QQLightOpenApiManager;
import com.sogou.upd.passport.manager.api.connect.form.BaseOpenApiParams;
import com.sogou.upd.passport.manager.api.connect.form.qq.QQLightOpenApiParams;
import com.sogou.upd.passport.web.BaseConnectController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.annotation.InterfaceSecurity;
import com.sogou.upd.passport.web.internal.connect.OpenApiParamsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-11-28
 * Time: 上午10:08
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/internal/connect")
public class QQLightOpenApiController extends BaseConnectController {

    private static final Logger logger = LoggerFactory.getLogger(QQLightOpenApiController.class);

    @Autowired
    private QQLightOpenApiManager sgQQLightOpenApiManager;
    @Autowired
    private ConnectApiManager sgConnectApiManager;

    /**
     * 根据用户信息，实现qq图标点亮
     *
     * @param params
     * @return
     */
    @InterfaceSecurity
    @RequestMapping(value = "/qq/light", method = RequestMethod.POST)
    @ResponseBody
    public Object getConnectQQApi(HttpServletRequest request, QQLightOpenApiParams params) throws Exception {
        Result result = new APIResultSupport(false);
        String resultString = null;
        String finalLogCode = null;
        try {
            // 仅支持qq账号调用此接口
            String openIdStr = params.getOpenid();
            String userIdStr = params.getUserid();
            if (AccountTypeEnum.getAccountType(openIdStr) != AccountTypeEnum.QQ || AccountTypeEnum.getAccountType(userIdStr) != AccountTypeEnum.QQ) {
                result.setCode(ErrorUtil.ERR_CODE_CONNECT_NOT_SUPPORTED);
                return result.toString();
            }

            // 参数校验
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            //调用sohu接口，获取QQ token，openid等参数
            BaseOpenApiParams baseOpenApiParams = new OpenApiParamsHelper().createQQConnectParams(params);
            Result openResult = sgConnectApiManager.obtainConnectTokenInfo(baseOpenApiParams, SHPPUrlConstant.APP_ID, SHPPUrlConstant.APP_KEY);
            resultString = openResult.toString();
            if (openResult.isSuccess()) {
                //获取用户的openId/openKey
                Map<String, String> accessTokenMap = (Map<String, String>) openResult.getModels().get("result");
                String openId = accessTokenMap.get("open_id").toString();
                String accessToken = accessTokenMap.get("access_token").toString();
                String resp = sgQQLightOpenApiManager.executeQQOpenApi(openId, accessToken, params);
                resultString = resp;
                result.setCode("0");
                result.setSuccess(true);
                if (!Strings.isNullOrEmpty(resp)) {
                    Map<String, Object> logMap = JacksonJsonMapperUtil.getMapper().readValue(resp, Map.class);
                    if (!CollectionUtils.isEmpty(logMap)) {
                        if (logMap.containsKey("ret")) {
                            finalLogCode = logMap.get("ret").toString().trim();
                        }
                    }
                }
            } else {
                result = openResult;
            }
        } catch (Exception e) {
            logger.error("getConnectQQApi:Get User Info Is Failed,UserId is " + params.getUserid(), e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            resultString = result.toString();
        } finally {
            //用户注册log
            String logCode;
            if (!Strings.isNullOrEmpty(finalLogCode)) {
                logCode = finalLogCode;
            } else {
                logCode = result.getCode();
            }
            UserOperationLog userOperationLog = new UserOperationLog(params.getUserid(), request.getRequestURI(), String.valueOf(params.getClient_id()), logCode, getIp(request));
            String referer = request.getHeader("referer");
            userOperationLog.putOtherMessage("ref", referer);
            userOperationLog.putOtherMessage("qqResult", resultString);
            UserOperationLogUtil.log(userOperationLog);
        }
        return resultString;
    }
}
