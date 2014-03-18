package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.form.WebRegisterParams;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-3-18
 * Time: 下午2:31
 * To change this template use File | Settings | File Templates.
 */
public class RegManagerTest extends BaseTest {

    @Autowired
    private RegManager regManager;

    @Test
    public void testCheckUserNotExists() throws Exception {
        Result result;
        result = regManager.isAccountExists(mobile, 1044);
        Assert.assertFalse(result.isSuccess());
    }

    /**
     * 个性账号正式注册，注释掉验证验证码代码
     *
     * @throws Exception
     */
    @Test
    public void testSogouReg() throws Exception {
        Result result;
        WebRegisterParams webRegisterParams = new WebRegisterParams();
        webRegisterParams.setUsername(userid_sogou);
        webRegisterParams.setPassword(password);
        webRegisterParams.setRu(ru);
        webRegisterParams.setClient_id(String.valueOf(clientId));
        result = regManager.webRegister(webRegisterParams, modifyIp);
        String username = (String) result.getModels().get("username");
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(userid + "@sogou.com", username);
    }

    /**
     * 手机号注册，注释掉验证短信验证码代码
     *
     * @throws Exception
     */
    @Test
    public void testMobileReg() throws Exception {
        Result result;
        WebRegisterParams webRegisterParams = new WebRegisterParams();
        webRegisterParams.setUsername(mobile);
        webRegisterParams.setPassword(password);
        webRegisterParams.setRu(ru);
        webRegisterParams.setClient_id(String.valueOf(clientId));
        result = regManager.webRegister(webRegisterParams, modifyIp);
        String username = (String) result.getModels().get("username");
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(mobile + "@sohu.com", username);
    }

    /**
     * 外域邮箱注册
     *
     * @throws Exception
     */
    @Test
    public void testMailReg() throws Exception {
        Result result;
        WebRegisterParams webRegisterParams = new WebRegisterParams();
        webRegisterParams.setUsername(userid_mail);
        webRegisterParams.setPassword(password);
        webRegisterParams.setRu(ru);
        webRegisterParams.setClient_id(String.valueOf(clientId));
        result = regManager.webRegister(webRegisterParams, modifyIp);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals("感谢注册，请立即激活账户！", result.getMessage());
        Assert.assertFalse((Boolean) result.getModels().get("isSetCookie"));
    }
}
