package com.sogou.upd.passport.web.account.api;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.PcAccountManager;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.form.CreateCookieUrlApiParams;
import com.sogou.upd.passport.manager.form.PcAuthTokenParams;
import com.sogou.upd.passport.manager.form.PcPairTokenParams;
import com.sogou.upd.passport.manager.form.PcRefreshTokenParams;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 桌面端登录流程Controller
 * User: shipengzhi
 * Date: 13-7-26
 * Time: 下午7:07
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class PCAccountController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(PCAccountController.class);

    @Autowired
    private PcAccountManager pcAccountManager;
    @Autowired
    private LoginApiManager proxyLoginApiManager;

    @RequestMapping(value = "/act/getpairtoken")
    @ResponseBody
    public Object getPairToken(PcPairTokenParams reqParams) throws Exception {
        Result result = new APIResultSupport(false);
        //参数验证
        String validateResult = ControllerHelper.validateParams(reqParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }

        result = pcAccountManager.createPairToken(reqParams);
        if (result.isSuccess()) {
            AccountToken accountToken = (AccountToken) result.getDefaultModel();
        }
        // TODO 获取昵称，返回格式

        return result.toString();
    }

    @RequestMapping(value = "/act/refreshtoken")
    @ResponseBody
    public Object refreshToken(PcRefreshTokenParams reqParams) throws Exception {
        Result result = new APIResultSupport(false);
        //参数验证
        String validateResult = ControllerHelper.validateParams(reqParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }

        if (result.isSuccess()) {
            AccountToken accountToken = (AccountToken) result.getDefaultModel();
        }
        // TODO 获取昵称，返回格式

        return result.toString();
    }

    @RequestMapping(value = "/act/authtoken", method = RequestMethod.GET)
    public String authToken(HttpServletRequest request, PcAuthTokenParams authPcTokenParams) throws Exception {
        Result result = new APIResultSupport(false);
        //参数验证
        String validateResult = ControllerHelper.validateParams(authPcTokenParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        result = pcAccountManager.authToken(authPcTokenParams);
        //重定向生成cookie
        if (result.isSuccess()) {
            CreateCookieUrlApiParams createCookieUrlApiParams = new CreateCookieUrlApiParams();
            createCookieUrlApiParams.setUserid(authPcTokenParams.getUserid());
            createCookieUrlApiParams.setRu("http://profile.ie.sogou.com/?status=0");
            Result createCookieResult = proxyLoginApiManager.buildCreateCookieUrl(createCookieUrlApiParams);
            if (createCookieResult.isSuccess()) {
                String setcookieUrl = createCookieResult.getModels().get("url").toString();
                return "redirect:" + setcookieUrl;
            } else {
                result.setCode(ErrorUtil.ERR_CODE_CREATE_COOKIE_FAILED);
                logger.error("authToken:createCookieUrl error");
            }
        }
        return "";
    }
}
