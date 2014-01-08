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
import com.sogou.upd.passport.web.BaseConnectController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.annotation.InterfaceSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
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
     * 获取用户QQ空间未读消息数
     *
     * @param request
     * @param params  第三方开放平台接口所需参数
     * @return
     * @throws Exception
     */
    @InterfaceSecurity
    @RequestMapping(value = "/qq/user/qzone/unread_num", method = RequestMethod.POST)
    @ResponseBody
    public Object qzoneConnectProxyOpenApi(HttpServletRequest request, ConnectProxyOpenApiParams params) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            // 仅支持qq账号调用此接口
            String userIdStr = params.getUserid();
            if (AccountTypeEnum.getAccountType(userIdStr) != AccountTypeEnum.QQ) {
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
            String url = request.getRequestURI();
            //调用搜狐接口，获取QQ token，openid等参数
            BaseOpenApiParams baseOpenApiParams = new BaseOpenApiParams();
            baseOpenApiParams.setUserid(params.getUserid());
            baseOpenApiParams.setOpenid(params.getUserid());
            Result openResult = proxyConnectApiManager.obtainConnectTokenInfo(baseOpenApiParams, SHPPUrlConstant.APP_ID, SHPPUrlConstant.APP_KEY);
            if (openResult.isSuccess()) {
                //获取用户的openId/openKey
                Map<String, String> tokenMap = (Map<String, String>) openResult.getModels().get("result");
                if (!CollectionUtils.isEmpty(tokenMap)) {
                    result = connectProxyOpenApiManager.handleConnectOpenApi(url, tokenMap, null);
                }
            } else {
                result = openResult;
            }
        } catch (Exception e) {
            logger.error("qzoneConnectProxyOpenApi Is Failed,UserId is " + params.getUserid(), e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        } finally {
            UserOperationLog userOperationLog = new UserOperationLog(params.getUserid(), request.getRequestURI(), String.valueOf(params.getClient_id()), result.getCode(), getIp(request));
            String referer = request.getHeader("referer");
            userOperationLog.putOtherMessage("ref", referer);
            userOperationLog.putOtherMessage("connectResult", result.toString());
            UserOperationLogUtil.log(userOperationLog);
        }
        return result.toString();
    }


    /**
     * 获取用户QQ微博未读消息数
     *
     * @param request
     * @param params  第三方开放平台接口所需参数
     * @return
     * @throws Exception
     */
    @InterfaceSecurity
    @RequestMapping(value = "/qq/user/weibo/unread_num", method = RequestMethod.POST)
    @ResponseBody
    public Object weiboConnectProxyOpenApi(HttpServletRequest request, ConnectProxyOpenApiParams params) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            // 仅支持qq账号调用此接口
            String userIdStr = params.getUserid();
            if (AccountTypeEnum.getAccountType(userIdStr) != AccountTypeEnum.QQ) {
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
            String url = request.getRequestURI();
            //调用搜狐接口，获取QQ token，openid等参数
            BaseOpenApiParams baseOpenApiParams = new BaseOpenApiParams();
            baseOpenApiParams.setUserid(params.getUserid());
            baseOpenApiParams.setOpenid(params.getUserid());
            Result openResult = proxyConnectApiManager.obtainConnectTokenInfo(baseOpenApiParams, SHPPUrlConstant.APP_ID, SHPPUrlConstant.APP_KEY);
            if (openResult.isSuccess()) {
                //获取用户的openId/openKey
                Map<String, String> tokenMap = (Map<String, String>) openResult.getModels().get("result");
                HashMap<String, Object> paramMap = new HashMap<>();
                paramMap.put("pf", "tapp");
                if (!CollectionUtils.isEmpty(tokenMap)) {
                    result = connectProxyOpenApiManager.handleConnectOpenApi(url, tokenMap, paramMap);
                }
            } else {
                result = openResult;
            }
        } catch (Exception e) {
            logger.error("weiboConnectProxyOpenApi Is Failed,UserId is " + params.getUserid(), e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        } finally {
            UserOperationLog userOperationLog = new UserOperationLog(params.getUserid(), request.getRequestURI(), String.valueOf(params.getClient_id()), result.getCode(), getIp(request));
            String referer = request.getHeader("referer");
            userOperationLog.putOtherMessage("ref", referer);
            userOperationLog.putOtherMessage("connectResult", result.toString());
            UserOperationLogUtil.log(userOperationLog);
        }
        return result.toString();
    }


    /**
     * 获取用户QQ邮箱未读消息数
     *
     * @param request
     * @param params  第三方开放平台接口所需参数
     * @return
     * @throws Exception
     */
    @InterfaceSecurity
    @RequestMapping(value = "/qq/user/mail/unread_num", method = RequestMethod.POST)
    @ResponseBody
    public Object mailConnectProxyOpenApi(HttpServletRequest request, ConnectProxyOpenApiParams params) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            // 仅支持qq账号调用此接口
            String userIdStr = params.getUserid();
            if (AccountTypeEnum.getAccountType(userIdStr) != AccountTypeEnum.QQ) {
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
            String url = request.getRequestURI();
            //调用搜狐接口，获取QQ token，openid等参数
            BaseOpenApiParams baseOpenApiParams = new BaseOpenApiParams();
            baseOpenApiParams.setUserid(params.getUserid());
            baseOpenApiParams.setOpenid(params.getUserid());
            Result openResult = proxyConnectApiManager.obtainConnectTokenInfo(baseOpenApiParams, SHPPUrlConstant.APP_ID, SHPPUrlConstant.APP_KEY);
            if (openResult.isSuccess()) {
                //获取用户的openId/openKey
                Map<String, String> tokenMap = (Map<String, String>) openResult.getModels().get("result");
                if (!CollectionUtils.isEmpty(tokenMap)) {
                    result = connectProxyOpenApiManager.handleConnectOpenApi(url, tokenMap, null);
                }
            } else {
                result = openResult;
            }
        } catch (Exception e) {
            logger.error("mailConnectProxyOpenApi Is Failed,UserId is " + params.getUserid(), e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        } finally {
            UserOperationLog userOperationLog = new UserOperationLog(params.getUserid(), request.getRequestURI(), String.valueOf(params.getClient_id()), result.getCode(), getIp(request));
            String referer = request.getHeader("referer");
            userOperationLog.putOtherMessage("ref", referer);
            userOperationLog.putOtherMessage("connectResult", result.toString());
            UserOperationLogUtil.log(userOperationLog);
        }
        return result.toString();
    }

}
