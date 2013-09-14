package com.sogou.upd.passport.service.app;

import com.sogou.upd.passport.BaseTest;

import com.sogou.upd.passport.common.math.Coder;
import junit.framework.Assert;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-20 Time: 下午8:02 To change this template use
 * File | Settings | File Templates.
 */
public class AppConfigServiceTest extends BaseTest {
    private static int CLIENT_ID = 1120;
    private static int CLIENT_ID_NOEXIST = 0;

    @Autowired
    private AppConfigService appConfigService;

    @Test
    public void testQueryClientName() {
        String resultStr = appConfigService.queryClientName(CLIENT_ID);
        Assert.assertTrue(resultStr.equals("搜狗通行证"));
        resultStr = appConfigService.queryClientName(CLIENT_ID_NOEXIST);
        Assert.assertNull(resultStr);
    }

    @Test
    public void testGeneratorSecret() {
        String random = RandomStringUtils.randomAlphanumeric(8);
        long time = System.currentTimeMillis();
        int client_id = 1065;
        try {
            String secret = Coder.encryptMD5(client_id + time + random);
            System.out.println("secret:" + secret);
            Assert.assertTrue(true);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }
}
