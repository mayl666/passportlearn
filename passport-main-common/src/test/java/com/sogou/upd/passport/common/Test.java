package com.sogou.upd.passport.common;

import com.sogou.upd.passport.common.math.Coder;

import java.net.URLDecoder;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-12-13
 * Time: 上午11:58
 * To change this template use File | Settings | File Templates.
 */
public class Test {
    public static void main(String args[]) throws Exception{
//        String str= "\\'\"@#$%^&*";
        String str= "你好，家盟abc-";

        String encodeStr = Coder.encodeUTF8(str);
        System.out.println("encodeStr:"+encodeStr);

        String deStr = URLDecoder.decode(encodeStr);
        System.out.println("decodeStr:"+deStr);


    }
}
