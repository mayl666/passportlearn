package com.sogou.upd.passport.oauth2.common;

/**
 *
 *
 *
 */
public abstract class OAuthError {

    //error response params
    public static final String OAUTH_ERROR = "error";
    public static final String OAUTH_ERROR_DESCRIPTION = "error_description";
    public static final String OAUTH_ERROR_URI = "error_uri";

    public static final class Response {
        /**
         * invalid_request
         * <p/>
         * The request is missing a required parameter, includes an
         * unsupported parameter value, or is otherwise malformed.
         */
        public static final String INVALID_REQUEST = "100";

        /**
         * client_id或client_secret不匹配
         * <p/>
         * Client authentication failed (e.g. unknown client, no
         * client authentication included, or unsupported
         * authentication method).  The authorization server MAY
         * return an HTTP 401 (Unauthorized) status code to indicate
         * which HTTP authentication schemes are supported.  If the
         * client attempted to authenticate via the "Authorization"
         * request header field, the authorization server MUST
         * respond with an HTTP 401 (Unauthorized) status code, and
         * include the "WWW-Authenticate" response header field
         * matching the authentication scheme used by the client.
         */
        public static final String INVALID_CLIENT = "101";

        /**
         * invalid_grant
         * The provided authorization grant (e.g. authorization
         * code, resource owner credentials, client credentials) is
         * invalid, expired, revoked, does not match the redirection
         * URI used in the authorization request, or was issued to
         * another client.
         */
        public static final String INVALID_GRANT = "102";

        /**
         * 错误的grant_type
         * The authorization grant type is not supported by the
         * authorization server.
         */
        public static final String UNSUPPORTED_GRANT_TYPE = "103";

        /**
         * unsupported_response_type
         * The authorization server does not support obtaining an
         * authorization code using this method.
         */
        public static final String UNSUPPORTED_RESPONSE_TYPE = "104";

        /**
         * invalid_scope
         * The requested scope is invalid, unknown, or malformed.
         */
        public static final String INVALID_SCOPE = "105";

        /**
         * insufficient_scope
         * The request requires higher privileges than provided by the
         * access token.
         */
        public static final String INSUFFICIENT_SCOPE = "106";

        /**
         * expired_token
         */
        public static final String EXPIRED_TOKEN = "107";

        /**
         * access_token不存在或已过期
         */
        public static final String INVALID_ACCESS_TOKEN = "108";

        /**
         * refresh_token不存在或已过期
         */
        public static final String INVALID_REFRESH_TOKEN = "109";
        
        /**
         * login/authorize fail,数据库写入失败
         */
        public static final String AUTHORIZE_FAIL = "110";

        /**
         * 用户名密码不匹配
         */
        public static final String USERNAME_PWD_MISMATCH = "111";

        /**
         * 用户不是有效账号，无法登录
         */
        public static final String INVALID_USER="112";

        /**
         * 此账号不允许绑定其他账号
         */
        public static final String UNABLE_BIND_ACCESS_TOKEN = "113";

        /**
         * 绑定账号时,数据库写入失败
         */
        public static final String BIND_FAIL = "114";
        
    }

}
