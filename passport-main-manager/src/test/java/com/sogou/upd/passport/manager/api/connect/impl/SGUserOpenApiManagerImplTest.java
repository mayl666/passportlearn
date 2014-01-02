package com.sogou.upd.passport.manager.api.connect.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.manager.api.connect.UserOpenApiManager;
import com.sogou.upd.passport.manager.api.connect.form.BaseOpenApiParams;
import com.sogou.upd.passport.manager.api.connect.form.user.UserOpenApiParams;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.oauth2.openresource.http.OAuthHttpClient;
import com.sogou.upd.passport.oauth2.openresource.request.OAuthClientRequest;
import com.sogou.upd.passport.oauth2.openresource.request.user.QQUserAPIRequest;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthClientResponse;
import com.sogou.upd.passport.oauth2.openresource.response.user.QQUserAPIResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

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
        params.setUserid("3323711589@baidu.sohu.com");
        params.setOpenid("3323711589@baidu.sohu.com");
        params.setClient_id(1115);
        Result result = sgUserOpenApiManager.getUserInfo(params);
        System.out.println("result data:" + result);
    }

}
