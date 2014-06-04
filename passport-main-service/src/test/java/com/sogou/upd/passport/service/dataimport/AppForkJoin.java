package com.sogou.upd.passport.service.dataimport;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.httpclient.RequestModelXml;
import com.sogou.upd.passport.common.parameter.HttpTransformat;
import com.sogou.upd.passport.common.utils.JsonUtil;
import com.sogou.upd.passport.common.utils.SGHttpClient;
import com.sogou.upd.passport.dao.account.AccountDAO;
import com.sogou.upd.passport.dao.account.AccountInfoDAO;
import com.sogou.upd.passport.dao.account.MobilePassportMappingDAO;
import com.sogou.upd.passport.dao.account.UniqNamePassportMappingDAO;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountInfo;
import com.sogou.upd.passport.model.account.UniqnamePassportMapping;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.perf4j.StopWatch;
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
//@Ignore
public class AppForkJoin extends BaseTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppForkJoin.class);

    public static final int CORE_COUNT = Runtime.getRuntime().availableProcessors();

    public static ForkJoinPool POOL = new ForkJoinPool(CORE_COUNT);

    @Autowired
    private UniqNamePassportMappingDAO mappingDAO;

    @Autowired
    private AccountDAO accountDAO;

    @Autowired
    private AccountInfoDAO accountInfoDAO;


    @Autowired
    private MobilePassportMappingDAO mobilePassportMappingDAO;

    private static final String appId = "1100";

    private static final String key = "yRWHIkB$2.9Esk>7mBNIFEcr:8\\[Cv";

    private static final String REQUEST_URL = "http://internal.passport.sohu.com/interface/getuserinfo";

    private static final String REQUEST_INFO = "info";


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

        Path increasePath = Paths.get("D:\\logs\\increase\\increase_1.txt");

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

    //    @Ignore
    @Test
    public void testCheckData() {

        Map<String, String> differenceMap = Maps.newConcurrentMap();

//        String passportId = "joblim@sogou.com";  //返回有昵称
        String passportId = "wangqingemail@sohu.com";  //返回有昵称


        /*StopWatch watch = new StopWatch();
        watch.start();*/

        RequestModelXml requestModelXml = bulidRequestModelXml(passportId);
        try {
            Map<String, Object> mapB = null;
            try {
                mapB = SGHttpClient.executeBean(requestModelXml, HttpTransformat.xml, Map.class);

                if (mapB.containsKey("birthday")) {
                    String birthday = String.valueOf(mapB.get("birthday"));
                    if (StringUtils.isNotEmpty(birthday)) {
                        mapB.put("birthday", birthday);
                    } else {
                        mapB.put("birthday", "1900-01-01");
                    }
                }

                System.out.println("===================mapB:" + mapB.toString());

            } catch (Exception e) {
//                failedList.add(passportId);
                LOGGER.error("FullDataCheckApp get account from sohu error.", e);
//                continue;
            }

/*
            if (StringUtils.isNotEmpty(passportId)) {

                Account account = accountDAO.getAccountByPassportId(passportId);

                //不验证 birthday 采用 getAccountInfoByPid4DataCheck 方法  email,gender, province, city,fullname,personalid
                AccountInfo accountInfo = accountInfoDAO.getAccountInfoByPid4DataCheck(passportId);
                if (account != null && accountInfo != null) {
                    //Test 库数据
                    Map<String, Object> mapA = Maps.newHashMap();

                    mapA.put("createip", account.getRegIp() == null || account.getRegIp() == "" ? StringUtils.EMPTY : account.getRegIp());
                    mapA.put("userid", passportId);
                    mapA.put("personalid", accountInfo.getPersonalid() == null ? StringUtils.EMPTY : accountInfo.getPersonalid());
                    mapA.put("city", accountInfo.getCity() == null ? StringUtils.EMPTY : accountInfo.getCity());

                    String createTime = String.valueOf(account.getRegTime());
                    if (StringUtils.isNotEmpty(createTime)) {
                        if (createTime.length() >= 19) {
                            mapA.put("createtime", StringUtils.substring(createTime, 0, 19));
                        }
                    } else {
                        mapA.put("createtime", StringUtils.EMPTY);
                    }

                    mapA.put("username", accountInfo.getFullname() == null ? StringUtils.EMPTY : accountInfo.getFullname());
                    mapA.put("email", accountInfo.getEmail() == null ? StringUtils.EMPTY : accountInfo.getEmail());
                    mapA.put("province", accountInfo.getProvince() == null ? StringUtils.EMPTY : accountInfo.getProvince());
                    mapA.put("gender", accountInfo.getGender() == null ? StringUtils.EMPTY : accountInfo.getGender());
                    mapA.put("mobile", account.getMobile() == null ? StringUtils.EMPTY : account.getMobile());


                    if (accountInfo.getBirthday() != null) {
                        if (accountInfo.getBirthday().toString().length() > 10) {
                            mapA.put("birthday", accountInfo.getBirthday().toString().substring(0, 10));
                        }
                    }

                    //比较文件
                    if (mapA != null && mapB != null) {
                        mapA.put("flag", mapB.get("flag"));
                        mapA.put("status", mapB.get("status"));

                        //记录调用搜狐接口获取用户信息对应的Flag
//                        userFlagMap.put(passportId, mapB.get("flag").toString());

                        MapDifference difference = Maps.difference(mapA, mapB);
                        if (!difference.areEqual()) {
                            if (!difference.entriesDiffering().isEmpty()) {
                                differenceMap.put(passportId, difference.entriesDiffering().toString());
//                                    LOGGER.info("mapA and mapB entriesDiffering {}", difference.entriesDiffering().toString());
                            } else if (!difference.entriesOnlyOnLeft().isEmpty()) {
                                differenceMap.put(passportId, difference.entriesOnlyOnLeft().toString());
//                                    LOGGER.info("mapA and mapB entriesOnlyOnLeft {}", difference.entriesOnlyOnLeft().toString());
                            } else if (!difference.entriesOnlyOnRight().isEmpty()) {
                                differenceMap.put(passportId, difference.entriesOnlyOnRight().toString());
//                                    LOGGER.info("mapA and mapB entriesOnlyOnRight {}", difference.entriesOnlyOnRight().toString());
                            }
                        }
                    }


                    //验证手机映射
                    {
                        try {
                            String passportId_MM = mobilePassportMappingDAO.getPassportIdByHashMobile(account.getMobile());
                            if (passportId.equalsIgnoreCase(passportId_MM)) {
                                LOGGER.info("account and mobile mapping passportId  equal");
                            } else {
                                LOGGER.info(String.format("account passportId:{},mobile mapping passportId:{} not equal"), passportId, passportId_MM);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            LOGGER.error("get mobile passport mapping error. passportId:" + passportId, e);
                        }

                    }
                }
            }
*/
        } catch (Exception e) {
            LOGGER.error("testCheckData error", e);
        }
        System.out.println("===============" + differenceMap.toString());
    }


    @Test
    public void testSubStr() {
        String str1 = "1980-01-01 00:00:00.0";
        String str2 = "2007-05-16 16:33:18.0";
        System.out.println("=====================" + StringUtils.substring(str1, 0, 10));
        System.out.println("=====================" + StringUtils.substring(str2, 0, 19));
    }


    @Test
    public void testMapDifference() {
        Map<String, String> mapA = Maps.newHashMap();
        Map<String, String> mapB = Maps.newHashMap();

        mapA.put("key1", "value1");
        mapA.put("key2", "value2");
        mapA.put("key3", "value3");
        mapA.put("key4", "value4");
        mapA.put("key5", "value5");
        mapA.put("key6", "value6");

        mapB.put("key1", "value1");
        mapB.put("key2", "value2");
        mapB.put("key3", "value3");
        mapB.put("key4", "value4");
        mapB.put("key5", "value5");
        mapB.put("key6", "value5");


        Map<String, String> map1 = Maps.newHashMap();
        Map<String, String> map2 = Maps.newHashMap();

        map1.put("key1", "value1");
        map1.put("key2", "value2");
        map1.put("key3", "value3");
        map1.put("key4", "value4");
        map1.put("key5", "value5");
        map1.put("key6", "value6");

        map2.put("key11", "value11");
        map2.put("key22", "value22");
        map2.put("key33", "value33");
        map2.put("key44", "value44");
        map2.put("key55", "value55");
        map2.put("key66", "value66");


       /* MapDifference difference = Maps.difference(mapA, mapB);
        System.out.println(difference.entriesDiffering().toString());
        Assert.assertEquals("check map difference ", difference.areEqual(), true);
*/

        List<Map<String, String>> maps = Lists.newArrayList();

        for (int i = 0; i < 2; i++) {
            maps.add(map1);
            maps.add(map2);
        }
        System.out.println("==============================" + maps.toString());

    }


    /**
     * 构建请求参数
     *
     * @return
     */
    public static RequestModelXml bulidRequestModelXml(String passportId) {

        RequestModelXml requestModelXml = new RequestModelXml(REQUEST_URL, REQUEST_INFO);
        try {
            long ct = System.currentTimeMillis();
            String code = passportId + appId + key + ct;
            code = Coder.encryptMD5(code);

            requestModelXml.addParam("mobile", "");
            requestModelXml.addParam("createtime", "");
            requestModelXml.addParam("createip", "");
            requestModelXml.addParam("email", "");
            requestModelXml.addParam("birthday", "");
            requestModelXml.addParam("gender", "");
            requestModelXml.addParam("province", "");
            requestModelXml.addParam("city", "");
            requestModelXml.addParam("username", "");
            requestModelXml.addParam("uniqname", "");
            requestModelXml.addParam("personalid", "");
            requestModelXml.addParam("userid", passportId);
            requestModelXml.addParam("appid", appId);
            requestModelXml.addParam("ct", ct);
            requestModelXml.addParam("code", code);
        } catch (Exception e) {
            LOGGER.error("build RequestModelXml error.", e);
            e.printStackTrace();
        }
        return requestModelXml;
    }
}
