package com.sogou.upd.passport.service.connect.parameters;

public class OAuth {

	// Authorization request params
	public static final String OAUTH_RESPONSE_TYPE = "response_type";
	public static final String OAUTH_CLIENT_ID = "client_id";
	public static final String OAUTH_CLIENT_SECRET = "client_secret";
	public static final String OAUTH_REDIRECT_URI = "redirect_uri";
	public static final String OAUTH_USERNAME = "username";
	public static final String OAUTH_PASSWORD = "password";
	public static final String OAUTH_SCOPE = "scope";
	public static final String OAUTH_STATE = "state"; //qq client端的状态值。用于第三方应用防止CSRF攻击，成功授权后回调时会原样带回。
	public static final String OAUTH_GRANT_TYPE = "grant_type";
	public static final String OAUTH_DISPLAY = "display"; // 样式
	public static final String OAUTH_RENREN_RENEW = "x_renew"; // renren强制登录
	public static final String OAUTH_SINA_RENEW = "forcelogin"; // sina强制登录

	// Authorization response params
	public static final String OAUTH_CODE = "code";
	public static final String OAUTH_ACCESS_TOKEN = "access_token";
	public static final String OAUTH_EXPIRES_IN = "expires_in";
	public static final String OAUTH_REFRESH_TOKEN = "refresh_token";
	public static final String OAUTH_RTOKEN_EXPIRES_IN = "refreshToken_expires_in";
	public static final String OAUTH_SINA_UID = "uid"; // sina微博获取access_token接口返回带uid参数

	// openId response params
	public static final String OAUTH_OPENID = "openid"; // qq 用access_token获取openId
	public static final String OAUTH_TOKEN_TYPE = "token_type";
	public static final String OAUTH_HEADER_NAME = "Bearer";
	
	//error response params
    public static final String OAUTH_ERROR = "error";
    public static final String OAUTH_ERROR_DESCRIPTION = "error_description";
    public static final String OAUTH_ERROR_URI = "error_uri";

}
