package com.sogou.upd.passport.service.connect.parameters;

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
