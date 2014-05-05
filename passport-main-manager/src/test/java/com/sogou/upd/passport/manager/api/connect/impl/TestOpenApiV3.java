package com.sogou.upd.passport.manager.api.connect.impl;

import com.sogou.upd.passport.oauth2.common.utils.qqutils.OpenApiV3;
import com.sogou.upd.passport.oauth2.common.utils.qqutils.OpensnsException;
import org.junit.Ignore;

import java.util.HashMap;

/**
 * OpenAPI V3 SDK 示例代码
 *
 * @author open.qq.com
 * @version 3.0.0
 * @copyright © 2012, Tencent Corporation. All rights reserved.
 * @History: 3.0.0 | nemozhang | 2012-03-21 12:01:05 | initialization
 * @since jdk1.5
 */
@Ignore
public class TestOpenApiV3 {
    public static void main(String args[]) {
        // 应用基本信息
        String appid = "100294784";
        String appkey = "a873ac91cd703bc037e14c2ef47d2021";

        // 用户的OpenID/OpenKey
        String openid = "CFF81AB013A94663D83FEC36AC117933";
        String openkey = "AC1311EBBADD950C4A1113B4A7C19E31";

        // OpenAPI的服务器IP 
        // 最新的API服务器地址请参考wiki文档: http://wiki.open.qq.com/wiki/API3.0%E6%96%87%E6%A1%A3 
        String serverName = "119.147.19.43";   //正式环境使用openapi.tencentyun.com


        // 所要访问的平台, pf的其他取值参考wiki文档: http://wiki.open.qq.com/wiki/API3.0%E6%96%87%E6%A1%A3
        String pf = "qzone";

        OpenApiV3 sdk = new OpenApiV3(appid, appkey);
        sdk.setServerName(serverName);

        System.out.println("===========test GetUserInfo===========");
        testGetUserInfo(sdk, openid, openkey, pf);
    }

    /**
     * 测试调用UserInfo接口
     */
    public static void testGetUserInfo(OpenApiV3 sdk, String openid, String openkey, String pf) {
        // 指定OpenApi Cgi名字 
        String scriptName = "/v3/user/sogou_flag";

        // 指定HTTP请求协议类型
        String protocol = "http";

        // 填充URL请求参数
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("openid", openid);
        params.put("openkey", openkey);
        params.put("opt", "set");
        params.put("value", "0");
        params.put("pf", pf);
        String method = "post";
        try {
            String resp = sdk.api(scriptName, params, protocol, method);
            System.out.println(resp);
        } catch (OpensnsException e) {
            System.out.printf("Request Failed. code:%d, msg:%s\n", e.getErrorCode(), e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
