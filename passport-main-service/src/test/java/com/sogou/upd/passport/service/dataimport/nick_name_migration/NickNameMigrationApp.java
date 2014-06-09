package com.sogou.upd.passport.service.dataimport.nick_name_migration;

import com.google.common.collect.Lists;
import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.utils.DBShardRedisUtils;
import com.sogou.upd.passport.common.utils.JsonUtil;
import com.sogou.upd.passport.dao.account.AccountBaseInfoDAO;
import com.sogou.upd.passport.dao.account.AccountDAO;
import com.sogou.upd.passport.dao.account.UniqNamePassportMappingDAO;
import com.sogou.upd.passport.service.dataimport.util.FileUtil;
import org.junit.Test;
import org.perf4j.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
    private UniqNamePassportMappingDAO uniqNamePassportMappingDAO;

    @Autowired
    private DBShardRedisUtils dbShardRedisUtils;

    private static final String DATA_STORE_PATH = "D:\\项目\\非第三方账号迁移\\内部昵称数据迁移\\account_base_info\\";

    @Test
    public void runMigrationApp() {
        LOGGER.info("NickNameMigrationApp start with {} processors ", CORE_COUNT);
        StopWatch watch = new StopWatch();
        watch.start();
        try {
            NickNameMigrationTasks tasks = new NickNameMigrationTasks(accountBaseInfoDAO, accountDAO, uniqNamePassportMappingDAO, dbShardRedisUtils);
            List<String> resultList = POOL.invoke(tasks);
            FileUtil.storeFile(DATA_STORE_PATH + "migration_nickname_fail.txt", resultList);
        } catch (Exception e) {
            LOGGER.error("NickNameMigrationApp failed." + e.getMessage(), e);
        }
        LOGGER.info("NickNameMigrationApp finish use time {} s", watch.stop());
    }


    @Test
    public void testFileStore2Local() {
        Map<String, String> map = new HashMap();
        map.put("1", "1");
        map.put("2", "1");
        map.put("3", "1");
        map.put("4", "1");
        try {
            FileUtil.storeFileMap2Local("D:\\项目\\非第三方账号迁移\\内部昵称数据迁移\\account_base_info\\test_data.txt", map);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<String> list1 = Lists.newArrayList();
        List<String> list2 = Lists.newArrayList();
        List<String> list3 = Lists.newArrayList();
        List<String> list4 = Lists.newArrayList();
        for (int i = 0; i < 4; i++) {
            list1.add("aaaa");
        }
        for (int i = 0; i < 4; i++) {
            list2.add("bbbb");
        }
        for (int i = 0; i < 4; i++) {
            list3.add("cccc");
        }
        for (int i = 0; i < 4; i++) {
            list4.add("dddd");
        }


        List<String> allList = Lists.newArrayList();
        allList.addAll(list1);
        allList.addAll(list2);
        allList.addAll(list3);
        allList.addAll(list4);

        System.out.println("========" + JsonUtil.obj2Json(allList));

    }


}
