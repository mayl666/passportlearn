package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.manager.api.account.form.BaseMobileApiParams;
import com.sogou.upd.passport.manager.api.account.form.CheckUserApiParams;
import com.sogou.upd.passport.manager.api.account.form.RegEmailApiParams;
import com.sogou.upd.passport.manager.api.account.form.RegMobileCaptchaApiParams;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.MobilePassportMappingService;
import com.sogou.upd.passport.service.account.generator.PassportIDGenerator;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-3-21
 * Time: 上午10:20
 * To change this template use File | Settings | File Templates.
 */
@ContextConfiguration(locations = {"classpath:spring-config-test.xml"})
public class SGRegisterApiManagerTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private RegisterApiManager sgRegisterApiManager;
    @Autowired
    private AccountService accountService;

    //手机账号
    private final static String MOBILE = "13581695053";
    private static final int PROVIDER_PHONE = AccountTypeEnum.PHONE.getValue();
    private final static String PASSPORT_MOBILE = PassportIDGenerator.generator(MOBILE, PROVIDER_PHONE);
    //搜狗账号
    private final static String SOGOU = "liulingtest@sogou.com";
    private static final int PROVIDER_SOGOU = AccountTypeEnum.EMAIL.getValue();
    private final static String PASSPORT_SOGOU = PassportIDGenerator.generator(SOGOU, PROVIDER_SOGOU);
    //邮箱账号
    private final static String MAIL = "liuling9460@163.com";
    private static final int PROVIDER_MAIL = AccountTypeEnum.EMAIL.getValue();
    private final static String PASSPORT_MAIL = PassportIDGenerator.generator(MAIL, PROVIDER_MAIL);

    private final static int CLIENT_ID = CommonConstant.SGPP_DEFAULT_CLIENTID;
    private final static String PASSWORD = "111111";
    private final static String CAPTHCA = "32815";
    private final static String IP = "127.0.0.1";

    /**
     * 验证搜狗、手机、外域账号是否存在
     */
    @Test
    public void testCheckUser() {
        //检查手机号是否存在
        CheckUserApiParams checkUserApiParamsForPhone = new CheckUserApiParams();
        checkUserApiParamsForPhone.setUserid(MOBILE);
        checkUserApiParamsForPhone.setClient_id(CLIENT_ID);
        Result resultPhone = sgRegisterApiManager.checkUser(checkUserApiParamsForPhone);
        Assert.assertFalse(resultPhone.isSuccess());

        //检查个性账号是否存在
        CheckUserApiParams checkUserApiParamsSogou = new CheckUserApiParams();
        checkUserApiParamsSogou.setUserid(PASSPORT_SOGOU);
        checkUserApiParamsSogou.setClient_id(CLIENT_ID);
        Result resultSogou = sgRegisterApiManager.checkUser(checkUserApiParamsSogou);
        Assert.assertFalse(resultSogou.isSuccess());

        //检查邮箱账号是否存在
        CheckUserApiParams checkUserApiParamsMail = new CheckUserApiParams();
        checkUserApiParamsSogou.setUserid(MAIL);
        checkUserApiParamsSogou.setClient_id(CLIENT_ID);
        Result resultMail = sgRegisterApiManager.checkUser(checkUserApiParamsMail);
        Assert.assertFalse(resultMail.isSuccess());
    }

    /**
     * 给注册手机号发送短信，前提是手机号必须不存在
     *
     * @throws Exception
     */
    @Test
    public void testSendRegMobileSms() throws Exception {
        String passportId = PassportIDGenerator.generator(MOBILE, PROVIDER_PHONE);
        Account account = accountService.queryAccountByPassportId(passportId);
        Assert.assertNull(account);
        BaseMobileApiParams baseMobileApiParams = new BaseMobileApiParams();
        baseMobileApiParams.setClient_id(CLIENT_ID);
        baseMobileApiParams.setMobile(MOBILE);
        Result result = sgRegisterApiManager.sendMobileRegCaptcha(baseMobileApiParams);
        Assert.assertTrue(result.isSuccess());
    }


    /**
     * 注册手机号码
     */
    @Test
    public void testRegMobileCaptchaUser() {
        RegMobileCaptchaApiParams regMobileCaptchaApiParams = new RegMobileCaptchaApiParams();
        regMobileCaptchaApiParams.setClient_id(CLIENT_ID);
        regMobileCaptchaApiParams.setMobile(MOBILE);
        regMobileCaptchaApiParams.setCaptcha(CAPTHCA);
        regMobileCaptchaApiParams.setIp(IP);
        regMobileCaptchaApiParams.setPassword(PASSWORD);
        Result result = sgRegisterApiManager.regMobileCaptchaUser(regMobileCaptchaApiParams);
        Assert.assertTrue(result.isSuccess());
        String userid = PassportIDGenerator.generator(MOBILE, PROVIDER_PHONE);
        Assert.assertEquals(userid, (String) result.getModels().get("userid"));
        Assert.assertEquals("注册成功！", result.getMessage());
        Assert.assertTrue((Boolean) result.getModels().get("isSetCookie"));
    }


    /**
     * 注册搜狗账号
     */
    @Test
    public void testRegSogouUser() {
        RegEmailApiParams regEmailApiParams = new RegEmailApiParams(PASSPORT_SOGOU, PASSWORD, IP, CLIENT_ID, null);
        Result result = sgRegisterApiManager.regMailUser(regEmailApiParams);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(PASSPORT_SOGOU, (String) result.getModels().get("userid"));
        Assert.assertEquals("注册成功！", result.getMessage());
        Assert.assertTrue((Boolean) result.getModels().get("isSetCookie"));
    }


    /**
     * 注册邮箱账号，manager自测没法通过，提示找不到.vm文件，必须在controller层测试
     */
    @Test
    public void testRegMailUser() {
        RegEmailApiParams regEmailApiParams = new RegEmailApiParams(PASSPORT_MAIL, PASSWORD, IP, CLIENT_ID, null);
        Result result = sgRegisterApiManager.regMailUser(regEmailApiParams);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(PASSPORT_SOGOU, (String) result.getModels().get("userid"));
        Assert.assertEquals("注册成功！", result.getMessage());
        Assert.assertFalse((Boolean) result.getModels().get("isSetCookie"));
    }
}


