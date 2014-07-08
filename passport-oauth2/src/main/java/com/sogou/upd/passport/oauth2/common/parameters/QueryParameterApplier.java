package com.sogou.upd.passport.oauth2.common.parameters;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.oauth2.authzserver.response.OAuthMessage;
import com.sogou.upd.passport.oauth2.common.utils.OAuthUtils;

import java.util.Map;

/**
 * GET请求 构造URL的参数
 * 不带#access_Token锚点
 *
 * @author shipengzhi
 */
public class QueryParameterApplier implements OAuthParametersApplier {

    public OAuthMessage applyOAuthParameters(OAuthMessage message, Map<String, Object> params) {

        String messageUrl = message.getLocationUri();
        if (messageUrl != null) {
            String url = applyOAuthParametersString(messageUrl, params);
            message.setLocationUri(url);
        }
        return message;
    }

    public static String applyOAuthParametersString(String messageUrl, Map<String, Object> params) {
        boolean isContainsQuery = messageUrl.contains("?");
        StringBuilder url = new StringBuilder(messageUrl);

        //scope参数不编码，QQ和RENREN,scope目前不为空，BAIDU和SINA的scope值为空,编码时暂时移除
//        Object scope = null;
//        if (params.containsKey(OAuth.OAUTH_SCOPE) && params.get(OAuth.OAUTH_SCOPE) != null) {
//            scope = params.get(OAuth.OAUTH_SCOPE);
//            params.remove(OAuth.OAUTH_SCOPE);
//        }

        StringBuilder query = new StringBuilder(OAuthUtils.format(params.entrySet(),
                CommonConstant.DEFAULT_CONTENT_CHARSET));

        //编码后将scope添加到query中
//        if (query.length() > 0) {
//            query.append(OAuthUtils.PARAMETER_SEPARATOR);
//        }
//        query.append(OAuth.OAUTH_SCOPE);
//        query.append(OAuthUtils.NAME_VALUE_SEPARATOR);
//        query.append(scope);
        if (!Strings.isNullOrEmpty(query.toString())) {
            if (isContainsQuery) {
                url.append("&").append(query);
            } else {
                url.append("?").append(query);
            }
        }
        return url.toString();
    }
}
