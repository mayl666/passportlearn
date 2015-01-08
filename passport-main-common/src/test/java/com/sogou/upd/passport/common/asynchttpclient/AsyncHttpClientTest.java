package com.sogou.upd.passport.common.asynchttpclient;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.Response;
import org.junit.Test;
import org.perf4j.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

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

   /* public void testAysnc() {
        try {

            final AtomicBoolean headerSent = new AtomicBoolean(false);
            final AtomicBoolean operationCompleted = new AtomicBoolean(false);
            String userId = null;
            String tKey = null;


            StopWatch watch = new StopWatch();
            watch.start();

            Response resp = httpClient.prepareGet("http://www.baidu.com").setBody("").execute(new AsyncCompletionHandler<Response>() {

                public STATE onHeaderWriteCompleted() {
                    headerSent.set(true);
                    return STATE.CONTINUE;
                }

                public STATE onContentWriteCompleted() {
                    operationCompleted.set(true);
                    return STATE.CONTINUE;
                }

                @Override
                public Response onCompleted(Response response) throws Exception {
                    return response;
                }
            }).get();
        } catch (Exception e) {

        }

    }
*/

}
