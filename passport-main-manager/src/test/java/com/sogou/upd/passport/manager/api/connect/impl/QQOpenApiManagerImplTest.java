package com.sogou.upd.passport.manager.api.connect.impl;

import com.sogou.upd.passport.manager.connect.QQOpenAPIManager;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.oauth2.common.utils.qqutils.OpenApiV3;
import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.manager.api.connect.QQLightOpenApiManager;
import com.sogou.upd.passport.manager.api.connect.form.qq.QQLightOpenApiParams;
import com.sogou.upd.passport.oauth2.openresource.vo.ConnectUserInfoVO;
import com.sogou.upd.passport.service.app.ConnectConfigService;
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
//@Ignore
public class QQOpenApiManagerImplTest extends BaseTest {

    @Autowired
    private QQLightOpenApiManager sgQQLightOpenApiManager;
    @Autowired
    private QQOpenAPIManager qqOpenApiManager;
    @Autowired
    private ConnectConfigService connectConfigService;

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
        String result = sgQQLightOpenApiManager.executeQQOpenApi(openId, accessToken, qqLightOpenApiParams, null);
        System.out.println("result:--------------------" + result);
    }

    @Test
    public void testGetQQUserInfo() throws Exception {
        //用户的openId/openKey
        String openId = "FF46B08FC3D97E66CCDB61FA14C78805";
        String openKey = "C569E9F7CC67311C800E6A6A89EBC9DE";
        ConnectConfig connectConfig = connectConfigService.queryConnectConfigByAppId("", 3);
        ConnectUserInfoVO res = qqOpenApiManager.getQQUserInfo(openId, openKey, connectConfig);
        //{ret=0, is_lost=0, nickname=SogouConnect, gender=男, country=中国, province=北京, city=海淀,
        // figureurl=http://thirdapp2.qlogo.cn/qzopenapp/8c56bf9a878474d27a4e33ecf336cb896a91f93edc7f2230d60e1e5af24d7706/50,
        // is_yellow_vip=0, is_yellow_year_vip=0, yellow_vip_level=0, is_yellow_high_vip=0}
        System.out.println(res);
    }
}
