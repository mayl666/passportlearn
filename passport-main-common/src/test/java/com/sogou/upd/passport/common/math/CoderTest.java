package com.sogou.upd.passport.common.math;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-11-20
 * Time: 下午2:49
 * To change this template use File | Settings | File Templates.
 */
public class CoderTest {
    public static void main(String args[])throws Exception{
        String ru ="ru=http://localhost:80/touch/index?sid=AVicEGmhoR2ogsKx5_cRLuld&v=5";
        String en = Coder.encodeUTF8(ru);
        String de =  Coder.decodeUTF8(en);
        System.out.println("de:"+de);
    }
}
