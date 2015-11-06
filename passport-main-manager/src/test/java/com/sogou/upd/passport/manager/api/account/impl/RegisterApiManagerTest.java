package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.result.APIResultForm;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.manager.api.account.form.RegEmailApiParams;
import com.sogou.upd.passport.manager.api.account.form.RegMobileApiParams;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
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
    private RegisterApiManager registerApiManager;

    private static final String both_no_username = "testliuling" + new Random().nextInt(1000) + "@sogou.com";
    private static final String both_no_gexing = "135test94" + new Random().nextInt(1000);
    private static final String both_no_username_sogou = "test" + new Random().nextInt(2000) + "@sogou.com";
    private static final String both_hava_username_sogou = userid_sogou_1;
    private static final String wrong_format_username = "soadminuhuanqitst45@sogou.com"; //格式有误的账号
    private static final String wrong_format = "testjisjf_c.com.com.com"; //格式有误的账号
    private static final String both_no_username_mail = "testmail" + new Random().nextInt(100) + "@163.com";
    private static final String username_sogou = userid_sogou_2;
    private static final String username_mail = userid_email;

    private static final String EMAIL_REG_VERIFY_URL = "https://account.sogou.com/web/reg/emailverify";
    private static final String LOGIN_INDEX_URL = "https://account.sogou.com";
    private static final String mobile_reged = mobile_2;
    private static final String right_mobile = "13720014130";

    /**
     * 两边都不存在此用户的情况下---检查用户是否存在:只检查@sogou.com账号和外域邮箱账号
     */
    @Test
    public void testCheckUser_1() throws IOException {
        String expectResult = "{\"statusText\":\"操作成功\",\"data\":{},\"status\":\"0\"}";
        APIResultForm expectForm = JacksonJsonMapperUtil.getMapper().readValue(expectResult.toString(), APIResultForm.class);
        Result actualResult = registerApiManager.checkUser(both_no_username, clientId,false);
        APIResultForm actualFrom = JacksonJsonMapperUtil.getMapper().readValue(actualResult.toString(), APIResultForm.class);
        Assert.assertTrue(expectForm.equals(actualFrom));
    }

    /**
     * 两边都存在此搜狗用户的情况下---检查用户是否存在:只检查@sogou.com账号和外域邮箱账号
     */
    @Test
    public void testCheckUser_2() throws IOException {
        String expectResult = "{\"statusText\":\"用户名已经存在\",\"data\":{\"flag\":\"1\",\"userid\":\"" + username_sogou + "\"},\"status\":\"20294\"}";
        APIResultForm expectForm = JacksonJsonMapperUtil.getMapper().readValue(expectResult.toString(), APIResultForm.class);
        Result actualResult = registerApiManager.checkUser(mobile_2, clientId,false);
        APIResultForm actualForm = JacksonJsonMapperUtil.getMapper().readValue(actualResult.toString(), APIResultForm.class);
        Assert.assertTrue(expectForm.equals(actualForm));
    }

    /**
     * 两边都存在此外域邮箱用户的情况下---检查用户是否存在:只检查@sogou.com账号和外域邮箱账号
     */
    @Test
    public void testCheckUser_3() throws IOException {
        String expectResult = "{\"statusText\":\"用户名已经存在\",\"data\":{\"flag\":\"1\",\"userid\":\"" + username_mail + "\"},\"status\":\"20294\"}";
        APIResultForm expectForm = JacksonJsonMapperUtil.getMapper().readValue(expectResult.toString(), APIResultForm.class);
        Result actualResult = registerApiManager.checkUser(username_mail, clientId,false);
        APIResultForm actualForm = JacksonJsonMapperUtil.getMapper().readValue(actualResult.toString(), APIResultForm.class);
        Assert.assertTrue(expectForm.equals(actualForm));
    }

    /**
     * 两边都存在此手机号的情况下
     *
     * @throws IOException
     */
    @Test
    public void testCheckUser_4() throws IOException {
        String expectResult = "{\"statusText\":\"用户名已经存在\",\"data\":{\"flag\":\"1\",\"userid\":\"" + mobile_2 + "@sohu.com\"},\"status\":\"20294\"}";
        APIResultForm expectForm = JacksonJsonMapperUtil.getMapper().readValue(expectResult.toString(), APIResultForm.class);
        Result actualResult = registerApiManager.checkUser(mobile_2+"@sohu.com", clientId,false);
        APIResultForm actualForm = JacksonJsonMapperUtil.getMapper().readValue(actualResult.toString(), APIResultForm.class);
        Assert.assertTrue(expectForm.equals(actualForm));
    }

    /**
     * 两边都不存在此个性账号的情况下用户的情况下---检查用户是否存在:只检查@sogou.com账号和外域邮箱账号
     */
    @Test
    public void testCheckUser_5() throws IOException {
        String expectResult = "{\"data\":{\"userid\":\"" + both_no_gexing + "\"},\"statusText\":\"非法userid\",\"status\":\"20239\"}";
        APIResultForm expectForm = JacksonJsonMapperUtil.getMapper().readValue(expectResult.toString(), APIResultForm.class);
        Result actualResult = registerApiManager.checkUser(both_no_gexing, clientId,false);
        APIResultForm actualFrom = JacksonJsonMapperUtil.getMapper().readValue(actualResult.toString(), APIResultForm.class);
        Assert.assertTrue(expectForm.equals(actualFrom));
    }

    /**
     * 格式错误的个性账号---检查用户是否存在:只检查@sogou.com账号和外域邮箱账号
     */
    @Test
    public void testCheckUser_6() throws IOException {
        String expectResult = "{\"data\":{\"userid\":\"" + wrong_format + "\"},\"statusText\":\"非法userid\",\"status\":\"20239\"}";
        APIResultForm expectForm = JacksonJsonMapperUtil.getMapper().readValue(expectResult.toString(), APIResultForm.class);
        Result actualResult = registerApiManager.checkUser(wrong_format, clientId,false);
        APIResultForm actualFrom = JacksonJsonMapperUtil.getMapper().readValue(actualResult.toString(), APIResultForm.class);
        Assert.assertTrue(expectForm.equals(actualFrom));
    }


    /**
     * 格式错误的个性账号---检查用户是否存在:只检查@sogou.com账号和外域邮箱账号
     */
    @Test
    public void testCheckUser_7() throws IOException {
        Result actualResult = registerApiManager.checkUser("sogou", clientId,false);
        APIResultForm actualFrom = JacksonJsonMapperUtil.getMapper().readValue(actualResult.toString(), APIResultForm.class);
    }

    /**
     * 发送手机短信验证码
     */
    @Test
    public void testSendMobileRegCaptcha() throws IOException {
        long ct = System.currentTimeMillis();
        //新手机，发送验证码
        String expectResult = "{\"statusText\":\"验证码已发送至" + new_mobile + "\",\"data\":{},\"status\":\"0\"}";
        APIResultForm expectForm = JacksonJsonMapperUtil.getMapper().readValue(expectResult.toString(), APIResultForm.class);
        Result actualResult = registerApiManager.sendMobileRegCaptcha(clientId, new_mobile);
        APIResultForm actualForm = JacksonJsonMapperUtil.getMapper().readValue(actualResult.toString(), APIResultForm.class);
        Assert.assertEquals(expectForm, actualForm);

        //手机已经注册发送验证码 note:线上已经是搜狗发送短信验证码了，所以此处不需要跟sohu接口返回结果做对比，只需跟原有线上返回结果做对比即可
        String code1 = ManagerHelper.generatorCodeGBK(mobile_2, clientId, serverSecret, ct);
        String expectResult1 = "{\"statusText\":\"手机号已绑定其他账号\",\"data\":{\"userid\":\"" + mobile_2 + "@sohu.com\"},\"status\":\"20225\"}";
        APIResultForm expectForm1 = JacksonJsonMapperUtil.getMapper().readValue(expectResult1.toString(), APIResultForm.class);
        Result actualResult1 = registerApiManager.sendMobileRegCaptcha(clientId, mobile_2);
        APIResultForm actualForm1 = JacksonJsonMapperUtil.getMapper().readValue(actualResult1.toString(), APIResultForm.class);
        Assert.assertEquals(expectForm1, actualForm1);   //todo 手机号注册时还需要写映射表  done

        //手机已经绑定主账号发送验证码 note:线上已经是搜狗发送短信验证码了，所以此处不需要跟sohu接口返回结果做对比，只需跟原有线上返回结果做对比即可
        String code2 = ManagerHelper.generatorCodeGBK(mobile_1, clientId, serverSecret, ct);
        String expectResult2 = "{\"statusText\":\"手机号已绑定其他账号\",\"data\":{\"userid\":\"loveerin@sogou.com\"},\"status\":\"20225\"}";
        APIResultForm expectForm2 = JacksonJsonMapperUtil.getMapper().readValue(expectResult2.toString(), APIResultForm.class);
        Result actualResult2 = registerApiManager.sendMobileRegCaptcha(clientId, mobile_1);
        System.out.println(actualResult2.toString());
        APIResultForm actualForm2 = JacksonJsonMapperUtil.getMapper().readValue(actualResult2.toString(), APIResultForm.class);
        Assert.assertEquals(expectForm2, actualForm2);
    }

    /**
     * 两边都不存在此外域邮箱的情况下---正式注册搜狗账号和外域邮箱账号
     * todo 外域邮箱发送激活邮件在manager中无法发送成功
     */
    @Test
    public void testRegMailUser_1() throws IOException {
        RegEmailApiParams regEmailApiParams = new RegEmailApiParams(both_no_username_mail, password, ip,
                clientId, EMAIL_REG_VERIFY_URL);
//        String expectResult = "{\"statusText\":\"注册成功\",\"data\":{\"userid\":\"" + both_no_username_mail + "\",\"isSetCookie\":false},\"status\":\"0\"}";
        Result actualResult = registerApiManager.regMailUser(regEmailApiParams);
        APIResultForm actualForm = JacksonJsonMapperUtil.getMapper().readValue(actualResult.toString(), APIResultForm.class);
    }

    /**
     * 两边都不存在此搜狗账号的情况下---正式注册搜狗账号和外域邮箱账号  done
     */
    @Test
    public void testRegMailUser_2() throws IOException {
        RegEmailApiParams regEmailApiParams = new RegEmailApiParams(both_no_username_sogou, password, ip,
                clientId, LOGIN_INDEX_URL);
//        String expectResult = "{\"statusText\":\"注册成功\",\"data\":{\"userid\":\"" + both_no_username_sogou + "\",\"isSetCookie\":true},\"status\":\"0\"}";
        Result actualResult = registerApiManager.regMailUser(regEmailApiParams);
        APIResultForm actualForm = JacksonJsonMapperUtil.getMapper().readValue(actualResult.toString(), APIResultForm.class);
    }

    /**
     * 两边都存在此搜狗账号的情况下---正式注册搜狗账号和外域邮箱账号
     * 当用户在两边都存在时，再注册时，搜狐会提示该用户已经注册，但搜狗则会执行更新操作,需要修改注册方法！！！ 已经修改！  done
     */
    @Test
    public void testRegMailUser_3() throws IOException {
        RegEmailApiParams regEmailApiParams = new RegEmailApiParams(both_hava_username_sogou, password, ip,
                clientId, LOGIN_INDEX_URL);
//        System.out.println(expectResult.toString());
//        String expectResult = "{\"statusText\":\"账号已注册\",\"data\":{},\"status\":\"20201\"}";
        Result actualResult = registerApiManager.regMailUser(regEmailApiParams);
        APIResultForm actualForm = JacksonJsonMapperUtil.getMapper().readValue(actualResult.toString(), APIResultForm.class);
    }


    /**
     * @sogou.com 账号格式有问题   done
     */
    @Test
    public void testRegMailUser_4() throws IOException {
        RegEmailApiParams regEmailApiParams = new RegEmailApiParams(wrong_format_username, password, ip,
                clientId, LOGIN_INDEX_URL);
//        String expectResult = "{\"statusText\":\"参数错误,请输入必填的参数\",\"data\":{},\"status\":\"10002\"}";
        Result actualResult = registerApiManager.regMailUser(regEmailApiParams);
        APIResultForm acturalForm = JacksonJsonMapperUtil.getMapper().readValue(actualResult.toString(), APIResultForm.class);
    }

    /**
     * 个性账号 账号格式有问题
     */
    @Test
    public void testRegMailUser_5() throws IOException {
        RegEmailApiParams regEmailApiParams = new RegEmailApiParams(wrong_format, password, ip,
                clientId, LOGIN_INDEX_URL);
//        String expectResult = "{\"statusText\":\"参数错误,请输入必填的参数\",\"data\":{},\"status\":\"10002\"}";
        Result actualResult = registerApiManager.regMailUser(regEmailApiParams);
        APIResultForm acturalForm = JacksonJsonMapperUtil.getMapper().readValue(actualResult.toString(), APIResultForm.class);
    }


    /**
     * 手机号注册接口测试用例（不需要验证，发验证码由SG发送）
     *
     * @throws IOException
     */
    @Test
    public void testRegMobileUser() throws IOException {
        RegMobileApiParams params = new RegMobileApiParams();
        params.setPassword(password);
        long ct = System.currentTimeMillis();
        params.setCt(ct);
        params.setClient_id(clientId);
        String code = ManagerHelper.generatorCodeGBK(new_mobile, clientId, serverSecret, ct);
        params.setCode(code);
        //可以正常注册
        params.setMobile(new_mobile);
        String expectResult = "{\"statusText\":\"注册成功\",\"data\":{\"userid\":\"" + new_mobile + "@sohu.com\",\"isSetCookie\":false},\"status\":\"0\"}";
        APIResultForm expectForm = JacksonJsonMapperUtil.getMapper().readValue(expectResult.toString(), APIResultForm.class);
        Result resultSG = registerApiManager.regMobileUser(params);
        APIResultForm formSG = JacksonJsonMapperUtil.getMapper().readValue(resultSG.toString(), APIResultForm.class);
        Assert.assertTrue(expectForm.equals(formSG));
        //手机号格式有误
        params.setMobile("135xxx3xxx41");
        String expectResult_format = "{\"statusText\":\"参数错误,请输入必填的参数\",\"data\":{},\"status\":\"10002\"}";
        APIResultForm expectForm_format = JacksonJsonMapperUtil.getMapper().readValue(expectResult_format.toString(), APIResultForm.class);
        Result resultSG_format = registerApiManager.regMobileUser(params);
        APIResultForm formSG_format = JacksonJsonMapperUtil.getMapper().readValue(resultSG_format.toString(), APIResultForm.class);
        Assert.assertTrue(expectForm_format.equals(formSG_format));
        //手机号已注册或绑定
        params.setMobile("13545210241");
        String expectResult_exist = "{\"data\":{},\"status\":\"20201\",\"statusText\":\"账号已注册\"}";
        APIResultForm expectForm_exist = JacksonJsonMapperUtil.getMapper().readValue(expectResult_exist.toString(), APIResultForm.class);
        Result resultSG_exist = registerApiManager.regMobileUser(params);
        System.out.println(resultSG_exist.toString());
        APIResultForm formSG_exist = JacksonJsonMapperUtil.getMapper().readValue(resultSG_exist.toString(), APIResultForm.class);
        Assert.assertTrue(expectForm_exist.equals(formSG_exist));
    }

}

