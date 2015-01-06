package com.sogou.upd.passport.web.internal.connect;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.parameter.HttpMethodEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.SGHttpClient;
import com.sogou.upd.passport.manager.api.account.form.BaseUserApiParams;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.annotation.InterfaceSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: lzy_clement
 * Date: 15-1-5
 * Time: 下午12:17
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "/internal/connect/qq")
public class InternalQQOpenAPiController extends BaseController {

    private static String QQ_FRIENDS_URL = "http://203.195.155.61:80/internal/qq/friends_info";

    @Autowired
    private ConnectApiManager sgConnectApiManager;

    //    @InterfaceSecurity
    @ResponseBody
    @RequestMapping(value = "/get_qqfriends")
    public String get_qqfriends(HttpServletRequest req, BaseUserApiParams params) throws Exception {
        Result result = new APIResultSupport(false);
        String userId = params.getUserid();
        int clientId = params.getClient_id();
        try {
            //参数校验
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            //判断访问者是否有权限
            if (!isAccessAccept(clientId, req)) {
                result.setCode(ErrorUtil.ACCESS_DENIED_CLIENT);
                return result.toString();
            }
            Result obtainTKeyResult = sgConnectApiManager.obtainTKey(userId, clientId);
            if (!obtainTKeyResult.isSuccess()) {
                return obtainTKeyResult.toString();
            }
            String tKey = (String) obtainTKeyResult.getModels().get("tKey");
            if (StringUtil.isEmpty(tKey)) {
                result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
                return result.toString();
            }
            RequestModel requestModel = new RequestModel(QQ_FRIENDS_URL);
            requestModel.addParam("userid", userId);
            requestModel.addParam("tKey", tKey);
            requestModel.setHttpMethodEnum(HttpMethodEnum.POST);
            String str = SGHttpClient.executeStr(requestModel);
            logger.info("test-qqopenapi:" + str);
//            result.setSuccess(true);
//            result.getModels().put("tKey", tKey);
            return result.toString();
            //TODO 使用HTTP协议请求passport腾讯Server获取好友
        } finally {
            //用于记录log
            UserOperationLog userOperationLog = new UserOperationLog(userId, String.valueOf(clientId), result.getCode(), getIp(req));
            UserOperationLogUtil.log(userOperationLog);
        }
    }
}
