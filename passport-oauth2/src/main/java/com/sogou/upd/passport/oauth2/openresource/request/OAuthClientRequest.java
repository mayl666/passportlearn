package com.sogou.upd.passport.oauth2.openresource.request;

import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.openresource.parameters.GrantTypeEnum;
import com.sogou.upd.passport.oauth2.openresource.parameters.ResponseTypeEnum;

/**
 * 第三方OAuth授权请求，包括：
 * 1.用户验证请求，获取code；
 * 2.用code获取access_token（含用refresh_token刷新access_token）；
 * 3.用access_token获取openid
 * @author shipengzhi
 *
 */
public class OAuthClientRequest extends OAuthRequest {

	public OAuthClientRequest(String url) {
		super(url);
	}

	public static AuthenticationRequestBuilder authorizationLocation(String url) {
		return new AuthenticationRequestBuilder(url);
	}

	public static TokenRequestBuilder tokenLocation(String url) {
		return new TokenRequestBuilder(url);
	}

	public static OpenidRequestBuilder openIdLocation(String url) {
		return new OpenidRequestBuilder(url);
	}

	/**
	 * 用户OAuth授权请求构造器
	 * @author shipengzhi(shipengzhi@sogou-inc.com)
	 *
	 */
	public static class AuthenticationRequestBuilder extends OAuthRequestBuilder {

		public AuthenticationRequestBuilder(String url) {
			super(url);
		}

		// 响应结果类型，code还是token
		public AuthenticationRequestBuilder setResponseType(ResponseTypeEnum responseType) {
			this.parameters.put(OAuth.OAUTH_RESPONSE_TYPE, responseType == null ? null : responseType.getValue());
			return this;
		}

		// 第三方appkey
		public AuthenticationRequestBuilder setAppKey(String appKey) {
			this.parameters.put(OAuth.OAUTH_CLIENT_ID, appKey);
			return this;
		}

		// 重定向url
		public AuthenticationRequestBuilder setRedirectURI(String uri) {
			this.parameters.put(OAuth.OAUTH_REDIRECT_URI, uri);
			return this;
		}

		// 授权方法名
		public AuthenticationRequestBuilder setScope(String scope) {
			this.parameters.put(OAuth.OAUTH_SCOPE, scope);
			return this;
		}

		// 授权页面样式
		public AuthenticationRequestBuilder setDisplay(String display) {
			this.parameters.put(OAuth.OAUTH_DISPLAY, display);
			return this;
		}
		
		// client端状态值
		public AuthenticationRequestBuilder setState(String state) {
			this.parameters.put(OAuth.OAUTH_STATE, state);
			return this;
		}
		
		// 是否强制用户输入用户名、密码
		public AuthenticationRequestBuilder setForceLogin(boolean force, int provider) {
			if(provider == AccountTypeEnum.RENREN.getValue()){
				this.parameters.put(OAuth.OAUTH_RENREN_FORCELOGIN, force);
			}else if(provider == AccountTypeEnum.SINA.getValue()){
				this.parameters.put(OAuth.OAUTH_SINA_FORCELOGIN, force);
			}
			return this;
		}
	}

	/**
	 * 获取access_token请求构造器
	 * @author shipengzhi(shipengzhi@sogou-inc.com)
	 */
	public static class TokenRequestBuilder extends OAuthRequestBuilder {

		protected TokenRequestBuilder(String url) {
			super(url);
		}

		public TokenRequestBuilder setGrantType(GrantTypeEnum grantType) {
			this.parameters.put(OAuth.OAUTH_GRANT_TYPE, grantType == null ? null : grantType.getValue());
			return this;
		}

		public TokenRequestBuilder setAppKey(String appKey) {
			this.parameters.put(OAuth.OAUTH_CLIENT_ID, appKey);
			return this;
		}

		public TokenRequestBuilder setAppSecret(String appSecret) {
			this.parameters.put(OAuth.OAUTH_CLIENT_SECRET, appSecret);
			return this;
		}

		/* 用户名和密码进行OAuth2授权 */
		public TokenRequestBuilder setUsername(String username) {
			this.parameters.put(OAuth.OAUTH_USERNAME, username);
			return this;
		}
		public TokenRequestBuilder setPassword(String password) {
			this.parameters.put(OAuth.OAUTH_PASSWORD, password);
			return this;
		}

		public TokenRequestBuilder setScope(String scope) {
			this.parameters.put(OAuth.OAUTH_SCOPE, scope);
			return this;
		}

		public TokenRequestBuilder setCode(String code) {
			this.parameters.put(OAuth.OAUTH_CODE, code);
			return this;
		}

		public TokenRequestBuilder setRedirectURI(String uri) {
			this.parameters.put(OAuth.OAUTH_REDIRECT_URI, uri);
			return this;
		}

		/* 用refresh_token刷新access_token */
		public TokenRequestBuilder setRefreshToken(String token) {
			this.parameters.put(OAuth.OAUTH_REFRESH_TOKEN, token);
			return this;
		}

		public TokenRequestBuilder setParameter(String paramName, String paramValue) {
			this.parameters.put(paramName, paramValue);
			return this;
		}

		public TokenRequestBuilder setState(String state) {
			this.parameters.put(OAuth.OAUTH_STATE, state);
			return this;
		}
	}

	/**
	 * 用access_token获取Openid的请求构造器
	 * 目前只有QQ需要
	 * @author shipengzhi(shipengzhi@sogou-inc.com)
	 *
	 */
	public static class OpenidRequestBuilder extends OAuthRequestBuilder {

		protected OpenidRequestBuilder(String url) {
			super(url);
		}

		public OpenidRequestBuilder setAccessToken(String accessToken) {
			this.parameters.put(OAuth.OAUTH_ACCESS_TOKEN, accessToken);
			return this;
		}
	}
	
}
