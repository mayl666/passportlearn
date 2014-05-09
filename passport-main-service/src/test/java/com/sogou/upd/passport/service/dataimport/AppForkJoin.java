package com.sogou.upd.passport.service.dataimport;

import com.google.common.collect.Lists;
import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.dao.account.UniqNamePassportMappingDAO;
import com.sogou.upd.passport.model.account.UniqnamePassportMapping;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
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


    @Test
    public void testGetTotal() {
        int total = mappingDAO.getUpmTotalCount();
        LOGGER.info("total count get db is :" + total);
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


    @Ignore
    @Test
    public void testInsertUpm32() {
        String uniqname = "限量版11111";
        String passport_id = "91A38EBFBD142B991EBB4EB7713BEADD@qq.sohu.com";
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        try {
            int result = mappingDAO.insertUpm0To32(uniqname, passport_id, timestamp);
            LOGGER.info("result : " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Ignore
    @Test
    public void testRunShard() {
//        int total = 1697007;
        int total = 1725908;
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
    private static void storeFile(String fileName, List<String> result) throws IOException {
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


    @Ignore
    @Test
    public void increaseData() throws IOException {

        /*Path increasePath = Paths.get("D:\\logs\\increase\\increase_1.txt");

        //记录导入增量数据失败记录
        List<String> failedIncrease = Lists.newArrayList();
        try (BufferedReader reader = Files.newBufferedReader(increasePath, Charset.defaultCharset())) {
            String line;
            while ((line = reader.readLine()) != null) {
                //增量数据
                UniqnamePassportMapping mapping = mappingDAO.getUpmByUniqName(line);
                if (mapping != null) {
                    Timestamp updateTime = new Timestamp(mapping.getUpdateTime().getTime());
                    int result;
                    try {
                        //插入前，先判断是否已经存在，若存在，先删除db、在清缓存，在插入新的映射
                        result = mappingDAO.insertUpm0To32(mapping.getUniqname(), mapping.getPassportId(), updateTime);
                    } catch (Exception e) {
                        LOGGER.error("increase data error.", e);
                        failedIncrease.add(mapping.getUniqname());
                        continue;
                    }
                    if (result == 0) {
                        LOGGER.info("increase data result equals 0. name:" + mapping.getUniqname());
                        failedIncrease.add(mapping.getUniqname());
                        continue;
                    }
                }
            }

            //记录导入增量数据失败的记录
            storeFile("increase_failed.txt", failedIncrease);
        }
*/
        /*try (BufferedReader reader = Files.newBufferedReader(filePath, Charset.defaultCharset())) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(" try with resources Files.newBufferedReader :" + line);
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(" try with resources FileReader :" + line);
            }
        }


        try {
            InputStream in = Files.newInputStream(filePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(" Files.newInputStream:" + line);
            }
        } catch (Exception e) {
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(filePath)))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(" try with resources  Files.newInputStream:" + line);
            }
        }

        try (BufferedInputStream in = new BufferedInputStream(Files.newInputStream(filePath))) {
            int inByte;
            while ((inByte = in.read()) != -1) {
                System.out.printf("%02X ", inByte);
            }
            System.out.printf("%n%n");
        }
    }*/
    }
}
