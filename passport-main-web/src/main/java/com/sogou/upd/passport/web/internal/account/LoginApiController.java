package com.sogou.upd.passport.web.internal.account;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.form.AppAuthTokenApiParams;
import com.sogou.upd.passport.manager.api.account.form.AuthUserApiParams;
import com.sogou.upd.passport.web.ControllerHelper;
import org.springframework.beans.factory.annotation.Autowired;
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
public class LoginApiController {

    @Autowired
    private ConfigureManager configureManager;

    @Autowired
    private LoginApiManager proxyLoginApiManager;

    /**
     * web端校验用户名和密码是否正确
     *
     * @param request
     * @param params
     * @return
     */
    @RequestMapping(value = "/account/authuser", method = RequestMethod.POST)
    @ResponseBody
    public Object webAuthUser(HttpServletRequest request, AuthUserApiParams params) {
        Result result = new APIResultSupport(false);
        // 参数校验
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        // 签名和时间戳校验
        result = configureManager.verifyInternalRequest(params.getUserid(), params.getClient_id(), params.getCt(), params.getCode());
        if (result.isSuccess()) {
            // 调用内部接口
            result = proxyLoginApiManager.webAuthUser(params);
        }

        return result.toString();
    }

    /**
     * 手机应用使用第三方登录完成之后，会通过302重定向的方式将token带给产品的服务器端，
     * 产品的服务器端通过传入userid和token验证用户的合法性，且token具有较长的有效期。
     * TODO 注意，目前接入应用全部是验证token，没有传入userid
     *
     * @return
     */
    @RequestMapping(value = "/account/authtoken", method = RequestMethod.POST)
    @ResponseBody
    public Object appAuthToken(HttpServletRequest request, AppAuthTokenApiParams params) {
        Result result = new APIResultSupport(false);
        // 参数校验
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        // 签名和时间戳校验
        result = configureManager.verifyInternalRequest(params.getToken(), params.getClient_id(), params.getCt(), params.getCode());
        if (result.isSuccess()) {
            // 调用内部接口
            result = proxyLoginApiManager.appAuthToken(params);
        }
        return result.toString();
    }

}
