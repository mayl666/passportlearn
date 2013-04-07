package com.sogou.upd.passport.oauth2.openresource.response;

import com.sogou.upd.passport.common.exception.ProblemException;
import com.sogou.upd.passport.common.exception.SystemException;
import com.sogou.upd.passport.oauth2.authzserver.request.OAuthRequest;
import com.sogou.upd.passport.oauth2.common.validators.OAuthValidator;
import com.sogou.upd.passport.oauth2.openresource.OpenOAuth;
import com.sogou.upd.passport.oauth2.openresource.validator.OAuthSinaSSOBindTokenValidator;
import com.sogou.upd.passport.oauth2.openresource.validator.OAuthSinaSSOTokenValidator;

import javax.servlet.http.HttpServletRequest;


/**
 * sina微博采用SSO-SDK，OAuth2登录授权成功后的响应结果
 *
 * @author shipengzhi(shipengzhi@sogou-inc.com)
 */
public class OAuthSinaSSOBindTokenRequest extends OAuthSinaSSOTokenRequest {

    public OAuthSinaSSOBindTokenRequest(HttpServletRequest request) throws SystemException, ProblemException {
        super(request);
    }

    @Override
    protected OAuthValidator<HttpServletRequest> initValidator() throws ProblemException {
        return new OAuthSinaSSOBindTokenValidator();
    }


    public String getBindToken() {
        return getParam(OpenOAuth.BIND_ACCESS_TOKEN);
    }

}
