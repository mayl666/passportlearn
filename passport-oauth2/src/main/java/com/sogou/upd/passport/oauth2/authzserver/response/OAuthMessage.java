package com.sogou.upd.passport.oauth2.authzserver.response;

import java.util.Map;

/**
 *
 *
 *
 */
public interface OAuthMessage {

    String getLocationUri();

    void setLocationUri(String uri);

    String getBody();

    void setBody(String body);

    String getHeader(String name);

    void addHeader(String name, String header);

    Map<String, String> getHeaders();

    void setHeaders(Map<String, String> headers);

}
