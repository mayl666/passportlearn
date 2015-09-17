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
 * OAuth协议相关的常量类
 */
public class OAuth {

    // Authorization request params
    public static final String OAUTH_RESPONSE_TYPE = "response_type";
    public static final String OAUTH_CLIENT_ID = "client_id";
    public static final String OAUTH_CLIENT_SECRET = "client_secret";
    public static final String OAUTH_REDIRECT_URI = "redirect_uri";
    public static final String OAUTH_INSTANCE_ID = "instance_id";
    public static final String OAUTH_USERNAME = "username";
    public static final String OAUTH_PASSWORD = "password";
    public static final String OAUTH_PASSWORD_TYPE = "pwd_type";
    public static final String OAUTH_SCOPE = "scope";
    public static final String OAUTH_STATE = "state";
    public static final String OAUTH_HREF="href";   //微信二维码样式
    public static final String OAUTH_DISPLAY = "display"; // 样式
    public static final String OAUTH_TAOBAO_DISPLAY = "view"; // taobao样式
    public static final String OAUTH_QQ_WAP_DISPLAY = "g_ut"; // 样式
    public static final String OAUTH_GRANT_TYPE = "grant_type";
    public static final String OAUTH_RENREN_FORCELOGIN = "x_renew"; // renren强制登录
    public static final String OAUTH_SINA_FORCELOGIN = "forcelogin"; // sina强制登录
    public static final String OAUTH_BAIDU_FORCELOGIN = "force_login";  // baidu强制登录
    public static final String OAUTH_WEIXIN_CLIENT_ID = "appid";  //weixin的应用ID
    public static final String OAUTH_WEIXIN_CLIENT_SECRET = "secret"; //weixin的应用密钥

    public static final String OAUTH_OPENID = "openid"; // qq 用access_token获取openId

    public static final String OAUTH_HEADER_NAME = "Bearer";

    //Authorization response params
    public static final String OAUTH_CODE = "code";
    public static final String OAUTH_ACCESS_TOKEN = "access_token";
    public static final String OAUTH_EXPIRES_TIME = "expires_time";   // sogou-passport返回结果中的过期时间点
    public static final String OAUTH_EXPIRES_IN = "expires_in";
    public static final String OAUTH_REFRESH_TOKEN = "refresh_token";
    public static final String OAUTH_RTOKEN_EXPIRES_IN = "refreshToken_expires_in";
    public static final String OAUTH_TOKEN_TYPE = "token_type";

    //resource
    public static final String OAUTH_RESOURCE_TYPE = "resource_type";

    // openId response params
    public static final ParameterStyleEnum DEFAULT_PARAMETER_STYLE = ParameterStyleEnum.HEADER;
    public static final TokenTypeEnum DEFAULT_TOKEN_TYPE = TokenTypeEnum.BEARER;

    //error response params
    public static final String OAUTH_ERROR = "error";
    public static final String OAUTH_ERROR_DESCRIPTION = "error_description";
    public static final String OAUTH_ERROR_URI = "error_uri";

    // open api
    public static final String AVATAR_SMALL_KEY = "avatar_small";  // 50pt*50pt 小头像
    public static final String AVATAR_MIDDLE_KEY = "avatar_middle";  // 100pt*300pt 小头像
    public static final String AVATAR_LARGE_KEY = "avatar_large";  // 200pt*600pt 小头像
}
