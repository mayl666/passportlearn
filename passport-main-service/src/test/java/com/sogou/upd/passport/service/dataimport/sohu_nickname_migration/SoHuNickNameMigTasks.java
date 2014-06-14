package com.sogou.upd.passport.service.dataimport.sohu_nickname_migration;

import com.google.common.collect.Lists;
import com.sogou.upd.passport.common.utils.DBShardRedisUtils;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.dao.account.AccountDAO;
import com.sogou.upd.passport.dao.account.UniqNamePassportMappingDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RecursiveTask;

/**
 * 搜狐昵称迁移Tasks
 * User: chengang
 * Date: 14-6-13
 * Time: 下午7:01
 */
public class SoHuNickNameMigTasks extends RecursiveTask<List<String>> {


    private static final long serialVersionUID = 6804496961460969071L;

    private static final Logger LOGGER = LoggerFactory.getLogger(SoHuNickNameMigTasks.class);

    private UniqNamePassportMappingDAO uniqNamePassportMappingDAO;

    private AccountDAO accountDAO;

    private DBShardRedisUtils dbShardRedisUtils;

    private RedisUtils redisUtils;

    private String filePath;

    private final List<RecursiveTask<List<String>>> forks = Lists.newLinkedList();


    public SoHuNickNameMigTasks(UniqNamePassportMappingDAO uniqNamePassportMappingDAO, AccountDAO accountDAO,
                                DBShardRedisUtils dbShardRedisUtils, RedisUtils redisUtils, String filePath) {
        this.uniqNamePassportMappingDAO = uniqNamePassportMappingDAO;
        this.accountDAO = accountDAO;
        this.dbShardRedisUtils = dbShardRedisUtils;
        this.redisUtils = redisUtils;
        this.filePath = filePath;
    }

    @Override
    protected List<String> compute() {
        LOGGER.info("SoHuNickNameMigTasks start....");

        //总结果
        List<String> fullList = Lists.newLinkedList();

        try {
            for (int i = 0; i < 1; i++) {
//                String dataFile = filePath + "sohu_nickname_userid_" + i + ".sql";
                String dataFile = filePath + "sohu_nickname_userid_test.sql";      //sohu_nickname_userid_test.sql
                SoHuNNMigrationTask task = new SoHuNNMigrationTask(uniqNamePassportMappingDAO, accountDAO, dbShardRedisUtils, redisUtils, dataFile);
                task.fork();
                forks.add(task);
            }

            //结果整合
            for (RecursiveTask<List<String>> task : forks) {
                try {
                    fullList.addAll(task.get());
                } catch (InterruptedException | ExecutionException e) {
                    LOGGER.error("SoHuNickNameMigTasks addAll error.", e);
                }
            }
        } catch (Exception e) {
            LOGGER.error("SoHuNickNameMigTasks MIGRATION SH NICKNAME error.", e);
        }
        return fullList;
    }
}
