package com.sogou.upd.passport.web.internal.debug;

import com.sogou.upd.passport.common.utils.DBShardRedisUtils;
import com.sogou.upd.passport.dao.account.AccountBaseInfoDAO;
import com.sogou.upd.passport.dao.account.AccountDAO;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.model.account.AccountBaseInfo;
import com.sogou.upd.passport.service.account.AccountInfoService;
import com.sogou.upd.passport.service.connect.ConnectAuthService;
import com.sogou.upd.passport.service.connect.ConnectTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private UserInfoApiManager proxyUserInfoApiManager;
    @Autowired
    private ConnectApiManager proxyConnectApiManager;
    @Autowired
    private AccountInfoService accountInfoService;
    @Autowired
    private ConnectAuthService connectAuthService;
    @Autowired
    private AccountDAO accountDAO;
    @Autowired
    private AccountBaseInfoDAO accountBaseInfoDAO;
    @Autowired
    private DBShardRedisUtils dbShardRedisUtils;

    private static ExecutorService service = Executors.newFixedThreadPool(50);

    /**
     * 验证sohu导出的数据与sohu线上是否一致 done
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
        //从05库的open_token_info_utf8导出的sohu的10个文件
        String[] fileNames = {"open_token_1.txt", "open_token_2.txt", "open_token_3.txt", "open_token_4.txt", "open_token_5.txt", "open_token_6.txt", "open_token_7.txt", "open_token_8.txt", "open_token_9.txt", "open_token_10.txt"};

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
     * 线下方式调用第三方API补全第三方用户信息 done
     *
     * @return
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    public Object addConnectUserInfo() throws Exception {
        long time = System.currentTimeMillis();
        String fileRoot = "/search/passport/log/liuling/";
        //从03线上库中的connect_token的32张表中导出的数据
        String[] fileNames = {"connect_token_1.txt", "connect_token_2.txt", "connect_token_3.txt", "connect_token_4.txt", "connect_token_5.txt", "connect_token_6.txt", "connect_token_7.txt", "connect_token_8.txt"};

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

    /**
     * 线下方式调用第三方API补全第三方用户信息 done
     *
     * @return
     */
    @RequestMapping(value = "/move/add")
    @ResponseBody
    public Object moveAddBaseInfoToAccountDB() throws Exception {
        long time = System.currentTimeMillis();
        String fileRoot = "D:\\transfer\\account_base_info\\";
        //从03线上库中的connect_token的32张表中导出的数据
        String[] fileNames = {"update_exception.txt"};

        int size = fileNames.length;

        CountDownLatch latch = new CountDownLatch(size);

        for (int i = 0; i < size; i++) {
            String fileName = fileRoot + fileNames[i];
            service.execute(new ReMoveBaseInfoToAccountThread(latch, accountDAO, accountBaseInfoDAO, fileName));
        }
        latch.await();
        System.out.println("总执行时间：" + (System.currentTimeMillis() - time));
        return buildSuccess("", null);


    }

    private List<List<AccountBaseInfo>> buildListRange(List<AccountBaseInfo> listConnectBaseInfo) throws IOException {
        int dbsize = listConnectBaseInfo.size();
        List<List<AccountBaseInfo>> listRange = new ArrayList<>();
        int count = dbsize / 1000;
        int start;
        int end = -1;
        for (int i = 0; i < count; i++) {
            start = end + 1;
            end = start + 999;
            List<AccountBaseInfo> listItem = new ArrayList<>();
            int index = start;
            while (index <= end) {
                listItem.add(listConnectBaseInfo.get(index));
                index++;
            }
            listRange.add(listItem);
        }
        return listRange;
    }


    /**
     * 从数据库表直接移动，将03线上库中account_base_info表中第三方账号的昵称、头像非空的记录移动到account 32张小表中
     * todo 代码上线前再跑这一块
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/move")
    @ResponseBody
    public Object moveBaseInfoToAccountDB() throws Exception {
        long time = System.currentTimeMillis();
        int totalCount = accountBaseInfoDAO.getConnectTotalCount(); //记录总数
        int pageSize = 20000; //每次处理记录的条数,暂定每次取1W
        int pageCount;  //循环次数，也即总处理次数
        if (totalCount % pageSize == 0) {
            pageCount = totalCount / pageSize;
        } else {
            pageCount = totalCount / pageSize + 1;
        }
        int currentPage = 0; //当前处理环数
        for (int i = 0; i < pageCount; i++) {
            long t = System.currentTimeMillis();
            int pageIndex = (pageSize + 1) * currentPage;
            List<AccountBaseInfo> listConnectBaseInfo = accountBaseInfoDAO.listConnectBaseInfoByPage(pageIndex, pageSize);
            List<List<AccountBaseInfo>> listRange = buildListRange(listConnectBaseInfo);
            CountDownLatch latch = new CountDownLatch(listRange.size());
            for (int j = 0; j < listRange.size(); j++) {
                List<AccountBaseInfo> listItem = listRange.get(j);
                service.execute(new MoveBaseInfoToAccountThread(latch, accountDAO, dbShardRedisUtils, listItem));
            }
            latch.await();
            System.out.println(i + "次循环总执行时间：" + (System.currentTimeMillis() - t));
            currentPage++;
        }
        System.out.println("循环总执行时间：" + (System.currentTimeMillis() - time));
        return buildSuccess("", null);


    }

    /**
     * 将03线上库中account表中第三方账号的个人资料从sohu移入03库中的account_info表中，数据源用验证token的10个open_token_1至open_token_10
     * done
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/info")
    @ResponseBody
    public Object getSecureInfoToAccountInfo() throws Exception {
        long time = System.currentTimeMillis();
        String fileRoot = "/search/passport/log/liuling/";
        //03线上库中connect_token 32张表中第三方账号
        String[] fileNames = {"open_token_1.txt", "open_token_2.txt", "open_token_3.txt", "open_token_4.txt", "open_token_5.txt", "open_token_6.txt", "open_token_7.txt", "open_token_8.txt", "open_token_9.txt", "open_token_10.txt"};

        int size = fileNames.length;

        CountDownLatch latch = new CountDownLatch(size);

        for (int i = 0; i < size; i++) {
            String fileName = fileRoot + fileNames[i];
            service.execute(new SecureInfoToAccountThread(latch, fileName, proxyUserInfoApiManager, accountInfoService, accountDAO));
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
