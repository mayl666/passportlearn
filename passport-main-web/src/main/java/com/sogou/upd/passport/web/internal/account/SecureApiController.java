package com.sogou.upd.passport.web.internal.account;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.api.account.SecureApiManager;
import com.sogou.upd.passport.manager.api.account.form.GetSecureInfoApiParams;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.web.ControllerHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-7
 * Time: 上午10:56
 */
@Controller
@RequestMapping("/internal/secure")
public class SecureApiController {

    @Autowired
    private ConfigureManager configureManager;

    @Autowired
    private SecureApiManager proxySecureApiManager;

    /**
     * 根据userId获取用户安全信息
     *
     * @param request
     * @param params
     * @return
     */
    @RequestMapping(value = "/info", method = RequestMethod.POST)
    @ResponseBody
    public Object regMobileCaptchaUser(HttpServletRequest request, GetSecureInfoApiParams params){
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
        if (!result.isSuccess()) {
            result.setCode(ErrorUtil.ERR_CODE_COM_SING);
            return result.toString();
        }
        // 调用内部接口
        result = proxySecureApiManager.getUserSecureInfo(params);
        return result.toString();
    }

}
