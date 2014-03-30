package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.result.APIResultForm;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.manager.api.account.form.AuthUserApiParams;
import com.sogou.upd.passport.manager.form.WebLoginParams;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.atomic.AtomicReference;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-7
 * Time: 下午2:02
 */
public class LoginManagerTest extends BaseTest {

    @Autowired
    private LoginManager LoginManagerImpl;


    private static final int clientId = 1100;
    private static final String username = "18600369478";
    private static final String ip = "192.168.226.174";
    private static final String pwd = "123456";

    @Test
    public void testAccountLogin() {
        try {
            WebLoginParams webLoginParams3 = getParam("tinkame@126.com","123456");
            Result result_email = LoginManagerImpl.accountLogin(webLoginParams3,ip);
            APIResultForm email_APIResultForm = JacksonJsonMapperUtil.getMapper().readValue(result_email.toString(),APIResultForm.class);
            String expire_email_data = "{\"data\":{\"userid\":\"tinkame@126.com\"},\"status\":\"0\",\"statusText\":\"操作成功\"}";
            APIResultForm expireResultForm =  JacksonJsonMapperUtil.getMapper().readValue(expire_email_data, APIResultForm.class);
            Assert.assertTrue(expireResultForm.equals(email_APIResultForm));


            WebLoginParams webLoginParams2 = getParam("13545210241@sohu.com","111111");
            Result result_soji = LoginManagerImpl.accountLogin(webLoginParams2,ip);
            String expire_phone_data = "{\"data\":{\"userid\":\"13545210241@sohu.com\"},\"status\":\"0\",\"statusText\":\"操作成功\"}";
            APIResultForm phone_APIResultForm = JacksonJsonMapperUtil.getMapper().readValue(result_soji.toString(),APIResultForm.class);
            APIResultForm expire_phone_ResultForm =  JacksonJsonMapperUtil.getMapper().readValue(expire_phone_data, APIResultForm.class);
            Assert.assertTrue(expire_phone_ResultForm.equals(phone_APIResultForm));

            WebLoginParams webLoginParams1 = getParam("tinkame_test@sogou.com","123456");
            Result result_sogou = LoginManagerImpl.accountLogin(webLoginParams1,ip);
            String expire_sogou_data = "{\"data\":{\"userid\":\"tinkame_test@sogou.com\"},\"status\":\"0\",\"statusText\":\"操作成功\"}";
            APIResultForm sogou_APIResultForm = JacksonJsonMapperUtil.getMapper().readValue(result_sogou.toString(),APIResultForm.class);
            APIResultForm expire_sogou_ResultForm =  JacksonJsonMapperUtil.getMapper().readValue(expire_sogou_data, APIResultForm.class);
            Assert.assertTrue(expire_sogou_ResultForm.equals(sogou_APIResultForm));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private WebLoginParams getParam(String username,String password){
        WebLoginParams webLoginParams = new WebLoginParams();
        webLoginParams.setClient_id("1100");
        webLoginParams.setUsername(username);
        webLoginParams.setPassword(password);
        return  webLoginParams;
    }
}
