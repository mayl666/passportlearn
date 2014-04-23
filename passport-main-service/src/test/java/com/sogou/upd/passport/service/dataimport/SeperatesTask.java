package com.sogou.upd.passport.service.dataimport;

import com.google.common.collect.Lists;
import com.sogou.upd.passport.dao.account.UniqNamePassportMappingDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RecursiveTask;

/**
 * 昵称映射表数据分表Task
 * Created with IntelliJ IDEA.
 * User: chengang
 * Date: 14-4-21
 * Time: 下午4:40
 */
public class SeperatesTask extends RecursiveTask<List<String>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeperatesTask.class);

    private static final long serialVersionUID = 8204749239959952837L;

    private volatile int CURRENT = 0;

    private UniqNamePassportMappingDAO uniqNamePassportMappingDAO;

    private final List<RecursiveTask<List<String>>> forks = new LinkedList<>();

    public SeperatesTask(UniqNamePassportMappingDAO uniqNamePassportMappingDAO) {
        super();
        this.uniqNamePassportMappingDAO = uniqNamePassportMappingDAO;
    }

    @Override
    protected List<String> compute() {
        List<String> items = Lists.newArrayList();
        long start = System.currentTimeMillis();
        try {
//            int totalCount = uniqNamePassportMappingDAO.getUpmTotalCount();
//            int pageSize = 100000;
//            int maxPage = totalCount % pageSize == 0 ? (totalCount / pageSize) : (totalCount / pageSize + 1);
//            int currentPage = 0;

            for (int i = 0; i < 17; i++) {
                LOGGER.info("SeperatesTask exec current:" + CURRENT);
                int pageIndex = 100001 * CURRENT;
                LOGGER.info("SeperatesTask exec pageIndex: " + pageIndex);
                UpmSeperateTask task = new UpmSeperateTask(pageIndex, uniqNamePassportMappingDAO);
                forks.add(task);
                task.fork();
                CURRENT++;
            }

            for (RecursiveTask<List<String>> task : forks) {
                try {
                    items.addAll(task.get());
                } catch (InterruptedException | ExecutionException e) {
                    LOGGER.error("Error occured", e);
                }
            }
        } catch (Exception e) {
            LOGGER.error("SeperateTask shard error." + e.getMessage(), e);
        }
        LOGGER.info("SeperateTask total use time :" + (System.currentTimeMillis() - start) + "ms");
        return items;
    }
}
