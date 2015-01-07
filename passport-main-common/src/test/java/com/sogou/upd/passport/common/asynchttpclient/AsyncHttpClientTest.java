package com.sogou.upd.passport.common.asynchttpclient;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: chengang
 * Date: 15-1-8
 * Time: 上午1:04
 */
public class AsyncHttpClientTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncHttpClientTest.class);

    @Test
    public void testAsyncHttpClient() {
        try {
            AsyncHttpClientService asyncHttpClientService = new AsyncHttpClientService();
            String response = asyncHttpClientService.sendGet("http://www.baidu.com", null, null);
            System.out.println("response:[" + response + "]");
        } catch (Exception e) {
            LOGGER.error("AsyncHttpClientService sendGet error", e);
        }

    }

}
