package com.sogou.upd.passport.common.math;

import junit.framework.TestCase;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-9-23
 * Time: 下午12:00
 * To change this template use File | Settings | File Templates.
 */
public class CoderTest extends TestCase {

    public void testBase64() {
        String str = "史鹏治";
        try {
            String encrypt = Coder.encryptBase64(str);
            System.out.println("encrypt:" + encrypt);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
