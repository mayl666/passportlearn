package com.sogou.upd.passport.service.dataimport;

import com.google.common.collect.Lists;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.httpclient.RequestModelXml;
import com.sogou.upd.passport.common.parameter.HttpTransformat;
import com.sogou.upd.passport.common.utils.SGHttpClient;
import com.sogou.upd.passport.dao.account.AccountDAO;
import com.sogou.upd.passport.dao.account.AccountInfoDAO;
import com.sogou.upd.passport.dao.account.UniqNamePassportMappingDAO;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountInfo;
import com.sogou.upd.passport.model.account.UniqnamePassportMapping;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.perf4j.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveTask;

/**
 * upm 分表 子任务task
 * Created with IntelliJ IDEA.
 * User: chengang
 * Date: 14-4-21
 * Time: 下午4:39
 */
public class UpmSeperateTask extends RecursiveTask<List<String>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpmSeperateTask.class);

    private static final long serialVersionUID = 430272436790251302L;

    private static final int LIMIT_THREHOLD = 100000;

    private UniqNamePassportMappingDAO uniqNamePassportMappingDAO;

    private int start;

    public UpmSeperateTask(int start, UniqNamePassportMappingDAO uniqNamePassportMappingDAO) {
        super();
        this.start = start;
        this.uniqNamePassportMappingDAO = uniqNamePassportMappingDAO;
    }


    /**
     * 任务需要能够重复执行
     * 实现上保证幂等性
     *
     * @return
     */
    @Override
    protected List<String> compute() {

        LOGGER.info("start u_p_m_0_32 shard .....");

        long startTime = System.currentTimeMillis();

        //记录失败记录
        List<String> failList = Lists.newArrayList();

        List<String> resultList = Lists.newArrayList();

        try {
            //获取指定范围的u_p_m 信息
            List<UniqnamePassportMapping> mappingList = uniqNamePassportMappingDAO.getUpmDataByPage(start, LIMIT_THREHOLD);

            if (CollectionUtils.isNotEmpty(mappingList)) {
                //子集合数据，根据 passport_id hash到具体分表中
                for (UniqnamePassportMapping mapping : mappingList) {
                    Timestamp updateTimeStamp = new Timestamp(mapping.getUpdateTime().getTime());
                    String passportId = mapping.getPassportId();
                    //加check,校验某 passport_id是否已经存在子表中
//                    UniqnamePassportMapping existMapping = uniqNamePassportMappingDAO.getUpmByPassportId(passportId);
//                    if (checkUpmExist(existMapping)) {
                    int result;
                    try {
                        result = uniqNamePassportMappingDAO.insertUpm0To32(mapping.getUniqname(), passportId, updateTimeStamp);
                    } catch (Exception e) {
                        e.printStackTrace();
                        failList.add(passportId);
                        LOGGER.info("insert into u_p_m_0_32 failed. passport_id:" + passportId);
                        continue;

                    }
                    if (result == 0) {
                        resultList.add(passportId);
                        continue;
                    }
//                    }
                }
                //记录插入shard 失败记录
                if (CollectionUtils.isNotEmpty(failList)) {
                    storeFile("u_p_m_" + start + "_failed.txt", failList);
                }
                if (CollectionUtils.isNotEmpty(resultList)) {
                    storeFile("result_0_failed.txt", resultList);
                }
            }
        } catch (Exception e) {
            LOGGER.error("u_p_m shard error." + e.getMessage(), e);
        }
        LOGGER.info("UpmSeperateTask use time :" + (System.currentTimeMillis() - startTime) + "ms");
        return failList;
    }


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
        }
    }



}
