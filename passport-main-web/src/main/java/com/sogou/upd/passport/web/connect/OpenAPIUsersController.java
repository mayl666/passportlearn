package com.sogou.upd.passport.web.connect;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.manager.connect.OpenAPIUsersManager;
import com.sogou.upd.passport.manager.form.ConnectClientObtainParams;
import com.sogou.upd.passport.manager.form.ConnectObtainParams;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.service.account.generator.InspectSecureSignForT3;
import com.sogou.upd.passport.web.BaseConnectController;
import com.sogou.upd.passport.web.ControllerHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-22
 * Time: 上午12:54
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class OpenAPIUsersController extends BaseConnectController {

    @Autowired
    private OpenAPIUsersManager openAPIUsersManager;
    @Autowired
    private ConfigureManager configureManager;

    @RequestMapping(value = "/v2/connect/users/getopenid", method = RequestMethod.GET)
    @ResponseBody
    public Object getopenid(HttpServletRequest request, ConnectObtainParams reqParams) throws Exception {

        //参数验证
        String validateResult = ControllerHelper.validateParams(reqParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            return Result.buildError(ErrorUtil.ERR_CODE_COM_REQURIE, validateResult);
        }

        int clientId = reqParams.getClient_id();
        String passportId = reqParams.getPassport_id();
        String providerStr = reqParams.getProvider();
        int provider = AccountTypeEnum.getProvider(providerStr);

        Result result = openAPIUsersManager.obtainOpenIdByPassportId(passportId, clientId, provider);

        return result;
    }

    @RequestMapping(value = "/connect/users/info", method = RequestMethod.GET)
    @ResponseBody
    public Object obtainConnectInfo(ConnectClientObtainParams reqParams) throws Exception {

        //参数验证
        String validateResult = ControllerHelper.validateParams(reqParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            return Result.buildError(ErrorUtil.ERR_CODE_COM_REQURIE, validateResult);
        }

        int clientId = reqParams.getClient_id();
        String accessToken = reqParams.getAccess_token();
        String providerStr = reqParams.getProvider();
        int provider = AccountTypeEnum.getProvider(providerStr);

        Result result;
        ConnectConfig connectConfig = configureManager.obtainConnectConfig(clientId, provider);
        if (connectConfig != null) {
            result = openAPIUsersManager.obtainUserInfo(accessToken, provider, connectConfig);
        } else {
            result = Result.buildError(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }
}
