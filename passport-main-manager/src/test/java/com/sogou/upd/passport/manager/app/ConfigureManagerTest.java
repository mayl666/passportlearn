package com.sogou.upd.passport.manager.app;

import com.sogou.upd.passport.BaseTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-7-23
 * Time: 下午2:35
 * To change this template use File | Settings | File Templates.
 */
public class ConfigureManagerTest extends BaseTest {

    @Test
    public void testGeneratorSecret() {
        String secret = RandomStringUtils.randomAscii(30);
        System.out.println("secret: " + secret);
    }
}
