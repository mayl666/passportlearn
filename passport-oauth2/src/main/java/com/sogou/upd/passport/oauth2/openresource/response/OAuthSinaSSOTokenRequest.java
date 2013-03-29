package com.sogou.upd.passport.oauth2.openresource.response;

import com.sogou.upd.passport.common.exception.ProblemException;
import com.sogou.upd.passport.common.exception.SystemException;
import com.sogou.upd.passport.model.connect.OAuthToken;
import com.sogou.upd.passport.oauth2.authzserver.request.OAuthRequest;
import com.sogou.upd.passport.oauth2.common.utils.OAuthUtils;
import com.sogou.upd.passport.oauth2.common.validators.OAuthValidator;
import com.sogou.upd.passport.oauth2.openresource.OpenOAuth;
import com.sogou.upd.passport.oauth2.openresource.validator.OAuthSinaSSOTokenValidator;

import javax.servlet.http.HttpServletRequest;


/**
 * sina微博采用SSO-SDK，OAuth2登录授权成功后的响应结果
 * @author shipengzhi(shipengzhi@sogou-inc.com)
 *
 */
public class OAuthSinaSSOTokenRequest extends OAuthRequest {

    public OAuthSinaSSOTokenRequest(HttpServletRequest request) throws SystemException, ProblemException {
        super(request);
    }

    @Override
    protected OAuthValidator<HttpServletRequest> initValidator() throws ProblemException, SystemException {
        return new OAuthSinaSSOTokenValidator();
    }

    public OAuthToken getOAuthToken(){
        return new OAuthToken(getAccessToken(), getExpiresIn(), getRefreshToken(), getScope(), getConnectUid());
    }

	public String getAccessToken() {
		return getParam(OpenOAuth.OAUTH_ACCESS_TOKEN);
	}

	public long getExpiresIn() {
        String value = getParam(OpenOAuth.OAUTH_EXPIRES_IN);
        return value == null ? null : Long.valueOf(value);
	}

	public String getRefreshToken() {
		return getParam(OpenOAuth.OAUTH_REFRESH_TOKEN);
	}

    public String getScope(){
        return getParam(OpenOAuth.OAUTH_SCOPE);
    }

    public String getConnectUid(){
        return getParam(OpenOAuth.OAUTH_SINA_UID);
    }

}
