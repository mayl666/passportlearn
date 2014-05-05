package com.sogou.upd.passport.web.account.action;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
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
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-4-29
 * Time: 下午7:37
 * To change this template use File | Settings | File Templates.
 */
@Ignore
public class LoginActionTest extends BaseActionTest {

    public static String httpUrl = "http://localhost";
//    public static String httpUrl= "http://10.11.211.152:8090";

    public static int clientId = 1100;
    public static String serverSecret = "yRWHIkB$2.9Esk>7mBNIFEcr:8\\[Cv";

    @Test
    public void testCheckNeedCaptcha() throws Exception {
        Map<String, String> params = getCheckNeedCaptchaParam("tinkame_0414@sogou.com");
        String expiredata = "{\"data\":{\"flag\":\"1\",\"userid\":\"tinkame_0414@sogou.com\",\"needCaptcha\":false},\"status\":\"0\",\"statusText\":\"\"}\n";
        APIResultForm expireForm = JacksonJsonMapperUtil.getMapper().readValue(expiredata, APIResultForm.class);
        String actualResult = sendGet(httpUrl + "/web/login/checkNeedCaptcha", params);
        APIResultForm actualForm = JacksonJsonMapperUtil.getMapper().readValue(actualResult, APIResultForm.class);
        Assert.assertTrue(expireForm.equals(actualForm));

        Map<String, String> params_soji = getCheckNeedCaptchaParam("13545210241@sohu.com");
        String result_soji = sendGet(httpUrl + "/web/login/checkNeedCaptcha", params_soji);
        APIResultForm form_soji = JacksonJsonMapperUtil.getMapper().readValue(result_soji, APIResultForm.class);
        String expire_soji_data = "{\"data\":{\"flag\":\"1\",\"userid\":\"13545210241@sohu.com\",\"needCaptcha\":false},\"status\":\"0\",\"statusText\":\"\"}";
        APIResultForm expireForm_soji = JacksonJsonMapperUtil.getMapper().readValue(expire_soji_data, APIResultForm.class);
        Assert.assertTrue(expireForm_soji.equals(form_soji));

        Map<String, String> params_waiju = getCheckNeedCaptchaParam("tinkame@126.com");
        String result_waiyu = sendGet(httpUrl + "/web/login/checkNeedCaptcha", params_waiju);
        APIResultForm form_waiyu = JacksonJsonMapperUtil.getMapper().readValue(result_waiyu, APIResultForm.class);
        String expire_waiyu_data = "{\"data\":{\"flag\":\"1\",\"userid\":\"tinkame@126.com\",\"needCaptcha\":false},\"status\":\"0\",\"statusText\":\"\"}";
        APIResultForm expireForm_waiyu = JacksonJsonMapperUtil.getMapper().readValue(expire_waiyu_data, APIResultForm.class);
        Assert.assertTrue(expireForm_waiyu.equals(form_waiyu));
    }

    public Map getWebLoginParams(String username, String password, String captcha, int authLogin) throws Exception {
        Map<String, String> params = new HashMap();
        params.put("client_id", String.valueOf(CommonConstant.SGPP_DEFAULT_CLIENTID));
        params.put("password", password);
        params.put("username", username);
        if (!Strings.isNullOrEmpty(captcha))
            params.put("captcha", captcha);
        params.put("autoLogin", String.valueOf(authLogin));
        params.put("xd", "https://account.sogou.com/static/api/jump.htm");
        params.put("token", "40c113813cef64ff0e9ce9458a37e0e1");
        return params;
    }

    public Map getCheckNeedCaptchaParam(String passportId) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("client_id", String.valueOf(clientId));
        params.put("username", passportId);
        return params;
    }

}
