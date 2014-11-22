package com.sogou.upd.passport.web.account.action.mapp;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.CheckManager;
import com.sogou.upd.passport.manager.api.connect.SessionServerManager;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.account.form.mapp.MappLogoutParams;
import com.sogou.upd.passport.web.account.form.mapp.MappStatReportParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 手机APP相关接口
 * User: mayan
 * Date: 14-3-3
 * Time: 下午3:46
 */
@Controller
@RequestMapping(value = "/mapp")
public class MappAction extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(MappAction.class);

    @Autowired
    private SessionServerManager sessionServerManager;
    @Autowired
    private CheckManager checkManager;

    @RequestMapping(value = {"/logout"})
    @ResponseBody
    public String logout(HttpServletRequest request, MappLogoutParams params) throws Exception {
        // 校验参数
        Result result = new APIResultSupport(false);
        String sgid = params.getSgid();
        String clientId = params.getClient_id();
        String code = params.getCode();
        String instanceId = params.getInstance_id();
        try {
            //参数验证
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            //验证code是否有效
            result = checkManager.checkMappLogoutCode(sgid, clientId, instanceId, code);
            if (!result.isSuccess()) {
                return result.toString();
            }
            //session server中清除cookie
            result = sessionServerManager.removeSession(sgid);
            if (result.isSuccess()) {
                result.setSuccess(true);
                result.setMessage("logout success!");
                return result.toString();
            }
        } catch (Exception e) {
            logger.error("logout " + "sgid:" + sgid + ",client_id:" + clientId);
        } finally {
            //用于记录log
            UserOperationLog userOperationLog = new UserOperationLog(sgid, clientId, result.getCode(), getIp(request));
            UserOperationLogUtil.log(userOperationLog);
        }
        return result.toString();
    }

    @RequestMapping(value = {"/stat/report"})
    @ResponseBody
    public String dataStat(HttpServletRequest request, MappStatReportParams params) throws Exception {
        // 校验参数
        Result result = new APIResultSupport(false);
        int clientId = params.getClient_id();
        String code = params.getCode();
        long ct = params.getCt();
        String ip = getIp(request);
        String udid = "";
        try {
            //参数验证
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            //解析cinfo信息
            TerminalAttributeDO attributeDO = new TerminalAttributeDO(request);
            udid = attributeDO.getUdid();
            //验证code是否有效
            boolean isVaildCode = checkManager.checkMappCode(udid, clientId, ct, code);
            if (!isVaildCode) {
                return result.toString();
            }
            //session server中清除cookie


        } catch (Exception e) {
            logger.error("mapp stat report error," + "udid:" + udid);
        } finally {
            //用于记录log
            UserOperationLog userOperationLog = new UserOperationLog(udid, String.valueOf(clientId), result.getCode(), ip);
            UserOperationLogUtil.log(userOperationLog);
        }
        return result.toString();
    }
}
