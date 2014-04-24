package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.result.APIResultForm;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.manager.api.account.form.*;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-4-21
 * Time: 下午9:20
 * To change this template use File | Settings | File Templates.
 */
public class RegisterApiManagerTest extends BaseTest {

    @Autowired
    private RegisterApiManager sgRegisterApiManager;
    @Autowired
    private RegisterApiManager proxyRegisterApiManager;


    private static final String both_no_username = "testliuling" + new Random().nextInt(1000) + "@sogou.com";
    private static final String both_no_username_sogou = "test" + new Random().nextInt(1000) + "@sogou.com";
    private static final String both_hava_username_sogou = userid_sogou_1;
    private static final String wrong_format_username = "adminhelpme@sogou.com"; //格式有误的账号
    private static final String both_no_username_mail = "testmail" + new Random().nextInt(100) + "@163.com";
    private static final String username_sogou = userid_sogou_2;
    private static final String username_mail = userid_email;
    private static final int clientId = CommonConstant.SGPP_DEFAULT_CLIENTID;
    private static final String serverSecret = CommonConstant.SGPP_DEFAULT_SERVER_SECRET;
    private static final String EMAIL_REG_VERIFY_URL = "https://account.sogou.com/web/reg/emailverify";
    private static final String LOGIN_INDEX_URL = "https://account.sogou.com";


    private static final String mobile_reged = mobile_2;
    private static final String right_mobile = "13720014130";


    /**
     * 两边都不存在此用户的情况下---检查用户是否存在:只检查@sogou.com账号和外域邮箱账号
     */
    @Test
    public void testCheckUser_1() throws IOException {
        CheckUserApiParams checkUserApiParams = new CheckUserApiParams();
        checkUserApiParams.setUserid(both_no_username);
//        Result expectResult = proxyRegisterApiManager.checkUser(checkUserApiParams);
        String expectResult = "{\"statusText\":\"操作成功\",\"data\":{},\"status\":\"0\"}";
        APIResultForm expectForm = JacksonJsonMapperUtil.getMapper().readValue(expectResult.toString(), APIResultForm.class);
        Result actualResult = sgRegisterApiManager.checkUser(checkUserApiParams);
        APIResultForm actualFrom = JacksonJsonMapperUtil.getMapper().readValue(actualResult.toString(), APIResultForm.class);
        Assert.assertTrue(expectForm.getStatus().equals(actualFrom.getStatus()));
    }

    /**
     * 两边都存在此搜狗用户的情况下---检查用户是否存在:只检查@sogou.com账号和外域邮箱账号
     */
    @Test
    public void testCheckUser_2() throws IOException {
        CheckUserApiParams checkUserApiParams = new CheckUserApiParams();
        checkUserApiParams.setUserid(username_sogou);
//        Result expectResult = proxyRegisterApiManager.checkUser(checkUserApiParams);
        String expectResult = "{\"statusText\":\"用户名已经存在\",\"data\":{\"flag\":\"1\",\"userid\":\"" + username_sogou + "\"},\"status\":\"20294\"}";
        APIResultForm expectForm = JacksonJsonMapperUtil.getMapper().readValue(expectResult.toString(), APIResultForm.class);
        Result actualResult = sgRegisterApiManager.checkUser(checkUserApiParams);
        APIResultForm actualForm = JacksonJsonMapperUtil.getMapper().readValue(actualResult.toString(), APIResultForm.class);
        Assert.assertTrue(expectForm.equals(actualForm));
    }

    /**
     * 两边都存在此外域邮箱用户的情况下---检查用户是否存在:只检查@sogou.com账号和外域邮箱账号
     */
    @Test
    public void testCheckUser_3() throws IOException {
        CheckUserApiParams checkUserApiParams = new CheckUserApiParams();
        checkUserApiParams.setUserid(username_mail);
//        Result expectResult = proxyRegisterApiManager.checkUser(checkUserApiParams);
        String expectResult = "{\"statusText\":\"用户名已经存在\",\"data\":{\"flag\":\"1\",\"userid\":\"" + username_mail + "\"},\"status\":\"20294\"}";
        APIResultForm expectForm = JacksonJsonMapperUtil.getMapper().readValue(expectResult.toString(), APIResultForm.class);
        Result actualResult = sgRegisterApiManager.checkUser(checkUserApiParams);
        APIResultForm actualForm = JacksonJsonMapperUtil.getMapper().readValue(actualResult.toString(), APIResultForm.class);
        Assert.assertTrue(expectForm.equals(actualForm));
    }


    /**
     * 两边都不存在此外域邮箱的情况下---正式注册搜狗账号和外域邮箱账号
     * todo 外域邮箱发送激活邮件在manager中无法发送成功 且搜狗的返回值中没有塞userid
     */
    @Test
    public void testRegMailUser_1() throws IOException {
        RegEmailApiParams regEmailApiParams = new RegEmailApiParams(both_no_username_mail, password, ip,
                clientId, EMAIL_REG_VERIFY_URL);
//        Result expectResult = proxyRegisterApiManager.regMailUser(regEmailApiParams);
//        System.out.println(expectResult.toString());
        String expectResult = "{\"statusText\":\"注册成功\",\"data\":{\"userid\":\"" + both_no_username_mail + "\",\"isSetCookie\":false},\"status\":\"0\"}";
        APIResultForm expectForm = JacksonJsonMapperUtil.getMapper().readValue(expectResult.toString(), APIResultForm.class);
        Result actualResult = sgRegisterApiManager.regMailUser(regEmailApiParams);
        APIResultForm actualForm = JacksonJsonMapperUtil.getMapper().readValue(actualResult.toString(), APIResultForm.class);
//        Assert.assertTrue(expectForm.equals(actualForm));
    }

    /**
     * 两边都不存在此搜狗账号的情况下---正式注册搜狗账号和外域邮箱账号
     */
    @Test
    public void testRegMailUser_2() throws IOException {
        RegEmailApiParams regEmailApiParams = new RegEmailApiParams(both_no_username_sogou, password, ip,
                clientId, LOGIN_INDEX_URL);
        Result expectResult = proxyRegisterApiManager.regMailUser(regEmailApiParams);
        System.out.println(expectResult.toString());
//        String expectResult = "{\"statusText\":\"注册成功\",\"data\":{\"userid\":\"" + both_no_username_sogou + "\",\"isSetCookie\":true},\"status\":\"0\"}";
        APIResultForm expectForm = JacksonJsonMapperUtil.getMapper().readValue(expectResult.toString(), APIResultForm.class);
        Result actualResult = sgRegisterApiManager.regMailUser(regEmailApiParams);
        APIResultForm actualForm = JacksonJsonMapperUtil.getMapper().readValue(actualResult.toString(), APIResultForm.class);
        Assert.assertTrue(expectForm.equals(actualForm));
    }

    /**
     * 两边都存在此搜狗账号的情况下---正式注册搜狗账号和外域邮箱账号
     * todo 当用户在两边都存在时，再注册时，搜狐会提示该用户已经注册，但搜狗则会执行更新操作,需要修改注册方法！！！
     */
    @Test
    public void testRegMailUser_3() throws IOException {
        RegEmailApiParams regEmailApiParams = new RegEmailApiParams(both_hava_username_sogou, password, ip,
                clientId, LOGIN_INDEX_URL);
//        Result expectResult = proxyRegisterApiManager.regMailUser(regEmailApiParams);
//        System.out.println(expectResult.toString());
        String expectResult = "{\"statusText\":\"账号已注册\",\"data\":{},\"status\":\"20201\"}";
        APIResultForm expectForm = JacksonJsonMapperUtil.getMapper().readValue(expectResult.toString(), APIResultForm.class);
        Result actualResult = sgRegisterApiManager.regMailUser(regEmailApiParams);
        APIResultForm actualForm = JacksonJsonMapperUtil.getMapper().readValue(actualResult.toString(), APIResultForm.class);
//        Assert.assertTrue(!expectForm.equals(actualForm)); //todo 状态码不一致
    }


    /**
     * 账号格式有问题
     */
    @Test
    public void testRegMailUser_4() throws IOException {
        RegEmailApiParams regEmailApiParams = new RegEmailApiParams(wrong_format_username, password, ip,
                clientId, LOGIN_INDEX_URL);
//        Result expectResult = proxyRegisterApiManager.regMailUser(regEmailApiParams);
        String expectResult = "{\"statusText\":\"非法userid\",\"data\":{},\"status\":\"20239\"}";
        APIResultForm expectForm = JacksonJsonMapperUtil.getMapper().readValue(expectResult, APIResultForm.class);
        Result actualResult = sgRegisterApiManager.regMailUser(regEmailApiParams);
        APIResultForm acturalForm = JacksonJsonMapperUtil.getMapper().readValue(actualResult.toString(), APIResultForm.class);
//        Assert.assertTrue(!expectForm.equals(acturalForm)); //todo sohu非法，sogou却通过
    }

    /**
     * 发送手机短信验证码  新账号注册
     * todo 检查项：1 帐号是否已经注册 2 手机号是否已绑定其他账号 3 今天的短信验证码是否已经达到上限
     */
    @Test
    public void testSendMobileRegCaptcha_1() throws IOException {
        BaseMoblieApiParams params = new BaseMoblieApiParams();
        params.setMobile(new_mobile);
        long ct = System.currentTimeMillis();
        params.setCt(ct);
        params.setClient_id(clientId);
        String code = ManagerHelper.generatorCodeGBK(new_mobile, clientId, serverSecret, ct);
        params.setCode(code);
//        Result expectResult = proxyRegisterApiManager.sendMobileRegCaptcha(params);
//        System.out.println(new_mobile);
//        System.out.println(expectResult.toString());
        String expectResult = "{\"statusText\":\"验证码已发送至" + new_mobile + "\",\"data\":{},\"status\":\"0\"}";
        APIResultForm expectForm = JacksonJsonMapperUtil.getMapper().readValue(expectResult.toString(), APIResultForm.class);
        Result actualResult = sgRegisterApiManager.sendMobileRegCaptcha(params);
        APIResultForm actualForm = JacksonJsonMapperUtil.getMapper().readValue(actualResult.toString(), APIResultForm.class);
        Assert.assertEquals(expectForm, actualForm);
    }

    /**
     * 发送手机短信验证码  账号已注册或绑定
     * todo 检查项：1 帐号是否已经注册 2 手机号是否已绑定其他账号 3 今天的短信验证码是否已经达到上限
     */
    @Test
    public void testSendMobileRegCaptcha_2() throws IOException {
        BaseMoblieApiParams params = new BaseMoblieApiParams();
        params.setMobile(mobile_2);
        long ct = System.currentTimeMillis();
        params.setCt(ct);
        params.setClient_id(clientId);
        String code = ManagerHelper.generatorCodeGBK(mobile_2, clientId, serverSecret, ct);
        params.setCode(code);
//        Result expectResult = proxyRegisterApiManager.sendMobileRegCaptcha(params);
//        System.out.println(expectResult.toString());
        String expectResult = "{\"statusText\":\"账号已注册\",\"data\":{},\"status\":\"20201\"}";
        APIResultForm expectForm = JacksonJsonMapperUtil.getMapper().readValue(expectResult.toString(), APIResultForm.class);
        Result actualResult = sgRegisterApiManager.sendMobileRegCaptcha(params);
        APIResultForm actualForm = JacksonJsonMapperUtil.getMapper().readValue(actualResult.toString(), APIResultForm.class);
//        Assert.assertTrue(!expectForm.getStatus().equals(actualForm.getStatus())); //todo 当手机号已经注册时，搜狗没有提示已经注册，业务逻辑还没移到此分支，且手机号注册时还需要写映射表
    }

    /**
     * 注册手机号@sohu.com的账号----1.手机号已经被注册过,且验证码错误或已经过期    会先报验证码的错误
     * todo  检查项：1 该手机号是否已经注册过，2 是否已经被绑定，3 是否发送验证码次数超限，4 验证码校验次数是否超限，5 验证码是否错误或过期
     * 搜狗只做了4，5，其它1，2，3都没做，需要 todo 补充
     */
    @Test
    public void testRegMobileCaptchaUser_1() throws IOException {
        RegMobileCaptchaApiParams params = new RegMobileCaptchaApiParams();
        params.setMobile(mobile_2);
        params.setPassword(password);
        params.setCaptcha("12x4");
        long ct = System.currentTimeMillis();
        params.setCt(ct);
        params.setClient_id(clientId);
        String code = ManagerHelper.generatorCodeGBK(mobile_2, clientId, serverSecret, ct);
        params.setCode(code);
//        Result expectResult = proxyRegisterApiManager.regMobileCaptchaUser(params);
        String expectResult = "{\"statusText\":\"验证码错误或已过期\",\"data\":{},\"status\":\"20208\"}";
        APIResultForm expectForm = JacksonJsonMapperUtil.getMapper().readValue(expectResult.toString(), APIResultForm.class);
        Result actualResult = sgRegisterApiManager.regMobileCaptchaUser(params);
        APIResultForm actualForm = JacksonJsonMapperUtil.getMapper().readValue(actualResult.toString(), APIResultForm.class);
//        Assert.assertTrue(!expectForm.getStatus().equals(actualForm.getStatus())); //todo 搜狐和搜狗返回错误码不一致，需要修改成一致的
    }


    /**
     * 注册手机号@sohu.com的账号----1.手机账号验证码正式注册
     */
    @Test
    public void testRegMobileCaptchaUser_2() throws IOException {
        RegMobileCaptchaApiParams params = new RegMobileCaptchaApiParams();
        params.setMobile(new_mobile);
        params.setPassword(password);
        params.setCaptcha("8824");     //sohu验证码四位数  todo 需要拿到下发的真实验证码
        long ct = System.currentTimeMillis();
        params.setCt(ct);
        params.setClient_id(clientId);
        String code = ManagerHelper.generatorCodeGBK(new_mobile, clientId, serverSecret, ct);
        params.setCode(code);
//        Result expectResult = proxyRegisterApiManager.regMobileCaptchaUser(params);
        String expectResult = "{\"statusText\":\"注册成功\",\"data\":{\"userid\":\"" + new_mobile + "\",\"isSetCookie\":true},\"status\":\"0\"}";
        APIResultForm expectForm = JacksonJsonMapperUtil.getMapper().readValue(expectResult.toString(), APIResultForm.class);
        params.setCaptcha("67629");//搜狗验证码五位数   todo 需要拿到下发的真实验证码
        Result actualResult = sgRegisterApiManager.regMobileCaptchaUser(params);
        APIResultForm actualForm = JacksonJsonMapperUtil.getMapper().readValue(actualResult.toString(), APIResultForm.class);
//        Assert.assertTrue(expectForm.getStatus().equals(actualForm.getStatus()));
    }


    /**
     * 注册手机号@sohu.com的账号----不需要短信验证码的手机注册
     * todo  检查项：1 该手机号是否已经注册过，2 是否已经被绑定
     */
    @Test
    public void testRegMobileUser_1() throws IOException {
        RegMobileApiParams params = new RegMobileApiParams();
        params.setMobile(new_mobile);
        params.setPassword(password);
        long ct = System.currentTimeMillis();
        params.setCt(ct);
        params.setClient_id(clientId);
        String code = ManagerHelper.generatorCodeGBK(new_mobile, clientId, serverSecret, ct);
        params.setCode(code);
        Result expectResult = proxyRegisterApiManager.regMobileUser(params);
        System.out.println(expectResult.toString());
//        String expectResult1 = "{\"statusText\":\"参数错误,请输入必填的参数\",\"data\":{},\"status\":\"10002\"}";
        String expectResult2 = "{\"statusText\":\"注册成功\",\"data\":{\"userid\":\"" + new_mobile + "\",\"isSetCookie\":false},\"status\":\"0\"}";
        APIResultForm expectForm = JacksonJsonMapperUtil.getMapper().readValue(expectResult.toString(), APIResultForm.class);
//        Result resultSG = sgRegisterApiManager.regMobileUser(params);
//        System.out.println(resultSG.toString());
//        APIResultForm formSG = JacksonJsonMapperUtil.getMapper().readValue(resultSG.toString(), APIResultForm.class);
//        Assert.assertTrue(expectForm.getStatus().equals(formSG.getStatus()));
    }

}

