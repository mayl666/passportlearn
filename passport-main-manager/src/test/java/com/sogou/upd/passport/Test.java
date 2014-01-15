package com.sogou.upd.passport;

import java.security.MessageDigest;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-11-25
 * Time: 下午9:12
 * To change this template use File | Settings | File Templates.
 */
public class Test {
    public static final String KEY_MD5 = "MD5";
    private static char[] HEXCHAR = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static void  main(String args[]) throws Exception{
//        long ct = System.currentTimeMillis();
//        System.out.println("ct:" + ct);
//        String ct =  "1381915491000";
        long ct =  1381915491000l;
        String token ="7faada06773b30155f7eb93955845dfb";
        String code = generatorCodeGBK(token, 1115, "RBCqf6a448Wj5a8#KF&POL75*5GBQ5", ct);
        System.out.println("code:" + code);

        try {
//            String pwdMD5 = Coder.encryptMD5("111111");
//            System.out.println("pwdMD5:" + pwdMD5);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String generatorCodeGBK(String token, int clientId, String secret, long ct) {
        //计算默认的code
        String code = "";
        try {
            code = token + clientId + secret + ct;
            code = encryptMD5GBK(code);
        } catch (Exception e) {
        }
        return code;
    }

    public static String encryptMD5GBK(String data) throws Exception {

        MessageDigest md5 = MessageDigest.getInstance(KEY_MD5);

        md5.update(data.getBytes("GBK"));

        return toHexString(md5.digest());

    }

    public static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte by : b) {
            sb.append(HEXCHAR[(by & 0xf0) >>> 4]);
            sb.append(HEXCHAR[by & 0x0f]);
        }
        return sb.toString();
    }
}
