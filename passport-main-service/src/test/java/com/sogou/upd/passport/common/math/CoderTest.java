package com.sogou.upd.passport.common.math;

import com.sogou.upd.passport.common.parameter.CommonParameters;
import com.sogou.upd.passport.service.account.generator.TokenGenerator;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-3-26
 * Time: 上午2:22
 * To change this template use File | Settings | File Templates.
 */
public class CoderTest {

    private String str = "shipengzhi1986@126.com|1003|1363018968121|dafasdfasdfasdfasdfasdfasdfa";

    @Test
    public void testEncryptBASE64_DecryptBASE64() throws Exception {
        String encryStr = Coder.encryptBASE64(str.getBytes());
        byte[] decryByte = Coder.decryptBASE64(encryStr);
        Assert.assertEquals(str,new String(decryByte));
    }

    @Test
    public void testInitMacKey() throws Exception {
        String key = Coder.initMacKey();
        System.out.println("SecretKey:" + key);
    }

    @Test
    public void testEncryptHMAC() throws Exception {
         byte[] encryByte = Coder.encryptHMAC(str.getBytes(), CommonParameters.HMAC_SHA_KEY);
        String str1 = RSA.toHexString(encryByte);
        System.out.println("str1:" + str1);

    }
}
