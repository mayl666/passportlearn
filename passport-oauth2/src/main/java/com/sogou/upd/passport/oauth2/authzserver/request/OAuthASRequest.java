package com.sogou.upd.passport.oauth2.authzserver.request;

import com.google.common.base.Strings;
import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.common.utils.OAuthUtils;
import com.sogou.upd.passport.oauth2.common.validators.OAuthValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * OAuth2.0请求抽象类
 */
public abstract class OAuthASRequest {

    private Logger log = LoggerFactory.getLogger(OAuthASRequest.class);

    protected HttpServletRequest request;
    protected OAuthValidator<HttpServletRequest> validator;
    protected Map<String, Class<? extends OAuthValidator<HttpServletRequest>>> validators =
            new HashMap<String, Class<? extends OAuthValidator<HttpServletRequest>>>();

    public OAuthASRequest(HttpServletRequest request) throws OAuthProblemException {
        this.request = request;
        validate();
    }

    public OAuthASRequest() {
    }

    protected void validate() throws OAuthProblemException {
        try {
            validator = initValidator();
            validator.validateMethod(request);
//            validator.validateContentType(request);
            validator.validateRequiredParameters(request);
        } catch (OAuthProblemException e) {
            try {
                String redirectUri = request.getParameter(OAuth.OAUTH_REDIRECT_URI);
                if (!Strings.isNullOrEmpty(redirectUri)) {
                    e.setRedirectUri(redirectUri);
                }
            } catch (Exception ex) {
                if (log.isDebugEnabled()) {
                    log.debug("Cannot read redirect_url from the request: {}", new Object[]{ex.getMessage()});
                }
            }
            throw e;
        }
    }

    protected abstract OAuthValidator<HttpServletRequest> initValidator() throws OAuthProblemException;

    public String getParam(String name) {
        return request.getParameter(name);
    }

    public int getClientId() {
        String value = getParam(OAuth.OAUTH_CLIENT_ID);
        return value == null ? 0 : Integer.valueOf(value);
    }

    public String getRedirectURI() {
        return getParam(OAuth.OAUTH_REDIRECT_URI);
    }

    public String getClientSecret() {
        return getParam(OAuth.OAUTH_CLIENT_SECRET);
    }

    public String getInstanceId() {
        return getParam(OAuth.OAUTH_INSTANCE_ID);
    }

    public Set<String> getScopes() {
        String scopes = getParam(OAuth.OAUTH_SCOPE);

        return OAuthUtils.decodeScopes(scopes);
    }

}
