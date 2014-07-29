package com.sogou.upd.passport.web.account.api;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.AccountRoamManager;
import com.sogou.upd.passport.manager.form.WebRoamParams;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.inteceptor.HostHolder;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 支持web端搜狗域、搜狐域、第三方账号漫游
 * User: chengang
 * Date: 14-7-29
 * Time: 下午4:28
 */
@Controller
public class WebRoamController extends BaseController {


    private static final Logger LOGGER = LoggerFactory.getLogger(WebRoamController.class);

    @Autowired
    private AccountRoamManager accountRoamManager;

    @Autowired
    private HostHolder hostHolder;


    @ResponseBody
    @RequestMapping(value = "/sso/web_roam", method = RequestMethod.POST)
    public void webRoam(HttpServletRequest request, HttpServletResponse response, WebRoamParams webRoamParams) throws Exception {
        Result result = new APIResultSupport(false);

        try {
            //参数验证
            String validateResult = ControllerHelper.validateParams(webRoamParams);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                returnErrMsg(response, webRoamParams.getRu(), result.getCode(), result.getMessage());
                return;
            }

            String sgLgUserId = StringUtils.EMPTY;
            if (hostHolder.isLogin()) {
                sgLgUserId = hostHolder.getPassportId();
            }

            result = accountRoamManager.webRoam(response, hostHolder.isLogin(), sgLgUserId, webRoamParams.getR_key(), webRoamParams.getRu(), Integer.parseInt(webRoamParams.getClient_id()));
            if (result.isSuccess()) {
                if (!Strings.isNullOrEmpty(webRoamParams.getRu())) {
                    response.sendRedirect(webRoamParams.getRu());
                    return;
                }
            }
        } catch (Exception e) {
            LOGGER.error(" web_roam error.userId:{},r_key:{}", result.getModels().get("userId"), webRoamParams.getR_key(), e);
        } finally {
            String resultCode = StringUtils.defaultIfEmpty(result.getCode(), "0");
            String userId = StringUtils.defaultString(String.valueOf(result.getModels().get("userId")));
            String createIp = StringUtils.defaultString(String.valueOf(result.getModels().get("createIp")));

            //记录用户操作日志
            UserOperationLog userOperationLog = new UserOperationLog(userId, request.getRequestURI(), webRoamParams.getClient_id(), resultCode, createIp);
            userOperationLog.putOtherMessage("ref", request.getHeader("referer"));
            UserOperationLogUtil.log(userOperationLog);
        }
    }

}
