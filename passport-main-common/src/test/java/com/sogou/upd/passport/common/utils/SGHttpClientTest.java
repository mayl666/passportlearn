package com.sogou.upd.passport.common.utils;

import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.model.httpclient.RequestModelJSON;
import com.sogou.upd.passport.common.model.httpclient.RequestModelXml;
import com.sogou.upd.passport.common.parameter.HttpMethodEnum;
import com.sogou.upd.passport.common.parameter.HttpTransformat;
import com.thoughtworks.xstream.XStream;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-5-29
 * Time: 下午2:42
 */
public class SGHttpClientTest {


    private static final String appId = "1120";

    private static final String key = "4xoG%9>2Z67iL5]OdtBq$l#>DfW@TY";

    private static final String userId = "upd_test@sogou.com";

    @Test
    public void testSetCookie() throws Exception {
        String ru = "http%3a%2f%2fie.sogou.com";
        String userId = "aad@qqq.com";
        String domain = "sogou.com";
        long ct = System.currentTimeMillis();
        String code = userId + appId + key + ct;
        code = Coder.encryptMD5(code);
        String url = "http://passport.sohu.com/act/setcookie?";
        url += "userid=" + userId;
        url += "&appid=" + appId;
        url += "&ru=" + ru;
        url += "&domain=" + domain;
        url += "&ct=" + ct;
        url += "&code=" + code;
        System.out.println(url);
    }

    @Test
    public void testCreateCode() throws Exception {
        String userId = "upd_test@sogou.com";
        long ct = System.currentTimeMillis();
        String code = userId + "1100" + "yRWHIkB$2.9Esk>7mBNIFEcr:8\\[Cv" + ct;
        code = Coder.encryptMD5(code);
        System.out.println("userId:"+userId);
        System.out.println("ct:"+ct);
        System.out.println("code:"+code);
    }


    @Test
    public void testUpdatePwd() throws Exception {
        RequestModel requestModel = new RequestModelXml("http://internal.passport.sohu.com/interface/updatepwd", "info");
        long ct = System.currentTimeMillis();
        String code = userId + appId + key + ct;
        code = Coder.encryptMD5(code);
        requestModel.addParam("userid", userId);
        requestModel.addParam("password", "testtest1");
        requestModel.addParam("appid", appId);
        requestModel.addParam("ct", ct);
        requestModel.addParam("code", code);
        requestModel.addParam("modifyip", "10.1.164.160");
        requestModel.addParam("newquestion", "测试啊，我是来测试的");
        requestModel.addParam("newanswer", "测试成功");
        requestModel.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:21.0) Gecko/20100101 Firefox/21.0");
        requestModel.setHttpMethodEnum(HttpMethodEnum.POST);
        String result = SGHttpClient.executeStr(requestModel);
        System.out.println(result);
    }

    @Test
    public void testGet() {
        RequestModel requestModel = new RequestModel("http://www.sogou.com");
        String result = SGHttpClient.executeStr(requestModel);
        System.out.println(result);
    }

    static String bindEmail="34310327@qq.com";

    @Test
    public void testBindEmail() throws Exception {
        RequestModel requestModel = new RequestModelXml("http://internal.passport.sohu.com/interface/bindemail", "info");
        long ct = System.currentTimeMillis();
        String code = userId + appId + key + ct;
        code = Coder.encryptMD5(code);
        requestModel.addParam("userid", userId);
        requestModel.addParam("password",  Coder.encryptMD5("testtest1"));
        requestModel.addParam("appid", appId);
        requestModel.addParam("ct", ct);
        requestModel.addParam("code", code);
        requestModel.addParam("newbindemail", bindEmail);
        requestModel.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:21.0) Gecko/20100101 Firefox/21.0");
        requestModel.setHttpMethodEnum(HttpMethodEnum.POST);
        String result = SGHttpClient.executeStr(requestModel);
        System.out.println(result);
    }


    @Test
    public void testPost() {
        RequestModel requestModel = new RequestModel("http://www.jiexi.com/user/login");
        requestModel.addParam("email", "l24610343@gmail.com");
        requestModel.addParam("password", "testtest");
        requestModel.addParam("test", "中文测试");
        requestModel.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:21.0) Gecko/20100101 Firefox/21.0");
        requestModel.setHttpMethodEnum(HttpMethodEnum.POST);
        String result = SGHttpClient.executeStr(requestModel);
        System.out.println(result);
    }

    @Test
    public void testPostXML() {
        RequestModel requestModel = new RequestModelXml("http://www.jiexi.com/user/login", "info");
        requestModel.addParam("email", "l24610343@gmail.com");
        requestModel.addParam("password", "testtest");
        requestModel.addParam("test", "中文测试");
        requestModel.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:21.0) Gecko/20100101 Firefox/21.0");
        requestModel.setHttpMethodEnum(HttpMethodEnum.POST);
        String result = SGHttpClient.executeStr(requestModel);
        System.out.println(result);
    }

    @Test
    public void testPostJSON() {
        RequestModel requestModel = new RequestModelJSON("http://www.jiexi.com/user/login");
        requestModel.addParam("email", "l24610343@gmail.com");
        requestModel.addParam("password", "testtest");
        requestModel.addParam("test", "中文测试");
        requestModel.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:21.0) Gecko/20100101 Firefox/21.0");
        requestModel.setHttpMethodEnum(HttpMethodEnum.POST);
        String result = SGHttpClient.executeStr(requestModel);
        System.out.println(result);
    }

    @Test
    public void testGetCookieValue() throws Exception {
        RequestModel requestModel = new RequestModel("http://internal.passport.sohu.com/act/getcookievalue");
        String userid = "upd_test@sogou.com";
        long ct = System.currentTimeMillis();
        String code = userid + "a80d&p4^9t" + ct;
        code = Coder.encryptMD5(code);
        requestModel.addParam("userid", userid);
        requestModel.addParam("ct", ct);
        requestModel.addParam("code", code);
        requestModel.addParam("ip", "10.1.164.160");
        requestModel.addParam("persistentcookie", "0");
        requestModel.setHttpMethodEnum(HttpMethodEnum.POST);
        String result = SGHttpClient.executeStr(requestModel);
        System.out.println(result);
    }

    @Test
    public void testGetCookieKey() throws Exception {
        RequestModel requestModel = new RequestModel("http://internal.passport.sohu.com/act/getcookiekey");
        long ct = System.currentTimeMillis();
        String code = appId+key+ ct;
        code = Coder.encryptMD5(code);
        requestModel.addParam("appid", appId);
        requestModel.addParam("ct", ct);
        requestModel.addParam("code", code);
        requestModel.addParam("date", "2013-06-08");
        requestModel.setHttpMethodEnum(HttpMethodEnum.POST);
        String result = SGHttpClient.executeStr(requestModel);
        System.out.println(result);
    }

    @Test
    public void testAuthTestModelUser() throws Exception {
        RequestModel requestModel = new RequestModelXml("http://internal.passport.sohu.com/interface/authuser", "info");
        String userid = "upd_test@sogou.com";
        long ct = System.currentTimeMillis();
        String code = userid + appId + key + ct;
        code = Coder.encryptMD5(code);
        requestModel.addParam("userid", userid);
        requestModel.addParam("appid", appId);
        requestModel.addParam("password", "testtest");
        requestModel.addParam("ct", ct);
        requestModel.addParam("code", code);
        requestModel.setHttpMethodEnum(HttpMethodEnum.POST);
        Result result = SGHttpClient.executeBean(requestModel, HttpTransformat.xml, Result.class);
        System.out.println(result);
    }

    @Test
    public void testXmlTresultoBean() {
        String xml =
                "<?xml version=\"1.0\" encoding=\"GBK\"?>" +
                        "<result>" +
                        "<uid>26f15b58d0c54d5s</uid>" +
                        "<status>0</status>" +
                        "<userid>upd_test@sogou.com</userid>" +
                        "<uuid>26f15b58d0c54d5s</uuid>" +
                        "<uniqname></uniqname>" +
                        "</result>";
        Result result = XMLUtil.xmlToBean(xml, Result.class);
        System.out.println(result.getStatus());
        xml =
                "<?xml version=\"1.0\" encoding=\"GBK\"?>" +
                        "<result>" +
                        "<uid>26f15b58d0c54d5s</uid>" +
                        "<status>1</status>" +
                        "<userid>upd_test@sogou.com</userid>" +
                        "<uuid>26f15b58d0c54d5s</uuid>" +
                        "<uniqname></uniqname>" +
                        "</result>";
        result = XMLUtil.xmlToBean(xml, Result.class);
        System.out.println(result.getStatus());
        xml =
                "<?xml version=\"1.0\" encoding=\"GBK\"?>" +
                        "<result>" +
                        "<uid>26f15b58d0c54d5s</uid>" +
                        "<status>2</status>" +
                        "<userid>upd_test@sogou.com</userid>" +
                        "<uuid>26f15b58d0c54d5s</uuid>" +
                        "<uniqname></uniqname>" +
                        "</result>";
        HashMap<String, String> map = XMLUtil.xmlToBean(xml, HashMap.class);
        System.out.println(map.get("status"));
    }


    @Test
    public void testXmlToBeanPref() {
        String xml =
                "<?xml version=\"1.0\" encoding=\"GBK\"?>" +
                        "<result>" +
                        "<uid>26f15b58d0c54d5s</uid>" +
                        "<status>0</status>" +
                        "<userid>upd_test@sogou.com</userid>" +
                        "<uuid>26f15b58d0c54d5s</uuid>" +
                        "<uniqname></uniqname>" +
                        "</result>";
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            Result result = XMLUtil.xmlToBean(xml, Result.class);
        }
        long time = System.currentTimeMillis() - startTime;
        System.out.println(time);
    }

    @Test
    public void testXmlToBean() {
        String xml =
                "<?xml version=\"1.0\" encoding=\"GBK\"?>" +
                        "<com.sogou.upd.passport.common.utils.Result>" +
                        "<uid>26f15b58d0c54d5s</uid>" +
                        "<status>1</status>" +
                        "<userid>upd_test@sogou.com</userid>" +
                        "<uuid>26f15b58d0c54d5s</uuid>" +
                        "<uniqname></uniqname>" +
                        "</com.sogou.upd.passport.common.utils.Result>";

        XStream xstream = new XStream();
        //注册将pojo转为map的coverter
        xstream.registerConverter(new PojoMapConverter());
        Result result = new Result();
        xstream.fromXML(xml, result);
        System.out.println(result.getStatus());
    }

}
