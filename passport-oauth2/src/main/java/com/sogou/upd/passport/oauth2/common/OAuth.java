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

package com.sogou.upd.passport.oauth2.common;

import com.sogou.upd.passport.oauth2.common.types.ParameterStyleEnum;
import com.sogou.upd.passport.oauth2.common.types.TokenTypeEnum;

/**
 *
 *
 *
 */
public class OAuth {

    public static final class HttpMethod {
        public static final String POST = "POST";
        public static final String GET = "GET";
        public static final String DELETE = "DELETE";
        public static final String PUT = "PUT";
    }

    public static final class HeaderType {
        public static final String CONTENT_TYPE = "Content-Type";
        public static final String WWW_AUTHENTICATE = "WWW-Authenticate";
        public static final String AUTHORIZATION = "Authorization";
    }

    public static final class WWWAuthHeader {
        public static final String REALM = "realm";
    }

    public static final class ContentType {
        public static final String URL_ENCODED = "application/x-www-form-urlencoded";
        public static final String JSON = "application/json";
    }

    public static final String UTF8 = "utf-8";

    // Authorization request params
    public static final String OAUTH_RESPONSE_TYPE = "response_type";
    public static final String OAUTH_CLIENT_ID = "client_id";
    public static final String OAUTH_CLIENT_SECRET = "client_secret";
    public static final String OAUTH_REDIRECT_URI = "redirect_uri";
    public static final String OAUTH_INSTANCE_ID = "instance_id";
    public static final String OAUTH_USERNAME = "username";
    public static final String OAUTH_PASSWORD = "password";
    public static final String OAUTH_SCOPE = "scope";
    public static final String OAUTH_STATE = "state";
    public static final String OAUTH_DISPLAY = "display"; // 样式
    public static final String OAUTH_GRANT_TYPE = "grant_type";
    public static final String OAUTH_RENREN_FORCELOGIN = "x_renew"; // renren强制登录
    public static final String OAUTH_SINA_FORCELOGIN = "forcelogin"; // sina强制登录

    public static final String OAUTH_HEADER_NAME = "Bearer";

    //Authorization response params
    public static final String OAUTH_CODE = "code";
    public static final String OAUTH_ACCESS_TOKEN = "access_token";
    public static final String OAUTH_EXPIRES_TIME = "expires_time";   // sogou-passport返回结果中的过期时间点
    public static final String OAUTH_EXPIRES_IN = "expires_in";
    public static final String OAUTH_REFRESH_TOKEN = "refresh_token";
    public static final String OAUTH_RTOKEN_EXPIRES_IN = "refreshToken_expires_in";

    public static final String OAUTH_TOKEN_TYPE = "token_type";

    // openId response params
    public static final ParameterStyleEnum DEFAULT_PARAMETER_STYLE = ParameterStyleEnum.HEADER;
    public static final TokenTypeEnum DEFAULT_TOKEN_TYPE = TokenTypeEnum.BEARER;

    //error response params
    public static final String OAUTH_ERROR = "error";
    public static final String OAUTH_ERROR_DESCRIPTION = "error_description";
    public static final String OAUTH_ERROR_URI = "error_uri";
}
