package com.sogou.upd.passport.manager.account;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.result.OAuthResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.manager.account.vo.OAuth2TokenVO;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.oauth2.authzserver.request.OAuthTokenASRequest;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-4-21
 * Time: 下午9:17
 * To change this template use File | Settings | File Templates.
 */
public class OAuth2AuthorizeManagerImplTest extends BaseTest {
    @Autowired
    private OAuth2AuthorizeManager oAuth2AuthorizeManager;
    @Autowired
    private PCAccountManager pcAccountManager;

    public static final int CLIENT_ID = 30000004;
    public static final int _CLIENT_ID = 1044;

    public static final String CLIENT_SECRET = "59be99d1f5e957ba5a20e8d9b4d76df6";
    public static final String INSTANCEID = "935972396";
    public static String ACCESS_TOKEN_SG = "SGCbbIq37qU6wHZKuVGjjbra2uCjIYpYeq7EUPknicL1aFpMtbcvHibBmib5JkljkCKHo";

    private static final String username_sogou = "tinkame731@sogou.com";
    private static final String pwd_sogou = "123456";

    @Test
    public void testOauth2Authorize() throws Exception {
        Result result_token = pcAccountManager.createAccountToken(username_sogou,INSTANCEID,_CLIENT_ID);
        Assert.assertTrue(result_token.isSuccess());
        AccountToken accountToken = (AccountToken)result_token.getDefaultModel();
        String accessToken = accountToken.getAccessToken();
        String refreshToken = accountToken.getRefreshToken();


        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setMethod("GET");

        mockRequest.addParameter("grant_type", "heartbeat");
        mockRequest.addParameter("client_id", String.valueOf(CLIENT_ID));
        mockRequest.addParameter("client_secret", CLIENT_SECRET);
        mockRequest.addParameter("scope", "all");
        mockRequest.addParameter("username", username_sogou);
        mockRequest.addParameter("instance_id", String.valueOf(INSTANCEID));
        mockRequest.addParameter("refresh_token", refreshToken);
        OAuthTokenASRequest oauthRequest = new OAuthTokenASRequest(mockRequest);
        Result result = oAuth2AuthorizeManager.oauth2Authorize(oauthRequest);
        OAuth2TokenVO accountToken_result = (OAuth2TokenVO)result.getDefaultModel();

        Assert.assertTrue(result.isSuccess());
        Assert.assertTrue(username_sogou.equals(accountToken_result.getSid()));
        Assert.assertTrue(!StringUtils.isBlank(accountToken_result.getAccess_token()));
        Assert.assertTrue(!StringUtils.isBlank(accountToken_result.getRefresh_token()));
    }
}
