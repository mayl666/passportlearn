package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.result.APIResultForm;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.manager.form.WebRegisterParams;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-5-7
 * Time: 下午5:22
 * To change this template use File | Settings | File Templates.
 */
public class RegManagerTest extends BaseTest {

    @Autowired
    private RegManager regManagerImpl;

    //检查账号是否存在
    private static final String both_no_username_sogou = "testbigday" + new Random().nextInt(1000) + "@sogou.com";
    private static final String both_no_username_gexing = "testfuckday" + new Random().nextInt(1000);
    private static final String both_hava_username_sogou = userid_sogou_1;
    private static final String wrong_format_username = "testjisjf_c.com.com.com";
    private static final String wrong_format_email = "testjisjf_c.com.com.com@gmail.com";
    private static final String bad_format_email = "dfer6}*&ere_9o@163.com";
    private static final String both_no_username_mail = "imatestmail" + new Random().nextInt(1000) + "@163.com";
    private static final String both_hava_username_mail = userid_email;
    private static final int clientId = CommonConstant.SGPP_DEFAULT_CLIENTID;
    private final String both_no_username_mobile = "13245600980";
    private static final String both_hava_username_mobile = mobile_2;
    //正式注册
    private static final String sogou = "testjust" + new Random().nextInt(1000) + "@sogou.com";
    private final String mobile = new_mobile;
    private static final String email = "testfer" + new Random().nextInt(1000) + "@gmail.com";
    private static final String createip = modifyIp;
    private static final String ru = "http://account.sogou.com";
    private static final String email_capthca = "grtyr";
    private static final String sogou_capthca = "g78se";

    @Test
    public void testResult() {
        Result result = new APIResultSupport(false);
        result.setDefaultModel("userid", "11111");
        result.setDefaultModel("userid", "22222");
        System.out.println(result.toString());
    }

    /**
     * 正式注册
     *
     * @throws Exception
     */
    @Test
    public void testWebRegister() throws Exception {
        //搜狗账号注册
        WebRegisterParams params_sogou = getRegParams(sogou, sogou_capthca);
        Result result_sogou = regManagerImpl.webRegister(params_sogou, createip);
        System.out.println(result_sogou.toString());
        APIResultForm actualForm1 = JacksonJsonMapperUtil.getMapper().readValue(result_sogou.toString(), APIResultForm.class);
        String expectString1 = "{\"statusText\":\"注册成功\",\"data\":{\"username\":\"" + sogou + "\",\"userid\":\"" + sogou + "\",\"isSetCookie\":true},\"status\":\"0\"}";
        APIResultForm expectForm1 = JacksonJsonMapperUtil.getMapper().readValue(expectString1, APIResultForm.class);
//        Assert.assertTrue(expectForm1.equals(actualForm1));    todo 需要页面真实验证码

        //外域邮箱账号注册
        WebRegisterParams params_email = getRegParams(email, email_capthca);
        Result result_email = regManagerImpl.webRegister(params_email, createip);
        System.out.println(result_email.toString());
        APIResultForm actualForm2 = JacksonJsonMapperUtil.getMapper().readValue(result_email.toString(), APIResultForm.class);
        String expectString2 = "{\"statusText\":\"注册成功\",\"data\":{\"username\":\"" + email + "\",\"userid\":\"" + email + "\",\"isSetCookie\":false},\"status\":\"0\"}";
        APIResultForm expectForm2 = JacksonJsonMapperUtil.getMapper().readValue(expectString2, APIResultForm.class);
//        Assert.assertTrue(expectForm2.equals(actualForm2));     todo 需要页面真实验证码

        //手机账号注册
        WebRegisterParams params_mobile = getRegParams(mobile, null);
        Result result_mobile = regManagerImpl.webRegister(params_mobile, createip);
        System.out.println(result_mobile.toString());
        APIResultForm actualForm3 = JacksonJsonMapperUtil.getMapper().readValue(result_mobile.toString(), APIResultForm.class);
        String expectString3 = "{\"statusText\":\"注册成功\",\"data\":{\"username\":\"" + mobile + "\",\"userid\":\"" + mobile + "\",\"isSetCookie\":true},\"status\":\"0\"}";
        APIResultForm expectForm3 = JacksonJsonMapperUtil.getMapper().readValue(expectString3, APIResultForm.class);
//        Assert.assertTrue(expectForm3.equals(actualForm3));      todo 需要下发的真实短信验证码
    }

    private WebRegisterParams getRegParams(String username, String captcha) {
        WebRegisterParams params = new WebRegisterParams();
        params.setClient_id(String.valueOf(clientId));
        params.setRu(ru);
        params.setPassword(password);
        params.setCaptcha(captcha);
        params.setUsername(username);
        return params;
    }
}
