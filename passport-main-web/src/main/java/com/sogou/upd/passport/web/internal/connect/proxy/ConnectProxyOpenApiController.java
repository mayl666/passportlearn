package com.sogou.upd.passport.web.internal.connect.proxy;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.manager.api.connect.ConnectProxyOpenApiManager;
import com.sogou.upd.passport.manager.api.connect.form.BaseOpenApiParams;
import com.sogou.upd.passport.manager.api.connect.form.proxy.ConnectProxyOpenApiParams;
import com.sogou.upd.passport.model.OAuthConsumer;
import com.sogou.upd.passport.model.OAuthConsumerFactory;
import com.sogou.upd.passport.web.BaseConnectController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.annotation.InterfaceSecurity;
import com.sogou.upd.passport.web.internal.connect.OpenApiParamsHelper;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-1-4
 * Time: 下午4:23
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/internal/connect")
public class ConnectProxyOpenApiController extends BaseConnectController {

    private static final Logger logger = LoggerFactory.getLogger(ConnectProxyOpenApiController.class);

    @Autowired
    private ConnectProxyOpenApiManager connectProxyOpenApiManager;
    @Autowired
    private ConnectApiManager proxyConnectApiManager;

    /**
     * 执行第三方开放平台代理接口的调用
     *
     * @param request
     * @param providerStr   第三方类型
     * @param interfaceName 请求调用第三方开放平台的接口
     * @param params        第三方开放平台接口所需参数
     * @return
     * @throws Exception
     */
    @InterfaceSecurity
    @RequestMapping(value = "/{providerStr}/{interfaceName}")
    public Object connectProxyOpenApi(HttpServletRequest request, @NotBlank @PathVariable("providerStr") String providerStr, @NotBlank @PathVariable("interfaceName") String interfaceName, ConnectProxyOpenApiParams params) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            OAuthConsumer oAuthConsumer = OAuthConsumerFactory.getOAuthConsumer(AccountTypeEnum.getProvider(providerStr));
            if (oAuthConsumer == null) {
                result.setCode(ErrorUtil.UNSUPPORT_THIRDPARTY);
                return result;
            }
            if (interfaceName.startsWith("/")) {
                result.setCode(ErrorUtil.ERR_CODE_CONNECT_INTERFACE);
                return result;
            }
            // 参数校验
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            //TODO ACCESS_TOKEN迁移至搜狗数据库后，此方法逻辑需要替换
            //调用搜狐接口，获取QQ token，openid等参数
            BaseOpenApiParams baseOpenApiParams = new BaseOpenApiParams();
            baseOpenApiParams.setUserid(params.getUserid());
            Result openResult = proxyConnectApiManager.obtainConnectTokenInfo(baseOpenApiParams, SHPPUrlConstant.APP_ID, SHPPUrlConstant.APP_KEY);
            if (openResult.isSuccess()) {
                //获取用户的openId/openKey
                Map<String, String> accessTokenMap = (Map<String, String>) openResult.getModels().get("result");
                String openId = accessTokenMap.get("open_id").toString();
                String accessToken = accessTokenMap.get("access_token").toString();
                if (!Strings.isNullOrEmpty(openId) && !Strings.isNullOrEmpty(accessToken)) {
                    result = connectProxyOpenApiManager.handleConnectOpenApi(openId, accessToken, providerStr, interfaceName, params);
                } else {
                    result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
                }
            }
        } catch (Exception e) {
            logger.error("connectProxyOpenApi Is Failed,UserId is " + params.getUserid(), e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        } finally {
            UserOperationLog userOperationLog = new UserOperationLog(params.getUserid(), request.getRequestURI(), String.valueOf(params.getClient_id()), result.getCode(), getIp(request));
            String referer = request.getHeader("referer");
            userOperationLog.putOtherMessage("ref", referer);
            userOperationLog.putOtherMessage("connectResult", result.toString());
            UserOperationLogUtil.log(userOperationLog);
        }
        return result;
    }

}
