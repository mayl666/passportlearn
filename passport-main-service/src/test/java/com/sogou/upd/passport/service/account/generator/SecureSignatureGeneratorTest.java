package com.sogou.upd.passport.service.account.generator;

import com.sogou.upd.passport.service.account.dataobject.SecureSignDO;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-5-6
 * Time: 下午5:30
 * To change this template use File | Settings | File Templates.
 */
@Ignore
public class SecureSignatureGeneratorTest extends BaseGeneratorTest {

    @Test
    public void testSign() {
        long ts = 1368104463568l;
        String nonce = "2888767791";
        SecureSignDO secureSignatureDO = new SecureSignDO();
        secureSignatureDO.setTs(ts);
        secureSignatureDO.setNonce(nonce);
        secureSignatureDO.setUri("/user/profile/get.do?friend_id=170000&user_id=170000");
        secureSignatureDO.setServerName("220.181.125.43");
        try {
            String signature = InspectSecureSignForT3.sign(secureSignatureDO, CLIENT_SECRET);
            System.out.println("signature:" + signature);
            boolean verify = InspectSecureSignForT3.verify(secureSignatureDO, CLIENT_SECRET, signature);
            Assert.assertTrue(verify);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }
}
