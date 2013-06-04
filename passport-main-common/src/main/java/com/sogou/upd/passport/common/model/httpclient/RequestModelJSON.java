package com.sogou.upd.passport.common.model.httpclient;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-2
 * Time: 下午10:24
 */
public class RequestModelJSON extends RequestModel {

    private static final Logger logger = LoggerFactory.getLogger(RequestModelJSON.class);

    public RequestModelJSON(String url) {
        super(url);
    }

    /**
     * 返回json格式的参数
     *
     * @return
     */
    @Override
    public HttpEntity getRequestEntity() {
        try {
            String json = new ObjectMapper().writeValueAsString(params);
            return new StringEntity(json, "text/xml",
                    DEFAULT_ENCODE);
        } catch (UnsupportedEncodingException e) {
            logger.error("http param url encode error ", e);
            throw new RuntimeException("http param url encode error", e);
        } catch (JsonMappingException e) {
            logger.error("http param JsonMappingException error ", e);
            throw new RuntimeException("http param JsonMappingException error", e);
        } catch (JsonGenerationException e) {
            logger.error("http param JsonGenerationException error ", e);
            throw new RuntimeException("http param JsonGenerationException error", e);
        } catch (IOException e) {
            logger.error("http param IOException error ", e);
            throw new RuntimeException("http param IOException error", e);
        }
    }
}
