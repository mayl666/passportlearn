package com.sogou.upd.passport.oauth2.openresource.request;

import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.authzserver.response.OAuthMessage;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.common.parameters.BodyURLEncodedParametersApplier;
import com.sogou.upd.passport.oauth2.common.parameters.ClientQueryParameterApplier;
import com.sogou.upd.passport.oauth2.common.parameters.OAuthParametersApplier;
import com.sogou.upd.passport.oauth2.common.parameters.QueryParameterApplier;
import com.sogou.upd.passport.oauth2.common.utils.OAuthUtils;

import java.util.HashMap;
import java.util.Map;

public class OAuthClientRequest implements OAuthMessage {

  protected String url;
  protected String body;
  protected Map<String, String> headers;

  protected OAuthClientRequest(String url) {
    this.url = url;
  }

  public String getLocationUri() {
    return url;
  }

  public void setLocationUri(String uri) {
    this.url = uri;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public String getHeader(String name) {
    return headers.get(name);
  }

  public void addHeader(String name, String header) {
    headers.put(name, header);
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public void setHeaders(Map<String, String> headers) {
    this.headers = headers;
  }

  public static <T extends OAuthClientRequestBuilder> T apiLocation(String url, Class<T> clazz)
      throws OAuthProblemException {
    T builder = (T) OAuthUtils.instantiateClassWithParameters(clazz, new Class[]{String.class},
                                                              new String[]{url});
    return builder;
  }

  public static class OAuthClientRequestBuilder {
    protected OAuthParametersApplier builder;
    protected Map<String, Object> parameters = new HashMap<String, Object>();
    protected String url;

    public Map<String, Object> getParameters() {
      return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
      this.parameters = parameters;
    }

    protected OAuthClientRequestBuilder(String url) {
      this.url = url;
    }

    // 构建GET请求所需的Query,#access_Token锚点
    public <T extends OAuthClientRequest> T buildQueryMessage(Class<T> clazz) throws OAuthProblemException {
      T request = (T) OAuthUtils.instantiateClassWithParameters(clazz, new Class[]{String.class},
                                                                new String[]{url});
      this.builder = new ClientQueryParameterApplier();
      return (T) builder.applyOAuthParameters(request, parameters);
    }

    // 构建POST请求所需的Body
    public <T extends OAuthClientRequest> T buildBodyMessage(Class<T> clazz) throws OAuthProblemException {
      T request = (T) OAuthUtils.instantiateClassWithParameters(clazz, new Class[]{String.class},
                                                                new String[]{url});
      this.builder = new BodyURLEncodedParametersApplier();
      return (T) builder.applyOAuthParameters(request, parameters);
    }

    public OAuthClientRequestBuilder setAccessToken(String accessToken) {
      this.parameters.put(OAuth.OAUTH_ACCESS_TOKEN, accessToken);
      return this;
    }

  }

}
