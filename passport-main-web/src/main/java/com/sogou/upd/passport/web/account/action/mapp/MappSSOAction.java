package com.sogou.upd.passport.web.account.action.mapp;

import com.google.common.base.Strings;

import com.sogou.upd.passport.common.LoginConstant;
import com.sogou.upd.passport.common.math.AES;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.CheckManager;
import com.sogou.upd.passport.manager.account.MappSSOManager;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.api.connect.SessionServerManager;
import com.sogou.upd.passport.model.mobileoperation.TerminalAttribute;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.account.form.mapp.MappCheckSSOAppParams;
import com.sogou.upd.passport.web.account.form.mapp.MappSSOSwapSidParams;

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
 * User: nahongxu
 * Date: 15-2-15
 * Time: 下午4:56
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "/mapp/sso")
public class MappSSOAction extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(MappSSOAction.class);

    @Autowired
    private SessionServerManager sessionServerManager;

    @Autowired
    private CheckManager checkManager;

    @Autowired
    private UserInfoApiManager sgUserInfoApiManager;

    @Autowired
    private MappSSOManager mappSSOManager;

    @RequestMapping(value = "/checkapp", method = RequestMethod.POST)
    @ResponseBody
    public String checkSSOApp(HttpServletRequest request, MappCheckSSOAppParams params) throws Exception {

        Result result = new APIResultSupport(false);
        int clientId = params.getClient_id();
        String ip = getIp(request);
        String packageSign = params.getSign();
        long ct = params.getCt();
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
            TerminalAttribute attributeDO = new TerminalAttribute(request);
            udid = attributeDO.getUdid();

            //解析packageSign
            result = mappSSOManager.checkAppPackageSign(clientId, ct, packageSign, udid);

        } catch (Exception e) {
            logger.error("mapp check SSO APP error," + "udid:" + udid);
            result.setSuccess(false);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        } finally {
            //记录useroperation
            UserOperationLog userOperationLog = new UserOperationLog(udid, String.valueOf(clientId), result.getCode(), ip);
            UserOperationLogUtil.log(userOperationLog);
        }

        return result.toString();

    }

    @RequestMapping(value = "/swapsgid", method = RequestMethod.POST)
    @ResponseBody
    public String swapsid(HttpServletRequest request, MappSSOSwapSidParams params) throws Exception {

        Result result = new APIResultSupport(false);
        int clientId = params.getClient_id();
        String ip = getIp(request);
        String stoken = params.getStoken();
        long ct = params.getCt();
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
            TerminalAttribute attributeDO = new TerminalAttribute(request);
            udid = attributeDO.getUdid();

            //解析packageSign
            Result getOldSidResult = mappSSOManager.getOldSgid(clientId, stoken, udid, ct);
            String oldSgid = "";
            String token = "";
            if (getOldSidResult.isSuccess()) {
                oldSgid = (String) getOldSidResult.getModels().get(LoginConstant.SSO_OLD_SID);
                token = (String) getOldSidResult.getModels().get(LoginConstant.SSO_TOKEN);

            } else {
                result.setCode(getOldSidResult.getCode());
                return result.toString();
            }

            //校验sgid
            String passportId = "";
            Result verifySidResult = sessionServerManager.getPassportIdBySgid(oldSgid, ip);
            if (verifySidResult.isSuccess()) {
                passportId = (String) verifySidResult.getModels().get("passport_id");
            } else {
                result.setCode(ErrorUtil.ERR_CODE_SSO_APP_NOT_LOGIN);
                return result.toString();
            }

            //生成、加密新的sgid
            Result createSidResult = sessionServerManager.createSession(passportId);
            if (!createSidResult.isSuccess()) {
                return createSidResult.toString();
            }

            //查询用户信息
            if(Strings.isNullOrEmpty(passportId)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CHECKLOGIN_FAILED);
                return result.toString();
            }
            GetUserInfoApiparams getUserinfoParams = buildGetUserInfoApiparams(params, passportId);
            result = sgUserInfoApiManager.getUserInfo(getUserinfoParams);

            String newSgid = (String) createSidResult.getModels().get(LoginConstant.COOKIE_SGID);
            String newSgidEncryped = AES.encryptSSO(newSgid, token);
            result.setDefaultModel(LoginConstant.SSO_NEW_SID, newSgidEncryped);
            result.setSuccess(true);
            result.setMessage("操作成功");


        } catch (Exception e) {
            logger.error("mapp SSO swap sgid error," + "udid:" + udid);
            result.setSuccess(false);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        } finally {
            //记录useroperation
            UserOperationLog userOperationLog = new UserOperationLog(udid, String.valueOf(clientId), result.getCode(), ip);
            UserOperationLogUtil.log(userOperationLog);
        }

        return result.toString();
    }

    private GetUserInfoApiparams buildGetUserInfoApiparams(MappSSOSwapSidParams params, String passportId) {
        GetUserInfoApiparams infoApiparams = new GetUserInfoApiparams();
        //设置默认fields
        String defaultFields="uniqname,gender,avatarurl,uid";
        infoApiparams.setFields(defaultFields);
        infoApiparams.setImagesize("30,50,180");
        infoApiparams.setUserid(passportId);
        infoApiparams.setClient_id(params.getClient_id());

        return infoApiparams;
    }
}
