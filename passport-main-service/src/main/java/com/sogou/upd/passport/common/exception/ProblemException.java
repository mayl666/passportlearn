package com.sogou.upd.passport.common.exception;

import com.google.common.base.Strings;

import java.util.HashMap;
import java.util.Map;

public class ProblemException extends Exception {

    private String error;
    private String description;
    private String uri;
    private String state;
    private String scope;
    private String redirectUri;

    private int responseStatus;

    private Map<String, String> parameters = new HashMap<String, String>();

    private ProblemException(String error) {
        this(error, "");
    }

    private ProblemException(String error, String description) {
        super(error + " " + description);
        this.description = description;
        this.error = error;
    }


    public static ProblemException error(String error) {
        return new ProblemException(error);
    }

    public static ProblemException error(String error, String description) {
        return new ProblemException(error, description);
    }

    public ProblemException description(String description) {
        this.description = description;
        return this;
    }

    public ProblemException uri(String uri) {
        this.uri = uri;
        return this;
    }

    public ProblemException state(String state) {
        this.state = state;
        return this;
    }

    public ProblemException scope(String scope) {
        this.scope = scope;
        return this;
    }

    public ProblemException responseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
        return this;
    }

    public ProblemException setParameter(String name, String value) {
        parameters.put(name, value);
        return this;
    }

    public String getError() {
        return error;
    }

    public String getDescription() {
        return description;
    }

    public String getUri() {
        return uri;
    }

    public String getState() {
        return state;
    }

    public String getScope() {
        return scope;
    }

    public int getResponseStatus() {
        return responseStatus == 0 ? 400 : responseStatus;
    }

    public String get(String name) {
        return parameters.get(name);
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    @Override
    public String getMessage() {
        StringBuffer b = new StringBuffer();
        if (!Strings.isNullOrEmpty(error)) {
            b.append(error);
        }

        if (!Strings.isNullOrEmpty(description)) {
            b.append(", ").append(description);
        }


        if (!Strings.isNullOrEmpty(uri)) {
            b.append(", ").append(uri);
        }


        if (!Strings.isNullOrEmpty(state)) {
            b.append(", ").append(state);
        }

        if (!Strings.isNullOrEmpty(scope)) {
            b.append(", ").append(scope);
        }

        return b.toString();
    }

    @Override
    public String toString() {
        return "ProblemException{"
                + "description='" + description + '\''
                + ", error='" + error + '\''
                + ", uri='" + uri + '\''
                + ", state='" + state + '\''
                + ", scope='" + scope + '\''
                + '}';
    }

}
