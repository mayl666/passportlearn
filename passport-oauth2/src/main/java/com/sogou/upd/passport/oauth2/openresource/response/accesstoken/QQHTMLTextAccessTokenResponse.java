package com.sogou.upd.passport.oauth2.openresource.response.accesstoken;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.exception.HTMLTextParseException;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.common.utils.HTMLTextUtils;

/**
 * QQ.web正确返回结果为text/html
 * 异常结果为(callback：{json})
 * QQ.wap正常和异常返回结果均为text/html
 * 响应码为200
 * 变态！变态！变态！变态！
 *
 * @author shipengzhi(shipengzhi@sogou-inc.com)
 */
public class QQHTMLTextAccessTokenResponse extends AbstractAccessTokenResponse {

    @Override
    public void setBody(String body) throws OAuthProblemException {
        try {
            this.body = body;
            parameters = HTMLTextUtils.parseHTMLText(body);
        } catch (HTMLTextParseException e) {
            throw OAuthProblemException.error(ErrorUtil.UNSUPPORTED_RESPONSE_TYPE,
                    "Invalid response! Response body is not " + OAuth.ContentType.JSON + " encoded");
        }

    }

    @Override
    public String getAccessToken() {
        return getParam(OAuth.OAUTH_ACCESS_TOKEN);
    }

    @Override
    public Long getExpiresIn() {
        String value = getParam(OAuth.OAUTH_EXPIRES_IN);
        return Strings.isNullOrEmpty(value) ? null : Long.valueOf(value);
    }

    @Override
    public String getRefreshToken() {
        return getParam(OAuth.OAUTH_REFRESH_TOKEN);
    }

    @Override
    public String getScope() {
        return getParam(OAuth.OAUTH_SCOPE);
    }

    /**
     * QQ Authoz Code不返回openid，需额外接口调用
     * @return
     */
    @Override
    public String getOpenid() {
        // QQ不返回openid，需调用获取openid的接口
        return "";
    }

    /**
     * QQ Authoz Code不返回nickName
     * @return ""
     */
    @Override
    public String getNickName() {
        return "";
    }

}
