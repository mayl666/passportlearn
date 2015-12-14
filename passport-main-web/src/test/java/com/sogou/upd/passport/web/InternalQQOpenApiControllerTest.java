package com.sogou.upd.passport.web;

import com.sogou.upd.passport.common.apache_asynhttpclient.ApacheAsynHttpClient;
import com.sogou.upd.passport.common.math.AES;
import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.parameter.HttpMethodEnum;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.common.utils.SGHttpClient;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.manager.connect.impl.QQOpenAPIManagerImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: lzy_clement
 * Date: 15-1-6
 * Time: 下午5:08
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-config-test.xml")
public class InternalQQOpenApiControllerTest {
    @Autowired
    private ConnectApiManager sgConnectApiManager;

    @Test
    public void testHttpClient(){
        RequestModel requestModel = new RequestModel("http://localhost/internal/connect/qq/getQQFriends");
        requestModel.addParam("userid","089DEEA78E4EFC388FECF28F780B7761@qq.sohu.com");
        requestModel.addParam("client_id","1024");
        requestModel.addParam("code","1024");
        requestModel.setHttpMethodEnum(HttpMethodEnum.POST);
        String str = SGHttpClient.executeStr(requestModel);
        System.out.println("========================================================");
        System.out.println(str);
        System.out.println("========================================================");
    }

    @Test
    public void testQcloud() throws Exception{
        String qCloudUrl="http://115.159.57.127:8888/internal/qq/friends_aesinfo";
        String userId="E74BEC2F5729AB12495986504FA64826@qq.sohu.com";
        int clientId=2040;
        String third_appid=null;
        Result obtainTKeyResult = sgConnectApiManager.obtainTKey(userId, clientId, third_appid);
        if (!obtainTKeyResult.isSuccess()) {
            System.out.println(obtainTKeyResult.toString());
            return;
        }

        String tKey = (String) obtainTKeyResult.getModels().get("tKey");

        RequestModel requestModel = new RequestModel(qCloudUrl);
        requestModel.addParam("userid", userId);
        requestModel.addParam("tKey", tKey);
        requestModel.setHttpMethodEnum(HttpMethodEnum.POST);
//            String returnVal = SGHttpClient.executeStr(requestModel);
        String returnVal = ApacheAsynHttpClient.executeStr(requestModel);
//            asyncHttpClientService.sendPreparePost(GET_QQ_FRIENDS_AES_URL);
        String str = AES.decryptURLSafeString(returnVal, QQOpenAPIManagerImpl.TKEY_SECURE_KEY);
       System.out.println("result:"+str);

    }
}
