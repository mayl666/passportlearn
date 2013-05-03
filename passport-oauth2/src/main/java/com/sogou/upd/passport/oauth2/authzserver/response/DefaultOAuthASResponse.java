/**
 *       Copyright 2010 Newcastle University
 *
 *          http://research.ncl.ac.uk/smart/
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sogou.upd.passport.oauth2.authzserver.response;


import com.sogou.upd.passport.oauth2.common.OAuth;

import javax.servlet.http.HttpServletRequest;

/**
 *
 *
 *
 */
public class DefaultOAuthASResponse extends OAuthASResponse {

    protected DefaultOAuthASResponse(String uri, int responseStatus) {
        super(uri, responseStatus);
    }

    public static OAuthAuthorizationResponseBuilder authorizationResponse(HttpServletRequest request, int code) {
        return new OAuthAuthorizationResponseBuilder(request, code);
    }

    public static OAuthTokenResponseBuilder tokenResponse(int code) {
        return new OAuthTokenResponseBuilder(code);
    }

    public static class OAuthAuthorizationResponseBuilder extends OAuthResponseBuilder {

        public OAuthAuthorizationResponseBuilder(HttpServletRequest request, int responseCode) {
            super(responseCode);
            //AMBER-45
            String state = request.getParameter(OAuth.OAUTH_STATE);
            if (state != null) {
                this.setState(state);
            }
        }

        OAuthAuthorizationResponseBuilder setState(String state) {
            this.parameters.put(OAuth.OAUTH_STATE, state);
            return this;
        }

        public OAuthAuthorizationResponseBuilder setCode(String code) {
            this.parameters.put(OAuth.OAUTH_CODE, code);
            return this;
        }

        public OAuthAuthorizationResponseBuilder setAccessToken(String token) {
            this.parameters.put(OAuth.OAUTH_ACCESS_TOKEN, token);
            return this;
        }

        public OAuthAuthorizationResponseBuilder setExpiresIn(String expiresIn) {
            this.parameters.put(OAuth.OAUTH_EXPIRES_TIME, expiresIn == null ? null : Long.valueOf(expiresIn));
            return this;
        }

        public OAuthAuthorizationResponseBuilder setExpiresIn(Long expiresIn) {
            this.parameters.put(OAuth.OAUTH_EXPIRES_TIME, expiresIn);
            return this;
        }

        public OAuthAuthorizationResponseBuilder location(String location) {
            this.location = location;
            return this;
        }

        public OAuthAuthorizationResponseBuilder setParam(String key, String value) {
            this.parameters.put(key, value);
            return this;
        }
    }


    public static class OAuthTokenResponseBuilder extends OAuthResponseBuilder {

        public OAuthTokenResponseBuilder(int responseCode) {
            super(responseCode);
        }

        public OAuthTokenResponseBuilder setAccessToken(String token) {
            this.parameters.put(OAuth.OAUTH_ACCESS_TOKEN, token);
            return this;
        }

        public OAuthTokenResponseBuilder setExpiresTime(long expiresTime) {
            this.parameters.put(OAuth.OAUTH_EXPIRES_TIME, expiresTime);
            return this;
        }

        public OAuthTokenResponseBuilder setRefreshToken(String refreshToken) {
            this.parameters.put(OAuth.OAUTH_REFRESH_TOKEN, refreshToken);
            return this;
        }

        public OAuthTokenResponseBuilder setTokenType(String tokenType) {
            this.parameters.put(OAuth.OAUTH_TOKEN_TYPE, tokenType);
            return this;
        }

        public OAuthTokenResponseBuilder setParam(String key, String value) {
            this.parameters.put(key, value);
            return this;
        }

        public OAuthTokenResponseBuilder location(String location) {
            this.location = location;
            return this;
        }
    }

}
