package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.result.Result;
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
            AtomicReference<WebLoginParams> webLoginParameters = new AtomicReference<WebLoginParams>(new WebLoginParams());
            webLoginParameters.get().setUsername("13545210241@sohu.com");
            webLoginParameters.get().setPassword("111111");
            webLoginParameters.get().setClient_id("1100");
            Result result =LoginManagerImpl.accountLogin(webLoginParameters.get(),ip,"http");
            Assert.assertEquals("0", result.getCode());


            WebLoginParams webLoginParams = new WebLoginParams();
            webLoginParams.setClient_id("1100");
            webLoginParams.setUsername("tinkame302@sohu.com");
            webLoginParams.setPassword("123456");
            Result result_sohu = LoginManagerImpl.accountLogin(webLoginParams,ip,"https");
            Assert.assertEquals("0", result_sohu.getCode());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
