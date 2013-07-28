package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.OAuthTokenManager;
import com.sogou.upd.passport.manager.api.account.form.*;
import com.sogou.upd.passport.manager.form.RefreshPcTokenParams;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-6
 * Time: 下午2:28
 */

public class ProxyOAuthLoginManagerImplTest extends BaseTest {

    private static final int clientId = 1100;

    @Autowired
    private OAuthTokenManager proxyOAuthTokenManager;

    /*
    http://passport.sohu.com/act/authtoken?h=DF9BB5F023D9D0007F4EC6345416E8FE&
    r=2170&v=3.2.0.4716&appid=1044
    &userid=tinkame700%40sogou.com&token=gpfn007vc63GSeY5L303hP7l7U0087&livetime=0
    &authtype=0&ru=http://profile.ie.sogou.com/&ts=1762651724

    http://passport.sohu.com/act/authtoken?h=DF9BB5F023D9D0007F4EC6345416E8FE&r=2170
    &v=3.2.0.4716&appid=1044&userid=tinkame700%40sogou.com
    &token=v5kWL7771j1R5J3qRLWFG83gVY402P&livetime=0&authtype=0&ru=http://profile.ie.sogou.com/&ts=2106063495

     */
    @Test
    public void testAppAuth() throws Exception {
        RefreshPcTokenParams params = new RefreshPcTokenParams();
        /*params.setAppid(1044);
        params.setUserid("test@sohu.com");
        params.setToken("abc");
        params.setRu("http://test.sohu.com");
        params.setLivetime(3600);
        params.setAuthtype(1);*/
        params.setAppid(String.valueOf(1044));
        params.setUserid("tinkame700@sogou.com");
        params.setAuthtype(0);
        params.setRefresh_token("g06O0kM3k6X1Ov7O4g8X72qgC4r5b8");
        params.setTs("2147483647");

        Result resultStr = proxyOAuthTokenManager.refreshToken(params);
        System.out.println("resultStr:"+resultStr.toString());
    }
}
