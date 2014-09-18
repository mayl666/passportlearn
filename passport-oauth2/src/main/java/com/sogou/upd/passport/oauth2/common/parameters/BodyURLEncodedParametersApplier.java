package com.sogou.upd.passport.oauth2.common.parameters;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.oauth2.authzserver.response.OAuthMessage;
import com.sogou.upd.passport.oauth2.common.utils.OAuthUtils;

import java.util.Map;

/**
 *
 *
 *
 */
public class BodyURLEncodedParametersApplier implements OAuthParametersApplier {

    public OAuthMessage applyOAuthParameters(OAuthMessage message, Map<String, Object> params) {

        String body = OAuthUtils.format(params.entrySet(), CommonConstant.DEFAULT_CHARSET);
        message.setBody(body);
        return message;

    }
}
