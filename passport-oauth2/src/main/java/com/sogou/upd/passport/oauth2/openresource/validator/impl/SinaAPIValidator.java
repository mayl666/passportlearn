package com.sogou.upd.passport.oauth2.openresource.validator.impl;

import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.parameters.SinaOAuthError;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthClientResponse;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class SinaAPIValidator extends AbstractClientValidator {

    // 新浪微博错误码与passport错误码对应表
    public static Map<String, String> SINA_OAUTH_ERROR_MAP = new HashMap<String, String>();

    static {
        SINA_OAUTH_ERROR_MAP.put("10016", ErrorUtil.ERR_CODE_COM_REQURIE);
        SINA_OAUTH_ERROR_MAP.put("20007", ErrorUtil.UPDATE_MULTIPART_IMAGE);
        SINA_OAUTH_ERROR_MAP.put("20005", ErrorUtil.UNSUPPORT_IMAGE_FORMAT);
        SINA_OAUTH_ERROR_MAP.put("20019", ErrorUtil.REPEAT_CONTENT);
        SINA_OAUTH_ERROR_MAP.put("20506", ErrorUtil.ALREADY_FOLLOWED);        /*新浪token过期、失效*/
        SINA_OAUTH_ERROR_MAP.put("21315", ErrorUtil.ERR_CODE_CONNECT_TOKEN_INVALID);
        SINA_OAUTH_ERROR_MAP.put("21316", ErrorUtil.ERR_CODE_CONNECT_TOKEN_INVALID);
        SINA_OAUTH_ERROR_MAP.put("21317", ErrorUtil.ERR_CODE_CONNECT_TOKEN_INVALID);
        SINA_OAUTH_ERROR_MAP.put("21327", ErrorUtil.ERR_CODE_CONNECT_TOKEN_INVALID);
    }

    @Override
    public void validateErrorResponse(OAuthClientResponse response) throws OAuthProblemException {
        String error_code = response.getParam(SinaOAuthError.ERROR_CODE);
        if (!StringUtils.isEmpty(error_code)) {
            String errorDesc = "";
            error_code = SINA_OAUTH_ERROR_MAP.get(error_code);
            if (StringUtil.isEmpty(error_code)) {
                error_code = ErrorUtil.CONNECT_USER_DEFINED_ERROR;
                errorDesc = response.getParam(SinaOAuthError.ERROR_DESCRIPTION);
            }
            throw OAuthProblemException.error(error_code).description(errorDesc);
        }

    }

}
