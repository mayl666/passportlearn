package com.sogou.upd.passport.manager.account;

import com.google.common.collect.Maps;
import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.result.APIResultForm;
import com.sogou.upd.passport.common.result.OAuthResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.manager.form.PCOAuth2LoginParams;
import com.sogou.upd.passport.manager.form.PCOAuth2ResourceParams;
import com.sogou.upd.passport.model.account.AccountToken;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-9-17
 * Time: 上午12:21
 * To change this template use File | Settings | File Templates.
 */
@Ignore
public class OAuth2ResourceManagerTest extends BaseTest {
    @Autowired
    private OAuth2ResourceManager oAuth2ResourceManager;
    @Autowired
    private PCOAuth2LoginManager pcOAuth2LoginManager;
    @Autowired
    private PCAccountManager pcAccountManager;


    public static final int CLIENT_ID = 30000004;
    public static final int _CLIENT_ID = 1044;

    public static final String CLIENT_SECRET = "59be99d1f5e957ba5a20e8d9b4d76df6";
    public static final String INSTANCEID = "935972396";

//    public static String ACCESS_TOKEN_SG = "SGCbbIq37qU6wHZKuVGjjbra2uCjIYpYeq7EUPknicL1aFpMtbcvHibBmib5JkljkCKHo";
//    public static String REFRESH_TOKEN_SG = "";


    public static final String resource_type_cookie = "cookie.get";
    public static final String resource_type_full = "full.get";

    private static final String username_waiyu = "tinkame@126.com";
    private static final String pwd_waiyu = "123456";

    private static final String username_phone = "13581695053@sohu.com";
    private static final String pwd_phone = "111111";

    private static final String username_sogou = "tinkame731@sogou.com";
    private static final String pwd_sogou = "123456";


    @Test
    public void testResource() {
        Result result_token = pcAccountManager.createAccountToken(username_sogou,INSTANCEID,_CLIENT_ID);
        Assert.assertTrue(result_token.isSuccess());
        AccountToken accountToken = (AccountToken)result_token.getDefaultModel();
        String accessToken = accountToken.getAccessToken();

        PCOAuth2ResourceParams params = new PCOAuth2ResourceParams();
        params.setClient_id(CLIENT_ID);
        params.setClient_secret(CLIENT_SECRET);
        params.setAccess_token(accessToken);
        params.setInstance_id(INSTANCEID);
        params.setResource_type("cookie.get");
        Result result = oAuth2ResourceManager.resource(params);
        Assert.assertTrue(result.isSuccess());

        params.setResource_type("full.get");
        Result result_full = oAuth2ResourceManager.resource(params);
        Assert.assertTrue(result_full.isSuccess());
    }

    @Test
    public void testAccountLogin() throws Exception{
        PCOAuth2LoginParams loginParams_phone = getParam(username_phone,pwd_phone);
        Result result_phone =  pcOAuth2LoginManager.accountLogin(loginParams_phone,"127.0.0.1","http");
        APIResultForm phone_APIResultForm = JacksonJsonMapperUtil.getMapper().readValue(result_phone.toString(),APIResultForm.class);
        String expire_phone_data = "{\"data\":{\"userid\":\""+username_phone+"\"},\"status\":\"0\",\"statusText\":\"操作成功\"}";
        APIResultForm expire_phone_resultForm =  JacksonJsonMapperUtil.getMapper().readValue(expire_phone_data, APIResultForm.class);
        Assert.assertTrue(expire_phone_resultForm.equals(phone_APIResultForm));

        PCOAuth2LoginParams loginParams_waiyu = getParam(username_waiyu,pwd_waiyu);
        Result result_waiyu =  pcOAuth2LoginManager.accountLogin(loginParams_waiyu,"127.0.0.1","http");
        APIResultForm waiyu_APIResultForm = JacksonJsonMapperUtil.getMapper().readValue(result_waiyu.toString(),APIResultForm.class);
        String expire_waiyu_data = "{\"data\":{\"userid\":\""+username_waiyu+"\"},\"status\":\"0\",\"statusText\":\"操作成功\"}";
        APIResultForm expire_waiyu_resultForm =  JacksonJsonMapperUtil.getMapper().readValue(expire_waiyu_data, APIResultForm.class);
        Assert.assertTrue(expire_waiyu_resultForm.equals(waiyu_APIResultForm));

        PCOAuth2LoginParams loginParams_sogou = getParam(username_sogou,pwd_sogou);
        Result result_sogou =  pcOAuth2LoginManager.accountLogin(loginParams_sogou,"127.0.0.1","http");
        APIResultForm sogou_APIResultForm = JacksonJsonMapperUtil.getMapper().readValue(result_sogou.toString(),APIResultForm.class);
        String expire_sogou_data = "{\"data\":{\"userid\":\""+username_sogou+"\"},\"status\":\"0\",\"statusText\":\"操作成功\"}";
        APIResultForm expire_sogou_resultForm =  JacksonJsonMapperUtil.getMapper().readValue(expire_sogou_data, APIResultForm.class);
        Assert.assertTrue(expire_sogou_resultForm.equals(sogou_APIResultForm));
    }


    @Test
    public void testGetCookieValue() {
        Result result_token = pcAccountManager.createAccountToken(username_sogou,INSTANCEID,_CLIENT_ID);
        Assert.assertTrue(result_token.isSuccess());
        AccountToken accountToken = (AccountToken)result_token.getDefaultModel();
        String accessToken = accountToken.getAccessToken();

        Result result = oAuth2ResourceManager.getCookieValue(accessToken,CLIENT_ID, CLIENT_SECRET, INSTANCEID,"");
        System.out.println("get cookie value result" + result.toString());
    }

    @Test
    public void testGetFullUserInfo() {
        Result result_token = pcAccountManager.createAccountToken(username_sogou,INSTANCEID,_CLIENT_ID);
        Assert.assertTrue(result_token.isSuccess());
        AccountToken accountToken = (AccountToken)result_token.getDefaultModel();
        String accessToken = accountToken.getAccessToken();

        Result result = oAuth2ResourceManager.getFullUserInfo( accessToken, CLIENT_ID,CLIENT_SECRET, INSTANCEID,"");
        System.out.println("get userinfo result" + result.toString());
    }

    @Test
    public void testResultJson() {
        Result result = new OAuthResultSupport(false);
        result.setCode(ErrorUtil.ERR_CODE_CREATE_COOKIE_FAILED);
        System.out.println("error result:" + result.toString());
        String ppinf = "dafdasfdsafasdfasdf";
        String pprdig = "daosuewxczyvzxgjoiwqen";
        result.setSuccess(true);
        Map resource = Maps.newHashMap();
        String[] cookieArray = {ppinf, pprdig};
        resource.put("msg", "get cookie success");
        resource.put("code", "0");
        resource.put("scookie", cookieArray);
        Map resourceMap = Maps.newHashMap();
        resourceMap.put("resource", resource);
        result.setModels(resourceMap);
        System.out.println("success result:" + result.toString());
    }

    private PCOAuth2LoginParams getParam(String username,String password) throws Exception{
        PCOAuth2LoginParams webLoginParams = new PCOAuth2LoginParams();
        webLoginParams.setUsername(username);
        webLoginParams.setPassword(Coder.encryptMD5(password));
        webLoginParams.setInstanceid(INSTANCEID);
        return  webLoginParams;
    }
}
