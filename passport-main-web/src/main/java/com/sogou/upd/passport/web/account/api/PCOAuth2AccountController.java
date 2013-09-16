package com.sogou.upd.passport.web.account.api;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.OAuthResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.common.utils.ServletUtil;
import com.sogou.upd.passport.manager.account.*;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.*;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.manager.form.PCOAuth2RegisterParams;
import com.sogou.upd.passport.manager.form.PcPairTokenParams;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.oauth2.authzserver.request.OAuthTokenASRequest;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.account.form.*;
import com.sogou.upd.passport.web.annotation.LoginRequired;
import com.sogou.upd.passport.web.annotation.ResponseResultType;
import com.sogou.upd.passport.web.inteceptor.HostHolder;
import com.sogou.upd.passport.web.annotation.InterfaceSecurity;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.net.URLDecoder;

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
    @Autowired
    private SecureManager secureManager;
    @Autowired
    private LoginApiManager proxyLoginApiManager;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private AccountInfoManager accountInfoManager;
    @Autowired
    private OAuth2AuthorizeManager oAuth2AuthorizeManager;
    @Autowired
    private ConfigureManager configureManager;
    @Autowired
    private RegManager regManager;
    @Autowired
    private PCOAuth2RegManager pcoAuth2RegManager;

    @RequestMapping(value = "/pclogin", method = RequestMethod.GET)
    public String pcLogin(Model model) throws Exception {
        return "/oauth2pc/pclogin";
    }

    @RequestMapping(value = "/token")
    @ResponseBody
    public Object authorize(HttpServletRequest request) throws Exception {
        OAuthTokenASRequest oauthRequest;
        Result result = new OAuthResultSupport(false);
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
        result = oAuth2AuthorizeManager.oauth2Authorize(oauthRequest, appConfig);

        return result.toString();
    }

    /**
     * 浏览器桌面端：用户注册检查用户名是否可用
     */
    @RequestMapping(value = "/checkregname", method = RequestMethod.POST)
    @ResponseBody
    public String checkRegisterName(PCAccountCheckRegNameParams checkParam)
            throws Exception {
        Result result = new APIResultSupport(false);
        //参数验证
        String validateResult = ControllerHelper.validateParams(checkParam);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        String username = URLDecoder.decode(checkParam.getUsername(), "utf-8");
        result = checkPCAccountNotExists(username);
        if (PhoneUtil.verifyPhoneNumberFormat(username) && ErrorUtil.ERR_CODE_ACCOUNT_REGED.equals(result.getCode())) {
            result.setMessage("该手机号已注册或已绑定，请直接登录");
        }
        return result.toString();
    }

    /**
     * 浏览器桌面端sohu+注册接口
     *
     * @param request
     * @param pcoAuth2RegisterParams
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public Object register(HttpServletRequest request, PCOAuth2RegisterParams pcoAuth2RegisterParams) throws Exception {
        Result result = new APIResultSupport(false);
        //参数验证
        String validateResult = ControllerHelper.validateParams(pcoAuth2RegisterParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        String ip = getIp(request);
        //TODO ip加安全限制
        result = checkPCAccountNotExists(pcoAuth2RegisterParams.getUsername());
        if (!result.isSuccess()) {
            return result.toString();
        }
        result = pcoAuth2RegManager.pcAccountRegister(pcoAuth2RegisterParams, ip);
        //注册成功后获取token
        if (result.isSuccess()) {
            PcPairTokenParams pcPairTokenParams = new PcPairTokenParams();
            pcPairTokenParams.setAppid(pcoAuth2RegisterParams.getClient_id());
            pcPairTokenParams.setUserid(result.getModels().get("userid").toString());
            pcPairTokenParams.setTs(pcoAuth2RegisterParams.getInstance_id());
            result = pcoAuth2RegManager.getPairToken(pcPairTokenParams);
            if (result.isSuccess()) {
                AccountToken accountToken = (AccountToken) result.getDefaultModel();
                // 获取昵称并返回
                String passportId = accountToken.getPassportId();
                GetUserInfoApiparams userInfoApiParams = new GetUserInfoApiparams(passportId, "uniqname");
                Result userInfoResult = proxyUserInfoApiManagerImpl.getUserInfo(userInfoApiParams);
                String uniqname;
                if (userInfoResult.isSuccess()) {
                    uniqname = (String) userInfoResult.getModels().get("uniqname");
                    uniqname = Strings.isNullOrEmpty(uniqname) ? defaultUniqname(passportId) : uniqname;
                } else {
                    uniqname = defaultUniqname(passportId);
                }
                result.setDefaultModel("uniqname", uniqname);
            }
        }
        return result.toString();
    }

    /**
     * 浏览器登陆流程
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Object login(HttpServletRequest request,PCOAuth2LoginParams loginParams)
            throws Exception {
        Result result = new APIResultSupport(false);
        String ip = getIp(request);
        //参数验证
        String validateResult = ControllerHelper.validateParams(loginParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }

//        result = loginManager.accountLogin(loginParams, ip, request.getScheme());
//
//        String userId = loginParams.getUsername();
//        //用户登录log
//        UserOperationLog userOperationLog = new UserOperationLog(userId, request.getRequestURI(), loginParams.getClient_id(), result.getCode(), getIp(request));
//        String referer = request.getHeader("referer");
//        userOperationLog.putOtherMessage("ref", referer);
//        UserOperationLogUtil.log(userOperationLog);
//
//        if (result.isSuccess()) {
//            userId = result.getModels().get("userid").toString();
//            int clientId = Integer.parseInt(loginParams.getClient_id());
//            loginManager.doAfterLoginSuccess(loginParams.getUsername(), ip, userId, clientId);
//        } else {
//            loginManager.doAfterLoginFailed(loginParams.getUsername(), ip);
//            //校验是否需要验证码
//            boolean needCaptcha = loginManager.needCaptchaCheck(loginParams.getClient_id(), loginParams.getUsername(), getIp(request));
//            if (needCaptcha) {
//                result.setDefaultModel("needCaptcha", true);
//            }
//            if(result.getCode().equals(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST)){
//                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_PWD_ERROR);
//                result.setMessage("密码错误");
//            }
//        }
//
//        result.setDefaultModel("xd", loginParams.getXd());
//        model.addAttribute("data", result.toString());
//        return "/login/api";
        return result.toString();
    }


    //检查用户是否存在
    private Result checkPCAccountNotExists(String username) throws Exception {
        Result result = new APIResultSupport(false);
        //不允许sohu域用户注册
        if (AccountDomainEnum.SOHU.equals(AccountDomainEnum.getAccountDomain(username))) {
            result.setCode(ErrorUtil.ERR_CODE_NOTSUPPORT_SOHU_REGISTER);
            return result;
        }
        //不允许邮箱注册
        if (username.indexOf("@") != -1) {
            result.setCode(ErrorUtil.ERR_CODE_REGISTER_EMAIL_NOT_ALLOWED);
            return result;
        }
        //判断是否是手机号注册
        if (PhoneUtil.verifyPhoneNumberFormat(username)) {
            result = pcoAuth2RegManager.isPcAccountNotExists(username, true);
        } else {
            result = pcoAuth2RegManager.isPcAccountNotExists(username, false);
        }
        return result;
    }


    @RequestMapping(value = "/userinfo/pcindex", method = RequestMethod.GET)
    public String pcindex(HttpServletRequest request, HttpServletResponse response, PCOAuth2IndexParams oauth2PcIndexParams, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        //参数验证
        String validateResult = ControllerHelper.validateParams(oauth2PcIndexParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        //TODO 校验token,获取userid
        String passportId = "tinkame700@sogou.com";

        //获取头像
        result=accountInfoManager.obtainPhoto(passportId,"180");
        if (result.getModels().get("180") != null) {
            model.addAttribute("imageUrl", result.getModels().get("180"));
        }else {
            model.addAttribute("imageUrl","");
        }

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

        //判断账号类型,判断绑定手机或者绑定邮箱是否可用
        AccountDomainEnum accountDomain = AccountDomainEnum.getAccountDomain(passportId);
        switch (accountDomain) {
            case SOHU:
                model.addAttribute("isBindEmailUsable", 0);
                model.addAttribute("isBindMobileUsable", 0);
                break;
            case THIRD:
                model.addAttribute("isBindEmailUsable", 0);
                model.addAttribute("isBindMobileUsable", 0);
                break;
            case PHONE:
                model.addAttribute("isBindEmailUsable", 1);
                model.addAttribute("isBindMobileUsable", 0);
                break;
            default:
                model.addAttribute("isBindEmailUsable", 1);
                model.addAttribute("isBindMobileUsable", 1);
                break;
        }
        //获取绑定手机，绑定邮箱
        result = secureManager.queryAccountSecureInfo(passportId, CommonConstant.PC_CLIENTID, true);
        if (result.isSuccess()) {
            if (result.getModels().get("sec_mobile") != null) {
                model.addAttribute("bindMoblile", result.getModels().get("sec_mobile"));
            } else {
                model.addAttribute("bindMoblile", "");
            }

            if (result.getModels().get("sec_email") != null) {
                model.addAttribute("bindEmail", result.getModels().get("sec_email"));
            } else {
                model.addAttribute("bindEmail", "");
            }
        }

        //生成cookie
        CreateCookieUrlApiParams createCookieUrlApiParams = new CreateCookieUrlApiParams();
        createCookieUrlApiParams.setUserid(passportId);
        createCookieUrlApiParams.setRu("https://account.sogou.com");
        //TODO sogou域账号迁移后cookie生成问题
        Result getCookieValueResult = proxyLoginApiManager.getCookieValue(createCookieUrlApiParams);
        if (getCookieValueResult.isSuccess()) {
            String ppinf = (String) getCookieValueResult.getModels().get("ppinf");
            String pprdig = (String) getCookieValueResult.getModels().get("pprdig");
            ServletUtil.setCookie(response, "ppinf", ppinf, -1, CommonConstant.SOGOU_ROOT_DOMAIN);
            ServletUtil.setCookie(response, "pprdig", pprdig, -1, CommonConstant.SOGOU_ROOT_DOMAIN);
            response.addHeader("Sohupp-Cookie", "ppinf,pprdig");
        }
        return "/oauth2pc/pcindex";
    }


    @RequestMapping(value = "/errorMsg")
    @ResponseBody
    public Object errorMsg(@RequestParam("msg") String msg) throws Exception {
        return msg;
    }

    private String defaultUniqname(String passportId) {
        return passportId.substring(0, passportId.indexOf("@"));
    }

    //头像上传
    @RequestMapping(value = "/userinfo/uploadavatar")
    @LoginRequired(resultType = ResponseResultType.redirect)
    @ResponseBody
    public Object uploadAvatar(HttpServletRequest request, UploadAvatarParams params)
    {
        Result result = new APIResultSupport(false);

        if (hostHolder.isLogin()) {

            //参数验证
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            //验证client_id是否存在
            int clientId = Integer.parseInt(params.getClient_id());
            if (!configureManager.checkAppIsExist(clientId)) {
                result.setCode(ErrorUtil.INVALID_CLIENTID);
                return result.toString();
            }

            String userId = hostHolder.getPassportId();

            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            CommonsMultipartFile multipartFile = (CommonsMultipartFile) multipartRequest.getFile("Filedata");

            byte[] byteArr = multipartFile.getBytes();
            result = accountInfoManager.uploadImg(byteArr, userId,"0");
        }else {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CHECKLOGIN_FAILED);
        }
        return result.toString();
    }

}