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
public class SGUserOpenApiManagerImplTest extends BaseTest {

    @Autowired
    private UserOpenApiManager sgUserOpenApiManager;


    /**
     * 通过搜狐代理接口调用第三方OpenAPI
     *
     * @throws Exception
     */
    @Test
    public void testGetUserInfo() throws Exception {
        UserOpenApiParams params = new UserOpenApiParams();
        params.setUserid("E4AB85CD9373A582582F05342BB36D2F@qq.sohu.com");
        params.setOpenid("E4AB85CD9373A582582F05342BB36D2F@qq.sohu.com");
        params.setClient_id(1115);
        Result result = sgUserOpenApiManager.getUserInfo(params);
        System.out.println("result data:" + result);
    }

}
