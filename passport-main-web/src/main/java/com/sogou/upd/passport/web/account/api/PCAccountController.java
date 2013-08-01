package com.sogou.upd.passport.web.account.api;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.PCAccountManager;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.CreateCookieUrlApiParams;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
    private PCAccountManager pcAccountManager;
    @Autowired
    private UserInfoApiManager proxyUserInfoApiManagerImpl;
    @Autowired
    private LoginApiManager proxyLoginApiManager;

    @RequestMapping(value = "/act/getpairtoken")
    @ResponseBody
    public Object getPairToken(PcPairTokenParams reqParams) throws Exception {
        //参数验证
        String validateResult = ControllerHelper.validateParams(reqParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            return getReturnStr(reqParams.getCb(),"1");
        }

        Result result = pcAccountManager.createPairToken(reqParams);
        String resStr ="";
        if (result.isSuccess()) {
            AccountToken accountToken = (AccountToken) result.getDefaultModel();
            // 获取昵称，返回格式
            String passportId = accountToken.getPassportId();
            GetUserInfoApiparams getUserInfoApiparams = new GetUserInfoApiparams(passportId, "uniqname");
            Result getUserInfoResult = proxyUserInfoApiManagerImpl.getUserInfo(getUserInfoApiparams);
            String uniqname;
            if (getUserInfoResult.isSuccess()) {
                uniqname = (String) getUserInfoResult.getModels().get("uniqname");
                uniqname = Strings.isNullOrEmpty(uniqname) ? defaultUniqname(passportId) : uniqname;
            } else {
                uniqname = defaultUniqname(passportId);
            }
            resStr = "0|" + accountToken.getAccessToken() + "|" + accountToken.getRefreshToken() + "|" + accountToken.getPassportId() + "|" + uniqname;   //0|token|refreshToken|userid|nick
        } else {
            resStr = handleGetPairTokenErr(result.getCode());
        }
        return getReturnStr(reqParams.getCb(),resStr);
    }

    @RequestMapping(value = "/act/refreshtoken")
    @ResponseBody
    public Object refreshToken(PcRefreshTokenParams reqParams) throws Exception {
        //参数验证
        String validateResult = ControllerHelper.validateParams(reqParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            return getReturnStr(reqParams.getCb(),"1|invalid|required_params"); //参数错误
        }

        Result result = pcAccountManager.authRefreshToken(reqParams);
        String resStr ="";
        if (result.isSuccess()) {
            AccountToken accountToken = (AccountToken) result.getDefaultModel();
            resStr = "0|" + accountToken.getAccessToken() + "|" + accountToken.getRefreshToken();
        } else {
            resStr = handleRefreshTokenErr(result.getCode());
        }
        return getReturnStr(reqParams.getCb(),resStr);
    }

    @RequestMapping(value = "/act/authtoken", method = RequestMethod.GET)
    public String authToken(PcAuthTokenParams authPcTokenParams) throws Exception {
        Result result = new APIResultSupport(false);
        //参数验证
        String validateResult = ControllerHelper.validateParams(authPcTokenParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            if (!Strings.isNullOrEmpty(authPcTokenParams.getRu())) {
                return "redirect:" + authPcTokenParams.getRu() + "?status=1";   //status=1表示参数错误
            }
            return "forward:/act/errorMsg?msg=Error: parameter error!";
        }
        result = pcAccountManager.authToken(authPcTokenParams);
        //重定向生成cookie
        if (result.isSuccess()) {
            CreateCookieUrlApiParams createCookieUrlApiParams = new CreateCookieUrlApiParams();
            createCookieUrlApiParams.setUserid(authPcTokenParams.getUserid());
            createCookieUrlApiParams.setRu(authPcTokenParams.getRu());
            if(authPcTokenParams.getLivetime() >0){
                createCookieUrlApiParams.setPersistentcookie(1);
            }
            Result createCookieResult = proxyLoginApiManager.buildCreateCookieUrl(createCookieUrlApiParams);
            if (createCookieResult.isSuccess()) {
                String setcookieUrl = createCookieResult.getModels().get("url").toString();
                return "redirect:" + setcookieUrl;
            } else {
                result.setCode(ErrorUtil.ERR_CODE_CREATE_COOKIE_FAILED);
                logger.error("authToken:createCookieUrl error");
            }
        }
        //token验证失败
        return "redirect:" + authPcTokenParams.getRu() + "?status=6";//status=6表示验证失败
    }

    @RequestMapping(value = "/act/errorMsg")
    @ResponseBody
    public Object errorMsg(@RequestParam("msg") String msg) throws Exception {
        return msg;
    }

    private String defaultUniqname(String passportId) {
        return passportId.substring(0, passportId.indexOf("@"));
    }

    private String getReturnStr(String cb,String resStr) {
        if(!Strings.isNullOrEmpty(cb)){
            return cb+"('"+resStr+"')";
        }
        return resStr;
    }

    private String handleGetPairTokenErr(String errCode){
        String errStr;
        switch (errCode) {
            case ErrorUtil.INVALID_CLIENTID:
                errStr = "1"; //参数错误
                break;
            case ErrorUtil.ERR_CODE_ACCOUNT_PHONE_NOBIND:
                errStr = "2";  //用户名不存在
                break;
            case ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_PWD_ERROR:
                errStr = "3";  //用户名密码错误
                break;
            case ErrorUtil.ERR_SIGNATURE_OR_TOKEN:
                errStr = "7|invalid sig"; //生成token失败
                break;
            default:
                errStr = "6"; //失败
                break;
        }
        return errStr;
    }

    private String handleRefreshTokenErr(String errCode){
        String errStr;
        switch (errCode) {
            case ErrorUtil.INVALID_CLIENTID:
                errStr = "1|invalid|required_params";
                break;
            case ErrorUtil.ERR_REFRESH_TOKEN:
                errStr = "2|invalid|refreshtoken";
                break;
            case ErrorUtil.CREATE_TOKEN_FAIL:
                errStr = "3|failed|createtoken"; //生成token失败
                break;
            default:
                errStr = "6|error|syste_error"; //系统错误
                break;
        }
        return errStr;
    }
}