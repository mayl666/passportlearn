package com.sogou.upd.passport.oauth2.openresource.response.user;

import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthClientResponse;

import java.util.Map;

/**
 * Created by xieyilun on 2016/4/12.
 * sina微博的校验access_token类API响应结果
 */
public class SinaTokenAPIResponse extends OAuthClientResponse {
    @Override
    public void init(String body, String contentType, int responseCode) throws OAuthProblemException {
        this.setBody(body);
    }

    @Override
    public void setBody(String body) throws OAuthProblemException {
        try {
            this.body = body;
            parameters = JacksonJsonMapperUtil.getMapper().readValue(this.body, Map.class);
        } catch (Exception e) {
            throw OAuthProblemException.error(ErrorUtil.UNSUPPORTED_RESPONSE_TYPE);
        }
    }
}
