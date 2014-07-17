package com.sogou.upd.passport.common.math;

import junit.framework.TestCase;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-9-14
 * Time: 下午5:06
 * To change this template use File | Settings | File Templates.
 */
@Ignore
public class AESTest extends TestCase {

    private static final String content = "5febe3cdf04429b1d0f580502022157f35cba910f42e72173a743593a3f2947e45a7eff5d80e354952d1ba9e65f3a494f3a9864d18872aca1a9849352622dea29a1f6c0e4614f2b96b63060c0b892bb5fa9e5f3339ffac165ec6dd654db29db924e9ad2da7b687aac9abc1205dbb5a5603f7aa3587ac1dd1b1f43ed43fb3fa6a";
    private static final String secKey = "afE0WZf345@werdm";

    @Test
    public void testEncAndDec() {

        try {
//            String encURLSafeStr = AES.encryptURLSafeString(content, secKey);
//            System.out.println("Base64URLSafe加密后:" + encURLSafeStr);
            String decURLSafeStr = AES.decryptURLSafeString(content, secKey);
            System.out.println("Base64URLSafe解密后：" + decURLSafeStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
