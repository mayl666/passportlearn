package com.sogou.upd.passport.oauth2.common;

import com.sogou.upd.passport.common.utils.ErrorUtil;

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
         * client_id或client_secret不匹配
         */
        public static final String INVALID_CLIENT = "101";

        /**
         * invalid_grant
         */
        public static final String INVALID_GRANT = "102";

        /**
         * 错误的grant_type
         */
        public static final String UNSUPPORTED_GRANT_TYPE = "103";

        /**
         * unsupported_response_type
         */
        public static final String UNSUPPORTED_RESPONSE_TYPE = "104";

        /**
         * invalid_scope
         */
        public static final String INVALID_SCOPE = "105";

        /**
         * insufficient_scope
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

    }

    static {
        ErrorUtil.ERR_CODE_MSG_MAP.put(Response.INVALID_CLIENT, "client_id or client_secret mismatch");
        ErrorUtil.ERR_CODE_MSG_MAP.put(Response.UNSUPPORTED_GRANT_TYPE, "unsupported_grant_type");
        ErrorUtil.ERR_CODE_MSG_MAP.put(Response.INVALID_REFRESH_TOKEN, "refresh_token not exist or expired");
        ErrorUtil.ERR_CODE_MSG_MAP.put(Response.AUTHORIZE_FAIL, "authorize fail");
        ErrorUtil.ERR_CODE_MSG_MAP.put(Response.USERNAME_PWD_MISMATCH, "username or password mismatch");
    }

}
