package com.sogou.upd.passport.service.dataimport.datacheck;

import com.google.common.collect.Lists;
import com.sogou.upd.passport.dao.account.AccountDAO;
import com.sogou.upd.passport.dao.account.AccountInfoDAO;
import com.sogou.upd.passport.dao.account.MobilePassportMappingDAO;
import org.perf4j.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RecursiveTask;

/**
 * Created with IntelliJ IDEA.
 * User: chengang
 * Date: 14-5-20
 * Time: 下午7:48
 */
public class FullDataCheckApps extends RecursiveTask<List<Map<String, String>>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FullDataCheckApps.class);

    private static final long serialVersionUID = 5274042337565024144L;

    private AccountDAO accountDAO;

    private AccountInfoDAO accountInfoDAO;

    private MobilePassportMappingDAO mobilePassportMappingDAO;

    private static final String BASE_FILE_PATH = "D:\\repairDataList\\inc_user_info_his_0609";


    //返回结果
//    private final List<RecursiveTask<List<String>>> forks = Lists.newLinkedList();

    private final List<RecursiveTask<Map<String, String>>> forks = Lists.newLinkedList();


    public FullDataCheckApps(AccountDAO accountDAO, AccountInfoDAO accountInfoDAO, MobilePassportMappingDAO mobilePassportMappingDAO) {
        this.accountDAO = accountDAO;
        this.accountInfoDAO = accountInfoDAO;
        this.mobilePassportMappingDAO = mobilePassportMappingDAO;
    }


    @Override
    protected List<Map<String, String>> compute() {
        LOGGER.info("FullDataCheckApps check full data 05 test sg start......");
//        List<Integer> differenceLists = Lists.newLinkedList();

        List<Map<String, String>> differences = new ArrayList<>();

        StopWatch watch = new StopWatch();
        watch.start();
        try {
            for (int i = 1; i < 4; i++) {
                String filePath = BASE_FILE_PATH + "_split_" + i;
                FullDataCheckApp task = new FullDataCheckApp(accountDAO, accountInfoDAO, mobilePassportMappingDAO, filePath);
                task.fork();
                forks.add(task);
            }
//            FullDataCheckApp task1 = new FullDataCheckApp(accountDAO, accountInfoDAO, mobilePassportMappingDAO, BASE_FILE_PATH);
//            task1.fork();
//            forks.add(task1);

            //结果整合
            for (RecursiveTask<Map<String, String>> task : forks) {
                try {
                    LOGGER.info(String.format("FullDataCheckApps check full data 05 test sg task:[%s] failList", task.getClass().getName()));
                    differences.add(task.get());

                    /*if (task.get().size() > 0) {
                        differenceLists.add(task.get().size());
                    }*/
                } catch (InterruptedException | ExecutionException e) {
                    LOGGER.error("FullDataCheckApps check full data 05 test sg fail.", e);
                }
            }
        } catch (Exception e) {
            LOGGER.error("FullDataCheckApps check full data 05 test sg  error.", e);
        }
        LOGGER.info("FullDataCheckApps finish use time {}", watch.stop());
        return differences;
    }
}
