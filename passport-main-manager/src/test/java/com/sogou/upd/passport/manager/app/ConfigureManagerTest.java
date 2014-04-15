package com.sogou.upd.passport.manager.app;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.math.Coder;
import junit.framework.TestCase;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-7-23
 * Time: 下午2:35
 * To change this template use File | Settings | File Templates.
 */
public class ConfigureManagerTest extends TestCase {

    @Test
    public void testGeneratorSecret() {
        String secret = RandomStringUtils.randomAscii(30);
        System.out.println("secret: " + secret);
    }

    @Test
    public void testGeneratorClientSecret() throws Exception {
        // 客户端密钥
        int appid = 2013;
        String randomClient = RandomStringUtils.randomAlphanumeric(10);
        long timestamp = System.currentTimeMillis();
        String baseStrClient = appid + "|" + timestamp + "|" + randomClient;
        String secretClient = new String(Coder.encryptMD5(baseStrClient));
        System.out.println("clientSecret:" + secretClient);
    }

}
