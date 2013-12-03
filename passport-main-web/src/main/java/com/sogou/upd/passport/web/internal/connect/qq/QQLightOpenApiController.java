package com.sogou.upd.passport.web.internal.connect.qq;

import com.google.common.base.Strings;
import com.qq.open.OpenApiV3;
import com.qq.open.OpensnsException;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.connect.QQLightOpenApiManager;
import com.sogou.upd.passport.manager.api.connect.form.BaseOpenApiParams;
import com.sogou.upd.passport.manager.api.connect.form.qq.QQLightOpenApiParams;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.annotation.InterfaceSecurity;
import com.sogou.upd.passport.web.internal.connect.OpenApiParamsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
public class QQLightOpenApiController {

    private static final Logger logger = LoggerFactory.getLogger(QQLightOpenApiController.class);

    @Autowired
    private QQLightOpenApiManager proxyQQLightOpenApiManager;

    /**
     * 根据用户信息，实现qq图标点亮
     *
     * @param params
     * @return
     */
    @InterfaceSecurity
    @RequestMapping(value = "/qq/light", method = RequestMethod.POST)
    @ResponseBody
    public Object getConnectQQApi(HttpServletRequest request, QQLightOpenApiParams params) throws OpensnsException {
        Result result = new APIResultSupport(false);
        String resultString = "";
        try {
            // 参数校验
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                resultString = result.toString();
                return resultString;
            }
            //调用sohu接口，获取QQ token，openid等参数
            BaseOpenApiParams baseOpenApiParams = new OpenApiParamsHelper().createQQConnectParams(params);
            result = proxyQQLightOpenApiManager.getProxyConnectUserInfo(baseOpenApiParams, SHPPUrlConstant.APP_ID, SHPPUrlConstant.APP_KEY);
            if (!result.isSuccess()) {
                //使用浏览器clientid=1044查询老用户的搜狐token
                BaseOpenApiParams baseOpenApiIEParams = new OpenApiParamsHelper().createQQConnectParams(params);
                result = proxyQQLightOpenApiManager.getProxyConnectUserInfo(baseOpenApiIEParams, SHPPUrlConstant.IE_CONNECT_APP_ID, SHPPUrlConstant.IE_CONNECT_APP_KEY);
            }
            resultString = result.toString();
            if (result.isSuccess()) {
                //获取用户的openId/openKey
                Map<String, String> accessTokenMap = (Map<String, String>) result.getModels().get("result");
                String openId = accessTokenMap.get("open_id").toString();
                String accessToken = accessTokenMap.get("access_token").toString();
                //测试可用
//                String openId = "94BE926A9FD3261C4F4045031A3C7966";
//                String accessToken = "9BC2B0AAF5D06C9474DC01F13D153A45";
                //应用的基本信息，搜狗在QQ的第三方appid与appkey
                String appkey = CommonConstant.APP_CONNECT_KEY;     //搜狗在QQ的appkey
                String appsecret = CommonConstant.APP_CONNECT_SECRET; //搜狗在QQ的appsecret
//                String appkey = "200034";
//                String appsecret = "8c0116a88d3b5ce01f25d69a376f381f";
                //QQ提供的openapi服务器
                String serverName = CommonConstant.QQ_SERVER_NAME;
                OpenApiV3 sdk = new OpenApiV3(appkey, appsecret);
                sdk.setServerName(serverName);
                //调用代理第三方接口，点亮或熄灭QQ图标
                String resp = proxyQQLightOpenApiManager.executeQQOpenApi(sdk, openId, accessToken, params);
                resultString = resp;
            }
        } catch (Exception e) {
            logger.error("getConnectQQApi:Get User Info Is Failed,UserId is " + params.getUserid(), e);
        } finally {
            //用户注册log
            UserOperationLog userOperationLog = new UserOperationLog(params.getUserid(), request.getRequestURI(), String.valueOf(params.getClient_id()), result.getCode(), "");
            String referer = request.getHeader("referer");
            userOperationLog.putOtherMessage("ref", referer);
            UserOperationLogUtil.log(userOperationLog);
        }
        return resultString;
    }
}
