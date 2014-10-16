package com.sogou.upd.passport.oauth2.openresource.validator.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.parameters.OpenOAuth;
import com.sogou.upd.passport.oauth2.openresource.parameters.QQOAuth;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthClientResponse;
import org.apache.commons.lang3.StringUtils;

public class AuthzCodeValidator extends AbstractClientValidator {

    public AuthzCodeValidator() {
        requiredParams.put(OpenOAuth.OAUTH_CODE, new String[]{});
        requiredParams.put(OpenOAuth.OAUTH_STATE, new String[]{});

        notAllowedParams.add(OpenOAuth.OAUTH_ACCESS_TOKEN);
        notAllowedParams.add(OpenOAuth.OAUTH_EXPIRES_IN);
    }

    @Override
    public void validateErrorResponse(OAuthClientResponse response) throws OAuthProblemException {
        String error = response.getParam(OAuth.OAUTH_ERROR);
        if (!StringUtils.isEmpty(error)) {
            String errorDesc = response.getParam(OAuth.OAUTH_ERROR_DESCRIPTION);
            throw OAuthProblemException.error(ErrorUtil.ERR_CODE_CONNECT_USER_DENIED_LOGIN).description(errorDesc);
        }
        //QQ校验是否是用户取消授权
        String usercancel=response.getParam(QQOAuth.USERCANCEL);
        if(!Strings.isNullOrEmpty(usercancel)){
            String errorDesc = ErrorUtil.getERR_CODE_MSG_MAP().get(ErrorUtil.ERR_CODE_CONNECT_USERCANAEL);
            throw OAuthProblemException.error(ErrorUtil.ERR_CODE_CONNECT_USERCANAEL).description(errorDesc);
        }
    }
}
