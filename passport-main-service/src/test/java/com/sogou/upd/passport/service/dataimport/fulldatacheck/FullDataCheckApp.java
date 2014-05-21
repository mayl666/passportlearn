package com.sogou.upd.passport.service.dataimport.fulldatacheck;

import com.google.common.collect.Lists;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.httpclient.RequestModelXml;
import com.sogou.upd.passport.common.parameter.HttpTransformat;
import com.sogou.upd.passport.common.utils.SGHttpClient;
import com.sogou.upd.passport.dao.account.AccountDAO;
import com.sogou.upd.passport.dao.account.AccountInfoDAO;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountInfo;
import com.sogou.upd.passport.service.dataimport.util.FileUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.perf4j.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveTask;

/**
 * Created with IntelliJ IDEA.
 * User: chengang
 * Date: 14-5-13
 * Time: 下午2:50
 */
public class FullDataCheckApp extends RecursiveTask<List<String>> {


    private static final Logger LOGGER = LoggerFactory.getLogger(FullDataCheckApp.class);

    private static final String appId = "1100";

    private static final String key = "yRWHIkB$2.9Esk>7mBNIFEcr:8\\[Cv";


    private static final String REQUEST_URL = "http://internal.passport.sohu.com/interface/getuserinfo";

    private static final String REQUEST_INFO = "info";

    //记录对比不同的结果
    private List<String> differenceList = Lists.newArrayList();


    //从搜狐获取数据失败记录
    private List<String> failedList = Lists.newArrayList();


    private AccountDAO accountDAO;

    private AccountInfoDAO accountInfoDAO;

    private String filePath;

    public FullDataCheckApp(AccountDAO accountDAO, AccountInfoDAO accountInfoDAO, String filePath) {
        this.accountDAO = accountDAO;
        this.accountInfoDAO = accountInfoDAO;
        this.filePath = filePath;
    }

    @Test
    public void testMapDifference() {
        Map<String, Object> mapA = Maps.newHashMap();
        Map<String, Object> mapB = Maps.newHashMap();

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

        MapDifference difference = Maps.difference(mapA, mapB);
        System.out.println(difference.entriesDiffering().toString());
        Assert.assertEquals("check map difference ", difference.areEqual(), true);
    }


    @Override
    protected List<String> compute() {

        LOGGER.info("start check full data ");

        StopWatch watch = new StopWatch();
        watch.start();

        Path checkFilePath = Paths.get(filePath);

        try (BufferedReader reader = Files.newBufferedReader(checkFilePath, Charset.defaultCharset())) {

            String passportId;
            while ((passportId = reader.readLine()) != null) {
                RequestModelXml requestModelXml = bulidRequestModelXml(passportId);

                Map<String, Object> mapB = null;
                try {
                    mapB = SGHttpClient.executeBean(requestModelXml, HttpTransformat.xml, Map.class);
                } catch (Exception e) {
                    failedList.add(passportId);
                    LOGGER.error("FullDataCheckApp get account from SOHU error.", e);
                    continue;
                }

                if (StringUtils.isNotEmpty(passportId)) {
                    Account account = accountDAO.getAccountByPassportId(passportId);
                    AccountInfo accountInfo = accountInfoDAO.getAccountInfoByPassportId(passportId);
                    if (account != null && accountInfo != null) {
                        //Test 库数据
                        Map<String, Object> mapA = Maps.newHashMap();
                        mapA.put("mobile", account.getMobile());
                        mapA.put("reg_time", account.getRegTime());
                        mapA.put("reg_ip", account.getRegIp());
                        mapA.put("email", accountInfo.getEmail());
                        mapA.put("birthday", accountInfo.getBirthday());
                        mapA.put("gender", accountInfo.getGender());
                        mapA.put("province", accountInfo.getProvince());
                        mapA.put("city", accountInfo.getCity());
                        mapA.put("fullname", accountInfo.getFullname());
                        mapA.put("personalid", accountInfo.getPassportId());

                        //比较文件
                        if (mapA != null && mapB != null) {
                            MapDifference difference = Maps.difference(mapA, mapB);
                            if (!difference.areEqual()) {
                                differenceList.add(passportId);
                                //记录对比不同的数据到Log 记录到文件
                                LOGGER.info("FullDataCheckApp check full data difference passportId {} " + passportId);
                            }
                        }
                    }
                } else {
                    LOGGER.info("check full data passportId |" + passportId + "| is Null");
                }
            }

            if (CollectionUtils.isNotEmpty(failedList)) {
                //记录调用搜狐接口，获取失败的数据，在对失败的数据进行验证
                FileUtil.storeFile("D:\\项目\\非第三方账号迁移\\check_full_data\\check_full_data_fail.txt", failedList);
            }

            LOGGER.info("FullDataCheckApp finish check full data use time {} s", watch.stop());
        } catch (Exception e) {
            LOGGER.error(" FullDataCheckApp check full data error.", e);
        }
        return differenceList;
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
