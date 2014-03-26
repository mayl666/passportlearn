package com.sogou.upd.passport.web.account.controller;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.web.BaseActionTest;
import com.sogou.upd.passport.web.account.form.APIResultForm;
import junit.framework.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-3-25
 * Time: 下午12:59
 * To change this template use File | Settings | File Templates.
 */
public class RegisterApiControllerTest extends BaseActionTest {

    private static final String PROXY_BASE_PATH_URL = "http://10.11.196.186:8090";
    private static final String SG_BASE_PATH_URL = "http://10.11.211.152:8090";

    private static final String mobile = "13581695053";
    private static final String right_mobile = "13720014130";
    private static final String wrong_mobile = "13581x95053";
    private static final int clientId = CommonConstant.SGPP_DEFAULT_CLIENTID;
    private static final String serverSecret = CommonConstant.SGPP_DEFAULT_SERVER_SECRET;


    //-------------------------------------------------发送验证码 Begin-----------------------------------------

    /**
     * 线上proxy：测试发送手机验证码---手机已经注册或绑定
     *
     * @throws IOException
     */
    @Test
    public void testProxySendRegCaptchaExists() throws IOException {
        long ct = System.currentTimeMillis();
        String code = ManagerHelper.generatorCodeGBK(mobile, clientId, serverSecret, ct);
        String apiUrl = "/internal/account/sendregcaptcha";
        Map<String, String> params = new HashMap<>();
        params.put("mobile", mobile);
        params.put("client_id", String.valueOf(clientId));
        params.put("ct", String.valueOf(ct));
        params.put("code", code);
        String result = sendPost(PROXY_BASE_PATH_URL + apiUrl, params);
        APIResultForm form = JacksonJsonMapperUtil.getMapper().readValue(result, APIResultForm.class);
        Assert.assertEquals(ErrorUtil.ERR_CODE_ACCOUNT_REGED, form.getStatus());
        Assert.assertEquals("账号已注册", form.getStatusText());

        String apiUrl1 = "/internal/account/sendregcaptcha";
        String result1 = sendPost(SG_BASE_PATH_URL + apiUrl1, params);
        System.out.println("-------------------------------");
        System.out.println(result);
        System.out.println(result1);
        APIResultForm form1 = JacksonJsonMapperUtil.getMapper().readValue(result1, APIResultForm.class);
        Assert.assertTrue(form1.equals(form));
        //{"data":{},"status":"20201","statusText":"账号已注册"}
    }

    /**
     * 搜狗分支：测试发送手机验证码---手机已经注册或绑定
     * 状态码与线上不一致，已修正！
     *
     * @throws IOException
     */
    @Test
    public void testSGSendRegCaptchaExists() throws IOException {
        long ct = System.currentTimeMillis();
        String code = ManagerHelper.generatorCodeGBK(mobile, clientId, serverSecret, ct);
        String apiUrl = "/internal/account/sendregcaptcha";
        Map<String, String> params = new HashMap<>();
        params.put("mobile", mobile);
        params.put("client_id", String.valueOf(clientId));
        params.put("ct", String.valueOf(ct));
        params.put("code", code);
        String result = sendPost(SG_BASE_PATH_URL + apiUrl, params);
        APIResultForm form = JacksonJsonMapperUtil.getMapper().readValue(result, APIResultForm.class);
//        Assert.assertTrue();
        //状态码已经修改为20201
        //{"status":"20225","statusText":"手机号已注册或已经绑定其他账号","data":{}}
    }

    /**
     * 线上proxy：测试发送手机验证码---手机格式错误
     *
     * @throws IOException
     */
    @Test
    public void testProxySendRegCaptchaWrongMobile() throws IOException {
        long ct = System.currentTimeMillis();
        String code = ManagerHelper.generatorCodeGBK(wrong_mobile, clientId, serverSecret, ct);
        String apiUrl = "/internal/account/sendregcaptcha";
        Map<String, String> params = new HashMap<>();
        params.put("mobile", wrong_mobile);
        params.put("client_id", String.valueOf(clientId));
        params.put("ct", String.valueOf(ct));
        params.put("code", code);
        String result = sendPost(PROXY_BASE_PATH_URL + apiUrl, params);
        System.out.println(result);
        //{"data":{},"status":"10002","statusText":"手机号格式不正确"}
    }

    /**
     * 搜狗分支：测试发送手机验证码---手机格式错误
     * 返回结果一致！
     *
     * @throws IOException
     */
    @Test
    public void testSGSendRegCaptchaWrongMobile() throws IOException {
        long ct = System.currentTimeMillis();
        String code = ManagerHelper.generatorCodeGBK(wrong_mobile, clientId, serverSecret, ct);
        String apiUrl = "/internal/account/sendregcaptcha";
        Map<String, String> params = new HashMap<>();
        params.put("mobile", wrong_mobile);
        params.put("client_id", String.valueOf(clientId));
        params.put("ct", String.valueOf(ct));
        params.put("code", code);
        String result = sendPost(SG_BASE_PATH_URL + apiUrl, params);
        System.out.println(result);
        //状态码已经修改为20201
        //{"status":"10002","statusText":"手机号格式不正确","data":{}}
    }


    /**
     * 线上proxy：测试发送手机验证码---可发送
     *
     * @throws IOException
     */
    @Test
    public void testProxySendRegCaptcha() throws IOException {
        long ct = System.currentTimeMillis();
        String code = ManagerHelper.generatorCodeGBK(right_mobile, clientId, serverSecret, ct);
        String apiUrl = "/internal/account/sendregcaptcha";
        Map<String, String> params = new HashMap<>();
        params.put("mobile", right_mobile);
        params.put("client_id", String.valueOf(clientId));
        params.put("ct", String.valueOf(ct));
        params.put("code", code);
        String result = sendPost(PROXY_BASE_PATH_URL + apiUrl, params);
        System.out.println(result);
        //{"data":{},"status":"0","statusText":"验证码已发送至13720014130"}
    }

    /**
     * 搜狗分支：测试发送手机验证码---可发送
     * 返回结果一致且都能收到
     *
     * @throws IOException
     */
    @Test
    public void testSGSendRegCaptcha() throws IOException {
        long ct = System.currentTimeMillis();
        String code = ManagerHelper.generatorCodeGBK("18146506140", clientId, serverSecret, ct);
        String apiUrl = "/internal/account/sendregcaptcha";
        Map<String, String> params = new HashMap<>();
        params.put("mobile", "18146506140");
        params.put("client_id", String.valueOf(clientId));
        params.put("ct", String.valueOf(ct));
        params.put("code", code);
        String result = sendPost(SG_BASE_PATH_URL + apiUrl, params);
        System.out.println(result);
        //{"status":"0","statusText":"验证码已发送至13720014130","data":{}}
    }
    //-------------------------------------------------发送验证码 End-----------------------------------------


    //-------------------------------------------------检查用户是否存在 Begin-----------------------------------------

    /**
     * 线上proxy：检查用户是否存在 ---手机用户已经存在
     *
     * @throws IOException
     */
    @Test
    public void testProxyCheckUser() throws IOException {
        long ct = System.currentTimeMillis();
        String code = ManagerHelper.generatorCodeGBK(mobile, clientId, serverSecret, ct);
        String apiUrl = "/internal/account/checkuser";
        Map<String, String> params = new HashMap<>();
        params.put("userid", mobile);
        params.put("client_id", String.valueOf(clientId));
        params.put("ct", String.valueOf(ct));
        params.put("code", code);
        String result = sendPost(PROXY_BASE_PATH_URL + apiUrl, params);
        System.out.println(result);
        //{"data":{"flag":"1","userid":"13581695053@sohu.com"},"status":"20225","statusText":"手机号已绑定其他账号"}

//        String resultStr = "{"data":{"flag":"1","userid":"13581695053@sohu.com"},"status":"20225","statusText":"手机号已绑定其他账号"}";
//        actualForm = JacksonJsonMapperUtil.getMapper()
//
//        String result ="" ;
//        sogouForm =
//
//        Assert.assertEquals(actualForm,sogouForm );
    }

    /**
     * 搜狗分支：测试发送手机验证码---手机用户已经存在
     * 状态码与线上不一致，已修正！
     *
     * @throws IOException
     */
    @Test
    public void testSGCheckUser() throws IOException {
        long ct = System.currentTimeMillis();
        String code = ManagerHelper.generatorCodeGBK(mobile, clientId, serverSecret, ct);
        String apiUrl = "/internal/account/checkuser";
        Map<String, String> params = new HashMap<>();
        params.put("userid", mobile);
        params.put("client_id", String.valueOf(clientId));
        params.put("ct", String.valueOf(ct));
        params.put("code", code);
        String result = sendPost(SG_BASE_PATH_URL + apiUrl, params);
        System.out.println(result);
        //状态码已经修改为20225，与线上保持一致了
        //{"status":"20201","statusText":"账号已注册，请直接登录","data":{}}
    }
}
