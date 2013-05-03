package com.sogou.upd.passport.oauth2.openresource.validator.impl;

import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.parameters.OpenOAuth;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthClientResponse;
import org.apache.commons.lang3.StringUtils;

public class AuthzCodeValidator extends AbstractClientValidator {

    public AuthzCodeValidator() {
        requiredParams.put(OpenOAuth.OAUTH_CODE, new String[]{});

        notAllowedParams.add(OpenOAuth.OAUTH_ACCESS_TOKEN);
        notAllowedParams.add(OpenOAuth.OAUTH_EXPIRES_IN);
    }

    @Override
    public void validateErrorResponse(OAuthClientResponse response) throws OAuthProblemException {
        String error = response.getParam(OAuth.OAUTH_ERROR);
        if (!StringUtils.isEmpty(error)) {
            String errorDesc = response.getParam(OAuth.OAUTH_ERROR_DESCRIPTION);
            throw OAuthProblemException.error(ErrorUtil.CONNECT_USER_DENIED_LOGIN).description(errorDesc);
        }

    }
}
