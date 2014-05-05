package com.sogou.upd.passport.web.internal.account;

import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.result.APIResultForm;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.web.BaseActionTest;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;


/**
 * User: mayan
 * Date: 13-6-7 Time: 下午5:48
 */
@Ignore
public class LoginApiControllerTest extends BaseActionTest {

    public static String httpUrl= "http://localhost";
//    public static String httpUrl= "http://10.11.211.152:8090";

    public static  int clientId = 1100;
    public static  String serverSecret = "yRWHIkB$2.9Esk>7mBNIFEcr:8\\[Cv";



    @Test
    public void testAuthemailuser() throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        int clientId = 1014;
        String serverSecret = "6n$gFQf<=Az_3MZb#W?4&LCm~)Qhm{";
        long ct = System.currentTimeMillis();
        String code = ManagerHelper.generatorCode("tinkame_test@sogou.com", clientId, serverSecret, ct) ;
        System.out.println("code:" + code);
        params.put("client_id", String.valueOf(clientId));
        params.put("password", Coder.encryptMD5("123456"));
        params.put("userid","tinkame_test@sogou.com");
        params.put("createip","127.0.0.1");
        params.put("code", code);
        params.put("ct", String.valueOf(ct));
        String result = sendPost(httpUrl+"/internal/account/authemailuser", params);

        String expire_data ="{\"data\":{\"userid\":\"tinkame_test@sogou.com\"},\"status\":\"0\",\"statusText\":\"操作成功\"}";
        APIResultForm expireResultForm =  JacksonJsonMapperUtil.getMapper().readValue(expire_data, APIResultForm.class);
        APIResultForm resultForm =  JacksonJsonMapperUtil.getMapper().readValue(result, APIResultForm.class);
        Assert.assertTrue(expireResultForm.equals(resultForm));
    }

    @Test
    public void testAuthuser() throws Exception {
        //搜狗账号校验用户名密码
        Map<String, String> params = getAuthuserParam("tinkame_0414@sogou.com", "123456");
        String result = sendPost(httpUrl+"/internal/account/authuser", params);
        String expect_data ="{\"data\":{\"userid\":\"tinkame_0414@sogou.com\"},\"status\":\"0\",\"statusText\":\"操作成功\"}";
        APIResultForm expectResultForm =  JacksonJsonMapperUtil.getMapper().readValue(expect_data, APIResultForm.class);
        APIResultForm resultForm =  JacksonJsonMapperUtil.getMapper().readValue(result, APIResultForm.class);
        Assert.assertTrue(expectResultForm.equals(resultForm));
        //手机账号校验用户名密码
        Map<String, String> params_soji = getAuthuserParam("13545210241@sohu.com","111111");
        String result_soji = sendPost(httpUrl+"/internal/account/authuser", params_soji);
        APIResultForm form_soji = JacksonJsonMapperUtil.getMapper().readValue(result_soji, APIResultForm.class);
        String expect_soji_data ="{\"data\":{\"userid\":\"13545210241@sohu.com\"},\"status\":\"0\",\"statusText\":\"操作成功\"}";
        APIResultForm expire_sojiResultForm =  JacksonJsonMapperUtil.getMapper().readValue(expect_soji_data, APIResultForm.class);
        Assert.assertTrue(expire_sojiResultForm.equals(form_soji));
        //外域邮箱校验用户名密码
        Map<String, String> params_waiyu = getAuthuserParam("tinkame@126.com","123456");
        String result_waiyu = sendPost(httpUrl+"/internal/account/authuser", params_waiyu);
        APIResultForm form_waiyu = JacksonJsonMapperUtil.getMapper().readValue(result_waiyu, APIResultForm.class);
        String expect_waiyu_data ="{\"data\":{\"userid\":\"tinkame@126.com\"},\"status\":\"0\",\"statusText\":\"操作成功\"}";
        APIResultForm expire_waiyu_ResultForm =  JacksonJsonMapperUtil.getMapper().readValue(expect_waiyu_data, APIResultForm.class);
        Assert.assertTrue(expire_waiyu_ResultForm.equals(form_waiyu));
        //搜狐账号校验用户名密码（模拟搜狐账号登录后创建无密码情况）
        Map<String, String> params_sohu = getAuthuserParam("testliuling@sohu.com","111111");
        String result_sohu = sendPost(httpUrl+"/internal/account/authuser", params_sohu);
        APIResultForm form_sohu = JacksonJsonMapperUtil.getMapper().readValue(result_sohu, APIResultForm.class);
        String expect_sohu_data ="{\"data\":{\"userid\":\"testliuling@sohu.com\"},\"status\":\"0\",\"statusText\":\"操作成功\"}";
        APIResultForm expire_sohu_ResultForm =  JacksonJsonMapperUtil.getMapper().readValue(expect_sohu_data, APIResultForm.class);
        Assert.assertTrue(expire_sohu_ResultForm.equals(form_sohu));
        //用户在搜狐库中有，在搜狗库中没有（模拟同步延迟的情况）
        Map<String, String> params_sohu_have = getAuthuserParam("testjiushiwo@sogou.com","111111");
        String result_sohu_have = sendPost(httpUrl+"/internal/account/authuser", params_sohu_have);
        APIResultForm form_sohu_have = JacksonJsonMapperUtil.getMapper().readValue(result_sohu_have, APIResultForm.class);
        String expect_sohu_have_data ="{\"data\":{\"userid\":\"testjiushiwo@sogou.com\"},\"status\":\"0\",\"statusText\":\"操作成功\"}";
        APIResultForm expire_sohu_have_ResultForm =  JacksonJsonMapperUtil.getMapper().readValue(expect_sohu_have_data, APIResultForm.class);
        Assert.assertTrue(expire_sohu_have_ResultForm.equals(form_sohu_have));
    }

    @Test
    public void testRenewcookie() throws Exception {
        Map<String, String> params = getAuthuserParam("tinkame_0414@sogou.com","123456");
        String result = sendPost(httpUrl+"/internal/account/renewcookie", params);
        //result:{"data":{"ppinf":"2|1395731824|1396941424|bG9naW5pZDowOnx1c2VyaWQ6MjI6dGlua2FtZV8wNDE0QHNvZ291LmNvbXxzZXJ2aWNldXNlOjIwOjAwMTAwMDAwMDAwMDAwMDAwMDAwfGNydDoxMDoyMDE0LTAzLTI1fGVtdDoxOjB8YXBwaWQ6NDoxMTIwfHRydXN0OjE6MXxwYXJ0bmVyaWQ6MTowfHJlbGF0aW9uOjA6fHV1aWQ6MTY6NTU3MWU3OGY0ODcxNGIzc3x1aWQ6MTY6NTU3MWU3OGY0ODcxNGIzc3x1bmlxbmFtZTowOnw","userid":"tinkame_0414@sogou.com","pprdig":"0CedF4wEWXffy9Au6ionb3l3aN06vyOunEV3p2JHZaMBR7Dnewags5uMY1uP3RC_EvR1tWQp-QhfuV2ZaeKqMQsbxtNcFRJr_uFEXFI-8_avsfLS-bYPWFUzn4Yxyh19Pv2P1aCExOr_GAC5sQIU0O62n3hO74bZN6LZqdX6i8c"},"status":"0","statusText":""}
        System.out.println("result:"+result);
        APIResultForm form = JacksonJsonMapperUtil.getMapper().readValue(result, APIResultForm.class);
        Assert.assertEquals("0",form.getStatus());
        Assert.assertEquals("",form.getStatusText());
        Assert.assertTrue(form.getData().get("ppinf") !=  null);
        Assert.assertTrue(form.getData().get("pprdig") !=  null);
        Assert.assertEquals("tinkame_0414@sogou.com",form.getData().get("userid"));

        Map<String, String> params_soji = getAuthuserParam("13545210241@sohu.com","111111");
        String result_soji = sendPost(httpUrl+"/internal/account/renewcookie", params_soji);
        APIResultForm form_soji = JacksonJsonMapperUtil.getMapper().readValue(result_soji, APIResultForm.class);
        Assert.assertEquals("0",form_soji.getStatus());
        Assert.assertEquals("",form_soji.getStatusText());
        Assert.assertTrue(form.getData().get("ppinf") != null);
        Assert.assertTrue(form.getData().get("pprdig") != null);
        Assert.assertEquals("13545210241@sohu.com",form_soji.getData().get("userid"));

        Map<String, String> params_waiyu = getAuthuserParam("tinkame@126.com","123456");
        String result_waiyu = sendPost(httpUrl+"/internal/account/renewcookie", params_waiyu);
        APIResultForm form_waiyu = JacksonJsonMapperUtil.getMapper().readValue(result_waiyu, APIResultForm.class);
        Assert.assertEquals("0",form_waiyu.getStatus());
        Assert.assertEquals("",form_waiyu.getStatusText());
        Assert.assertTrue(form_waiyu.getData().get("ppinf") != null);
        Assert.assertTrue(form_waiyu.getData().get("pprdig") !=  null);
        Assert.assertEquals("tinkame@126.com",form_waiyu.getData().get("userid"));
    }

    public Map getAuthuserParam(String passportId,String password) throws Exception{
        Map<String, String> params = new HashMap<String, String>();
        long ct = System.currentTimeMillis();
        String code = ManagerHelper.generatorCode(passportId, clientId, serverSecret, ct) ;
        params.put("client_id", String.valueOf(clientId));
        params.put("password", Coder.encryptMD5(password));
        params.put("userid",passportId);
        params.put("code", code);
        params.put("ct", String.valueOf(ct));
        return params;
    }

}
