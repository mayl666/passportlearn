package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.manager.api.account.form.CheckUserApiParams;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-9
 * Time: 下午2:08
 */
public class ProxyRegisterApiManagerImplTest extends BaseTest {

    @Autowired
    private RegisterApiManager proxyRegisterApiManager;

    @Test
    public void testCheckUser(){
        CheckUserApiParams checkUserApiParams=new CheckUserApiParams();
        checkUserApiParams.setUserid("test_lg_upd@sogou.com");
        Result result = proxyRegisterApiManager.checkUser(checkUserApiParams);
        System.out.println(result.toString());
        Assert.assertTrue(result.isSuccess());
        checkUserApiParams=new CheckUserApiParams();
        checkUserApiParams.setUserid("lg-coder@sogou.com");
        result = proxyRegisterApiManager.checkUser(checkUserApiParams);
        System.out.println(result.toString());
        Assert.assertFalse(result.isSuccess());
    }


}
