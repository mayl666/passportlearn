package com.sogou.upd.passport.common.math;

import org.junit.Ignore;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-11-20
 * Time: 下午2:49
 * To change this template use File | Settings | File Templates.
 */
@Ignore
public class CoderTest {
    public static void main(String args[]) throws Exception {
        String ru = "ru=http://localhost:80/touch/index?sid=AVicEGmhoR2ogsKx5_cRLuld&v=5";
        String en = Coder.encodeUTF8(ru);
        String de = Coder.decodeUTF8(en);
        System.out.println("de:" + de);

        String base64Str = "oSVlAX3MA1z4BsdsWnbsjzirChL7AuqGdn_F6PtaPcptrdgwAla6dqTT_8yg9XDyVzg5-lUfo_ZmJH32QY6ffuYu79JmTS4ysyDosPuvN1ow1k2WStNNdmQBnJ2-JrZ0xkEDU9vSqwc5-vM_SqBmThNgtnlPCH81S9EsWaVsBGA";
        String str = Coder.decodeBASE64(base64Str);
        System.out.println("str:" + str);
    }
}
