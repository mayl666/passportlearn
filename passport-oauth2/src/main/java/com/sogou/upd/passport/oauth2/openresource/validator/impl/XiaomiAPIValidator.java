package com.sogou.upd.passport.oauth2.openresource.validator.impl;

import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.parameters.XiaomiOAuth;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthClientResponse;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class XiaomiAPIValidator extends AbstractClientValidator {

    // 新浪微博错误码与passport错误码对应表
    public static Map<String, String> XIAOMI_OAUTH_ERROR_MAP = new HashMap<String, String>();

    static {
        XIAOMI_OAUTH_ERROR_MAP.put("96004", ErrorUtil.ERR_CODE_CONNECT_REQUEST_NO_AUTHORITY);
        XIAOMI_OAUTH_ERROR_MAP.put("96008", ErrorUtil.ERR_CODE_CONNECT_TOKEN_INVALID);
        XIAOMI_OAUTH_ERROR_MAP.put("96009", ErrorUtil.ERR_CODE_CONNECT_TOKEN_INVALID);
        XIAOMI_OAUTH_ERROR_MAP.put("96010", ErrorUtil.ERR_CODE_CONNECT_INVALID_OAUTH);
        XIAOMI_OAUTH_ERROR_MAP.put("96012", ErrorUtil.ERR_CODE_CONNECT_USERCANAEL);
        XIAOMI_OAUTH_ERROR_MAP.put("96013", ErrorUtil.ERR_CODE_CONNECT_TOKEN_INVALID);

    }

    @Override
    public void validateErrorResponse(OAuthClientResponse response) throws OAuthProblemException {
        String error_code = response.getParam(XiaomiOAuth.CODE);
        if (!StringUtils.isEmpty(error_code) && !error_code.equals(XiaomiOAuth.SUCCESS_CODE)) {
            String errorDesc = "";
            error_code = XIAOMI_OAUTH_ERROR_MAP.get(error_code);
            if (StringUtil.isEmpty(error_code)) {
                error_code = ErrorUtil.CONNECT_USER_DEFINED_ERROR;
                errorDesc = response.getParam(XiaomiOAuth.DESCRIPTION);
            }
            throw OAuthProblemException.error(error_code).description(errorDesc);
        }

    }

}
