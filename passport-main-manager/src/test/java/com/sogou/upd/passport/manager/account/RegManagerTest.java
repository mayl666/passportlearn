package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.result.Result;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-3-18
 * Time: 下午2:31
 * To change this template use File | Settings | File Templates.
 */
public class RegManagerTest extends BaseTest {

    @Autowired
    private RegManager regManager;

    @Test
    public void testCheckUser() throws Exception {
        Result result;
        result = regManager.isAccountExists(mobile, clientId);
        Assert.assertTrue(result.isSuccess());

    }

}
