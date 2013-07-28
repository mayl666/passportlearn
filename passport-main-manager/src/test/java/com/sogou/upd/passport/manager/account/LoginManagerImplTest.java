package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.form.WebLoginParams;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.atomic.AtomicReference;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-7
 * Time: 下午2:02
 */
public class LoginManagerImplTest extends BaseTest {

    @Autowired
    private LoginManager LoginManagerImpl;


    private static final int clientId = 1100;
    private static final String username = "18600369478";
    private static final String ip = "192.168.226.174";
    private static final String pwd = "123456";
//    private static String passpword = Coder.encryptMD5("spz1986411");
    @Test
    public void testAccountLogin() {
        try {
            AtomicReference<WebLoginParams> webLoginParameters = new AtomicReference<WebLoginParams>(new WebLoginParams());
            webLoginParameters.get().setUsername(username);
//            String pwdMD5 = needMD5 ? DigestUtils.md5Hex(pwd.getBytes()) : pwd;
//            webLoginParameters.get().setPassword(Coder.encryptMD5("123456"));
            webLoginParameters.get().setPassword(pwd);
//            webLoginParameters.get().setCaptcha("ssss");
            Result result =LoginManagerImpl.accountLogin(webLoginParameters.get(),ip,"http");
            System.out.println("testAccountLogin:"+result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
