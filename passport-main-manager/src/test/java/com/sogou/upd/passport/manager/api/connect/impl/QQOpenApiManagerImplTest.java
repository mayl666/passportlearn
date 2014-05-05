package com.sogou.upd.passport.manager.api.connect.impl;

import com.sogou.upd.passport.oauth2.common.utils.qqutils.OpenApiV3;
import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.manager.api.connect.QQLightOpenApiManager;
import com.sogou.upd.passport.manager.api.connect.form.qq.QQLightOpenApiParams;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-11-29
 * Time: 上午12:00
 * To change this template use File | Settings | File Templates.
 */
@Ignore
@ContextConfiguration(locations = "classpath:spring-config-test.xml")
public class QQOpenApiManagerImplTest extends BaseTest {

    @Autowired
    private QQLightOpenApiManager sgQQLightOpenApiManager;

    @Test
    public void testGetConnectQQUserInfo() throws Exception {
        //用户的openId/openKey
        String openId = "CFF81AB013A94663D83FEC36AC117933";
        String accessToken = "AC1311EBBADD950C4A1113B4A7C19E31";
        //应用的基本信息
        String appkey = CommonConstant.APP_CONNECT_KEY;     //搜狗在QQ的appkey
        String appsecret = CommonConstant.APP_CONNECT_SECRET; //搜狗在QQ的appsecret
        //openapi的服务器，测试环境或正式环境
        String serverName = CommonConstant.QQ_SERVER_NAME;   //正式环境可以使用域名：openapi.tencentyun.com
        OpenApiV3 sdk = new OpenApiV3(appkey, appsecret);
        sdk.setServerName(serverName);
        QQLightOpenApiParams qqLightOpenApiParams = new QQLightOpenApiParams();
        Map<String,String> maps = new HashMap<String,String>();
        maps.put("format","json");
        maps.put("opt","set");
        maps.put("pf","qzone");
        maps.put("value","1");
        maps.put("userip","10.129.192.121");
        qqLightOpenApiParams.setParams(maps);
        qqLightOpenApiParams.setOpenApiName("/v3/user/sogou_flag");
        String result = sgQQLightOpenApiManager.executeQQOpenApi(openId, accessToken, qqLightOpenApiParams);
        System.out.println("result:--------------------" + result);
    }
}
