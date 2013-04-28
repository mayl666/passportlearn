package com.sogou.upd.passport.web.connect;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.connect.OpenAPIUsersManager;
import com.sogou.upd.passport.web.BaseConnectController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.manager.form.ConnectObtainParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-22
 * Time: 上午12:54
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/v2/connect")
public class OpenAPIUsersController extends BaseConnectController{

    @Autowired
    private OpenAPIUsersManager openAPIUsersManager;

    @RequestMapping(value = "/users/getopenid", method = RequestMethod.GET)
    @ResponseBody
    public Object getopenid(ConnectObtainParams reqParams) throws Exception {

        //参数验证
        String validateResult = ControllerHelper.validateParams(reqParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            return Result.buildError(ErrorUtil.ERR_CODE_COM_REQURIE, validateResult);
        }

        int clientId = reqParams.getClient_id();
        String passportId = reqParams.getPassport_id();
        String providerStr = reqParams.getProvider();
        int provider = AccountTypeEnum.getProvider(providerStr);

        Result result = openAPIUsersManager.getOpenIdByPassportId(passportId, clientId, provider);

        return result;
    }
}
