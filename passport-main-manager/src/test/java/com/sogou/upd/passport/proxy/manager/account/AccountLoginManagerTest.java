package com.sogou.upd.passport.proxy.manager.account;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.utils.XMLUtil;
import com.sogou.upd.passport.manager.form.proxy.account.AuthUserParameters;
import com.sogou.upd.passport.manager.proxy.account.AccountLoginProxyManager;
import org.junit.Test;

import javax.inject.Inject;
import java.util.Map;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-6
 * Time: 下午2:28
 */

public class AccountLoginManagerTest  extends BaseTest {

    private static final String clientId = "1100";

    private static final String passportId = "upd_test@sogou.com";

    @Inject
    private AccountLoginProxyManager accountLoginProxyManager;

    @Test
    public void testAuthUser(){
        AuthUserParameters authUserParameters=new AuthUserParameters();
        authUserParameters.setPassport_id(passportId);
        authUserParameters.setClientId(clientId);
        authUserParameters.setPassword("testtest1");
        Map<String, Object> map= (Map<String, Object>) accountLoginProxyManager.authUser(authUserParameters);
        String result= XMLUtil.mapToXml("result", map).asXML();
        System.out.println(result);
    }

}
