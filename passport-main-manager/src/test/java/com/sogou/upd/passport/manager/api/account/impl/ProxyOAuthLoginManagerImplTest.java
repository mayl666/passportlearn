package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.OAuthTokenApiManager;
import com.sogou.upd.passport.manager.form.PcRefreshTokenParams;
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
    private OAuthTokenApiManager proxyOAuthTokenManager;

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
        PcRefreshTokenParams params = new PcRefreshTokenParams();
        params.setAppid(String.valueOf(1044));
        params.setUserid("shipengzhi1986@sogou.com");
        params.setAuthtype(0);
        params.setRefresh_token("27GOU5s4Rw40mA1eAc8m00u0el8OB3");
        params.setTs("856416207");

        Result resultStr = proxyOAuthTokenManager.refreshToken(params);
        System.out.println("resultStr:"+resultStr.toString());
    }
}
