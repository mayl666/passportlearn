package com.sogou.upd.passport.common.math;

import junit.framework.TestCase;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-9-14
 * Time: 下午5:06
 * To change this template use File | Settings | File Templates.
 */
public class AESTest extends TestCase {

    private static final String content = "shipengzhi@sogou.com|1386523804000";
    private static final String secKey = "40db9c5a312a145e8ee8181f4de8957334c5800a";

    @Test
    public void testEncAndDec() {

        try {
            String encURLSafeStr = AES.encryptURLSafeString(content, secKey);
            System.out.println("Base64URLSafe加密后:" + encURLSafeStr);
            String decURLSafeStr = AES.decryptURLSafeString(encURLSafeStr, secKey);
            System.out.println("Base64URLSafe解密后：" + decURLSafeStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
