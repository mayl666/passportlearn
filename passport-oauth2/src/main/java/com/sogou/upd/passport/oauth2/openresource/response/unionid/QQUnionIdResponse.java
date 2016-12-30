package com.sogou.upd.passport.oauth2.openresource.response.unionid;

import com.google.common.base.Strings;

import com.sogou.upd.passport.common.HttpConstant;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.parameters.QQOAuth;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthClientResponse;
import com.sogou.upd.passport.oauth2.openresource.validator.impl.TokenValidator;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * QQ unionId 查询结果
 */
public class QQUnionIdResponse extends OAuthClientResponse {

    protected static final Logger logger = LoggerFactory.getLogger(QQUnionIdResponse.class);

    private String unionId;
    
    @Override
    public void setBody(String body) throws OAuthProblemException {
        if(StringUtils.isBlank(body)) {
            this.body = "{}";
        } else {
            // 处理为标准 json
            body = StringUtils.substringBetween(body, "callback(", ");");
    
            this.body = body;
            try {
                this.parameters = JacksonJsonMapperUtil.getMapper().readValue(this.body, Map.class);
            } catch (Exception e) {
                logger.error("Invalid qq unionid response! Response body is not " + HttpConstant.ContentType.JSON + " encoded");
                this.body = "{}";
            }
        }
    }
    
    
    public String getUnionId() {
        String value = getParam(QQOAuth.UNIONID);
        return Strings.isNullOrEmpty(value) ? "" : value;
    }

    @Override
    public void init(String body, String contentType, int responseCode) throws OAuthProblemException {
        validator = new TokenValidator();
        super.init(body, contentType, responseCode);
    }
}
