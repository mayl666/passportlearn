package com.sogou.upd.passport.manager.api.connect.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.manager.api.connect.form.BaseOpenApiParams;
import com.sogou.upd.passport.model.connect.ConnectToken;
import com.sogou.upd.passport.oauth2.openresource.vo.OAuthTokenVO;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-12-19
 * Time: 下午3:31
 * To change this template use File | Settings | File Templates.
 */
public class ConnectApiManagerImplTest extends BaseTest {

    @Autowired
    private ConnectApiManager proxyConnectApiManager;
    @Autowired
    private ConnectApiManager connectApiManager;

    @Test
    public void testSGGetUserInfo() throws Exception {
        BaseOpenApiParams baseOpenApiParams = new BaseOpenApiParams();
        baseOpenApiParams.setOpenid("E4AB85CD9373A582582F05342BB36D2F@qq.sohu.com");
        baseOpenApiParams.setUserid("E4AB85CD9373A582582F05342BB36D2F@qq.sohu.com");
        Result openResult = proxyConnectApiManager.obtainConnectToken(baseOpenApiParams, SHPPUrlConstant.APP_ID, SHPPUrlConstant.APP_KEY);
        if (openResult.isSuccess()) {
            //获取用户的openId/openKey
            Map<String, String> accessTokenMap = (Map<String, String>) openResult.getModels().get("result");
            String openId = accessTokenMap.get("open_id").toString();
            String accessToken = accessTokenMap.get("access_token").toString();
            System.out.println("openId:" + openId + ", accessToken:" + accessToken);
        } else {
            Assert.assertTrue(false);
        }
    }

    /**
     * 第三方账号迁移时获取token，双读
     *
     * @throws Exception
     */
    @Test
    public void testObtainConnectToken() throws Exception {
        int clientId = 1120;
        String clientKey = "4xoG%9>2Z67iL5]OdtBq$l#>DfW@TY";
        //用户的openId/openKey
        String userId = "CFF81AB013A94663D83FEC36AC117933@qq.sohu.com";
        BaseOpenApiParams baseOpenApiParams = new BaseOpenApiParams();
        baseOpenApiParams.setUserid(userId);
        baseOpenApiParams.setOpenid(userId);
        Result result = connectApiManager.obtainConnectToken(baseOpenApiParams, clientId, clientKey);
        System.out.println("--------------------------结果如下-------------------------");
        System.out.println(result);
        ConnectToken connectToken = null;
        if (result.isSuccess()) {
            connectToken = (ConnectToken) result.getModels().get("connectToken");
        }
        Assert.assertTrue(connectToken != null);
    }

    /**
     * 第三方账号迁移时创建第三方账号，包含双写
     *
     * @throws Exception
     */
    @Test
    public void testBuildConnectAccount() throws Exception {
        String appKey = CommonConstant.APP_CONNECT_KEY;
        int provider = AccountTypeEnum.QQ.getValue();
        long expiresIn = 7776000;
        String refreshToken = null;
        //用户的openId/openKey
        String openId = "CFF81AB013A94663D83FEC36AC117933";
        String accessToken = "AC1311EBBADD950C4A1113B4A7C19E31";
        OAuthTokenVO oAuthTokenVO = new OAuthTokenVO(accessToken, expiresIn, refreshToken);
        oAuthTokenVO.setOpenid(openId);
        Result result = connectApiManager.buildConnectAccount(appKey, provider, oAuthTokenVO);
        System.out.println("---------------------结果如下--------------------");
        System.out.println(result);
        Assert.assertTrue(result.isSuccess());
    }


    /**
     * 生成ct code
     *
     * @throws Exception
     */
    @Test
    public void testCreateCtAndCode() throws Exception {
        BufferedReader reader = null;
        try {
            String fileName = "D:\\50W.txt";
            File file = new File(fileName);
            reader = new BufferedReader(new FileReader(file));
            String tempString;
//            int count = 0;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                String[] rowString = tempString.split(" ");
                String openIdString = rowString[0];
                String passportIdString = openIdString + "@qq.sohu.com";
                String accessTokenString = rowString[1];
                long ct = System.currentTimeMillis();
                String code = ManagerHelper.generatorCodeGBK(passportIdString, clientId, serverSecret, ct);
                //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
                FileWriter writer = new FileWriter("D:\\openid.txt", true);
                writer.write(passportIdString + "," + openIdString + "," + accessTokenString + "," + ct + "," + code);
                writer.write("\r\n");
                writer.close();
//                count++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

}
