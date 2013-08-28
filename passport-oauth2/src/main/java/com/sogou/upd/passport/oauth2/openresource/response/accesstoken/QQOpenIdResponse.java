package com.sogou.upd.passport.oauth2.openresource.response.accesstoken;

import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.exception.HTMLTextParseException;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.common.utils.HTMLTextUtils;
import com.sogou.upd.passport.oauth2.common.utils.OAuthUtils;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthClientResponse;
import com.sogou.upd.passport.oauth2.openresource.validator.impl.OpenIdValidator;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-8-29
 * Time: 上午12:04
 * To change this template use File | Settings | File Templates.
 */
public class QQOpenIdResponse extends OAuthClientResponse {

    @Override
    protected void setBody(String body) throws OAuthProblemException {
        try {
            this.body = body;
            parameters = HTMLTextUtils.parseHTMLText(body);
        } catch (HTMLTextParseException e) {
            parameters = OAuthUtils.parseQQIrregularJSONObject(body);
        }
    }

    public String getOpenId() {
        return getParam(OAuth.OAUTH_OPENID);
    }

    public String getBody() {
        return body;
    }

    @Override
    public void init(String body, String contentType, int responseCode) throws OAuthProblemException {
        validator = new OpenIdValidator();
        super.init(body, contentType, responseCode);
    }
}
