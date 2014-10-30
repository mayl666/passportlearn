package com.sogou.upd.passport.oauth2.openresource.validator.impl;

import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.parameters.BaiduOAuthError;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthClientResponse;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class BaiduAPIValidator extends AbstractClientValidator {

    // renren错误码与passport错误码对应表
    public static Map<String, String> BAIDU_OAUTH_ERROR_MAP = new HashMap<String, String>();

    static {
        BAIDU_OAUTH_ERROR_MAP.put("6", ErrorUtil.ERR_CODE_CONNECT_REQUEST_NO_AUTHORITY);/*用户没有此api权限*/
        BAIDU_OAUTH_ERROR_MAP.put("211", ErrorUtil.ERR_CODE_CONNECT_REQUEST_NO_AUTHORITY);
        BAIDU_OAUTH_ERROR_MAP.put("111", ErrorUtil.ERR_CODE_CONNECT_TOKEN_INVALID);        /*access_token无效、过期*/
        BAIDU_OAUTH_ERROR_MAP.put("2002", ErrorUtil.ERR_CODE_CONNECT_TOKEN_INVALID);

    }

    @Override
    public void validateErrorResponse(OAuthClientResponse response) throws OAuthProblemException {
        String error_code = response.getParam(BaiduOAuthError.ERROR_CODE);
        if (!StringUtils.isEmpty(error_code)) {
            String errorDesc = "";
            error_code = BAIDU_OAUTH_ERROR_MAP.get(error_code);
            if (StringUtil.isEmpty(error_code)) {
                error_code = ErrorUtil.CONNECT_USER_DEFINED_ERROR;
                errorDesc = response.getParam(BaiduOAuthError.ERROR_DESCRIPTION);
            }
            throw OAuthProblemException.error(error_code).description(errorDesc);
        }
    }

}
