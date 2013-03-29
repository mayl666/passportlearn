package com.sogou.upd.passport.oauth2.openresource.parameters;

public enum GrantTypeEnum {

	AUTHORIZATION_CODE("authorization_code"), 
	PASSWORD("password"), 
	REFRESH_TOKEN("refresh_token"), 
	CLIENT_CREDENTIALS("client_credentials");

	private String grantType;

	GrantTypeEnum(String grantType) {
		this.grantType = grantType;
	}
	
	public String getValue(){
		return grantType;
	}

//	@Override
//	public String toString() {
//		return grantType;
//	}
}
