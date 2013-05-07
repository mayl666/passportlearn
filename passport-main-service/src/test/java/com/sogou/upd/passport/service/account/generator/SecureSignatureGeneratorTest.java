package com.sogou.upd.passport.service.account.generator;

import com.sogou.upd.passport.service.account.dataobject.SecureSignDO;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-5-6
 * Time: 下午5:30
 * To change this template use File | Settings | File Templates.
 */
public class SecureSignatureGeneratorTest extends BaseGeneratorTest {

    @Test
    public void testSign() {
        long ts = 1367908585l;
        String nonce = "tMwHF9bL";
        SecureSignDO secureSignatureDO = new SecureSignDO();
        secureSignatureDO.setTs(ts);
        secureSignatureDO.setNonce(nonce);
        secureSignatureDO.setUri("/v2/connect/users/getopenid?provider=sina&client_id=1001&passport_id=1666643531@sina.sohu.com");
        secureSignatureDO.setServerName("127.0.0.1");
        try {
            String signature = SecureSignatureGenerator.sign(secureSignatureDO, CLIENT_SECRET);
            System.out.println("signature:" + signature);
            boolean verify = SecureSignatureGenerator.verify(secureSignatureDO, CLIENT_SECRET, signature);
            Assert.assertTrue(verify);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }
}
