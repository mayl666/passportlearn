package com.sogou.upd.passport.web.account.action.wap;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
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
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * wap的注册接口。基本等同于web的注册接口。
 * Created by denghua on 14-4-28.
 */
@Controller
public class WapRegAction  extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(WapRegAction.class);

    private static final String LOGIN_INDEX_URL ="" ;

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


    @RequestMapping(value = "/wap/reguser", method = RequestMethod.POST)
    @ResponseBody
    public Object reguser(HttpServletRequest request, HttpServletResponse response, RegMobileParams regParams, Model model)  throws Exception{

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
            result = regManager.registerMobile(regParams.getMobile(),regParams.getPassword(),regParams.getClient_id(),regParams.getCaptcha());


            if (result.isSuccess()) {
                Result sessionResult = sessionServerManager.createSession(result.getModels().get("username").toString());
                String sgid = null;
                if (sessionResult.isSuccess()) {
                    sgid = (String) sessionResult.getModels().get("sgid");
                    result.getModels().put("userid", result.getModels().get("userid").toString());
                    if (!Strings.isNullOrEmpty(sgid)) {
                        result.getModels().put("sgid", sgid);
                    }
                }


            }
        } catch (Exception e) {
            logger.error("wap reguser:User Register Is Failed,Username is " + regParams.getMobile(), e);
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
            UserOperationLog userOperationLog = new UserOperationLog(regParams.getMobile(), request.getRequestURI(), regParams.getClient_id()+"", logCode, getIp(request));
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

}
