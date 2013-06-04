package com.sogou.upd.passport.oauth2.openresource.response;

import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.common.utils.OAuthUtils;
import com.sogou.upd.passport.oauth2.openresource.validator.impl.AbstractClientValidator;
import com.sogou.upd.passport.oauth2.openresource.validator.impl.AuthzCodeValidator;
import com.sogou.upd.passport.oauth2.openresource.validator.impl.TokenValidator;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


/**
 * 第三方OAuth2授权成功后的响应结果
 *
 * @author shipengzhi(shipengzhi@sogou-inc.com)
 */
public class OAuthAuthzClientResponse extends OAuthClientResponse {

    private HttpServletRequest request;

    protected OAuthAuthzClientResponse(HttpServletRequest request, AbstractClientValidator validator) {
        this.request = request;
        @SuppressWarnings("unchecked") Map<String, String[]> params = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            if (!OAuthUtils.hasEmptyValues(values)) {
                parameters.put(key, values[0]);
            }
        }
        this.validator = validator;
    }

    /* Authorization Code Flow 响应结果，返回code，根据code获取accessToken */
    public static OAuthAuthzClientResponse oauthCodeAuthzResponse(HttpServletRequest request)
            throws OAuthProblemException {
        OAuthAuthzClientResponse response = new OAuthAuthzClientResponse(request, new AuthzCodeValidator());
        response.validate();
        return response;
    }

    /* Implicit Flow 响应结果，返回accessToken */
    public static OAuthAuthzClientResponse oauthTokenAuthzResponse(HttpServletRequest request)
            throws OAuthProblemException {
        OAuthAuthzClientResponse response = new OAuthAuthzClientResponse(request, new TokenValidator());
        response.validate();
        return response;
    }

    public String getAccessToken() {
        return getParam(OAuth.OAUTH_ACCESS_TOKEN);
    }

    public Long getExpiresIn() {
        String value = getParam(OAuth.OAUTH_EXPIRES_IN);
        return value == null? null: Long.valueOf(value);
    }

    public String getRefreshToken() {
        return getParam(OAuth.OAUTH_REFRESH_TOKEN);
    }

    public String getScope() {
        return getParam(OAuth.OAUTH_SCOPE);
    }

    public String getCode() {
        return getParam(OAuth.OAUTH_CODE);
    }

    public String getRedirect_url() {
        return getParam(OAuth.OAUTH_CODE);
    }

    public String getState() {
        return getParam(OAuth.OAUTH_STATE);
    }

    public String getTokenType(){
        return getParam(OAuth.OAUTH_TOKEN_TYPE);
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    @Override
    public void setBody(String body) {
        this.body = body;
    }

    @Override
    protected void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    protected void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

}
