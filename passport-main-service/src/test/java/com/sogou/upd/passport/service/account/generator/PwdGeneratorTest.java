package com.sogou.upd.passport.service.account.generator;

import junit.framework.Assert;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-23
 * Time: 上午2:34
 * To change this template use File | Settings | File Templates.
 */
@Ignore
public class PwdGeneratorTest extends BaseGeneratorTest {

    @Test
    public void testPwdGenerator() {
        try {
            String pwdSign = PwdGenerator.generatorStoredPwd(PASSWORD, true);
            System.out.println(pwdSign);
            Assert.assertTrue(true);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testGeneratorStoredPwd() {
        try {
            String pwd = "123456";
            String pwdMD5 = DigestUtils.md5Hex(pwd.getBytes());
            System.out.println("pwdMD5:" + pwdMD5);

            String pwdSign = PwdGenerator.generatorStoredPwd("123456", true);
            System.out.println("pwdSign:" + pwdSign);
            Assert.assertTrue(true);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testVerify() {
        try {
            boolean verify = PwdGenerator.verify("96E79218965eb72c9", false, "CxnfgqKg$eU7p8GOjOevmWRq53zhcX/");
            Assert.assertTrue(verify);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }
}
