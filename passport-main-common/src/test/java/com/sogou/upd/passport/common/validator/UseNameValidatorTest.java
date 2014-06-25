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
        Assert.assertEquals(true, result);

        username = "dfdfd.fdf@163.com";
        result = unv.isValid(username, null);
        Assert.assertEquals(true, result);

        username = "dfdfd_fdf@sina.com";
        result = unv.isValid(username, null);
        Assert.assertEquals(true, result);

        username = "admisogoulpwwe@sogou.com";
        result = unv.isValid(username, null);
        Assert.assertFalse(result);

    }

}
