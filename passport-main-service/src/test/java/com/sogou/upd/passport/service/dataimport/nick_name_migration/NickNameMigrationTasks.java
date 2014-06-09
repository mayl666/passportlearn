package com.sogou.upd.passport.service.dataimport.nick_name_migration;

import com.google.common.collect.Lists;
import com.sogou.upd.passport.common.utils.DBShardRedisUtils;
import com.sogou.upd.passport.dao.account.AccountBaseInfoDAO;
import com.sogou.upd.passport.dao.account.AccountDAO;
import com.sogou.upd.passport.dao.account.UniqNamePassportMappingDAO;
import org.perf4j.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RecursiveTask;

/**
 * Created with IntelliJ IDEA.
 * User: chengang
 * Date: 14-5-7
 * Time: 上午11:27
 */
public class NickNameMigrationTasks extends RecursiveTask<List<String>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NickNameMigrationTasks.class);

    private static final long serialVersionUID = -6427450835193991812L;

    private static final int DATA_RANGE_LIMIT = 150000;
//    private static final int DATA_RANGE_LIMIT = 100;

    private static volatile int CURRENT = 0;

    //返回结果
    private final List<RecursiveTask<List<String>>> forks = Lists.newLinkedList();

    private AccountBaseInfoDAO baseInfoDAO;

    private AccountDAO accountDAO;

    private UniqNamePassportMappingDAO uniqNamePassportMappingDAO;

    private DBShardRedisUtils dbShardRedisUtils;


    public NickNameMigrationTasks(AccountBaseInfoDAO baseDao, AccountDAO accountDao, UniqNamePassportMappingDAO uniqNamePassportMappingDAO, DBShardRedisUtils shardRedisUtils) {
        this.baseInfoDAO = baseDao;
        this.accountDAO = accountDao;
        this.uniqNamePassportMappingDAO = uniqNamePassportMappingDAO;
        this.dbShardRedisUtils = shardRedisUtils;
    }


    @Override
    protected List<String> compute() {
        List<String> failList = Lists.newLinkedList();
        StopWatch watch = new StopWatch();
        watch.start();
        try {
            LOGGER.info("NickNameMigrationTasks start ");
            //查询非第三方账号+搜狐域账号总数
//            int totalCount = 400;
//            int totalCount = baseInfoDAO.getNotThirdPartyTotalCount();
//            int maxPage = totalCount % DATA_RANGE_LIMIT == 0 ? (totalCount / DATA_RANGE_LIMIT) : (totalCount / DATA_RANGE_LIMIT + 1);
            for (int i = 0; i < 4; i++) {
                int pageIndex = (DATA_RANGE_LIMIT + 1) * CURRENT;
                MigrationTask migrationTask = new MigrationTask(baseInfoDAO, accountDAO, uniqNamePassportMappingDAO, dbShardRedisUtils, pageIndex);
                migrationTask.fork();
                forks.add(migrationTask);
                CURRENT++;
            }

            //结果整合
            for (RecursiveTask<List<String>> task : forks) {
                try {
                    failList.addAll(task.get());
                } catch (InterruptedException | ExecutionException e) {
                    LOGGER.error("get migration task result fail.", e);
                }
            }
        } catch (Exception e) {
            LOGGER.error("NickNameMigrationTasks error.", e);
        }
        LOGGER.info("NickNameMigrationTasks finish use time {} s", watch.stop());
        return failList;
    }
}
