package com.sogou.upd.passport.manager.api.connect.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.connect.UserOpenApiManager;
import com.sogou.upd.passport.manager.api.connect.form.user.UserOpenApiParams;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-7-10
 * Time: 下午4:37
 * To change this template use File | Settings | File Templates.
 */
public class ProxyUserOpenApiManagerImplTest extends BaseTest {

    @Autowired
    private UserOpenApiManager proxyUserOpenApiManager;

    @Test
    public void testGetUserInfo() throws Exception {
        UserOpenApiParams params = new UserOpenApiParams();
        params.setUserid("1666643531@sina.sohu.com");
        params.setOpenid("1666643531@sina.sohu.com");

        Result result = proxyUserOpenApiManager.getUserInfo(params);
        System.out.println(result);
    }



}
