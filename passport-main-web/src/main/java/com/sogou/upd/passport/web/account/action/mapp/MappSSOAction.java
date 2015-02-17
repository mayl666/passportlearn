package com.sogou.upd.passport.web.account.action.mapp;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.CheckManager;
import com.sogou.upd.passport.manager.account.MappSSOManager;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.connect.SessionServerManager;
import com.sogou.upd.passport.model.mobileoperation.TerminalAttribute;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.account.form.mapp.MappCheckSSOAppParams;
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
            //验证code是否有效
            boolean isVaildCode = checkManager.checkMappCode(udid, clientId, ct, params.getCode());
            if (!isVaildCode) {
                result.setCode(ErrorUtil.INTERNAL_REQUEST_INVALID);
                return result.toString();
            }

            //解析packageSign
            result = mappSSOManager.checkAppPackageSign(clientId, ct, packageSign, udid);

        } catch (Exception e) {
            logger.error("mapp check SSO APP error," + "udid:" + udid);
        } finally {
            //记录useroperation
            UserOperationLog userOperationLog = new UserOperationLog(udid, String.valueOf(clientId), result.getCode(), ip);
            UserOperationLogUtil.log(userOperationLog);
        }

        return result.toString();

    }


}
