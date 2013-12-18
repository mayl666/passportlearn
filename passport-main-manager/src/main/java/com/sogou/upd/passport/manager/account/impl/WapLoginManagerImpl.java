package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.LoginManager;
import com.sogou.upd.passport.manager.account.WapLoginManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.connect.SessionServerManager;
import com.sogou.upd.passport.manager.form.WapLoginParams;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.OperateTimesService;
import com.sogou.upd.passport.service.account.WapTokenService;
import com.sogou.upd.passport.service.account.generator.TokenGenerator;
import com.sogou.upd.passport.service.app.AppConfigService;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-11-12
 * Time: 下午7:41
 * To change this template use File | Settings | File Templates.
 */
@Component
public class WapLoginManagerImpl implements WapLoginManager {
    private static final Logger logger = LoggerFactory.getLogger(WapLoginManagerImpl.class);

    @Autowired
    private LoginManager loginManager;
    @Autowired
    private WapTokenService wapTokenService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private OperateTimesService operateTimesService;
    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private SessionServerManager sessionServerManager;

    @Override
    public Result accountLogin(WapLoginParams loginParams, String ip) {
        Result result = new APIResultSupport(false);
        String username = loginParams.getUsername();
        String password = loginParams.getPassword();
        String pwdMD5 = DigestUtils.md5Hex(password.getBytes());
        String passportId = username;
        try {
            //验证验证码
            result = checkCaptchaVaild(username, ip, loginParams.getClient_id(), loginParams.getCaptcha(), loginParams.getToken());
            if (!result.isSuccess()) {
                return result;
            }
            result = loginManager.authUser(username, ip, pwdMD5);
            if (result.isSuccess()) {
                String userId = result.getModels().get("userid").toString();
                String token = wapTokenService.saveWapToken(userId);
                result.setDefaultModel("token", token);
            }
        } catch (Exception e) {
            logger.error("accountLogin fail,passportId:" + passportId, e);
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_LOGIN_FAILED);
            return result;
        }
        return result;
    }

    @Override
    public Result authtoken(String token) {
        Result result = new APIResultSupport(false);
        try {
            String passportId = wapTokenService.getPassprotIdByToken(token);
            if (!Strings.isNullOrEmpty(passportId)) {
                result.setSuccess(true);
                result.setDefaultModel("userid", passportId);
            }
            return result;
        } catch (Exception e) {
            result.setSuccess(false);
            return result;
        }
    }

    @Override
    public Result checkCaptchaVaild(String username, String ip, String clientId,String captchaCode,String token ) {
        Result result = new APIResultSupport(true);
        //校验验证码
        if (needCaptchaCheck(clientId, username, ip)) {
            if (!accountService.checkCaptchaCodeIsVaild(token, captchaCode)) {
                logger.info("[checkCaptchaVaild captchaCode wrong warn]:username=" + username + ", ip=" + ip + ", token=" + token + ", captchaCode=" + captchaCode);
                result.setSuccess(false);
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_CODE_FAILED);
                return result;
            }
        }
        return result;
    }

    @Override
    public Result removeSession(String sgid) {
        Result result = sessionServerManager.removeSession(sgid);

        return result;
    }

    @Override
    public boolean needCaptchaCheck(String client_id, String username, String ip) {
        if (operateTimesService.loginFailedTimesNeedCaptcha(username, ip)) {
            return true;
        }
        return false;
    }

    @Override
    public void doAfterLoginSuccess(final String username, final String ip, final String passportId, final int clientId) {
        //记录登陆次数
        operateTimesService.incLoginTimes(username, ip,true);
    }

}
