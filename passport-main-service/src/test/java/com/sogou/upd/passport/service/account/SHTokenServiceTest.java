package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.model.account.SHToken;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.app.AppConfigService;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.danga.MemCached.MemCachedClient;
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
    private AppConfig appConfig;

    @Before
    public void init() {
        appConfig = appConfigService.queryAppConfigByClientId(CLIENT_ID);
    }

    @Test
    public void testQueryAccountToken() {
        try {
            SHToken shToken = shTokenService.queryRefreshToken(PASSPORT_ID, CLIENT_ID, INSTANCE_ID);
            Assert.assertTrue(shToken != null);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    public static void main(String[] args) {
        MemCachedClient client = new MemCachedClient("192.168.131.22:11211");
//        client.add("aa", "aa");
//        System.out.println(client.get("aa"));
        client.set("aa", "bb");
        System.out.println(client.get("aa"));
//        client.replace("aa", "cc");
//        System.out.println(client.get("aa"));
//        client.delete("aa");
//        System.out.println(client.get("aa"));
    }

}
