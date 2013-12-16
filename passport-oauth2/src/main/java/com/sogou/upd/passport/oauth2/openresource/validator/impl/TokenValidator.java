package com.sogou.upd.passport.oauth2.openresource.validator.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.parameters.OpenOAuth;
import com.sogou.upd.passport.oauth2.openresource.parameters.QQOAuthError;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthClientResponse;

public class TokenValidator extends AbstractClientValidator {

    public TokenValidator() {

        requiredParams.put(OpenOAuth.OAUTH_ACCESS_TOKEN, new String[]{});

        notAllowedParams.add(OpenOAuth.OAUTH_CODE);
    }

    @Override
    public void validateErrorResponse(OAuthClientResponse response) throws OAuthProblemException {
        String errorCode = response.getParam(QQOAuthError.ERROR_CODE);
        if (!Strings.isNullOrEmpty(errorCode) && !errorCode.equals("0")) {    // QQ的根据code获取accesstoken接口返回结果错误码为ret
            String errorDesc = response.getParam(QQOAuthError.ERROR_DESCRIPTION);
            throw OAuthProblemException.error(errorCode).description(errorDesc);
        } else {
            errorCode = response.getParam(OAuth.OAUTH_ERROR);
            if (!Strings.isNullOrEmpty(errorCode)) {
                String errorDesc = response.getParam(OAuth.OAUTH_ERROR_DESCRIPTION);
                throw OAuthProblemException.error(errorCode).description(errorDesc);
            }
        }
    }
}
