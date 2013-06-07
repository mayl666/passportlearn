package com.sogou.upd.passport.manager.proxy.account.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.utils.XMLUtil;
import java.util.Map;
import com.sogou.upd.passport.manager.proxy.account.LoginApiManager;
import com.sogou.upd.passport.manager.proxy.account.form.AuthUserApiParams;
import org.junit.Test;
import javax.inject.Inject;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-6
 * Time: 下午2:28
 */

public class AccountLoginManagerTest extends BaseTest {

    @Inject
    private LoginApiManager accountLoginProxyManager;

    @Test
    public void testAuthUser() {
        AuthUserApiParams authUserParameters = new AuthUserApiParams();
        authUserParameters.setPassport_id(passportId);
        authUserParameters.setClient_id(clientId);
        authUserParameters.setPassword("testtest1");
        Map<String, Object> map = accountLoginProxyManager.authUser(authUserParameters);
        String result = XMLUtil.mapToXml("result", map).asXML();
        System.out.println(result);
    }

}
