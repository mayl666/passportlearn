package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.form.PcAuthTokenParams;
import com.sogou.upd.passport.manager.form.PcPairTokenParams;
import com.sogou.upd.passport.manager.form.PcRefreshTokenParams;
import com.sogou.upd.passport.model.account.AccountToken;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-7-29
 * Time: 下午8:57
 * To change this template use File | Settings | File Templates.
 */
public class PCAccountManagerImplTest extends BaseTest {
    @Autowired
    private PCAccountManager pcAccountManager;

    public static String accesstoken = "";
    public static String refreshtoken = "";
    @Test
    public void testCreatePairToken() {
        try {
            PcPairTokenParams params = new PcPairTokenParams();
            params.setAppid(String.valueOf(1044));
            params.setUserid("tinkame700@sogou.com");
            params.setPassword(Coder.encryptMD5("123456"));
            params.setTs("2147483647");

            Result result = pcAccountManager.createPairToken(params);
            System.out.println("testCreatePairToken:"+result.isSuccess());
            AccountToken accountToken = (AccountToken)result.getDefaultModel();
            accesstoken = accountToken.getAccessToken();
            refreshtoken = accountToken.getRefreshToken();

            System.out.println("getPassportId:"+accountToken.getPassportId()+",getInstanceId:"+accountToken.getInstanceId()+
                    ",getAccessToken:"+accountToken.getAccessToken()+",getAccessValidTime:"+accountToken.getAccessValidTime()+",getRefreshToken:"
                    +accountToken.getRefreshToken()+",getRefreshValidTime:"+accountToken.getRefreshValidTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testAuthRefreshToken() {
        try {
            testCreatePairToken();

            PcRefreshTokenParams params = new PcRefreshTokenParams();
            params.setAppid(String.valueOf(1044));
            params.setUserid("tinkame700@sogou.com");
            params.setAuthtype(0);
            params.setRefresh_token(refreshtoken);
            params.setTs("2147483647");
            Result result = pcAccountManager.authRefreshToken(params);
            System.out.println("testAuthRefreshToken:"+result.isSuccess());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testAuthToken() {
        try {
            testCreatePairToken();

            PcAuthTokenParams params = new PcAuthTokenParams();
            params.setAppid(String.valueOf(1044));
            params.setUserid("tinkame700@sogou.com");
            params.setAuthtype(0);
            params.setToken(accesstoken);
            params.setTs("2147483647");

            Result result = pcAccountManager.authToken(params);
            System.out.println("testAuthToken:"+result.isSuccess());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
