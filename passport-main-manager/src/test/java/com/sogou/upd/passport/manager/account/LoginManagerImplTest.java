package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.BindApiManager;
import com.sogou.upd.passport.manager.api.account.form.*;
import com.sogou.upd.passport.manager.form.WebLoginParameters;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-7
 * Time: 下午2:02
 */
public class LoginManagerImplTest extends BaseTest {

    @Autowired
    private LoginManager LoginManagerImpl;


    private static final int clientId = 1100;
    private static final String username = "13621009174";
    private static final String ip = "192.168.226.174";
//    private static String passpword = Coder.encryptMD5("spz1986411");
    @Test
    public void testAccountLogin() {
        try {
            WebLoginParameters webLoginParameters = new WebLoginParameters();
            webLoginParameters.setUsername(username);
            webLoginParameters.setPassword(Coder.encryptMD5("spz1986411"));
            webLoginParameters.setCaptcha("");
            Result result =LoginManagerImpl.accountLogin(webLoginParameters,ip);
            System.out.println("testAccountLogin:"+result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
