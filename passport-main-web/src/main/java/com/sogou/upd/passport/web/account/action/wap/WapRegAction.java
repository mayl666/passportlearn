package com.sogou.upd.passport.web.account.action.wap;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.WapConstant;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.common.utils.ServletUtil;
import com.sogou.upd.passport.manager.account.CookieManager;
import com.sogou.upd.passport.manager.account.RegManager;
import com.sogou.upd.passport.manager.account.SecureManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.api.account.form.RegMobileCaptchaApiParams;
import com.sogou.upd.passport.manager.api.account.form.RegMobileParams;
import com.sogou.upd.passport.manager.api.connect.SessionServerManager;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.manager.form.ActiveEmailParams;
import com.sogou.upd.passport.manager.form.WebRegisterParams;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.account.action.RegAction;
import com.sogou.upd.passport.web.account.form.CheckUserNameExistParameters;
import com.sogou.upd.passport.web.account.form.MoblieCodeParams;
import com.sogou.upd.passport.web.account.form.WapIndexParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * wap的注册接口。基本等同于web的注册接口。
 * Created by denghua on 14-4-28.
 */
@Controller
public class WapRegAction extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(WapRegAction.class);

    private static final String LOGIN_INDEX_URL = "";

    @Autowired
    private RegManager regManager;
    @Autowired
    private ConfigureManager configureManager;
    @Autowired
    private SecureManager secureManager;
    @Autowired
    private RegisterApiManager sgRegisterApiManager;
    @Autowired
    private CookieManager cookieManager;
    @Autowired
    private SessionServerManager sessionServerManager;


    @Autowired
    private UserInfoApiManager proxyUserInfoApiManager;
    @Autowired
    private UserInfoApiManager sgUserInfoApiManager;


    @RequestMapping(value = "/wap/reguser", method = RequestMethod.POST)
    @ResponseBody
    public Object reguser(HttpServletRequest request, HttpServletResponse response, RegMobileParams regParams, Model model) throws Exception {

        Result result = new APIResultSupport(false);
        String ip = null;
        String uuidName = null;
        String finalCode = null;
        try {
            //参数验证
            String validateResult = ControllerHelper.validateParams(regParams);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }

            ip = getIp(request);
            //校验用户是否允许注册
            uuidName = ServletUtil.getCookie(request, "uuidName");
            result = regManager.checkRegInBlackList(ip, uuidName);
            if (!result.isSuccess()) {
                if (result.getCode().equals(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST)) {
                    finalCode = ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST;
                    result.setCode(ErrorUtil.ERR_CODE_REGISTER_UNUSUAL);
                    result.setMessage("注册失败");
                }
                return result.toString();
            }

            // 调用内部接口
            if (PhoneUtil.verifyPhoneNumberFormat(regParams.getUsername())) {
                result = regManager.registerMobile(regParams.getUsername(), regParams.getPassword(), regParams.getClient_id(), regParams.getCaptcha(), null);
            } else {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage("只支持手机号注册");
                return result.toString();
            }


            if (result.isSuccess()) {

                //第三方获取个人资料
                String userid = result.getModels().get("userid").toString();
                AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(userid);
                // 调用内部接口
                GetUserInfoApiparams userInfoApiparams = new GetUserInfoApiparams(userid, "uniqname,avatarurl,gender");
                if (domain == AccountDomainEnum.THIRD) {
                    result = sgUserInfoApiManager.getUserInfo(userInfoApiparams);
                } else {
                    result = proxyUserInfoApiManager.getUserInfo(userInfoApiparams);
                }
                System.out.println("wap reg userinfo result:" + result);
                Result sessionResult = sessionServerManager.createSession(userid);
                String sgid = null;
                if (sessionResult.isSuccess()) {
                    sgid = (String) sessionResult.getModels().get("sgid");
                    result.getModels().put("userid", userid);
                    if (!Strings.isNullOrEmpty(sgid)) {
                        result.getModels().put("sgid", sgid);
                    }
                } else {
                    logger.warn("can't get session result, userid:" + result.getModels().get("userid"));
                }
            }
        } catch (Exception e) {
            logger.error("wap reguser:User Register Is Failed,Username is " + regParams.getUsername(), e);
        } finally {
            String logCode = null;
            if (!Strings.isNullOrEmpty(finalCode)) {
                logCode = finalCode;
            } else {
                logCode = result.getCode();
            }
            regManager.incRegTimes(ip, uuidName);
            String userId = (String) result.getModels().get("userid");
            if (!Strings.isNullOrEmpty(userId) && AccountDomainEnum.getAccountDomain(userId) != AccountDomainEnum.OTHER) {
                if (result.isSuccess()) {
                    // 非外域邮箱用户不用验证，直接注册成功后记录登录记录
                    int clientId = regParams.getClient_id();
                    secureManager.logActionRecord(userId, clientId, AccountModuleEnum.LOGIN, ip, null);
                }
            }
            //用户注册log
            UserOperationLog userOperationLog = new UserOperationLog(regParams.getUsername(), request.getRequestURI(), regParams.getClient_id() + "", logCode, getIp(request));
            String referer = request.getHeader("referer");
            userOperationLog.putOtherMessage("ref", referer);
            UserOperationLogUtil.log(userOperationLog);
        }
        return result.toString();
    }


    //检查用户是否存在
    protected Result checkAccountNotExists(String username, int clientId) throws Exception {
        Result result = new APIResultSupport(false);
        //校验是否是搜狐域内用户

        if (AccountDomainEnum.SOHU.equals(AccountDomainEnum.getAccountDomain(username))) {
            result.setCode(ErrorUtil.ERR_CODE_NOTSUPPORT_SOHU_REGISTER);
            return result;
        }
        //校验是否是搜狗用户
        if (AccountDomainEnum.SOGOU.equals(AccountDomainEnum.getAccountDomain(username))) {
            result.setCode(ErrorUtil.ERR_CODE_NOTSUPPORT_SOGOU_REGISTER);
            return result;
        }

        //判断是否是个性账号
        if (username.indexOf("@") == -1) {
            //判断是否是手机号注册
            if (PhoneUtil.verifyPhoneNumberFormat(username)) {
                result = regManager.isAccountNotExists(username, true, clientId);
            } else {
                username = username + "@sogou.com";
                result = regManager.isAccountNotExists(username, false, clientId);
            }
        } else {
            result = regManager.isAccountNotExists(username, false, clientId);
        }
        return result;
    }


    /**
     * 找回密码
     * @param ru
     * @param redirectAttributes
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/wap/findpwd",method = RequestMethod.GET)
    public String findPwdView(String ru, RedirectAttributes redirectAttributes) throws Exception {
        if (Strings.isNullOrEmpty(ru)) {
            ru = CommonConstant.DEFAULT_CONNECT_REDIRECT_URL;
        }
        redirectAttributes.addAttribute("ru", ru);
        return "redirect:" + SHPPUrlConstant.SOHU_FINDPWD_URL + "?ru={ru}";
    }


    /**
     * wap注册首页
     * @param request
     * @param response
     * @param model
     * @param wapIndexParams
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/wap/reg",method = RequestMethod.GET)
    public String regist(HttpServletRequest request, HttpServletResponse response, Model model, WapIndexParams wapIndexParams) throws Exception {

        if (WapConstant.WAP_SIMPLE.equals(wapIndexParams.getV())) {
            response.setHeader("Content-Type", "text/vnd.wap.wml;charset=utf-8");
            return "wap/regist_simple";
        } else if (WapConstant.WAP_TOUCH.equals(wapIndexParams.getV())) {
            return "wap/regist_touch";
        } else {
            return "wap/regist_color";
        }
    }
}
