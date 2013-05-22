package com.sogou.upd.passport.common;

import com.sogou.upd.passport.common.utils.StringUtil;

import junit.framework.TestCase;

import org.junit.Test;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

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
}
