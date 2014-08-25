package com.sogou.upd.passport.oauth2.openresource.request;

import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.types.GrantTypeEnum;
import com.sogou.upd.passport.oauth2.common.types.ResponseTypeEnum;
import com.sogou.upd.passport.oauth2.openresource.parameters.QQOAuth;

/**
 * Passport访问开放平台进行OAuth授权请求类，包括：
 * 1.用户验证请求，获取code；
 * 2.用code获取access_token（含用refresh_token刷新access_token）；
 * 3.用access_token获取openid
 *
 * @author shipengzhi
 */
public class OAuthAuthzClientRequest extends OAuthClientRequest {

    public OAuthAuthzClientRequest(String url) {
        super(url);
    }

    public static AuthenticationRequestBuilder authorizationLocation(String url) {
        return new AuthenticationRequestBuilder(url);
    }

    public static TokenRequestBuilder tokenLocation(String url) {
        return new TokenRequestBuilder(url);
    }

    public static VerifyAccessTokenRequestBuilder verifyTokenLocation(String url) {
        return new VerifyAccessTokenRequestBuilder(url);
    }

    /**
     * 用户OAuth授权请求构造器
     *
     * @author shipengzhi(shipengzhi@sogou-inc.com)
     */
    public static class AuthenticationRequestBuilder extends OAuthClientRequestBuilder {

        public AuthenticationRequestBuilder(String url) {
            super(url);
        }

        // 响应结果类型，code还是token
        public AuthenticationRequestBuilder setResponseType(ResponseTypeEnum responseType) {
            this.parameters.put(OAuth.OAUTH_RESPONSE_TYPE, responseType == null ? null : responseType.getValue());
            return this;
        }

        // 第三方appkey
        public AuthenticationRequestBuilder setAppKey(String appKey) {
            this.parameters.put(OAuth.OAUTH_CLIENT_ID, appKey);
            return this;
        }

        // 重定向url
        public AuthenticationRequestBuilder setRedirectURI(String uri) {
            this.parameters.put(OAuth.OAUTH_REDIRECT_URI, uri);
            return this;
        }

        // 授权方法名
        public AuthenticationRequestBuilder setScope(String scope) {
            this.parameters.put(OAuth.OAUTH_SCOPE, scope);
            return this;
        }

        // 授权页面样式
        public AuthenticationRequestBuilder setDisplay(String display, int provider) {
            if (AccountTypeEnum.TAOBAO.getValue() == provider) {
                this.parameters.put(OAuth.OAUTH_TAOBAO_DISPLAY, display);
            } else if (AccountTypeEnum.QQ.getValue() == provider) {
                if (QQOAuth.WML_DISPLAY.equals(display)) {
                    this.parameters.put(OAuth.OAUTH_QQ_WAP_DISPLAY, "1");
                    this.parameters.put(OAuth.OAUTH_DISPLAY, "mobile");
                } else if (QQOAuth.XHTML_DISPLAY.equals(display)) {
                    this.parameters.put(OAuth.OAUTH_QQ_WAP_DISPLAY, "2");
                    this.parameters.put(OAuth.OAUTH_DISPLAY, "mobile");
                } else if (QQOAuth.MOBILE_DISPLAY.equals(display)) {
                    this.parameters.put(OAuth.OAUTH_DISPLAY, "mobile");
                }
            } else {
                this.parameters.put(OAuth.OAUTH_DISPLAY, display);
            }

            return this;
        }

        // client端状态值
        public AuthenticationRequestBuilder setState(String state) {
            this.parameters.put(OAuth.OAUTH_STATE, state);
            return this;
        }

        // 是否隐藏授权信息
        public AuthenticationRequestBuilder setShowAuthItems(int show_auth_items) {
            this.parameters.put(QQOAuth.SHOW_AUTH_ITEMS, show_auth_items);
            return this;
        }

        public AuthenticationRequestBuilder setViewPage(String viewPage) {
            this.parameters.put(QQOAuth.VIEW_PAGE, viewPage);
            return this;
        }

        // 是否强制用户输入用户名、密码
        public AuthenticationRequestBuilder setForceLogin(boolean force, int provider) {
            if (provider == AccountTypeEnum.RENREN.getValue()) {
                this.parameters.put(OAuth.OAUTH_RENREN_FORCELOGIN, force);
            } else if (provider == AccountTypeEnum.SINA.getValue()) {
                this.parameters.put(OAuth.OAUTH_SINA_FORCELOGIN, force);
            } else if (provider == AccountTypeEnum.BAIDU.getValue()) {
                this.parameters.put(OAuth.OAUTH_BAIDU_FORCELOGIN, force);
            }
            return this;
        }
    }

    /**
     * 获取access_token请求构造器
     *
     * @author shipengzhi(shipengzhi@sogou-inc.com)
     */
    public static class TokenRequestBuilder extends OAuthClientRequestBuilder {

        protected TokenRequestBuilder(String url) {
            super(url);
        }

        public TokenRequestBuilder setGrantType(GrantTypeEnum grantType) {
            this.parameters.put(OAuth.OAUTH_GRANT_TYPE, grantType == null ? null : grantType.getValue());
            return this;
        }

        public TokenRequestBuilder setAppKey(String appKey) {
            this.parameters.put(OAuth.OAUTH_CLIENT_ID, appKey);
            return this;
        }

        public TokenRequestBuilder setAppSecret(String appSecret) {
            this.parameters.put(OAuth.OAUTH_CLIENT_SECRET, appSecret);
            return this;
        }

        /* 用户名和密码进行OAuth2授权 */
        public TokenRequestBuilder setUsername(String username) {
            this.parameters.put(OAuth.OAUTH_USERNAME, username);
            return this;
        }

        public TokenRequestBuilder setPassword(String password) {
            this.parameters.put(OAuth.OAUTH_PASSWORD, password);
            return this;
        }

        public TokenRequestBuilder setScope(String scope) {
            this.parameters.put(OAuth.OAUTH_SCOPE, scope);
            return this;
        }

        public TokenRequestBuilder setCode(String code) {
            this.parameters.put(OAuth.OAUTH_CODE, code);
            return this;
        }

        public TokenRequestBuilder setRedirectURI(String uri) {
            this.parameters.put(OAuth.OAUTH_REDIRECT_URI, uri);
            return this;
        }

        /* 用refresh_token刷新access_token */
        public TokenRequestBuilder setRefreshToken(String token) {
            this.parameters.put(OAuth.OAUTH_REFRESH_TOKEN, token);
            return this;
        }

        public TokenRequestBuilder setParameter(String paramName, String paramValue) {
            this.parameters.put(paramName, paramValue);
            return this;
        }

        public TokenRequestBuilder setState(String state) {
            this.parameters.put(OAuth.OAUTH_STATE, state);
            return this;
        }
    }

    /*验证access_token的有效性*/
    public static class VerifyAccessTokenRequestBuilder extends OAuthClientRequestBuilder {

        protected VerifyAccessTokenRequestBuilder(String url) {
            super(url);
        }

        public VerifyAccessTokenRequestBuilder setOpenid(String openid) {
            this.parameters.put(OAuth.OAUTH_OPENID, openid);
            return this;
        }

        public VerifyAccessTokenRequestBuilder setAccessToken(String accessToken) {
            this.parameters.put(OAuth.OAUTH_ACCESS_TOKEN, accessToken);
            return this;
        }

    }

}
