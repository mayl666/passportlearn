package com.sogou.upd.passport.oauth2.common.parameters;

import com.sogou.upd.passport.common.exception.SystemException;
import com.sogou.upd.passport.oauth2.common.OAuthMessage;

import java.util.Map;

/**
 * Applies given parameters to the OAuth message.
 * Provided implementations include OAuth parameters in one of those:
 * <ul>
 * <li>HTTP request URI Query</li>
 * <li>HTTP request entity-body with application/x-www-form-urlencoded encoding</li>
 * <li>HTTP request entity-body with application/json encoding</li>
 * <li>HTTP request Authorization/WWW-Authenticate header</li>
 * </ul>
 * <p/>
 * Additional implementations can be provided.
 *
 *
 *
 *
 */
public interface OAuthParametersApplier {

    OAuthMessage applyOAuthParameters(OAuthMessage message, Map<String, Object> params) throws
            SystemException;
}
