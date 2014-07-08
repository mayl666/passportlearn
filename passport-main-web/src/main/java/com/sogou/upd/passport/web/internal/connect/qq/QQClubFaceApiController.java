package com.sogou.upd.passport.web.internal.connect.qq;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.manager.api.connect.QQClubFaceApiManager;
import com.sogou.upd.passport.manager.api.connect.form.qq.QQClubFaceOpenApiParams;
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
 * User: Mayan
 * Date: 14-03-17
 * Time: 下午16:33
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/internal/connect")
public class QQClubFaceApiController extends BaseConnectController {

    private static final Logger logger = LoggerFactory.getLogger(QQClubFaceApiController.class);

    @Autowired
    private QQClubFaceApiManager qqClubFaceApiManager;
    @Autowired
    private ConnectApiManager sgConnectApiManager;

    /**
     * QQ表情同步
     *
     * @param params
     * @return
     */
    @InterfaceSecurity
    @RequestMapping(value = "/qq/clubface", method = RequestMethod.POST)
    @ResponseBody
    public Object getConnectQQClubFaceApi(HttpServletRequest request, QQClubFaceOpenApiParams params) throws Exception {
        Result result = new APIResultSupport(false);
        String resultString = "";
        String passportId = params.getUserid();
        int clientId = params.getClient_id();
        try {
            // 仅支持qq账号调用此接口
            if (AccountTypeEnum.getAccountType(passportId) != AccountTypeEnum.QQ) {
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
            Result openResult = sgConnectApiManager.obtainConnectToken(passportId, clientId);
            resultString = openResult.toString();
            if (openResult.isSuccess()) {
                //获取用户的openId/openKey
                ConnectToken connectToken = (ConnectToken) openResult.getModels().get("connectToken");
                String openId = connectToken.getOpenid();
                String accessToken = connectToken.getAccessToken();
                String resp;
                if (!Strings.isNullOrEmpty(openId) && !Strings.isNullOrEmpty(accessToken)) {
                    resp = qqClubFaceApiManager.executeQQOpenApi(openId, accessToken, params);
                    if (!Strings.isNullOrEmpty(resp)) {
                        resultString = resp;
                        result.setSuccess(true);
                    } else {
                        result.setCode(ErrorUtil.ERR_CODE_CONNECT_FAILED);
                        resultString = result.toString();
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
            logger.error("getConnectQQApi:Get User Info Is Failed,UserId is " + passportId, e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            resultString = result.toString();
        } finally {
            //用户注册log
            String code = result.getCode();
            if (!resultString.contains("\"ret\":0")) {      //以后改，这样硬编码不行
                code = ErrorUtil.CONNECT_USER_DEFINED_ERROR;
            }
            UserOperationLog userOperationLog = new UserOperationLog(passportId, request.getRequestURI(), String.valueOf(clientId), code, getIp(request));
            String referer = request.getHeader("referer");
            userOperationLog.putOtherMessage("ref", referer);
            UserOperationLogUtil.log(userOperationLog);
        }
        return resultString;
    }
}
