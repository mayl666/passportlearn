package com.sogou.upd.passport.web.internal.account;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.manager.proxy.account.BindApiManager;
import com.sogou.upd.passport.manager.proxy.account.form.BaseMoblieApiParams;
import com.sogou.upd.passport.web.ControllerHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Web登录的内部接口
 * User: shipengzhi
 * Date: 13-6-6
 * Time: 下午2:40
 */
@Controller
@RequestMapping("/internal")
public class BindMobileApiController {

    @Autowired
    private ConfigureManager configureManager;

    private BindApiManager proxyBindApiManager;

    @Autowired
    public void setAbstractFlightSearchAO(
            @Qualifier("proxyBindApiManager") BindApiManager proxyBindApiManager) {
        this.proxyBindApiManager = proxyBindApiManager;
    }

    @RequestMapping(value = "/account/mobilegetpid", method = RequestMethod.POST)
    @ResponseBody
    public Object webAuthUser(HttpServletRequest request, BaseMoblieApiParams params) {
        Result result = new APIResultSupport(false);
        // 参数校验
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        // 签名和时间戳校验
        result = configureManager.verifyInternalRequest(params.getMobile(), params.getClient_id(), params.getCt(), params.getCode());
        if (!result.isSuccess()) {
            result.setCode(ErrorUtil.ERR_CODE_COM_SING);
            return result.toString();
        }
        // 调用内部接口
        result = proxyBindApiManager.queryPassportIdByMobile(params);
        return result.toString();
    }

}
