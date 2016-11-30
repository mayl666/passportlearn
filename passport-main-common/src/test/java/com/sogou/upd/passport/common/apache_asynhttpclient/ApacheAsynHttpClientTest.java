package com.sogou.upd.passport.common.apache_asynhttpclient;

import com.sogou.upd.passport.common.math.AES;
import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.parameter.HttpMethodEnum;

/**
 * Created with IntelliJ IDEA.
 * User: lzy_clement
 * Date: 15-3-4
 * Time: 下午12:22
 * To change this template use File | Settings | File Templates.
 */
public class ApacheAsynHttpClientTest {
    public static final String TKEY_SECURE_KEY = "adfab231rqwqerq";

    public static void main(String args[]) throws Exception {
        RequestModel requestModel = new RequestModel("http://localhost/internal/test");
        requestModel.setHttpMethodEnum(HttpMethodEnum.POST);
        System.out.println(ApacheAsynHttpClient.executeStr(requestModel));
//        System.out.print(AES.decryptURLSafeString(ApacheAsynHttpClient.executeStr(requestModel), TKEY_SECURE_KEY));
    }
}
