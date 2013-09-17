package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.app.AppConfigService;
import junit.framework.Assert;
import org.junit.Before;
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
@ContextConfiguration(locations = "classpath:spring-config-test.xml")
public class SHTokenServiceTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private SHTokenService shTokenService;
    @Autowired
    private AppConfigService appConfigService;

    private static final String INSTANCE_ID = "37318746";
    private static final int CLIENT_ID = 1044;
    private static final String PASSPORT_ID = "tinkame700@sogou.com";
    private static final String timestamp = "1377142102497";
    private static final String sig = "0309cf25d02aad4d7aea0f4136904045";
    private AppConfig appConfig;

    @Before
    public void init() {
        appConfig = appConfigService.queryAppConfigByClientId(CLIENT_ID);
    }

    @Test
    public void testQueryAccountToken() {
        try {
            String shToken = shTokenService.queryRefreshToken(PASSPORT_ID, CLIENT_ID, INSTANCE_ID);
            System.out.println("shToken:" + shToken);
            String sigString = PASSPORT_ID + CLIENT_ID + shToken + timestamp + appConfig.getClientSecret();
            String actualSig = Coder.encryptMD5(sigString);

            System.out.println("result:" + actualSig.equalsIgnoreCase(sig));
            Assert.assertTrue(shToken != null);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testQueryOldAccountToken() {
        try {
//            String shToken = shTokenService.queryOldRefreshToken(PASSPORT_ID, CLIENT_ID, INSTANCE_ID);
//            System.out.println("shToken:"+shToken);
//            String shToken = "A4XT700HK103616J885v75FW6ypFDP";
            String shToken = "up67L64TwaP1o5db307u03u27eoX0t";
            String clientSec = appConfig.getClientSecret();
            String sigString = PASSPORT_ID + CLIENT_ID + shToken + timestamp + clientSec;
            String actualSig = Coder.encryptMD5(sigString);

            System.out.println("result:" + actualSig.equalsIgnoreCase(sig));
            Assert.assertTrue(shToken != null);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }


}
