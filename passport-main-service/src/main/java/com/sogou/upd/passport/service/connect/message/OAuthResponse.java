package com.sogou.upd.passport.service.connect.message;

import java.util.HashMap;
import java.util.Map;

import com.sogou.upd.passport.common.exception.ProblemException;
import com.sogou.upd.passport.service.connect.validator.impl.AbstractResponseValidator;

public abstract class OAuthResponse {

	protected String body;
	protected String contentType;
	protected int responseCode;

	protected AbstractResponseValidator validator;
	protected Map<String, Object> parameters = new HashMap<String, Object>();

	public String getParam(String param) {
		Object value = parameters.get(param);
		return value == null ? null : String.valueOf(value);
	}
	
	public abstract void setBody(String body) throws ProblemException;

	protected abstract void setContentType(String contentTypr);

	protected abstract void setResponseCode(int responseCode);

	public void init(String body, String contentType, int responseCode) throws ProblemException {
		this.setBody(body);
		this.setContentType(contentType);
		this.setResponseCode(responseCode);
		this.validate();
	}

	protected void validate() throws ProblemException {
		validator.validate(this);
	}

}
