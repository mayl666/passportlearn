package com.sogou.upd.passport.web;

import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.parameter.HttpMethodEnum;
import com.sogou.upd.passport.common.utils.SGHttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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

    @Test
    public void testHttpClient(){
        RequestModel requestModel = new RequestModel("http://localhost/internal/connect/qq/get_qqfriends");
        requestModel.addParam("userid","089DEEA78E4EFC388FECF28F780B7761@qq.sohu.com");
        requestModel.addParam("client_id","1024");
        requestModel.addParam("code","1024");
        requestModel.setHttpMethodEnum(HttpMethodEnum.POST);
        String str = SGHttpClient.executeStr(requestModel);
        System.out.println("========================================================");
        System.out.println(str);
        System.out.println("========================================================");
    }
}
