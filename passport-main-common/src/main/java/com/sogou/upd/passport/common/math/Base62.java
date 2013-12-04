package com.sogou.upd.passport.common.math;

import java.io.ByteArrayOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-12-4
 * Time: 下午10:52
 * To change this template use File | Settings | File Templates.
 */
public class Base62 {

    private static char[] encodes = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
            .toCharArray();
    private static byte[] decodes = new byte[256];

    static {
        for (int i = 0; i < encodes.length; i++) {
            decodes[encodes[i]] = (byte) i;
        }
    }

    public static StringBuffer encodeBase62(byte[] data) {
        StringBuffer sb = new StringBuffer(data.length * 2);
        int pos = 0, val = 0;
        for (int i = 0; i < data.length; i++) {
            val = (val << 8) | (data[i] & 0xFF);
            pos += 8;
            while (pos > 5) {
                char c = encodes[val >> (pos -= 6)];
                sb.append(
            /**/c == 'i' ? "ia" :
            /**/c == '+' ? "ib" :
            /**/c == '/' ? "ic" : c);
                val &= ((1 << pos) - 1);
            }
        }
        if (pos > 0) {
            char c = encodes[val << (6 - pos)];
            sb.append(
        /**/c == 'i' ? "ia" :
        /**/c == '+' ? "ib" :
        /**/c == '/' ? "ic" : c);
        }
        return sb;
    }

    public static byte[] decodeBase62(char[] data) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(data.length);
        int pos = 0, val = 0;
        for (int i = 0; i < data.length; i++) {
            char c = data[i];
            if (c == 'i') {
                c = data[++i];
                c =
            /**/c == 'a' ? 'i' :
            /**/c == 'b' ? '+' :
            /**/c == 'c' ? '/' : data[--i];
            }
            val = (val << 6) | decodes[c];
            pos += 6;
            while (pos > 7) {
                baos.write(val >> (pos -= 8));
                val &= ((1 << pos) - 1);
            }
        }
        return baos.toByteArray();
    }

    public static void main(String args[]) throws Exception {
        String passportId ="C8FB68EC3C5C62D21A8774B2870E79BC@qq.sohu.com";

//          String passportId ="C8FB68EC3C5C62D21A8774B2870E79BC@sina.sohu.com";
//        String passportId = "tinkame710ssssss@sogou.com";
        int expiresIn = 604800;
        String clientSecret = "c1756a351db27d817225e2a4fd7b3f7d";
       String encodeStr = Base62.encodeBase62(passportId.getBytes("utf-8")).toString();
        System.out.println("encodeStr:"+encodeStr);


        byte[] str = Base62.decodeBase62(encodeStr.toCharArray());

        String decodeStr = new String(str,"utf-8");

        System.out.println("decodeStr:"+decodeStr);


    }
}
