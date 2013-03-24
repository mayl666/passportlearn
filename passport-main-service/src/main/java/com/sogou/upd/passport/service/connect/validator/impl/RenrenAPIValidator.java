package com.sogou.upd.passport.service.connect.validator.impl;

import java.util.HashMap;
import java.util.Map;

import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;

import com.sogou.upd.passport.common.exception.ProblemException;
import com.sogou.upd.passport.service.connect.message.OAuthResponse;
import com.sogou.upd.passport.service.connect.parameters.RenrenOAuthError;

public class RenrenAPIValidator extends AbstractResponseValidator {

	// renren错误码与passport错误码对应表
	public static Map<String, String> RENREN_OAUTH_ERROR_MAP = new HashMap<String, String>();

	static {
		RENREN_OAUTH_ERROR_MAP.put("3", ErrorUtil.INVALID_REQUEST);
		/*用户没有此api权限*/
		RENREN_OAUTH_ERROR_MAP.put("200", ErrorUtil.REQUEST_NO_AUTHORITY);
		RENREN_OAUTH_ERROR_MAP.put("202", ErrorUtil.REQUEST_NO_AUTHORITY);
		/*renren access_token无效、过期*/
		RENREN_OAUTH_ERROR_MAP.put("2001", ErrorUtil.CONNECT_ASSOCIATE_TOKEN_INVALID);
		RENREN_OAUTH_ERROR_MAP.put("2002", ErrorUtil.CONNECT_ASSOCIATE_TOKEN_INVALID);

	}

	@Override
	public void validateErrorResponse(OAuthResponse response) throws ProblemException {
		String error_code = response.getParam(RenrenOAuthError.ERROR_CODE);
		if (!StringUtils.isEmpty(error_code)) {
			String errorDesc = "";
			error_code = RENREN_OAUTH_ERROR_MAP.get(error_code);
			if (StringUtil.isEmpty(error_code)) {
				error_code = ErrorUtil.CONNECT_USER_DEFINED_ERROR;
				errorDesc = response.getParam(RenrenOAuthError.ERROR_DESCRIPTION);
			}
			throw ProblemException.error(error_code).description(errorDesc);
		}
	}

}
