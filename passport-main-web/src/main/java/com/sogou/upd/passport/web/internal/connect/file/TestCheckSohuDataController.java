package com.sogou.upd.passport.web.internal.connect.file;

import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.manager.api.connect.form.user.UserOpenApiParams;
import com.sogou.upd.passport.service.connect.ConnectAuthService;
import com.sogou.upd.passport.service.connect.ConnectTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 根据sohu导出的文件从sogou库中读取记录，依次与sohu线上库对比，记录不同错误类型。
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-2-23
 * Time: 下午7:38
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class TestCheckSohuDataController {

    @Autowired
    private ConnectTokenService connectTokenService;
    @Autowired
    private ConnectApiManager proxyConnectApiManager;
    @Autowired
    private ConnectAuthService connectAuthService;

    private static ExecutorService service = Executors.newFixedThreadPool(50);

    /**
     * 验证sohu导出的数据与sohu线上是否一致
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/check")
    @ResponseBody
    public Object move()
            throws Exception {
        long time = System.currentTimeMillis();
        String fileRoot = "/search/passport/log/liuling/";
//        String[] fileNames = {"open_token_1.txt", "open_token_2.txt", "open_token_3.txt", "open_token_4.txt", "open_token_5.txt", "open_token_6.txt", "open_token_7.txt", "open_token_8.txt", "open_token_9.txt", "open_token_10.txt"};
        String[] fileNames = {"open_token_1.txt"};

        int size = fileNames.length;

        CountDownLatch latch = new CountDownLatch(size);

        for (int i = 0; i < size; i++) {
            String fileName = fileRoot + fileNames[i];
            service.execute(new MoveCacheThread(latch, fileName, connectTokenService, proxyConnectApiManager));
        }
        latch.await();
        System.out.println("总执行时间：" + (System.currentTimeMillis() - time));
        return buildSuccess("", null);
    }

    /**
     * 线下方式调用第三方API补全第三方用户信息
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    public Object addConnectUserInfo(UserOpenApiParams params) throws Exception {
        long time = System.currentTimeMillis();
        String fileRoot = "D:\\";
        //从03线上库中的connect_token32张表中导出的信息
        String[] fileNames = {"connect_token_111.txt", "connect_token_222.txt", "connect_token_333.txt", "connect_token_444.txt"};

        int size = fileNames.length;

        CountDownLatch latch = new CountDownLatch(size);

        for (int i = 0; i < size; i++) {
            String fileName = fileRoot + fileNames[i];
            service.execute(new AddConnectUserInfoThread(latch, fileName, connectTokenService, connectAuthService));
        }
        latch.await();
        System.out.println("总执行时间：" + (System.currentTimeMillis() - time));
        return buildSuccess("", null);


    }

    private Map<String, Object> buildSuccess(String msg, Map<String, Object> data) {
        Map<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("0", "完成");
        return retMap;
    }

}
