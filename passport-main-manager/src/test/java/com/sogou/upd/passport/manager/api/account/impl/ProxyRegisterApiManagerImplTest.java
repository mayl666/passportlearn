package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.manager.api.account.form.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-9
 * Time: 下午2:08
 */
public class ProxyRegisterApiManagerImplTest extends BaseTest {

    private static final String MOBILE = "13520069535";
    private static final String USERID = "281168178@qq.com";
    private static final String PASSWORD = "111111";

    @Autowired
    private RegisterApiManager proxyRegisterApiManager;

    @Test
    public void testRegMailUser() {
        RegEmailApiParams params = new RegEmailApiParams();
        params.setUserid("23dsafasdf@qq.com");
        params.setPassword(PASSWORD);
        params.setCreateip("10.1.164.65");
        Result result = proxyRegisterApiManager.regMailUser(params);
        System.out.println(result);
    }

    @Test
    public void testSendMobileRegCaptcha() {
        BaseMobileApiParams baseMobileApiParams = new BaseMobileApiParams();
        baseMobileApiParams.setMobile("18952461329");
        Result result = proxyRegisterApiManager.sendMobileRegCaptcha(baseMobileApiParams);
        System.out.println(result);
    }

    @Test
    public void testRegMobileCaptchaUser() throws Exception {
        RegMobileCaptchaApiParams regMobileCaptchaApiParams = new RegMobileCaptchaApiParams();
        regMobileCaptchaApiParams.setMobile("13521134303");
        regMobileCaptchaApiParams.setPassword(Coder.encryptMD5("111111"));
        regMobileCaptchaApiParams.setCaptcha("7808");
        Result result = proxyRegisterApiManager.regMobileCaptchaUser(regMobileCaptchaApiParams);
        System.out.println(result);
    }

    @Test
    public void testCheckUser() {
        CheckUserApiParams checkUserApiParams = new CheckUserApiParams();
        checkUserApiParams.setUserid("scanrecord@sogou.com");
        Result result = proxyRegisterApiManager.checkUser(checkUserApiParams);
        System.out.println("result1:" + result.toString());
        checkUserApiParams = new CheckUserApiParams();
        checkUserApiParams.setUserid("BD2012111@sohu.com");
        result = proxyRegisterApiManager.checkUser(checkUserApiParams);
        System.out.println("result2:" + result.toString());
        checkUserApiParams = new CheckUserApiParams();
        checkUserApiParams.setUserid("LowOfSolipsism@sohu.com");
        result = proxyRegisterApiManager.checkUser(checkUserApiParams);
        System.out.println("result3:" + result.toString());
        checkUserApiParams = new CheckUserApiParams();
        checkUserApiParams.setUserid("q435053906@game.sohu.com");
        result = proxyRegisterApiManager.checkUser(checkUserApiParams);
        System.out.println("result4:" + result.toString());
    }

    @Test
    public void testRegMobileUser() {
        RegMobileApiParams regMobileCaptchaApiParams = new RegMobileApiParams();
        regMobileCaptchaApiParams.setMobile("13621009174");
        regMobileCaptchaApiParams.setPassword(PASSWORD);
        Result result = proxyRegisterApiManager.regMobileUser(regMobileCaptchaApiParams);
        System.out.println(result);
    }
}
