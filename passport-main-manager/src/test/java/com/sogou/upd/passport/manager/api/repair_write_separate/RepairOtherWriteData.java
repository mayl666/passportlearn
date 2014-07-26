package com.sogou.upd.passport.manager.api.repair_write_separate;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.FileIOUtil;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.httpclient.RequestModelXml;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.parameter.HttpTransformat;
import com.sogou.upd.passport.common.parameter.PasswordTypeEnum;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.*;
import com.sogou.upd.passport.dao.account.AccountDAO;
import com.sogou.upd.passport.dao.account.AccountInfoDAO;
import com.sogou.upd.passport.dao.account.MobilePassportMappingDAO;
import com.sogou.upd.passport.dao.account.UniqNamePassportMappingDAO;
import com.sogou.upd.passport.dao.repairdata.IncUserExtInfoHisDAO;
import com.sogou.upd.passport.dao.repairdata.IncUserInfoHisDAO;
import com.sogou.upd.passport.dao.repairdata.IncUserOtherInfoHisDAO;
import com.sogou.upd.passport.manager.api.account.BindApiManager;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.manager.api.account.form.BaseMoblieApiParams;
import com.sogou.upd.passport.manager.api.account.form.CheckUserApiParams;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountInfo;
import com.sogou.upd.passport.model.repairdata.IncUserExtInfo;
import com.sogou.upd.passport.model.repairdata.IncUserInfo;
import com.sogou.upd.passport.model.repairdata.IncUserOtherInfo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 其他账号写分离阶段，修复数据测试类
 * User: shipengzhi
 * Date: 14-7-15
 * Time: 下午3:43
 * To change this template use File | Settings | File Templates.
 */
//@Ignore
public class RepairOtherWriteData extends BaseTest {

    private static final String appId = "1120";
    private static final String key = "4xoG%9>2Z67iL5]OdtBq$l#>DfW@TY";
    private static final String REQUEST_URL = "http://internal.passport.sohu.com/interface/getuserinfo";
    private static final String REQUEST_INFO = "info";

    @Autowired
    private MobilePassportMappingDAO mobilePassportMappingDAO;
    @Autowired
    private UniqNamePassportMappingDAO uniqNamePassportMappingDAO;
    @Autowired
    private AccountDAO accountDAO;
    @Autowired
    private AccountInfoDAO accountInfoDAO;
    @Autowired
    private BindApiManager proxyBindApiManager;
    @Autowired
    private RegisterApiManager proxyRegisterApiManager;
    @Autowired
    private IncUserInfoHisDAO incUserInfoHisDAO;
    @Autowired
    private IncUserOtherInfoHisDAO incUserOtherInfoHisDAO;
    @Autowired
    private IncUserExtInfoHisDAO incUserExtInfoHisDAO;

    /*
    * 检查账号是否在搜狗数据库里
    * 输入:useroperation log里的"username"，个性账号、个性账号@sogou.com、手机号、手机号@sohu.com、外域邮箱
    * 输出:不在搜狗数据库里"userid"
    */
    @Test
    public void checkIsSogouExistDate() {
        List<String> contentList = Lists.newArrayList();
        List<String> passportList = FileIOUtil.readFileByLines("D:\\数据迁移\\写分离前需要迁移的账号\\xaa");
        String content;
        int count = 0;
        String sgPassportId;
        CheckUserApiParams checkUserApiParams = new CheckUserApiParams();
        for (String passportId : passportList) {
            try {
                if (Strings.isNullOrEmpty(passportId)) {
                    continue;
                }
                passportId = passportId.replaceAll(" ", "");
                if (AccountDomainEnum.isIndivid(passportId)) {
                    passportId = passportId + CommonConstant.SOGOU_SUFFIX;
                }
                if (AccountDomainEnum.getAccountDomain(passportId) == AccountDomainEnum.SOGOU) {
                    passportId = passportId.toLowerCase();
                }
                if (PhoneUtil.verifyPhoneNumberFormat(passportId)) {
                    sgPassportId = mobilePassportMappingDAO.getPassportIdByMobile(passportId);
                    if (Strings.isNullOrEmpty(sgPassportId)) {
                        BaseMoblieApiParams params = new BaseMoblieApiParams(passportId);
                        Result shResult = proxyBindApiManager.getPassportIdByMobile(params);
                        if (shResult.isSuccess()) {
                            content = passportId + "," + shResult.getModels().get("userid");
                            count++;
                            contentList.add(content);
                        }
                    }
                } else {
                    Account account = accountDAO.getAccountByPassportId(passportId);
                    if (account == null) {
                        checkUserApiParams.setUserid(passportId);
                        Result shResult = proxyRegisterApiManager.checkUser(checkUserApiParams);
                        if (shResult.getCode().equals(ErrorUtil.ERR_CODE_USER_ID_EXIST)) {
                            content = passportId + "," + shResult.getModels().get("userid");
                            count++;
                            contentList.add(content);
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
        content = "total:" + passportList.size() + ",count:" + count;
        contentList.add(content);
        try {
            FileUtil.storeFile("D:\\数据迁移\\写分离前需要迁移的账号\\userlog_sogou_userid_0526_0722_xaa", contentList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * 检查搜狐域账号绑定的手机号，且手机号不在搜狗数据库里
     * 输入：手机号或手机号@sohu.com
     * 输出：“手机号”
     */
    @Test
    public void checkSohuBindMobile() {
        List<String> contentList = Lists.newArrayList();
        List<String> dataList = FileIOUtil.readFileByLines("D:\\数据迁移\\用搜狐域绑定的手机号登录\\xab");
        String content;
        int count = 0;
        long start = System.currentTimeMillis();
        for (String data : dataList) {
            String mobile = data;
            if (data.endsWith("@sohu.com")) {
                mobile = data.substring(0, data.lastIndexOf("@sohu.com"));
            }
            if (!PhoneUtil.verifyPhoneNumberFormat(mobile)) {
                continue;
            }
            String sgPassportId = mobilePassportMappingDAO.getPassportIdByMobile(mobile);
            if (Strings.isNullOrEmpty(sgPassportId)) {
                BaseMoblieApiParams params = new BaseMoblieApiParams(mobile);
                Result shResult = proxyBindApiManager.getPassportIdByMobile(params);
                if (shResult.isSuccess()) {
                    String shPassportId = (String) shResult.getModels().get("userid");
                    if (!Strings.isNullOrEmpty(shPassportId) && AccountDomainEnum.SOHU.equals(AccountDomainEnum.getAccountDomain(shPassportId))) {
                        content = mobile;
                        count++;
                        contentList.add(content);
                    }
                }
            }
        }
        content = "total:" + dataList.size() + ",SohuBindMobile:" + count;
        contentList.add(content);
        try {
            com.sogou.upd.passport.common.utils.FileUtil.storeFile("D:\\数据迁移\\用搜狐域绑定的手机号登录\\userlog_phone_userid_end_0725_xae", contentList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("use time:" + (end - start) / 1000 / 60 / 60 + "h");
    }

    /**
     * 补全线上表缺失的搜狗账号
     * 输入：sgPassportId,shPassportId
     * 输出：写入线上account表、account_info表、mobile_passportId_mapping表、uniqname_passportId_mapping表
     */
    @Test
    public void fillSogouWriteSGDB() {
        List<String> contentList = Lists.newArrayList();
        List<String> dataList = FileIOUtil.readFileByLines("D:\\数据迁移\\写分离前需要迁移的账号\\userlog_sogou_userid_0526_0722_xaa");
        String content;
        int count = 0;
        long start = System.currentTimeMillis();
        for (String data : dataList) {
            String[] dataArray = data.split(",");
            String passportId;
            if (dataArray.length == 2) {
                passportId = dataArray[1];
            } else {
                passportId = dataArray[0];
            }
            if (Strings.isNullOrEmpty(passportId)) {
                continue;
            }
            passportId = passportId.toLowerCase();
            if (accountDAO.getAccountByPassportId(passportId) != null) {
                continue;
            }
            try {
                IncUserInfo incUserInfo = incUserInfoHisDAO.getIncUserInfo(passportId);
                if (incUserInfo == null || ("0".equals(incUserInfo.getFlag()) | Strings.isNullOrEmpty(incUserInfo.getPassword()))) {
                    content = passportId + ",IncUserInfo is empty or flag==0 or password==null";
                    contentList.add(content);
                    continue;
                }
                IncUserOtherInfo incUserOtherInfo = incUserOtherInfoHisDAO.getIncUserOtherInfo(passportId);
                IncUserExtInfo incUserExtInfo = incUserExtInfoHisDAO.getIncUserExtInfo(passportId);
                String email = null, mobile = null, personalId = null, province = "", city = "", uniqname = null;
                if (incUserOtherInfo != null) {
                    email = "1".equals(incUserOtherInfo.getEmailflag()) ? incUserOtherInfo.getEmail() : "";
                    mobile = "1".equals(incUserOtherInfo.getMobileflag()) ? incUserOtherInfo.getMobile() : "";
                    personalId = !Strings.isNullOrEmpty(incUserOtherInfo.getPersonalid()) ? incUserOtherInfo.getPersonalid() : "";
                    province = !Strings.isNullOrEmpty(incUserOtherInfo.getProvince()) ? incUserOtherInfo.getProvince() : "";
                    city = !Strings.isNullOrEmpty(incUserOtherInfo.getCity()) ? incUserOtherInfo.getCity() : "";
                    uniqname = !Strings.isNullOrEmpty(incUserOtherInfo.getUniqname()) ? incUserOtherInfo.getUniqname() : "";
                }
                String question = null, answer = null, gender = "0", fullname = null, reg_ip = null;
                Date birthday = DateUtil.parse("1900-00-00", DateUtil.DATE_FMT_3);
                Date reg_time = new Date();
                if (incUserExtInfo != null) {
                    question = !Strings.isNullOrEmpty(incUserExtInfo.getQuestion()) ? incUserExtInfo.getQuestion() : "";
                    answer = !Strings.isNullOrEmpty(incUserExtInfo.getAnswer()) ? incUserExtInfo.getAnswer() : "";
                    gender = !Strings.isNullOrEmpty(incUserExtInfo.getGender()) ? incUserExtInfo.getGender() : "";
                    fullname = !Strings.isNullOrEmpty(incUserExtInfo.getUsername()) ? incUserExtInfo.getUsername() : "";
                    reg_ip = !Strings.isNullOrEmpty(incUserExtInfo.getCreateip()) ? incUserExtInfo.getCreateip() : "";
                }

                Account account = new Account();
                account.setPassportId(passportId);
                account.setPassword(incUserInfo.getPassword());
                account.setPasswordtype(Integer.parseInt(incUserInfo.getPasswordtype()));
                account.setMobile(mobile);
                account.setRegTime(reg_time);
                account.setRegIp(reg_ip);
                account.setFlag(1);
                account.setAccountType(AccountTypeEnum.SOGOU.getValue());
                account.setUniqname(uniqname);

                AccountInfo accountInfo = new AccountInfo();
                accountInfo.setPassportId(passportId);
                accountInfo.setQuestion(question);
                accountInfo.setAnswer(answer);
                accountInfo.setEmail(email);
                accountInfo.setBirthday(birthday);
                accountInfo.setGender(gender);
                accountInfo.setProvince(province);
                accountInfo.setCity(city);
                accountInfo.setFullname(fullname);
                accountInfo.setPersonalid(personalId);
                accountInfo.setUpdateTime(new Date());
                accountInfo.setCreateTime(reg_time);

                int mobileMapSucc = 1, uniqMapSucc = 1, accountSucc = 1, accountInfoSucc = 1;
                if (!Strings.isNullOrEmpty(mobile) && mobilePassportMappingDAO.getPassportIdByMobile(mobile) == null) {
                    String mobilePassportId = mobilePassportMappingDAO.getPassportIdByMobile(mobile);
                    if (Strings.isNullOrEmpty(mobilePassportId)) {
                        mobileMapSucc = mobilePassportMappingDAO.insertMobilePassportMapping(mobile, passportId);
                    } else {
                        account.setMobile(null);
                    }
                }
                if (!Strings.isNullOrEmpty(uniqname) && uniqNamePassportMappingDAO.getPassportIdByUniqName(uniqname) == null) {
                    String uniqPassportId = uniqNamePassportMappingDAO.getPassportIdByUniqName(uniqname);
                    if (Strings.isNullOrEmpty(uniqPassportId)) {
                        uniqMapSucc = uniqNamePassportMappingDAO.insertUniqNamePassportMapping(uniqname, passportId);
                    } else {
                        account.setUniqname(null);
                    }
                }
                if (accountDAO.getAccountByPassportId(passportId) == null) {
                    accountSucc = accountDAO.insertAccount(passportId, account);
                }
                if (accountInfoDAO.getAccountInfoByPassportId(passportId) == null) {
                    accountInfoSucc = accountInfoDAO.saveInfoOrInsert(passportId, accountInfo);
                }
                if (mobileMapSucc == 1 && accountSucc == 1 && accountInfoSucc == 1) {
                    count++;
                } else {
                    content = passportId + ",mobileMap-" + mobileMapSucc + ",uniqMap-" + uniqMapSucc + ",account-" + accountSucc + ",accountInfo-" + accountInfoSucc;
                    contentList.add(content);
                }
            } catch (Exception e) {
                content = passportId + ",getUserInfoFromSohu OR insertSogouDB Error," + e.getMessage();
                contentList.add(content);
            }
        }
        content = "total:" + dataList.size() + ",SogouInertSuccess:" + count;
        contentList.add(content);
        long end = System.currentTimeMillis();
        content = "use time:" + (end - start) / 1000 / 60 + "m";
        contentList.add(content);
        try {
            com.sogou.upd.passport.common.utils.FileUtil.storeFile("D:\\数据迁移\\用搜狐域绑定的手机号登录\\fill_sogou_userid_0526_0725_xaa", contentList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 搜狐域绑定的手机账号写入搜狗线上库
     * 输入：手机号
     * 输出：写入线上account表、account_info表、mobile_passportId_mapping表
     */
    @Test
    public void sohuBindMobileWriteSGDB() {
        List<String> contentList = Lists.newArrayList();
        List<String> mobileList = FileIOUtil.readFileByLines("D:\\数据迁移\\用搜狐域绑定的手机号登录\\userlog_phone_userid_end_0725_xaa");
        String content;
        int count = 0;
        long start = System.currentTimeMillis();
        for (String mobile : mobileList) {
            if (!PhoneUtil.verifyPhoneNumberFormat(mobile)) {
                continue;
            }
            if (mobile.endsWith("@sohu.com")) {
                mobile = mobile.substring(0, mobile.lastIndexOf("@sohu.com"));
            }
            String sgPassportId = mobilePassportMappingDAO.getPassportIdByMobile(mobile);
            if (!Strings.isNullOrEmpty(sgPassportId)) {
                continue;
            }
            BaseMoblieApiParams params = new BaseMoblieApiParams(mobile);
            Result shResult = proxyBindApiManager.getPassportIdByMobile(params);
            if (shResult.isSuccess()) {
                String shPassportId = (String) shResult.getModels().get("userid");
                if (!Strings.isNullOrEmpty(shPassportId) && AccountDomainEnum.SOHU.equals(AccountDomainEnum.getAccountDomain(shPassportId))) {
                    //构建参数
                    RequestModelXml requestModelXml = buildRequestModelXml(shPassportId);
                    if (requestModelXml == null) {
                        content = mobile + ":buildRequestModelXmlError," + requestModelXml.toString();
                        contentList.add(content);
                        continue;
                    }
                    try {
                        Map<String, Object> infoMap = SGHttpClient.executeBean(requestModelXml, HttpTransformat.xml, Map.class);
                        String birthdayStr = String.valueOf(infoMap.get("birthday"));
                        Date birthday = !Strings.isNullOrEmpty(birthdayStr) ? DateUtil.parse(birthdayStr, DateUtil.DATE_FMT_3) : DateUtil.parse("1900-01-01", DateUtil.DATE_FMT_3);
                        String email = "1".equals(infoMap.get("emailflag")) ? (String) infoMap.get("email") : "";
                        String createTime = (String) infoMap.get("createtime");
                        Date reg_time = new Date();
                        if (!Strings.isNullOrEmpty(createTime)) {
                            reg_time = DateUtil.parse(createTime, DateUtil.DATE_FMT_2);
                        }
                        String reg_ip = (String) infoMap.get("createip");
                        reg_ip = Strings.isNullOrEmpty(reg_ip) ? "" : reg_ip;
                        String uniqname = !Strings.isNullOrEmpty((String) infoMap.get("uniqname")) ? (String) infoMap.get("uniqname") : null;

                        Account account = new Account();
                        account.setPassportId(shPassportId);
                        account.setMobile(mobile);
                        account.setRegTime(reg_time);
                        account.setRegIp(reg_ip);
                        account.setFlag(1);
                        account.setPasswordtype(PasswordTypeEnum.NOPASSWORD.getValue());
                        account.setAccountType(AccountTypeEnum.SOHU.getValue());
                        AccountInfo accountInfo = new AccountInfo();
                        accountInfo.setPassportId(shPassportId);
                        accountInfo.setEmail(email);
                        accountInfo.setBirthday(birthday);
                        accountInfo.setGender((String) infoMap.get("gender"));
                        accountInfo.setProvince((String) infoMap.get("province"));
                        accountInfo.setCity((String) infoMap.get("city"));
                        accountInfo.setFullname((String) infoMap.get("username"));
                        accountInfo.setPersonalid((String) infoMap.get("personalid"));
                        accountInfo.setUpdateTime(new Date());
                        accountInfo.setCreateTime(reg_time);
                        int mobileMapSucc = 1, accountSucc = 1, accountInfoSucc = 1, uniqMapSucc = 1;
                        mobileMapSucc = mobilePassportMappingDAO.insertMobilePassportMapping(mobile, shPassportId);
                        if (!Strings.isNullOrEmpty(uniqname) && uniqNamePassportMappingDAO.getPassportIdByUniqName(uniqname) == null) {
                            String uniqPassportId = uniqNamePassportMappingDAO.getPassportIdByUniqName(uniqname);
                            if (Strings.isNullOrEmpty(uniqPassportId)) {
                                uniqMapSucc = uniqNamePassportMappingDAO.insertUniqNamePassportMapping(uniqname, shPassportId);
                            } else {
                                account.setUniqname(null);
                            }
                        }
                        if (accountDAO.getAccountByPassportId(shPassportId) == null) {
                            accountSucc = accountDAO.insertAccount(shPassportId, account);
                        }
                        if (accountInfoDAO.getAccountInfoByPassportId(shPassportId) == null) {
                            accountInfoSucc = accountInfoDAO.saveInfoOrInsert(shPassportId, accountInfo);
                        }
                        if (mobileMapSucc == 1 && uniqMapSucc == 1 && accountSucc == 1 && accountInfoSucc == 1) {
                            count++;
                        } else {
                            content = mobile + "," + shPassportId + ",mobileMap-" + mobileMapSucc + ",uniqMap-" + uniqMapSucc + ",account-" + accountSucc + ",accountInfo-" + accountInfoSucc;
                            contentList.add(content);
                        }
                    } catch (Exception e) {
                        content = mobile + "," + shPassportId + ",getUserInfoFromSohu OR insertSogouDB Error," + e.getMessage();
                        contentList.add(content);
                    }
                }
            }
        }
        content = "total:" + mobileList.size() + ",initSuccess:" + count;
        contentList.add(content);
        long end = System.currentTimeMillis();
        content = "use time:" + (end - start) / 1000 / 60 + "m";
        contentList.add(content);
        try {
            com.sogou.upd.passport.common.utils.FileUtil.storeFile("D:\\数据迁移\\用搜狐域绑定的手机号登录\\fill_phone_userid_0526_0725_xaa", contentList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*据来源：双读异常log里用搜狐域账号绑定的手机号来登录的useridld列表，检查是否在搜狗数据库里
    * 输入："mobile userid"
    * 输出：不在搜狗数据库里的账号
    */
    @Test
    public void checkSohuBindMobileDate() {
        List<String> contentList = Lists.newArrayList();
        List<String> dataList = FileIOUtil.readFileByLines("D:\\数据迁移\\用搜狐域绑定的手机号登录\\sohubindmobile_0629_0712_total");
        String content;
        int count = 0;
        for (String data : dataList) {
            String[] dataArray = data.split(" ");
            String mobile = dataArray[0];
            String passportId = dataArray[1];
            String sgPassportId = mobilePassportMappingDAO.getPassportIdByMobile(mobile);
            if (Strings.isNullOrEmpty(sgPassportId)) {
                content = mobile + "," + passportId + ",sogou no exist";
            } else if (!passportId.equals(sgPassportId)) {
                BaseMoblieApiParams params = new BaseMoblieApiParams();
                params.setMobile(mobile);
                Result shResult = proxyBindApiManager.getPassportIdByMobile(params);
                content = mobile + "," + passportId + ",sgPassportId:" + sgPassportId + ",shUserId:" + shResult.getModels().get("userid");
            } else {
                continue;
            }
            count++;
            contentList.add(content);
        }
        content = "total:" + dataList.size() + ", sogouNoExist:" + count;
        contentList.add(content);
        try {
            com.sogou.upd.passport.common.utils.FileUtil.storeFile("D:\\数据迁移\\用搜狐域绑定的手机号登录\\sohubindmobile_0629_0712_total_result", contentList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * 检查因为搜狐接口响应超时导致搜狗账号注册没写入搜狐库的数据
     */
    @Test
    public void checkRegFailDate() {
        List<String> failedList = Lists.newArrayList();
        List<String> passportIdList = FileIOUtil.readFileByLines("D:\\regfail_0716_total");
        String content = null;
        int count = 0;
        CheckUserApiParams checkUserApiParams = new CheckUserApiParams();
        for (String passportId : passportIdList) {
            checkUserApiParams.setUserid(passportId);
            Result result = proxyRegisterApiManager.checkUser(checkUserApiParams);
            if (result.isSuccess()) {
                content = passportId + ":" + result.toString();
            }
            failedList.add(content);
            count++;
        }
        System.out.println("count:" + count);
        try {
            FileUtil.storeFile("D:\\regfail_0716_total_result", failedList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static RequestModelXml buildRequestModelXml(String passportId) {

        RequestModelXml requestModelXml = new RequestModelXml(REQUEST_URL, REQUEST_INFO);
        try {
            long ct = System.currentTimeMillis();
            String code = passportId + appId + key + ct;
            code = Coder.encryptMD5(code);
//            requestModelXml.addParam("question", "");
//            requestModelXml.addParam("mobile", "");
//            requestModelXml.addParam("mobileflag", "");
            requestModelXml.addParam("createtime", "");
            requestModelXml.addParam("createip", "");
            requestModelXml.addParam("email", "");
            requestModelXml.addParam("emailflag", "");
            requestModelXml.addParam("birthday", ""); //数据验证,暂先不取生日
            requestModelXml.addParam("gender", "");
            requestModelXml.addParam("province", "");
            requestModelXml.addParam("city", "");
            requestModelXml.addParam("username", "");
            requestModelXml.addParam("personalid", "");
            requestModelXml.addParam("userid", passportId);
            requestModelXml.addParam("uniqname", "");
            requestModelXml.addParam("appid", appId);
            requestModelXml.addParam("ct", ct);
            requestModelXml.addParam("code", code);
        } catch (Exception e) {
            return null;
        }
        return requestModelXml;
    }

}
