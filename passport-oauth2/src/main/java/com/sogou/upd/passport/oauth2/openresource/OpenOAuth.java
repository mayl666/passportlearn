package com.sogou.upd.passport.oauth2.openresource;

import com.sogou.upd.passport.oauth2.common.OAuth;

public class OpenOAuth extends OAuth{

    // Authorization request params
    public static final String DEFAULT_OPEN_UID = "openid"; // 默认的第三方开放平台的用户id
    public static final String BIND_ACCESS_TOKEN = "bind_token"; // 要绑定的账号accessToken

	// Authorization response params
	public static final String OAUTH_SINA_UID = "uid"; // sina微博获取access_token接口返回带uid参数

	// openId response params
	public static final String OAUTH_QQ_OPENID = "openid"; // qq 用access_token获取openId


}
