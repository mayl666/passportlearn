package com.sogou.upd.passport.common.utils;

import java.util.Date;

import com.sogou.upd.passport.common.math.Coder;
import junit.framework.Assert;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

public class AppTest {

    @Test
    public void appSecretGenerator() throws Exception {
        // 服务端
        int maxAppid = 1001;
        long timestamp = System.currentTimeMillis();
        String random = RandomStringUtils.randomAlphanumeric(10);
        String secretKey = RandomStringUtils.randomAlphanumeric(10);
        String baseStr = maxAppid + "" + timestamp + "" + random;
        byte[] encryByte = Coder.encryptHMAC(baseStr.getBytes(), secretKey);
        System.out.println("app server secret : " + Coder.toHexString(encryByte));
        // 客户端密钥
        String randomClient = RandomStringUtils.randomAlphanumeric(10);
        String clientSecretKey = RandomStringUtils.randomAlphanumeric(10);
        long timestampClient = new Date().getTime();
        String clientBaseStr = maxAppid + "" + timestampClient + "" + randomClient;
        byte[] clientEncryByte = Coder.encryptHMAC(clientBaseStr.getBytes(), clientSecretKey);
        System.out.println("app client secret : " + Coder.toHexString(clientEncryByte));

        Assert.assertTrue(true);
    }

    @Test
    public void aesSecretGenerator() throws Exception {
        long timestamp = System.currentTimeMillis();
        String random = RandomStringUtils.randomAlphanumeric(10);
        String secretKey = RandomStringUtils.randomAlphanumeric(10);
        String baseStr = timestamp + "" + random;

        byte[] encryByte = Coder.encryptHMAC(baseStr.getBytes(), secretKey);
        System.out.println("secret generator: " + Coder.toHexString(encryByte));

        Assert.assertTrue(true);
    }
}
