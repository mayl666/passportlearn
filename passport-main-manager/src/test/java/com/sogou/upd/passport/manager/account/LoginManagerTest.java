package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.result.APIResultForm;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.manager.api.account.form.AuthUserApiParams;
import com.sogou.upd.passport.manager.form.WebLoginParams;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.atomic.AtomicReference;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-7
 * Time: 下午2:02
 */
public class LoginManagerTest extends BaseTest {

    @Autowired
    private LoginManager LoginManagerImpl;


    private static final int clientId = 1100;
    private static final String username = "18600369478";
    private static final String ip = "192.168.226.174";
    private static final String pwd = "123456";

    @Test
    public void testAccountLogin() {
        try {

            WebLoginParams webLoginParams1 = getParam("13545210241@sohu.com","111111");
            Result result = LoginManagerImpl.accountLogin(webLoginParams1,ip);
            Assert.assertEquals("0", result.getCode());
            Assert.assertEquals("操作成功", result.getMessage());
            Assert.assertEquals("13545210241@sohu.com",result.getModels().get("data"));

            /*String expireResultStr = "{\"data\":{\"userid\":\"13545210241@sohu.com\"},\"statusText\":\"操作成功\",\"status\":\"0\"}";
            APIResultForm expireResult = JacksonJsonMapperUtil.getMapper().readValue(expireResultStr, APIResultForm.class);
            Assert.assertTrue(expireResult.equals(result));


            WebLoginParams webLoginParams2 = getParam("tinkame_test@sogou.com","123456");
            Result result2 = LoginManagerImpl.accountLogin(webLoginParams2,ip);
            String expireResultStr2 = "{\"data\":{\"userid\":\"tinkame_test@sogou.com\"},\"statusText\":\"操作成功\",\"status\":\"0\"}";
            APIResultForm expireResult2 = JacksonJsonMapperUtil.getMapper().readValue(expireResultStr2, APIResultForm.class);
            Assert.assertTrue(expireResult2.equals(result2));

            WebLoginParams webLoginParams3 = getParam("tinkame@126.com","123456");
            Result result3 = LoginManagerImpl.accountLogin(webLoginParams3,ip);
            String expireResultStr3 = "{\"data\":{\"userid\":\"tinkame_test@sogou.com\"},\"statusText\":\"操作成功\",\"status\":\"0\"}";
            APIResultForm expireResult3 = JacksonJsonMapperUtil.getMapper().readValue(expireResultStr3, APIResultForm.class);
            Assert.assertTrue(expireResult3.equals(result3));      */


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private WebLoginParams getParam(String username,String password){
        WebLoginParams webLoginParams = new WebLoginParams();
        webLoginParams.setClient_id("1100");
        webLoginParams.setUsername(username);
        webLoginParams.setPassword(password);
        return  webLoginParams;
    }
}
