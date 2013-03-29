package com.sogou.upd.passport.oauth2.openresource.parameters;

public enum ResponseTypeEnum {

	CODE("code"), 
	TOKEN("token");

	private String code;

	ResponseTypeEnum(String code) {
		this.code = code;
	}
	
	public String getValue(){
		return code;
	}

//	@Override
//	public String toString() {
//		return code;
//	}

}
