package com.sogou.upd.passport.common;

import com.sogou.upd.passport.common.lang.StringUtil;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-22 Time: 下午5:34 To change this template use
 * File | Settings | File Templates.
 */

public class StringUtilTest  extends TestCase {
    private static final String STR1 = "hello";
    private static final String STR2 = " world";
    private static final String STR3 = "ni hao";
    private static final String STR4 = "yes!!!";
    private static final String SPACE1 = " ";
    private static final String SPACE2 = "  ";
    private static final String SPACE3 = "\n";
    private static final String EMPTY = "";
    private static final String NULL1 = null;

    @Test
    public void testCheckExistNullOrEmpty() {
        System.out.println("false值：" + StringUtil.checkExistNullOrEmpty(STR1));
        System.out.println("false值：" + StringUtil.checkExistNullOrEmpty(STR1, STR2, STR3, STR4));
        System.out.println("false值：" + StringUtil.checkExistNullOrEmpty(SPACE1));
        System.out.println("true值：" + StringUtil.checkExistNullOrEmpty(STR1, EMPTY, SPACE1, STR3, STR4));
        System.out.println("true值：" + StringUtil.checkExistNullOrEmpty(EMPTY));
        System.out.println("true值：" + StringUtil.checkExistNullOrEmpty(NULL1));
        System.out.println("true值：" + StringUtil.checkExistNullOrEmpty(STR1, NULL1, STR3, STR4));
        System.out.println("true值：" + StringUtil.checkExistNullOrEmpty(SPACE2, EMPTY));
        System.out.println("true值：" + StringUtil.checkExistNullOrEmpty(STR1, SPACE3, STR3, STR4, EMPTY));
        System.out.println("false值：" + StringUtil.checkExistNullOrEmpty());
    }

    public void testStrToUTF8(){
        String str1 = "abc_+}{?><23";
        String str2 = "打法工必存在";
        String str3 = "媽爾哦女廁";
        String str4 = "┞┱┿┰÷∪⊥∪";
        String str5 = "のねせちつへへゃ";
        String str6 = "●◎◆◆＃︿♂";
        try {
            System.out.println("Str:" + str1 + " to UTF8:" + StringUtil.strToUTF8(str1));
            System.out.println("Str:" + str2 + " to UTF8:" + StringUtil.strToUTF8(str2));
            System.out.println("Str:" + str3 + " to UTF8:" + StringUtil.strToUTF8(str3));
            System.out.println("Str:" + str4 + " to UTF8:" + StringUtil.strToUTF8(str4));
            System.out.println("Str:" + str5 + " to UTF8:" + StringUtil.strToUTF8(str5));
            System.out.println("Str:" + str6 + " to UTF8:" + StringUtil.strToUTF8(str6));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
