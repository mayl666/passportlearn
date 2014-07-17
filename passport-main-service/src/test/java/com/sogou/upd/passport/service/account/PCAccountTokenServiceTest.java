package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.app.AppConfigService;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-7-30
 * Time: 上午12:08
 * To change this template use File | Settings | File Templates.
 */
@Ignore
@ContextConfiguration(locations = "classpath:spring-config-test.xml")
public class PCAccountTokenServiceTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private PCAccountTokenService pcAccountTokenService;
    @Autowired
    private AppConfigService appConfigService;

    private static final String INSTANCE_ID = "856416207";
    private static final int CLIENT_ID = 1044;
    private static final String PASSPORT_ID = "shipengzhi1986@sogou.com";
    private AppConfig appConfig;

    @Before
    public void init() {
        appConfig = appConfigService.queryAppConfigByClientId(CLIENT_ID);
    }

    @Test
    public void testInitialAccountToken() {
        try {
            AccountToken accountToken = pcAccountTokenService.initialAccountToken(PASSPORT_ID, INSTANCE_ID, appConfig);
            Assert.assertTrue(accountToken != null);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testupdateAccountToken() {
        try {
            AccountToken accountToken = pcAccountTokenService.updateAccountToken(PASSPORT_ID, INSTANCE_ID, appConfig);
            Assert.assertTrue(accountToken != null);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testQueryAccountToken() {
        try {
            AccountToken accountToken = pcAccountTokenService.queryAccountToken(PASSPORT_ID, CLIENT_ID, INSTANCE_ID);
            Assert.assertTrue(accountToken != null);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }

}
