package com.sogou.upd.passport.common.validator;

import com.sogou.upd.passport.common.validation.constraints.UserNameValidator;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-6-9
 * Time: 下午2:11
 * To change this template use File | Settings | File Templates.
 */
public class UseNameValidatorTest {

    @Test
    public void testUserName() {

        boolean result;
        UserNameValidator unv = new UserNameValidator();

        String username = "dfdfd-fdf@qq.com";
        result = unv.isValid(username, null);
        Assert.assertTrue(result);

        username = "dfdfd.fdf@163.com";
        result = unv.isValid(username, null);
        Assert.assertTrue(result);

        username = "dfdfd_fdf@sina.com";
        result = unv.isValid(username, null);
        Assert.assertTrue(result);

        username = "admisogoulpwwe@sogou.com";
        result = unv.isValid(username, null);
        Assert.assertTrue(result);

        username = "&lt;/title&gt;&lt;ScRiPt%20&gt;";
        result = unv.isValid(username, null);
        Assert.assertFalse(result);

//        Assert.assertFalse(unv.isValid("%^@CFD@sogou.com", null));
//        Assert.assertFalse(unv.isValid("DAEW13251XZcSDF24R23@sogou.com", null));
//        Assert.assertFalse(unv.isValid("13560148744@sogou.com", null));
//        Assert.assertTrue(unv.isValid("13560148744", null));
//        Assert.assertTrue(unv.isValid("dafsdsfaasdfasd@qq.sohu.com", null));
//        Assert.assertTrue(unv.isValid("dasfdsfas@sohu.com", null));

    }

    @Test
    public void testXSS() {
        String value = "</title><ScRiPt%20>";
        value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        value = value.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
        value = value.replaceAll("'", "&#39;");
        value = value.replaceAll("eval\\((.*)\\)", "");
        value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
        value = value.replaceAll("script", "");
        System.out.println(value);
    }

}
