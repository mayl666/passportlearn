package com.sogou.upd.passport.oauth2.openresource.validator.impl;

import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.parameters.OpenOAuth;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthClientResponse;
import org.apache.commons.lang3.StringUtils;

public class OpenIdValidator extends AbstractClientValidator {

    public OpenIdValidator() {
        requiredParams.put(OpenOAuth.OAUTH_QQ_OPENID, new String[]{});
        requiredParams.put(OpenOAuth.OAUTH_CLIENT_ID, new String[]{});

        notAllowedParams.add(OpenOAuth.OAUTH_CODE);
        notAllowedParams.add(OpenOAuth.OAUTH_ACCESS_TOKEN);
    }

    @Override
    public void validateErrorResponse(OAuthClientResponse response) throws OAuthProblemException {
        String error = response.getParam(OpenOAuth.OAUTH_ERROR);
        if (!StringUtils.isEmpty(error)) {
            String errorDesc = response.getParam(OpenOAuth.OAUTH_ERROR_DESCRIPTION);
            throw OAuthProblemException.error(error).description(errorDesc);
        }
    }
}
