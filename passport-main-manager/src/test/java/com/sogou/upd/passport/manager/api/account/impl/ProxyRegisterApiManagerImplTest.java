package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.manager.api.account.form.BaseMoblieApiParams;
import com.sogou.upd.passport.manager.api.account.form.CheckUserApiParams;
import com.sogou.upd.passport.manager.api.account.form.RegEmailApiParams;
import com.sogou.upd.passport.manager.api.account.form.RegMobileCaptchaApiParams;
import junit.framework.Assert;
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
        params.setUserid(USERID);
        params.setPassword(PASSWORD);
        params.setCreateip("10.1.164.65");
        Result result = proxyRegisterApiManager.regMailUser(params);
        System.out.println(result);
    }

    @Test
    public void testSendMobileRegCaptcha() {
        BaseMoblieApiParams baseMoblieApiParams = new BaseMoblieApiParams();
        baseMoblieApiParams.setMobile("18952461329");
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

    @Test
    public void testCheckUser() {
        CheckUserApiParams checkUserApiParams = new CheckUserApiParams();
        checkUserApiParams.setUserid("shipengzhi1986@sogou.com");
        Result result = proxyRegisterApiManager.checkUser(checkUserApiParams);
        System.out.println("result1:" + result.toString());
        checkUserApiParams = new CheckUserApiParams();
        checkUserApiParams.setUserid("spz1986411@sohu.com");
        result = proxyRegisterApiManager.checkUser(checkUserApiParams);
        System.out.println("result2:" + result.toString());
        checkUserApiParams = new CheckUserApiParams();
        checkUserApiParams.setUserid("13621009174");
        result = proxyRegisterApiManager.checkUser(checkUserApiParams);
        System.out.println("result3:" + result.toString());
        checkUserApiParams = new CheckUserApiParams();
        checkUserApiParams.setUserid("D6BDDEDDA8A9C09C7B22A7D7140CC167@qq.sohu.com");
        result = proxyRegisterApiManager.checkUser(checkUserApiParams);
        System.out.println("result4:" + result.toString());
    }


}
