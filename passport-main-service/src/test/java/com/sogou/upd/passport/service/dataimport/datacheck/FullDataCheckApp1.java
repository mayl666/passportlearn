package com.sogou.upd.passport.service.dataimport.datacheck;

import com.google.common.collect.Lists;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.httpclient.RequestModelXml;
import com.sogou.upd.passport.common.model.httpclient.RequestModelXmlGBK;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.HttpTransformat;
import com.sogou.upd.passport.common.utils.SGHttpClient;
import com.sogou.upd.passport.dao.account.AccountDAO;
import com.sogou.upd.passport.dao.account.AccountInfoDAO;
import com.sogou.upd.passport.dao.account.MobilePassportMappingDAO;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountInfo;
import com.sogou.upd.passport.service.dataimport.util.FileUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Ignore;
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
@Ignore
//public class FullDataCheckApp extends RecursiveTask<List<String>> {
public class FullDataCheckApp1 extends RecursiveTask<Map<String, String>> {


    private static final Logger LOGGER = LoggerFactory.getLogger(FullDataCheckApp1.class);

    private static final String appId = "1100";

    private static final String key = "yRWHIkB$2.9Esk>7mBNIFEcr:8\\[Cv";


    private static final String REQUEST_URL = "http://internal.passport.sohu.com/interface/getuserinfo";

    private static final String REQUEST_INFO = "info";

    private static final long serialVersionUID = 506833609075209712L;

    //记录对比不同的结果
    private List<String> differenceList = Lists.newArrayList();


    //记录对不不同的结果，记录不同的内容
    private Map<String, String> differenceMap = Maps.newConcurrentMap();


    private Map<String, String> userFlagMap = Maps.newConcurrentMap();

    //记录 mobile mapping passportId not equal account passportId result
    private Map<String, String> mobile_passport_mappingMap = Maps.newConcurrentMap();


    //从搜狐获取数据失败记录
    private List<String> failedList = Lists.newArrayList();


    //检查用户flag信息 存储文件
    private static final String CHECK_USER_FLAG_FILE_PATCH = "D:\\项目\\非第三方账号迁移\\check_full_data\\check_user_flag.txt";


    private AccountDAO accountDAO;

    private AccountInfoDAO accountInfoDAO;


    private MobilePassportMappingDAO mobilePassportMappingDAO;

    private String filePath;

    public FullDataCheckApp1(AccountDAO accountDAO, AccountInfoDAO accountInfoDAO, MobilePassportMappingDAO mobilePassportMappingDAO, String filePath) {
        this.accountDAO = accountDAO;
        this.accountInfoDAO = accountInfoDAO;
        this.mobilePassportMappingDAO = mobilePassportMappingDAO;
        this.filePath = filePath;
    }


    @Override
//    protected List<String> compute() {
    protected Map<String, String> compute() {

        LOGGER.info("start check full data ");

        StopWatch watch = new StopWatch();
        watch.start();

        Path checkFilePath = Paths.get(filePath);

//        Path saveFlagLocalPath = Paths.get(CHECK_USER_FLAG_FILE_PATCH);

        try (BufferedReader reader = Files.newBufferedReader(checkFilePath, Charset.defaultCharset())) {

//            BufferedWriter writer = Files.newBufferedWriter(saveFlagLocalPath, Charset.defaultCharset());

            String passportId;
            while ((passportId = reader.readLine()) != null) {
                if (StringUtils.isNotEmpty(passportId)) {
                    try {
                        Account account = accountDAO.getAccountByPassportId(passportId);
                        if (account != null) {
                            //手机账号，验证 插入 mobile passportId mapping
                            try {
                                if (StringUtils.isNotEmpty(account.getMobile())) {
                                    String passportId_MM = mobilePassportMappingDAO.getPassportIdByMobile(account.getMobile());
                                    if (!passportId.equalsIgnoreCase(passportId_MM)) {
//                                            LOGGER.info(String.format("account passportId:{},mobile mapping passportId:{} not equal"), passportId, passportId_MM);
                                        //记录到文件
                                        mobile_passport_mappingMap.put(account.getMobile(), passportId + " | " + passportId_MM);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                LOGGER.error("get mobile passport mapping error. passportId:" + passportId, e);
                                continue;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        LOGGER.error("get  account  error. passportId:" + passportId, e);
                        continue;
                    }

                }
            }

            //记录mobile mapping passportId not equal account passportId result 到文件
            /*if (mobile_passport_mappingMap != null && !mobile_passport_mappingMap.isEmpty()) {
                FileUtil.storeFileMap2Local("D:\\项目\\非第三方账号迁移\\check_full_data\\check_05_test_mobile_mapping_passportId.txt", mobile_passport_mappingMap);
            }*/

            /*if (CollectionUtils.isNotEmpty(failedList)) {
                //记录调用搜狐接口，获取失败的数据，在对失败的数据进行验证
                com.sogou.upd.passport.common.utils.FileUtil.storeFile("D:\\项目\\非第三方账号迁移\\check_full_data\\check_full_data_05_test_fail.txt", failedList);
            }*/

            LOGGER.info("FullDataCheckApp finish check full data 05 phone mapping use time {} s", watch.stop());
        } catch (Exception e) {
            LOGGER.error(" FullDataCheckApp check full data error.", e);
        }
//        return differenceList;
        return mobile_passport_mappingMap;
    }

    /**
     * 构建请求参数
     *
     * @return
     */
    public static RequestModelXml buildRequestModelXml(String passportId) {

        RequestModelXml requestModelXml = new RequestModelXml(REQUEST_URL, REQUEST_INFO);
        try {
            long ct = System.currentTimeMillis();
            String code = passportId + appId + key + ct;
            code = Coder.encryptMD5(code);

            requestModelXml.addParam("question", "");
            requestModelXml.addParam("mobile", "");
            requestModelXml.addParam("createtime", "");
            requestModelXml.addParam("createip", "");
            requestModelXml.addParam("email", "");
            requestModelXml.addParam("birthday", ""); //数据验证,暂先不取生日
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

    public static RequestModelXmlGBK bulidRequestModelXmlGBK(String passportId) {

        RequestModelXmlGBK requestModelXml = new RequestModelXmlGBK(REQUEST_URL, REQUEST_INFO);
        try {
            long ct = System.currentTimeMillis();
            String code = passportId + appId + key + ct;
            code = Coder.encryptMD5(code);

            requestModelXml.addParam("question", "");
            requestModelXml.addParam("mobile", "");
            requestModelXml.addParam("createtime", "");
            requestModelXml.addParam("createip", "");
            requestModelXml.addParam("email", "");
//            requestModelXml.addParam("birthday", ""); //数据验证,暂先不取生日
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
