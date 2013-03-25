package com.sogou.upd.passport.common.math;

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

    private String str = "dafasdfasdfasdf;ljdflasd;fjosdfjioas";
    @Test
    public void testEncryptBASE64_DecryptBASE64() throws Exception {
        String encryStr = Coder.encryptBASE64(str.getBytes());
        byte[] decryByte = Coder.decryptBASE64(encryStr);
        Assert.assertEquals(str,new String(decryByte));
    }
}
