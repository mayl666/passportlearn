package com.sogou.upd.passport.manager.account;

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
public class SGRegisterApiManagerImplTest extends BaseTest {

    private static final String MOBILE = "18738963584";
    private static final String USERID = "dfsfs234232@qq.com";
    private static final String PASSWORD = "111111";

    @Autowired
    private RegisterApiManager proxyRegisterApiManager;

    @Test
    public void testRegMailUser() {
        RegEmailApiParams params = new RegEmailApiParams();
        params.setUserid(USERID);
        params.setPassword(PASSWORD);
        params.setRu("http://wan.sogou.com");
        params.setCreateip("10.1.164.65");
        Result result = proxyRegisterApiManager.regMailUser(params);
        System.out.println(result);
    }

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

    @Test
    public void testCheckUser() {
        CheckUserApiParams checkUserApiParams = new CheckUserApiParams();
        checkUserApiParams.setUserid("test_lg_upd@sogou.com");
        Result result = proxyRegisterApiManager.checkUser(checkUserApiParams);
        System.out.println(result.toString());
        Assert.assertTrue(result.isSuccess());
        checkUserApiParams = new CheckUserApiParams();
        checkUserApiParams.setUserid("lg-coder@sogou.com");
        result = proxyRegisterApiManager.checkUser(checkUserApiParams);
        System.out.println(result.toString());
        Assert.assertFalse(result.isSuccess());
        checkUserApiParams = new CheckUserApiParams();
        checkUserApiParams.setUserid("13621009174@sohu.com");
        result = proxyRegisterApiManager.checkUser(checkUserApiParams);
        System.out.println(result.toString());
        Assert.assertTrue(result.isSuccess());
    }


}
