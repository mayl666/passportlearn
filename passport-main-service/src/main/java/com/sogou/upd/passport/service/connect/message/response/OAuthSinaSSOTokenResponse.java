package com.sogou.upd.passport.service.connect.message.response;

import com.sogou.upd.passport.common.exception.ProblemException;
import com.sogou.upd.passport.model.connect.OAuthToken;
import com.sogou.upd.passport.service.connect.ConnectHelper;
import com.sogou.upd.passport.service.connect.message.OAuthResponse;
import com.sogou.upd.passport.service.connect.parameters.OAuth;
import com.sogou.upd.passport.service.connect.validator.impl.AbstractResponseValidator;
import com.sogou.upd.passport.service.connect.validator.impl.TokenValidator;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


/**
 * sina微博采用SSO-SDK，OAuth2登录授权成功后的响应结果
 * @author shipengzhi(shipengzhi@sogou-inc.com)
 *
 */
public class OAuthSinaSSOTokenResponse extends OAuthResponse {

	private HttpServletRequest request;

	protected OAuthSinaSSOTokenResponse(HttpServletRequest request, AbstractResponseValidator validator) {
		this.request = request;
		@SuppressWarnings("unchecked") Map<String, String[]> params = request.getParameterMap();
		for (Map.Entry<String, String[]> entry : params.entrySet()) {
			String key = entry.getKey();
			String[] values = entry.getValue();
			if (!ConnectHelper.hasEmptyValues(values)) {
				parameters.put(key, values[0]);
			}
		}
		this.validator = validator;
	}

	public static OAuthSinaSSOTokenResponse oauthCodeAuthzResponse(HttpServletRequest request)
			throws ProblemException {
        OAuthSinaSSOTokenResponse response = new OAuthSinaSSOTokenResponse(request, new TokenValidator());
		response.validate();
		return response;
	}

    public OAuthToken getOAuthToken(){
        return new OAuthToken(getAccessToken(), getExpiresIn(), getRefreshToken(), getScope(), getConnectUid());
    }

	public String getAccessToken() {
		return getParam(OAuth.OAUTH_ACCESS_TOKEN);
	}

	public long getExpiresIn() {
        String value = getParam(OAuth.OAUTH_EXPIRES_IN);
        return value == null ? null : Long.valueOf(value);
	}

	public String getRefreshToken() {
		return getParam(OAuth.OAUTH_REFRESH_TOKEN);
	}

    public String getScope(){
        return getParam(OAuth.OAUTH_SCOPE);
    }

    public String getConnectUid(){
        return getParam(OAuth.OAUTH_SINA_UID);
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
