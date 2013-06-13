package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.manager.api.account.form.AuthUserApiParams;
import com.sogou.upd.passport.manager.api.account.form.BaseMoblieApiParams;
import com.sogou.upd.passport.manager.api.account.form.RegMobileCaptchaApiParams;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-6
 * Time: 下午2:28
 */

public class ProxyRegisterApiManagerTest extends BaseTest {

    private static final String MOBILE = "18738963584";
    private static final String PASSWORD = "111111";

    @Autowired
    private RegisterApiManager proxyRegisterApiManager;

    @Test
    public void testSendMobileRegCaptcha() {
        BaseMoblieApiParams baseMoblieApiParams = new BaseMoblieApiParams();
        baseMoblieApiParams.setMobile(MOBILE);
        Result result = proxyRegisterApiManager.sendMobileRegCaptcha(baseMoblieApiParams);
        System.out.println(result);
    }

    @Test
    public void testRegMobileCaptchaUser() {
        RegMobileCaptchaApiParams regMobileCaptchaApiParams = new RegMobileCaptchaApiParams();
        regMobileCaptchaApiParams.setMobile(MOBILE);
        regMobileCaptchaApiParams.setPassword(PASSWORD);
        regMobileCaptchaApiParams.setCaptcha("1540");
        Result result = proxyRegisterApiManager.regMobileCaptchaUser(regMobileCaptchaApiParams);
        System.out.println(result);
    }

}
