package com.sogou.upd.passport.common.utils;

import com.sogou.upd.passport.common.model.RequestModel;
import com.sogou.upd.passport.common.parameter.HttpMethodEnum;
import org.junit.Test;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-5-29
 * Time: 下午2:42
 */
public class SGHttpClientTest {

    @Test
    public void testGet(){
        RequestModel requestModel=new RequestModel("http://www.sogou.com");
        String result=SGHttpClient.executeStr(requestModel);
        System.out.println(result);
    }

    //TODO not work
    @Test
    public void testPost(){
        RequestModel requestModel=new RequestModel("http://www.jiexi.com/user/login");
        requestModel.addParam("email","l24610343@gmail.com");
        requestModel.addParam("password","testtest");

        requestModel.addHeader("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:21.0) Gecko/20100101 Firefox/21.0");

        requestModel.setHttpMethodEnum(HttpMethodEnum.POST);

        String result=SGHttpClient.executeStr(requestModel);
        System.out.println(result);
    }
}
