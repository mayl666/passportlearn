package com.sogou.upd.passport.oauth2.authzserver.response;

import com.sogou.upd.passport.common.HttpConstant;
import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.OAuthError;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.common.parameters.*;

import java.util.HashMap;
import java.util.Map;

/**
 * OAuth2.0响应类
 */
public class OAuthASResponse implements OAuthMessage {

    protected int responseStatus;
    protected String uri;
    protected String body;

    protected Map<String, String> headers = new HashMap<String, String>();

    protected OAuthASResponse(String uri, int responseStatus) {
        this.uri = uri;
        this.responseStatus = responseStatus;
    }

    public static OAuthResponseBuilder status(int code) {
        return new OAuthResponseBuilder(code);
    }

    public static OAuthErrorResponseBuilder errorResponse(int code) {
        return new OAuthErrorResponseBuilder(code);
    }

    public static OAuthErrorResponseBuilder errorResponse() {
        return new OAuthErrorResponseBuilder();
    }

    @Override
    public String getLocationUri() {
        return uri;
    }

    @Override
    public void setLocationUri(String uri) {
        this.uri = uri;
    }

    @Override
    public String getBody() {
        return body;
    }

    @Override
    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    @Override
    public void addHeader(String name, String header) {
        headers.put(name, header);
    }

    public static class OAuthResponseBuilder {

        protected OAuthParametersApplier applier;
        protected Map<String, Object> parameters = new HashMap<String, Object>();
        protected int responseCode;
        protected String location;

        public OAuthResponseBuilder() {
        }

        public OAuthResponseBuilder(int responseCode) {
            this.responseCode = responseCode;
        }

        public OAuthResponseBuilder location(String location) {
            this.location = location;
            return this;
        }

        public OAuthResponseBuilder setScope(String value) {
            this.parameters.put(OAuth.OAUTH_SCOPE, value);
            return this;
        }

        public OAuthResponseBuilder setParam(String key, String value) {
            this.parameters.put(key, value);
            return this;
        }

        public OAuthASResponse buildQueryMessage() throws OAuthProblemException {
            OAuthASResponse msg = new OAuthASResponse(location, responseCode);
            this.applier = new QueryParameterApplier();
            return (OAuthASResponse) applier.applyOAuthParameters(msg, parameters);
        }

        public OAuthASResponse buildBodyMessage() throws OAuthProblemException {
            OAuthASResponse msg = new OAuthASResponse(location, responseCode);
            this.applier = new BodyURLEncodedParametersApplier();
            return (OAuthASResponse) applier.applyOAuthParameters(msg, parameters);
        }

        public OAuthASResponse buildJSONMessage() throws OAuthProblemException {
            OAuthASResponse msg = new OAuthASResponse(location, responseCode);
            this.applier = new JSONBodyParametersApplier();
            return (OAuthASResponse) applier.applyOAuthParameters(msg, parameters);
        }

        public OAuthASResponse buildHeaderMessage() throws OAuthProblemException {
            OAuthASResponse msg = new OAuthASResponse(location, responseCode);
            this.applier = new WWWAuthHeaderParametersApplier();
            return (OAuthASResponse) applier.applyOAuthParameters(msg, parameters);
        }
    }

    public static class OAuthErrorResponseBuilder extends OAuthResponseBuilder {

        public OAuthErrorResponseBuilder() {
            super();
        }

        public OAuthErrorResponseBuilder(int responseCode) {
            super(responseCode);
        }

        public OAuthErrorResponseBuilder error(OAuthProblemException ex) {
            this.parameters.put(OAuthError.OAUTH_ERROR, ex.getError());
            this.parameters.put(OAuthError.OAUTH_ERROR_DESCRIPTION, ex.getDescription());
//            this.parameters.put(OAuthError.OAUTH_ERROR_URI, ex.getUri());
//            this.parameters.put(OAuth.OAUTH_STATE, ex.getState());
            return this;
        }

        public OAuthErrorResponseBuilder setError(String error) {
            this.parameters.put(OAuthError.OAUTH_ERROR, error);
            return this;
        }

        public OAuthErrorResponseBuilder setErrorDescription(String desc) {
            this.parameters.put(OAuthError.OAUTH_ERROR_DESCRIPTION, desc);
            return this;
        }

        public OAuthErrorResponseBuilder setErrorUri(String state) {
            this.parameters.put(OAuthError.OAUTH_ERROR_URI, state);
            return this;
        }

        public OAuthErrorResponseBuilder setState(String state) {
            this.parameters.put(OAuth.OAUTH_STATE, state);
            return this;
        }

        public OAuthErrorResponseBuilder setRealm(String realm) {
            this.parameters.put(HttpConstant.WWWAuthHeader.REALM, realm);
            return this;
        }

        public OAuthErrorResponseBuilder location(String location) {
            this.location = location;
            return this;
        }
    }

}
