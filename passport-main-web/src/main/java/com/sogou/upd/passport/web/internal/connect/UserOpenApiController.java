package com.sogou.upd.passport.web.internal.connect;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.ServletUtil;
import com.sogou.upd.passport.manager.api.connect.UserOpenApiManager;
import com.sogou.upd.passport.manager.api.connect.form.user.UserOpenApiParams;
import com.sogou.upd.passport.web.BaseController;
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
 * User: shipengzhi
 * Date: 13-7-10
 * Time: 下午12:40
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/internal/connect/users")
public class UserOpenApiController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(UserOpenApiController.class);

    @Autowired
    private UserOpenApiManager sgUserOpenApiManager;

    /**
     * 获取第三方账号的个人资料
     *
     * @param params
     * @return
     */
    @InterfaceSecurity
    @RequestMapping(value = "/info", method = RequestMethod.POST)
    @ResponseBody
    public Object getUserInfo(UserOpenApiParams params, HttpServletRequest request) {
        Result result = new APIResultSupport(false);
        try {
            // 参数校验
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            // 调用内部接口
            result =  sgUserOpenApiManager.getUserInfo(params);

        } catch (Exception e) {
            logger.error("getUserInfo:Get User For Internal Is Failed,Userid is " + params.getOpenid(), e);
        } finally {
            //记录log
            if(result.isSuccess()) {
                result.setCode("0");
            }
            UserOperationLog userOperationLog = new UserOperationLog(params.getUserid(), request.getRequestURI(), String.valueOf(params.getClient_id()), result.getCode(), getIp(request));
            userOperationLog.putOtherMessage("param", ServletUtil.getParameterString(request));
            UserOperationLogUtil.log(userOperationLog);
        }

        return result.toString();
    }

}
