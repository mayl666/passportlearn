package com.sogou.upd.passport.web.internal.account;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.SecureManager;
import com.sogou.upd.passport.manager.api.account.form.BaseResetPwdApiParams;
import com.sogou.upd.passport.manager.form.UserNamePwdMappingParams;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.annotation.InterfaceSecurity;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-7
 * Time: 上午10:56
 */
@Controller
@RequestMapping("/internal/security")
public class SecureApiController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(SecureApiController.class);

    @Autowired
    private SecureManager secureManager;

    /**
     * 手机发送短信重置密码
     */
    @RequestMapping(value = "/resetpwd_batch", method = RequestMethod.POST)
    @ResponseBody
    @InterfaceSecurity
    public String resetpwd(HttpServletRequest request, BaseResetPwdApiParams params) throws Exception {
        Result result = new APIResultSupport(false);
        String lists = params.getLists();
        int clientId = params.getClient_id();
        String ip = getIp(request);
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            //判断访问者是否有权限
            if (!isAccessAccept(clientId, request)) {
                result.setCode(ErrorUtil.ACCESS_DENIED_CLIENT);
                return result.toString();
            }
            if (!Strings.isNullOrEmpty(lists)) {
                List<UserNamePwdMappingParams> list = new ObjectMapper().readValue(lists, new TypeReference<List<UserNamePwdMappingParams>>() {
                });
                result = secureManager.resetPwd(list, clientId);
            } else {
                result.setSuccess(true);
                result.setMessage("lists为空");
            }
            return result.toString();
        } catch (Exception e) {
            log.error("Batch resetpwd fail!", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result.toString();
        } finally {
            UserOperationLog userOperationLog = new UserOperationLog(params.getMobile(), String.valueOf(clientId), result.getCode(), ip);
            userOperationLog.putOtherMessage("lists", lists);
            userOperationLog.putOtherMessage("result", result.toString());
            UserOperationLogUtil.log(userOperationLog);
        }
    }

}
