package com.sogou.upd.passport.service.dataimport.nick_migration;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.utils.DBShardRedisUtils;
import com.sogou.upd.passport.dao.account.AccountBaseInfoDAO;
import com.sogou.upd.passport.dao.account.AccountDAO;
import com.sogou.upd.passport.service.dataimport.util.FileUtil;
import org.junit.Test;
import org.perf4j.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

/**
 * sohu 内部昵称迁移App
 * Created with IntelliJ IDEA.
 * User: chengang
 * Date: 14-5-6
 * Time: 下午2:57
 */
public class NickNameMigrationApp extends BaseTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(NickNameMigrationApp.class);

    public static final int CORE_COUNT = Runtime.getRuntime().availableProcessors();

    public static ForkJoinPool POOL = new ForkJoinPool(CORE_COUNT);

    @Autowired
    private AccountBaseInfoDAO accountBaseInfoDAO;

    @Autowired
    private AccountDAO accountDAO;

    @Autowired
    private DBShardRedisUtils dbShardRedisUtils;

    @Test
    public void runMigrationApp() {
        LOGGER.info("NickNameMigrationApp start with {} processors ", CORE_COUNT);
        StopWatch watch = new StopWatch();
        watch.start();
        try {
            NickNameMigrationTasks tasks = new NickNameMigrationTasks(accountBaseInfoDAO, accountDAO, dbShardRedisUtils);
            List<String> resultList = POOL.invoke(tasks);
            FileUtil.storeFile("migration_nickname_fail.txt", resultList);
        } catch (Exception e) {
            LOGGER.error("NickNameMigrationApp failed." + e.getMessage(), e);
        }
        LOGGER.info("NickNameMigrationApp finish use time {} s", watch.stop());
    }


}
