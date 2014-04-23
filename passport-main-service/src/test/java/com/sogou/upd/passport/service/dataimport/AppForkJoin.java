package com.sogou.upd.passport.service.dataimport;

import com.google.common.collect.Lists;
import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.dao.account.UniqNamePassportMappingDAO;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

/**
 * User: chengang
 * Date: 14-4-22
 * Time: 下午2:22
 */
@Ignore
public class AppForkJoin extends BaseTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppForkJoin.class);

    public static final int CORE_COUNT = Runtime.getRuntime().availableProcessors();

    public static ForkJoinPool POOL = new ForkJoinPool(CORE_COUNT);

    @Autowired
    private UniqNamePassportMappingDAO mappingDAO;


    @Ignore
    @Test
    public void runShard() {
        LOGGER.info("u_p_m_0_32 shard started with {} processors ", CORE_COUNT);

        long start = System.currentTimeMillis();
        try {
            SeperatesTask task = new SeperatesTask(mappingDAO);
            List<String> generatedItems = POOL.invoke(task);
            storeFile("failed.txt", generatedItems);
            LOGGER.info("Pools size " + POOL.getPoolSize());
        } catch (Exception e) {
            LOGGER.error("AppForkJoin failed." + e.getMessage(), e);
        }
        LOGGER.info("u_p_m_0_32 shard  total use time :" + (System.currentTimeMillis() - start) + "ms");

    }


    @Ignore
    @Test
    public void testRunShard() {
        int total = 1697007;
        int pageSize = 100000;
        int maxPage = total % pageSize == 0 ? (total / pageSize) : (total / pageSize + 1);
        LOGGER.info("maxPage:" + maxPage);
        int current = 0;
        for (int i = 0; i < maxPage; i++) {
            LOGGER.info("for each current:" + current);
            int pageIndex = (pageSize + 1) * current;
            LOGGER.info("for each pageIndex:" + pageIndex);
            current++;
        }


    }

/*    public static void main(String[] args) throws IOException {
        LOGGER.info("Application started with {} processors ", CORE_COUNT);
        try {
            SeperatesTask task = new SeperatesTask(mappingDAO);
            List<String> generatedItems = POOL.invoke(task);
            storeFile("shard.txt", generatedItems);
        } catch (Exception e) {
            LOGGER.error("AppForkJoin failed." + e.getMessage(), e);
        }
        LOGGER.info("Done");
    }*/


    @Ignore
    @Test
    public void testStoreFile() throws IOException, URISyntaxException {
        List<String> result = Lists.newArrayList();
        result.add("hy83126@sohu.com");
        result.add("502011527@renren.sohu.com");
        result.add("luohan1018@sohu.com");

        storeFile("shard.txt", result);
    }

    /**
     * 存储文件到本地
     *
     * @param fileName
     * @param result
     * @throws IOException
     * @throws URISyntaxException
     */
    private static void storeFile(String fileName, List<String> result) throws IOException, URISyntaxException {
        Path filePath = Paths.get("D:\\logs\\" + fileName);
        Files.deleteIfExists(filePath);
        BufferedWriter writer = Files.newBufferedWriter(filePath, Charset.defaultCharset());
        if (CollectionUtils.isNotEmpty(result)) {
            for (String item : result) {
                writer.write(item);
                writer.newLine();
            }
            writer.flush();
        } else {
            LOGGER.info("u_p_m_0_32 shard success!");
        }

    }

}
