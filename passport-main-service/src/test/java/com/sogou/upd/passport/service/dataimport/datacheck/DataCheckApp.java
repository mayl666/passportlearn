package com.sogou.upd.passport.service.dataimport.datacheck;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.dao.account.AccountDAO;
import com.sogou.upd.passport.dao.account.AccountInfoDAO;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.dataimport.util.FileUtil;
import org.junit.Assert;
import org.junit.Test;
import org.perf4j.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

/**
 * Created with IntelliJ IDEA.
 * User: chengang
 * Date: 14-5-20
 * Time: 下午7:07
 */
public class DataCheckApp extends BaseTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataCheckApp.class);

    public static final int CORE_COUNT = Runtime.getRuntime().availableProcessors();

    public static ForkJoinPool POOL = new ForkJoinPool(CORE_COUNT);

    @Autowired
    private AccountDAO accountDAO;

    @Autowired
    private AccountInfoDAO accountInfoDAO;

    @Test
    public void checkFullData() {
        LOGGER.info("DataCheckApp start check full data ");

        LOGGER.info("DataCheckApp start with {} processors ", CORE_COUNT);
        StopWatch watch = new StopWatch();
        watch.start();
        try {
            FullDataCheckApps tasks = new FullDataCheckApps(accountDAO, accountInfoDAO);
            List<Map<String, String>> resultList = POOL.invoke(tasks);
            FileUtil.storeFileToLocal("D:\\项目\\非第三方账号迁移\\check_full_data\\check_full_data_difference_2.txt", resultList);
        } catch (Exception e) {
            LOGGER.error("DataCheckApp failed." + e.getMessage(), e);
        }
        LOGGER.info("DataCheckApp finish use time {} s", watch.stop());
    }


    @Test
    public void checkFullData1() {
        LOGGER.info("DataCheckApp start check full data ");

        LOGGER.info("DataCheckApp checkFullData1 start with {} processors ", CORE_COUNT);
        StopWatch watch = new StopWatch();
        watch.start();
        try {
            FullDataCheckApps1 tasks = new FullDataCheckApps1(accountDAO, accountInfoDAO);
            List<Map<String, String>> resultList = POOL.invoke(tasks);
            FileUtil.storeFileToLocal("D:\\项目\\非第三方账号迁移\\check_full_data\\check_full_data_difference_3.txt", resultList);
        } catch (Exception e) {
            LOGGER.error("DataCheckApp checkFullData1 failed." + e.getMessage(), e);
        }
        LOGGER.info("DataCheckApp checkFullData1 finish use time {} s", watch.stop());
    }


    @Test
    public void testConnectTestDB() {
        String passportId = "13067315087@sohu.com";
        Account account = accountDAO.getAccountByPassportId(passportId);
        Assert.assertNotNull(account);
        Assert.assertNotNull("check account from test db is or not exist", account);
    }


}
