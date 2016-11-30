package com.sogou.upd.passport.common.asynchttpclient;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
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

    @Test
    public void testAysnc() {
        try {

            final AtomicBoolean headerSent = new AtomicBoolean(false);
            final AtomicBoolean operationCompleted = new AtomicBoolean(false);

            StopWatch watch = new StopWatch();
            watch.start();

            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
            Response resp = asyncHttpClient.prepareGet("http://www.baidu.com").setBody("").execute(new AsyncCompletionHandler<Response>() {
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
            System.out.println(resp.getResponseBody());
        } catch (Exception e) {
            LOGGER.error("error", e);
        }
    }


    @Test
    public void testAysncPost() {
        try {
            String userId = "6F8ECBDA703857FD29BD4E4132E2A1E9@qq.sohu.com";
//            String userId ="C2A6E7174FC56A79F8BE08697E5F1EC1@qq.sohu.com";

            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
            Response response = asyncHttpClient.preparePost("http://10.136.24.127:8090/internal/connect/get_friends_info")
                    .addQueryParameter("userid", userId).addQueryParameter("client_id", "1024").addQueryParameter("code", "1024")
                    .execute(new AsyncCompletionHandler<Response>() {
                        @Override
                        public Response onCompleted(Response response) throws Exception {
                            return response;
                        }
                    }).get();

            System.out.println(response.getResponseBody());
        } catch (Exception e) {

        }

    }

}
