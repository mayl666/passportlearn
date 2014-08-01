package com.sogou.upd.passport.manager.api.repair_write_separate;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.BaseTest;
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
import com.sogou.upd.passport.model.account.*;
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
        List<String> passportList = FileUtil.readFileByLines("D:\\数据迁移\\写分离前需要迁移的账号\\userlog_sogou_userid_0801");
        String content;
        int count = 0;
        String sgPassportId;
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
                        String shPassportId = (String) shResult.getModels().get("userid");
                        shPassportId = shPassportId.replaceAll(" ", "");
                        if (shResult.isSuccess()) {
                            content = passportId + "," + shPassportId;
                            count++;
                            contentList.add(content);
                        }
                    }
                } else {
                    Account account = accountDAO.getAccountByPassportId(passportId);
                    if (account == null) {
                        CheckUserApiParams checkUserApiParams = new CheckUserApiParams();
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
                content = passportId + "," + passportId;
                contentList.add(content);
            }
        }
        content = "total:" + passportList.size() + ",count:" + count;
        contentList.add(content);
        try {
            FileUtil.storeFile("D:\\数据迁移\\写分离前需要迁移的账号\\userlog_sogou_userid_0801", contentList);
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
        List<String> dataList = FileUtil.readFileByLines("D:\\数据迁移\\用搜狐域绑定的手机号登录\\userlog_phone_userid_login_success_0726_0727");
        String content;
        int count = 0;
        long start = System.currentTimeMillis();
        for (String data : dataList) {
            String mobile = data;
            mobile = mobile.replaceAll(" ", "");
            if (data.endsWith("@sohu.com")) {
                mobile = data.substring(0, data.lastIndexOf("@sohu.com"));
            }
            if (!PhoneUtil.verifyPhoneNumberFormat(mobile)) {
                continue;
            }
            try {
                String sgPassportId = mobilePassportMappingDAO.getPassportIdByMobile(mobile);
                if (Strings.isNullOrEmpty(sgPassportId)) {
                    BaseMoblieApiParams params = new BaseMoblieApiParams(mobile);
                    Result shResult = proxyBindApiManager.getPassportIdByMobile(params);
                    if (shResult.isSuccess()) {
                        String shPassportId = (String) shResult.getModels().get("userid");
                        shPassportId = shPassportId.replaceAll(" ", "");
                        if (!Strings.isNullOrEmpty(shPassportId) && AccountDomainEnum.SOHU.equals(AccountDomainEnum.getAccountDomain(shPassportId))) {
                            content = mobile;
                            count++;
                            contentList.add(content);
                        }
                    }
                }
            } catch (Exception e) {
                content = mobile;
                contentList.add(content);
            }
        }
        content = "total:" + dataList.size() + ",SohuBindMobile:" + count;
        contentList.add(content);
        try {
            FileUtil.storeFile("D:\\数据迁移\\用搜狐域绑定的手机号登录\\userlog_phone_userid_login_success_0726_0727_result", contentList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("use time:" + (end - start) / 1000 / 60 + "m");
    }

    /**
     * 补全线上表缺失的搜狗账号
     * 输入：sgPassportId,shPassportId
     * 输出：写入线上account表、account_info表、mobile_passportId_mapping表、uniqname_passportId_mapping表
     */
    @Test
    public void fillSogouWriteSGDB() {
        List<String> contentList = Lists.newArrayList();
        List<String> dataList = FileUtil.readFileByLines("D:\\数据迁移\\写分离前需要迁移的账号\\userlog_sogou_userid_0526_0722_result");
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

                int mobileMapSucc = 1, accountSucc = 1, accountInfoSucc = 1, uniqMapSucc = 1;
                if (!Strings.isNullOrEmpty(mobile)) {
                    try{
                    mobileMapSucc = mobilePassportMappingDAO.insertMobilePassportMapping(mobile, passportId);
                    }catch (Exception e){
                        account.setMobile(null);
                    }
                }
                if (!Strings.isNullOrEmpty(uniqname)) {
                    try{
                    uniqMapSucc = uniqNamePassportMappingDAO.insertUniqNamePassportMapping(uniqname, passportId);
                    }catch (Exception e){
                        account.setUniqname(null);
                    }
                }
                try{
                accountSucc = accountDAO.insertAccount(passportId, account);
                }catch (Exception e){
                }
                try{
                accountInfoSucc = accountInfoDAO.saveInfoOrInsert(passportId, accountInfo);
                }catch (Exception e){
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
            com.sogou.upd.passport.common.utils.FileUtil.storeFile("D:\\数据迁移\\用搜狐域绑定的手机号登录\\fill_sogou_userid_0526_0725_result", contentList);
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
        List<String> mobileList = FileUtil.readFileByLines("D:\\数据迁移\\用搜狐域绑定的手机号登录\\userlog_phone_userid_end_0725_result_bak");
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
            try {
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

                            if (fillSGDB(uniqname, mobile, shPassportId, account, accountInfo, contentList)) {
                                count++;
                            }
                        } catch (Exception e) {
                            content = mobile + "," + shPassportId + ",getUserInfoFromSohu OR insertSogouDB Error," + e.getMessage();
                            contentList.add(content);
                        }
                    }
                }
            } catch (Exception e) {
                content = mobile + ",getUserInfoFromSohu OR insertSogouDB Error," + e.getMessage();
                contentList.add(content);
            }
        }
        content = "total:" + mobileList.size() + ",initSuccess:" + count;
        contentList.add(content);
        long end = System.currentTimeMillis();
        content = "use time:" + (end - start) / 1000 / 60 + "m";
        contentList.add(content);
        try {
            com.sogou.upd.passport.common.utils.FileUtil.storeFile("D:\\数据迁移\\用搜狐域绑定的手机号登录\\fill_sogou_userid_end_0725_xab_xae", contentList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 补全线上表缺失的外域邮箱或手机账号
     * 输入：搜狐的user_info表、user_other_info表、user_ext_info表
     * 输出：写入线上account表、account_info表、mobile_passportId_mapping表、uniqname_passportId_mapping表
     */
    @Test
    public void fillOtherOrPhoneWriteSGDB() {
        String content;
        int count = 0;
        long start = System.currentTimeMillis();
        List<String> contentList = Lists.newArrayList();
        List<String> userInfoList = FileUtil.readFileByLines("D:\\数据迁移\\写分离前需要迁移的账号\\waiyu_4097_phone_0526_0722\\passport.user_info_tmp.2014-07-28");
        List<String> userOtherInfoList = FileUtil.readFileByLines("D:\\数据迁移\\写分离前需要迁移的账号\\waiyu_4097_phone_0526_0722\\passport.user_other_info_tmp.2014-07-28");
        List<String> userExtInfoList = FileUtil.readFileByLines("D:\\数据迁移\\写分离前需要迁移的账号\\waiyu_4097_phone_0526_0722\\passport.user_ext_info_tmp.2014-07-28");
        Map<String, UserInfoTmp> userInfoMap = Maps.newHashMap();
        for (String data : userInfoList) {
            String[] userInfoArray = data.split(":%:");
            String passportId = userInfoArray[0].toLowerCase();
            if (userInfoArray.length != 4) {
                content = passportId + ",userInfo length != 4";
                contentList.add(content);
                continue;
            }
            UserInfoTmp userInfoTmp = new UserInfoTmp();
            userInfoTmp.setUserid(passportId);
            userInfoTmp.setPassword(userInfoArray[1]);
            userInfoTmp.setPasswordtype(userInfoArray[2]);
            userInfoTmp.setFlag(userInfoArray[3]);
            userInfoMap.put(passportId, userInfoTmp);
        }
        Map<String, UserOtherInfoTmp> userOtherInfoMap = Maps.newHashMap();
        for (String data : userOtherInfoList) {
            String[] userOtherInfoArray = data.split(":%:");
            String passportId = userOtherInfoArray[0].toLowerCase();
            if (userOtherInfoArray.length < 8) {
                content = passportId + ",userOtherInfo length < 8";
                contentList.add(content);
                continue;
            }
            UserOtherInfoTmp userOtherInfoTmp = new UserOtherInfoTmp();
            userOtherInfoTmp.setUserid(passportId);
            userOtherInfoTmp.setPersonalid(userOtherInfoArray[1]);
            userOtherInfoTmp.setMobile(userOtherInfoArray[2]);
            userOtherInfoTmp.setMobileflag(userOtherInfoArray[3]);
            userOtherInfoTmp.setEmail(userOtherInfoArray[4]);
            userOtherInfoTmp.setEmailflag(userOtherInfoArray[5]);
            userOtherInfoTmp.setProvince(userOtherInfoArray[6]);
            if (userOtherInfoArray.length >= 9) {
                userOtherInfoTmp.setCity(userOtherInfoArray[8]);
            } else {
                userOtherInfoTmp.setCity("");
            }
            userOtherInfoMap.put(passportId, userOtherInfoTmp);
        }
        Map<String, UserExtInfoTmp> userExtInfoTmpMap = Maps.newHashMap();
        for (String data : userExtInfoList) {
            String[] userExtInfoArray = data.split(":%:");
            String passportId = userExtInfoArray[0].toLowerCase();
            if (userExtInfoArray.length < 7) {
                content = passportId + ",userExtInfo length < 7";
                contentList.add(content);
                continue;
            }
            UserExtInfoTmp userExtInfoTmp = new UserExtInfoTmp();
            userExtInfoTmp.setUserid(passportId);
            userExtInfoTmp.setUsername(userExtInfoArray[3]);
            userExtInfoTmp.setBirthday(userExtInfoArray[4]);
            userExtInfoTmp.setGender(userExtInfoArray[5]);
            if (userExtInfoArray.length >= 7) {
                String createtime = userExtInfoArray[6];
                if (!"NULL".equals(createtime)) {
                    userExtInfoTmp.setCreateip(createtime);
                }
            }
            if (userExtInfoArray.length >= 8) {
                userExtInfoTmp.setCreateip(userExtInfoArray[7]);
            }
            userExtInfoTmpMap.put(passportId, userExtInfoTmp);
        }

        for (String passportId : userInfoMap.keySet()) {
            if (Strings.isNullOrEmpty(passportId)) {
                continue;
            }
            passportId = passportId.toLowerCase();
            if (accountDAO.getAccountByPassportId(passportId) != null) {
                continue;
            }
            UserInfoTmp userInfoTmp = userInfoMap.get(passportId);
            UserOtherInfoTmp userOtherInfoTmp = userOtherInfoMap.get(passportId);
            UserExtInfoTmp userExtInfoTmp = userExtInfoTmpMap.get(passportId);
            if (userInfoTmp == null || ("0".equals(userInfoTmp.getFlag()) | Strings.isNullOrEmpty(userInfoTmp.getPassword()))) {
                content = passportId + ",IncUserInfo is empty or flag==0 or password==null";
                contentList.add(content);
                continue;
            }
            try {
                String email = null, mobile = null, personalId = null, province = "", city = "", uniqname = null;
                if (userOtherInfoTmp != null) {
                    email = "1".equals(userOtherInfoTmp.getEmailflag()) ? userOtherInfoTmp.getEmail() : "";
                    mobile = "1".equals(userOtherInfoTmp.getMobileflag()) ? userOtherInfoTmp.getMobile() : "";
                    personalId = !Strings.isNullOrEmpty(userOtherInfoTmp.getPersonalid()) ? userOtherInfoTmp.getPersonalid() : "";
                    province = !Strings.isNullOrEmpty(userOtherInfoTmp.getProvince()) ? userOtherInfoTmp.getProvince() : "";
                    city = !Strings.isNullOrEmpty(userOtherInfoTmp.getCity()) ? userOtherInfoTmp.getCity() : "";
                    uniqname = !Strings.isNullOrEmpty(userOtherInfoTmp.getUniqname()) ? userOtherInfoTmp.getUniqname() : "";
                }
                String question = null, answer = null, gender = "0", fullname = null, reg_ip = null;
                Date birthday = DateUtil.parse("1900-00-00", DateUtil.DATE_FMT_3);
                Date reg_time = new Date();
                if (userExtInfoTmp != null) {
                    question = !Strings.isNullOrEmpty(userExtInfoTmp.getQuestion()) ? userExtInfoTmp.getQuestion() : "";
                    answer = !Strings.isNullOrEmpty(userExtInfoTmp.getAnswer()) ? userExtInfoTmp.getAnswer() : "";
                    gender = !Strings.isNullOrEmpty(userExtInfoTmp.getGender()) ? userExtInfoTmp.getGender() : "";
                    fullname = !Strings.isNullOrEmpty(userExtInfoTmp.getUsername()) ? userExtInfoTmp.getUsername() : "";
                    reg_ip = !Strings.isNullOrEmpty(userExtInfoTmp.getCreateip()) ? userExtInfoTmp.getCreateip() : "";
                    reg_time = !Strings.isNullOrEmpty(userExtInfoTmp.getCreatetime()) ? DateUtil.parse(userExtInfoTmp.getCreatetime(), DateUtil.DATE_FMT_3) : reg_time;
                }

                Account account = new Account();
                account.setPassportId(passportId);
                account.setPassword(userInfoTmp.getPassword());
                account.setPasswordtype(Integer.parseInt(userInfoTmp.getPasswordtype()));
                account.setMobile(mobile);
                account.setRegTime(reg_time);
                account.setRegIp(reg_ip);
                account.setFlag(1);
                if (AccountDomainEnum.OTHER.equals(AccountDomainEnum.getAccountDomain(passportId))) {
                    account.setAccountType(AccountTypeEnum.EMAIL.getValue());
                } else if (AccountDomainEnum.PHONE.equals(AccountDomainEnum.getAccountDomain(passportId))) {
                    account.setAccountType(AccountTypeEnum.PHONE.getValue());
                } else {
                    content = passportId + ",accountType not other or phone";
                    contentList.add(content);
                    continue;
                }
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

                if (fillSGDB(uniqname, mobile, passportId, account, accountInfo, contentList)) {
                    count++;
                }
            } catch (Exception e) {
                content = passportId + ",insertSogouDB Error," + e.getMessage();
                contentList.add(content);
            }
        }
        content = "total:" + userInfoMap.size() + ",InertSuccess:" + count;
        contentList.add(content);
        long end = System.currentTimeMillis();
        content = "use time:" + (end - start) / 1000 / 60 + "m";
        contentList.add(content);
        try {
            FileUtil.storeFile("D:\\数据迁移\\写分离前需要迁移的账号\\fill_phone_userid_0526_0725", contentList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * 缺失账号写入搜狗数据库
     */
    private boolean fillSGDB(String uniqname, String mobile, String passportId, Account account, AccountInfo accountInfo, List<String> contentList) throws Exception {
        int mobileMapSucc = 1, accountSucc = 1, accountInfoSucc = 1, uniqMapSucc = 1;
        if (!Strings.isNullOrEmpty(mobile)) {
            String mobilePassportId = mobilePassportMappingDAO.getPassportIdByMobile(mobile);
            if (Strings.isNullOrEmpty(mobilePassportId)) {
                mobileMapSucc = mobilePassportMappingDAO.insertMobilePassportMapping(mobile, passportId);
            } else {
                account.setMobile(null);
            }
        }
        if (!Strings.isNullOrEmpty(uniqname)) {
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
            return true;
        } else {
            String content = passportId + ",mobileMap-" + mobileMapSucc + ",uniqMap-" + uniqMapSucc + ",account-" + accountSucc + ",accountInfo-" + accountInfoSucc;
            contentList.add(content);
            return false;
        }
    }

    /*据来源：双读异常log里用搜狐域账号绑定的手机号来登录的useridld列表，检查是否在搜狗数据库里
    * 输入："mobile userid"
    * 输出：不在搜狗数据库里的账号
    */
    @Test
    public void checkSohuBindMobileDate() {
        List<String> contentList = Lists.newArrayList();
        List<String> dataList = FileUtil.readFileByLines("D:\\数据迁移\\用搜狐域绑定的手机号登录\\sohubindmobile_0629_0712_total");
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

    /**
     * 验证搜狐导出的外域邮箱或手机账号是否在搜狐存在
     */
    @Test
    public void testBatchCheckUser() {
        List<String> contentList = Lists.newArrayList();
        List<String> passportList = FileUtil.readFileByLines("D:\\phone_diff");
        CheckUserApiParams checkUserApiParams = new CheckUserApiParams();
        String content;
        for (String passportId : passportList) {
            checkUserApiParams.setUserid("toptxy123@sogou.com");
            Result result = proxyRegisterApiManager.checkUser(checkUserApiParams);
            if (!result.isSuccess()) {
                content = passportId + "," + result.toString();
                contentList.add(content);
            }
        }
        try {
            FileUtil.storeFile("D:\\数据迁移\\写分离前需要迁移的账号\\phone_diff_result", contentList);
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
        List<String> passportIdList = FileUtil.readFileByLines("D:\\regfail_0716_total");
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


    private static RequestModelXml buildRequestModelXml(String passportId) {

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
