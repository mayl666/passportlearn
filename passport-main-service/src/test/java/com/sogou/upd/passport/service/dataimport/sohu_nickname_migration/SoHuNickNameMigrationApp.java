package com.sogou.upd.passport.service.dataimport.sohu_nickname_migration;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.utils.DBShardRedisUtils;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.dao.account.AccountDAO;
import com.sogou.upd.passport.dao.account.UniqNamePassportMappingDAO;
import com.sogou.upd.passport.service.dataimport.util.FileUtil;
import org.junit.Test;
import org.perf4j.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

/**
 * 搜狐昵称迁移App
 * User: chengang
 * Date: 14-6-13
 * Time: 下午6:59
 */
public class SoHuNickNameMigrationApp extends BaseTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SoHuNickNameMigrationApp.class);

    public static final int CORE_COUNT = Runtime.getRuntime().availableProcessors();

    public static ForkJoinPool POOL = new ForkJoinPool(CORE_COUNT);

    @Autowired
    private UniqNamePassportMappingDAO uniqNamePassportMappingDAO;

    @Autowired
    private AccountDAO accountDAO;

    @Autowired
    private DBShardRedisUtils dbShardRedisUtils;

    @Autowired
    private RedisUtils redisUtils;

    //日志文件存储目录
    private static final String DATA_STORE_PATH = "D:\\项目\\非第三方账号迁移\\搜狐昵称迁移\\";


    @Test
    public void runMigSHNickName() {
        LOGGER.info("SoHuNickNameMigrationApp start with {} processors ", CORE_COUNT);
        StopWatch watch = new StopWatch();
        watch.start();
        try {
            SoHuNickNameMigTasks tasks = new SoHuNickNameMigTasks(uniqNamePassportMappingDAO, accountDAO, dbShardRedisUtils, redisUtils, DATA_STORE_PATH);
            List<String> resultList = POOL.invoke(tasks);
            FileUtil.storeFile(DATA_STORE_PATH + "migration_sh_nickname_fail.txt", resultList);
        } catch (Exception e) {
            LOGGER.error("SoHuNickNameMigrationApp failed." + e.getMessage(), e);
        }
        LOGGER.info("SoHuNickNameMigrationApp finish use time {} s", watch.stop());

    }


}
