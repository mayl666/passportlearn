package com.sogou.upd.passport.web.account.api;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.OAuth2AuthorizeManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserUniqnameApiParams;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.oauth2.authzserver.request.OAuthTokenASRequest;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.account.form.PCOAuth2IndexParams;
import com.sogou.upd.passport.web.account.form.PCOAuth2UpdateNickParams;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * sohu+浏览器相关接口替换
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-9-9
 * Time: 下午7:37
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/oauth2")
public class PCOAuth2AccountController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(PCOAuth2AccountController.class);
    @Autowired
    private UserInfoApiManager proxyUserInfoApiManagerImpl;

    @RequestMapping(value = "/pclogin", method = RequestMethod.GET)
    public String pcLogin(Model model) throws Exception {
        return "";
    }

    @RequestMapping(value = "/pcindex", method = RequestMethod.GET)
    public String pcindex(HttpServletRequest request, PCOAuth2IndexParams oauth2PcIndexParams, Model model) throws Exception {
        //参数验证
        String validateResult = ControllerHelper.validateParams(oauth2PcIndexParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            //为了sohu+返回错误一致
            return "forward:/oauth2/errorMsg?msg='{\"code\":-99,\"msg\":\"accesstoken illegal\"}'";
        }
        //TODO 校验token,获取userid
        String passportId = "tinkame700@sogou.com";

        //获取昵称
        GetUserInfoApiparams getUserInfoApiparams = new GetUserInfoApiparams(passportId, "uniqname");
        Result getUserInfoResult = proxyUserInfoApiManagerImpl.getUserInfo(getUserInfoApiparams);
        String uniqname;
        if (getUserInfoResult.isSuccess()) {
            uniqname = (String) getUserInfoResult.getModels().get("uniqname");
            uniqname = Strings.isNullOrEmpty(uniqname) ? defaultUniqname(passportId) : uniqname;
        } else {
            uniqname = defaultUniqname(passportId);
        }
        model.addAttribute("userid", passportId);
        model.addAttribute("uniqname", uniqname);



        return "/oauth2pc/pcindex";
    }
    @RequestMapping(value = "/checknickname", method = RequestMethod.GET)
    @ResponseBody
    public Object checkNickName(HttpServletRequest request,@RequestParam(value = "nickname") String nickname){
        StringBuilder resutStr = new StringBuilder();
        resutStr.append("{");
        UpdateUserUniqnameApiParams updateUserUniqnameApiParams=new UpdateUserUniqnameApiParams();
        updateUserUniqnameApiParams.setUniqname(nickname);
        updateUserUniqnameApiParams.setClient_id(SHPPUrlConstant.APP_ID);
        Result result = proxyUserInfoApiManagerImpl.checkUniqName(updateUserUniqnameApiParams);
        if(result.isSuccess()){
            resutStr.append("\"code\":0,\"msg\":\"check nick success,nick:"+nickname+"\"}");
        }else {
            resutStr.append("\"code\":-1,\"msg\":\"check nick failed,nick:"+nickname+"\"}");
        }
        return resutStr.toString();

    }

    @RequestMapping(value = "/updateNickName",method = RequestMethod.POST)
    @ResponseBody
    public Object updateNickName(HttpServletRequest request,PCOAuth2UpdateNickParams pcOAuth2UpdateNickParams) throws Exception {
        StringBuilder resutStr = new StringBuilder();
        resutStr.append("{");
        //参数验证
        String validateResult = ControllerHelper.validateParams(pcOAuth2UpdateNickParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            //TODO 错误与sohu+保持一致
            return validateResult;
        }
        //TODO 校验token,获取userid
        String passportId = "tinkame700@sogou.com";
        resutStr.append("\"code\":0,\"msg\":\"success\"}");
        return resutStr.toString();

    }

    @RequestMapping(value = "/errorMsg")
    @ResponseBody
    public Object errorMsg(@RequestParam("msg") String msg) throws Exception {
        return msg;
    }
    private String defaultUniqname(String passportId) {
        return passportId.substring(0, passportId.indexOf("@"));
    }

    @Autowired
    private OAuth2AuthorizeManager oAuth2AuthorizeManager;

    @Autowired
    private ConfigureManager configureManager;

    @RequestMapping(value = "/token")
    @ResponseBody
    public Object authorize(HttpServletRequest request) throws Exception {
        OAuthTokenASRequest oauthRequest;
        Result result = new APIResultSupport(false);
        try {
            oauthRequest = new OAuthTokenASRequest(request);
        } catch (OAuthProblemException e) {
            result.setCode(e.getError());
            result.setMessage(e.getDescription());
            return result.toString();
        }

        int clientId = oauthRequest.getClientId();

        // 检查client_id和client_secret是否有效
        AppConfig appConfig = configureManager.verifyClientVaild(clientId, oauthRequest.getClientSecret());
        if (appConfig == null) {
            result.setCode(ErrorUtil.INVALID_CLIENT);
            return result.toString();
        }
        result = oAuth2AuthorizeManager.oauth2Authorize(oauthRequest,appConfig);

        return result.toString();
    }
}