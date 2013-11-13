package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.LoginManager;
import com.sogou.upd.passport.manager.account.WapLoginManager;
import com.sogou.upd.passport.manager.form.WapLoginParams;
import com.sogou.upd.passport.service.account.WapTokenService;
import com.sogou.upd.passport.service.account.generator.TokenGenerator;
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

    @Override
    public Result accountLogin(WapLoginParams loginParams, String ip) {
        Result result = new APIResultSupport(false);
        String username = loginParams.getUsername();
        String password = loginParams.getPassword();
        String pwdMD5 = DigestUtils.md5Hex(password.getBytes());
        String passportId = username;
        try {
            //验证验证码
            result = loginManager.checkCaptchaVaild(username, ip, loginParams.getClient_id(), loginParams.getCaptcha(), loginParams.getToken());
            if (!result.isSuccess()) {
                return result;
            }
            result = loginManager.authUser(username, ip, pwdMD5);
            if (result.isSuccess()) {
                String userId = result.getModels().get("userid").toString();
                String token = TokenGenerator.getWapToken(userId);
                wapTokenService.saveWapToken(token, userId);
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
}
