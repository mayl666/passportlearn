package com.sogou.upd.passport.web.internal.connect.qq;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.manager.api.connect.QQLightOpenApiManager;
import com.sogou.upd.passport.manager.api.connect.form.BaseOpenApiParams;
import com.sogou.upd.passport.manager.api.connect.form.qq.QQLightOpenApiParams;
import com.sogou.upd.passport.model.connect.ConnectToken;
import com.sogou.upd.passport.web.BaseConnectController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.annotation.InterfaceSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

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
        String resultString = "";
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
            BaseOpenApiParams baseOpenApiParams = new BaseOpenApiParams();
            baseOpenApiParams.setUserid(params.getUserid());
            baseOpenApiParams.setOpenid(params.getOpenid());
            Result openResult = sgConnectApiManager.obtainConnectToken(baseOpenApiParams, SHPPUrlConstant.APP_ID, SHPPUrlConstant.APP_KEY);
            resultString = openResult.toString();
            if (openResult.isSuccess()) {
                //获取用户的openId/openKey
                ConnectToken connectToken = (ConnectToken) openResult.getModels().get("connectToken");
                String openId = connectToken.getOpenid();
                String accessToken = connectToken.getAccessToken();
                String resp;
                if (!Strings.isNullOrEmpty(openId) && !Strings.isNullOrEmpty(accessToken)) {
                    resp = sgQQLightOpenApiManager.executeQQOpenApi(openId, accessToken, params);
                    if (!Strings.isNullOrEmpty(resp)) {
                        resultString = resp;
                        result.setSuccess(true);
                    }
                } else {
                    result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
                    resultString = result.toString();
                }
            } else {
                result = openResult;
                resultString = result.toString();
            }
        } catch (Exception e) {
            logger.error("getConnectQQApi:Get User Info Is Failed,UserId is " + params.getUserid(), e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            resultString = result.toString();
        } finally {
            //用户注册log
            String code = result.getCode();
            if (!resultString.contains("\"ret\":0")) {      //以后改，这样硬编码不行
                code = ErrorUtil.CONNECT_USER_DEFINED_ERROR;
            }
            UserOperationLog userOperationLog = new UserOperationLog(params.getUserid(), request.getRequestURI(), String.valueOf(params.getClient_id()), code, getIp(request));
            String referer = request.getHeader("referer");
            userOperationLog.putOtherMessage("ref", referer);
            userOperationLog.putOtherMessage("qqResult", resultString);
            UserOperationLogUtil.log(userOperationLog);
        }
        return resultString;
    }
}
