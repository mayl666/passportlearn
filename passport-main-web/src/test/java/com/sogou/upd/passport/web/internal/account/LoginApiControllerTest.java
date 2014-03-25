package com.sogou.upd.passport.web.internal.account;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.result.APIResultForm;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.web.BaseActionTest;
import com.sogou.upd.passport.web.account.form.CheckUserNameExistParameters;
import junit.framework.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * User: mayan
 * Date: 13-6-7 Time: 下午5:48
 */
public class LoginApiControllerTest extends BaseActionTest {

//    public static String httpUrl= "http://10.11.196.186:8090";
    public static String httpUrl= "http://10.11.211.152:8090";

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
        params.put("code", code);
        params.put("ct", String.valueOf(ct));
        String result = sendPost(httpUrl+"/internal/account/authemailuser", params);
        System.out.println("testAuthemailuser_reslut:"+result);
    }

    @Test
    public void testAuthuser() throws Exception {
        Map<String, String> params = getAuthuserParam("tinkame_0414@sogou.com", "123456");
        String result = sendPost(httpUrl+"/internal/account/authuser", params);
        APIResultForm form = JacksonJsonMapperUtil.getMapper().readValue(result, APIResultForm.class);
        Assert.assertEquals("0",form.getStatus());
        Assert.assertEquals("操作成功",form.getStatusText());
        Assert.assertEquals("tinkame_0414@sogou.com",form.getData().get("userid"));

        Map<String, String> params_soji = getAuthuserParam("13545210241@sohu.com","111111");
        String result_soji = sendPost(httpUrl+"/internal/account/authuser", params_soji);
        APIResultForm form_soji = JacksonJsonMapperUtil.getMapper().readValue(result_soji, APIResultForm.class);
        Assert.assertEquals("0",form_soji.getStatus());
        Assert.assertEquals("操作成功",form_soji.getStatusText());
        Assert.assertEquals("13545210241@sohu.com",form_soji.getData().get("userid"));

        Map<String, String> params_waiyu = getAuthuserParam("tinkame@126.com","123456");
        String result_waiyu = sendPost(httpUrl+"/internal/account/authuser", params_waiyu);
        APIResultForm form_waiyu = JacksonJsonMapperUtil.getMapper().readValue(result_waiyu, APIResultForm.class);
        Assert.assertEquals("0",form_waiyu.getStatus());
        Assert.assertEquals("操作成功",form_waiyu.getStatusText());
        Assert.assertEquals("tinkame@126.com",form_waiyu.getData().get("userid"));
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

    @Test
    public void testCheckNeedCaptcha() throws Exception {
        Map<String, String> params = getCheckNeedCaptchaParam("tinkame_0414@sogou.com");
        String result = sendGet(httpUrl+"/web/login/checkNeedCaptcha", params);
        //{"data":{"flag":"1","userid":"tinkame_0414@sogou.com","needCaptcha":false},"status":"0","statusText":"用户名已经存在"}
        APIResultForm form = JacksonJsonMapperUtil.getMapper().readValue(result, APIResultForm.class);
        Assert.assertEquals("0",form.getStatus());
        Assert.assertEquals("用户名已经存在",form.getStatusText());
        Assert.assertEquals("tinkame_0414@sogou.com",form.getData().get("userid"));
        Assert.assertEquals("1",form.getData().get("flag"));
        Assert.assertEquals(false,form.getData().get("needCaptcha"));

        Map<String, String> params_soji = getCheckNeedCaptchaParam("13545210241@sohu.com");
        String result_soji = sendGet(httpUrl+"/web/login/checkNeedCaptcha", params_soji);
        //{"data":{"flag":"1","userid":"tinkame_0414@sogou.com","needCaptcha":false},"status":"0","statusText":"用户名已经存在"}
        APIResultForm form_soji = JacksonJsonMapperUtil.getMapper().readValue(result_soji, APIResultForm.class);
        Assert.assertEquals("0",form_soji.getStatus());
        Assert.assertEquals("用户名已经存在",form_soji.getStatusText());
        Assert.assertEquals("13545210241@sohu.com",form_soji.getData().get("userid"));
        Assert.assertEquals("1",form_soji.getData().get("flag"));
        Assert.assertEquals(false,form_soji.getData().get("needCaptcha"));

        Map<String, String> params_waiju = getCheckNeedCaptchaParam("tinkame@126.com");
        String result_waiyu = sendGet(httpUrl+"/web/login/checkNeedCaptcha", params_waiju);
        //{"data":{"flag":"1","userid":"tinkame_0414@sogou.com","needCaptcha":false},"status":"0","statusText":"用户名已经存在"}
        APIResultForm form_waiyu = JacksonJsonMapperUtil.getMapper().readValue(result_waiyu, APIResultForm.class);
        Assert.assertEquals("0",form_waiyu.getStatus());
        Assert.assertEquals("用户名已经存在",form_waiyu.getStatusText());
        Assert.assertEquals("tinkame@126.com",form_waiyu.getData().get("userid"));
        Assert.assertEquals("1",form_waiyu.getData().get("flag"));
        Assert.assertEquals(false,form_waiyu.getData().get("needCaptcha"));



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

    public Map getCheckNeedCaptchaParam(String passportId) throws Exception{
        Map<String, String> params = new HashMap<String, String>();
        params.put("client_id", String.valueOf(clientId));
        params.put("username",passportId);
        return params;
    }
}
